package com.rui.mvvmlazy.base.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.gyf.immersionbar.ImmersionBar
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView
import com.rui.mvvmlazy.base.BaseViewModel
import com.rui.mvvmlazy.base.ContainerActivity
import com.rui.mvvmlazy.base.IBaseView
import java.lang.reflect.ParameterizedType

/**
 * Created by zjr on 2020/6/15.
 * 一个拥有DataBinding框架的基Activity
 * 这里根据项目业务可以换成你自己熟悉的BaseActivity, 但是需要继承RxAppCompatActivity,方便LifecycleProvider管理生命周期
 */
abstract class BaseVmActivity<VM : BaseViewModel> : AppCompatActivity(), IBaseView {
    lateinit var viewModel: VM
    var viewModelId = 0

    /**
     * 加载框
     */
    private var loadingPopup: LoadingPopupView? = null
    private var mContext: Context? = null

    /**
     * 标题栏对象
     */
    private var mTitleBar: TitleBar? = null

    /**
     * 状态栏沉浸
     */
    private var mImmersionBar: ImmersionBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //私有的初始化ViewModel方法
        viewModelId = initVariableId()
        viewModel = initViewModel()
        //让ViewModel拥有View的生命周期感应
        lifecycle.addObserver(viewModel)
        if (!initViewDataBinding()) {
            setContentView(initContentView())
        }
        //私有的ViewModel与View的契约事件回调逻辑
        registorUIChangeLiveDataCallBack()
        mContext = this
        //页面接受的参数方法
        initParam()
        initLayout()
        //页面数据初始化方法base
        initData()
        //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable()
        //注册RxBus
        viewModel.registerEventBus()
        viewModel.initData()
    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.removeEventBus()
    }

    /**
     * 初始化ViewModel的id
     *
     * @return BR的id
     */
    abstract fun initVariableId(): Int

    /**
     * 注入绑定
     */
    open fun initViewDataBinding(): Boolean = false

    /**
     * 是否使用沉浸式状态栏
     */
    private val isStatusBarEnabled: Boolean
        get() = true

    /**
     * 状态栏字体深色模式
     */
    private val isStatusBarDarkFont: Boolean
        get() = true

    /**
     * 获取状态栏沉浸的配置对象
     */
    val statusBarConfig: ImmersionBar
        get() {
            if (mImmersionBar == null) {
                mImmersionBar = createStatusBarConfig()
            }
            return mImmersionBar as ImmersionBar
        }

    /**
     * 初始化沉浸式状态栏,可重写此方法进行状态栏定制
     */
    protected open fun createStatusBarConfig(): ImmersionBar {
        return ImmersionBar.with(this) // 默认状态栏字体颜色为黑色
            .statusBarDarkFont(isStatusBarDarkFont) // 指定导航栏背景颜色
            .navigationBarColor(android.R.color.white) // 状态栏字体和导航栏内容自动变色，必须指定状态栏颜色和导航栏颜色才可以自动变色
            .autoDarkModeEnable(true, 0.2f)
    }

    /**
     * =====================================================================
     */
    //注册ViewModel与View的契约UI回调事件
    private fun registorUIChangeLiveDataCallBack() {
        //加载对话框显示
        viewModel.uc.showDialogEvent.observe(this, { title -> showDialog(title) })
        //加载对话框消失
        viewModel.uc.dismissDialogEvent.observe(this, { dismissDialog() })
        //跳入新页面
        viewModel.uc.startActivityEvent.observe(this, { params ->
            val clz = params!![BaseViewModel.ParameterField.CLASS] as Class<*>?
            val bundle = params[BaseViewModel.ParameterField.BUNDLE] as Bundle?
            startActivity(clz, bundle)
        })
        //跳入ContainerActivity
        viewModel.uc.startContainerActivityEvent.observe(this, { params ->
            val canonicalName = params!![BaseViewModel.ParameterField.CANONICAL_NAME] as String?
            val bundle = params[BaseViewModel.ParameterField.BUNDLE] as Bundle?
            startContainerActivity(canonicalName, bundle)
        })
        //关闭界面
        viewModel.uc.finishEvent.observe(this, { finish() })
        //关闭上一层
        viewModel.uc.onBackPressedEvent.observe(this, { onBackPressed() })
    }

    fun showDialog(title: String?) {
        if (loadingPopup == null) {
            loadingPopup = XPopup.Builder(this)
                .dismissOnBackPressed(false)
                .asLoading(title)
                .show() as LoadingPopupView
        } else {
            loadingPopup!!.setTitle(title)
            loadingPopup!!.show()
        }
    }

    fun dismissDialog() {
        if (loadingPopup != null && loadingPopup!!.isShow) {
            loadingPopup!!.dismiss()
        }
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    fun startActivity(clz: Class<*>?) {
        startActivity(Intent(this, clz))
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    fun startActivity(clz: Class<*>?, bundle: Bundle?) {
        val intent = Intent(this, clz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     */
    @JvmOverloads
    fun startContainerActivity(canonicalName: String?, bundle: Bundle? = null) {
        val intent = Intent(this, ContainerActivity::class.java)
        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName)
        if (bundle != null) {
            intent.putExtra(ContainerActivity.BUNDLE, bundle)
        }
        startActivity(intent)
    }

    /**
     * =====================================================================
     */
    private fun initLayout() {
        mTitleBar = obtainTitleBar(findViewById(Window.ID_ANDROID_CONTENT))
        if (mTitleBar != null) {
            initTitleBar(mTitleBar)
            mTitleBar!!.setOnTitleBarListener(object : OnTitleBarListener {
                override fun onLeftClick(view: View) {
                    onBackPressed()
                }

                override fun onTitleClick(view: View) {}
                override fun onRightClick(view: View) {}
            })
        }
        // 初始化沉浸式状态栏
        if (isStatusBarEnabled) {
            statusBarConfig.init()
            // 设置标题栏沉浸
            if (mTitleBar != null) {
                ImmersionBar.setTitleBar(this, mTitleBar)
            }
        }
    }

    override fun initParam() {}

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    abstract fun initContentView(): Int


    /**
     * 初始化ViewModel
     *
     * @return 继承BaseViewModel的ViewModel
     */
    private fun initViewModel(): VM {
        return ViewModelProvider(this).get(getVmClazz(this))
    }

    fun <T> getVmClazz(obj: Any): T {
        return (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as T
    }

    /**
     * 递归获取 ViewGroup 中的 TitleBar 对象
     */
    fun obtainTitleBar(group: ViewGroup): TitleBar? {
        for (i in 0 until group.childCount) {
            val view = group.getChildAt(i)
            if (view is TitleBar) {
                return view
            } else if (view is ViewGroup) {
                val titleBar = obtainTitleBar(view)
                if (titleBar != null) {
                    return titleBar
                }
            }
        }
        return null
    }

    open fun initTitleBar(titleBar: TitleBar?) {}
    override fun initData() {}
    override fun initViewObservable() {}

}