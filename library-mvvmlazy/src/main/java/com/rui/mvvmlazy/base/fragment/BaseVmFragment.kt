package com.rui.mvvmlazy.base.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gyf.immersionbar.ImmersionBar
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView
import com.rui.mvvmlazy.base.BaseViewModel
import com.rui.mvvmlazy.base.BaseViewModel.ParameterField
import com.rui.mvvmlazy.base.ContainerActivity
import com.rui.mvvmlazy.base.IBaseView
import java.lang.reflect.ParameterizedType

/**
 * Created by zjr on 2020/6/15.
 */
abstract class BaseVmFragment<VM : BaseViewModel> : Fragment(), IBaseView {
    lateinit var viewModel: VM
    var viewModelId = 0
    private var loadingPopup: LoadingPopupView? = null

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
        viewModelId = initVariableId()
        viewModel = initViewModel()
        //让ViewModel拥有View的生命周期感应
        lifecycle.addObserver(viewModel)
        initParam()
        initSavedInstanceState(savedInstanceState)
    }

    private fun initLayout() {
        mTitleBar = obtainTitleBar(view as ViewGroup?)
        if (mTitleBar != null) {
            initTitleBar(mTitleBar)
            mTitleBar!!.setOnTitleBarListener(object : OnTitleBarListener {
                override fun onLeftClick(view: View) {
                    requireActivity().onBackPressed()
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
    private val statusBarConfig: ImmersionBar
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
     * 递归获取 ViewGroup 中的 TitleBar 对象
     */
    private fun obtainTitleBar(group: ViewGroup?): TitleBar? {
        for (i in 0 until group!!.childCount) {
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

    override fun onDestroy() {
        super.onDestroy()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.removeEventBus()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //私有的ViewModel与View的契约事件回调逻辑
        registorUIChangeLiveDataCallBack()
        //页面数据初始化方法
        initData()
        //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable()
        initLayout()
        //注册RxBus
        viewModel.registerEventBus()
        viewModel.initData()
    }


    /**
     * =====================================================================
     */
    //注册ViewModel与View的契约UI回调事件
    protected fun registorUIChangeLiveDataCallBack() {
        //加载对话框显示
        viewModel.uc.showDialogEvent.observe(
            this.viewLifecycleOwner,
            { title -> showDialog(title) })
        //加载对话框消失
        viewModel.uc.dismissDialogEvent.observe(this.viewLifecycleOwner, { dismissDialog() })
        //跳入新页面
        viewModel.uc.startActivityEvent.observe(this.viewLifecycleOwner, { params ->
            val clz = params!![ParameterField.CLASS] as Class<*>?
            val bundle = params[ParameterField.BUNDLE] as Bundle?
            startActivity(clz, bundle)
        })
        //跳入ContainerActivity
        viewModel.uc.startContainerActivityEvent.observe(this.viewLifecycleOwner, { params ->
            val canonicalName = params!![ParameterField.CANONICAL_NAME] as String?
            val bundle = params[ParameterField.BUNDLE] as Bundle?
            startContainerActivity(canonicalName, bundle)
        })
        //关闭界面
        viewModel.uc.finishEvent.observe(this.viewLifecycleOwner, { requireActivity().finish() })
        //关闭上一层
        viewModel.uc.onBackPressedEvent.observe(
            this.viewLifecycleOwner,
            { requireActivity().onBackPressed() })
    }

    fun showDialog(title: String?) {
        if (loadingPopup == null) {
            loadingPopup = XPopup.Builder(requireContext())
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
        startActivity(Intent(context, clz))
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    fun startActivity(clz: Class<*>?, bundle: Bundle?) {
        val intent = Intent(context, clz)
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
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName)
        if (bundle != null) {
            intent.putExtra(ContainerActivity.BUNDLE, bundle)
        }
        startActivity(intent)
    }


    override fun initParam() {}
    open fun initTitleBar(titleBar: TitleBar?) {}
    private fun initSavedInstanceState(savedInstanceState: Bundle?) {}

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    abstract fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int

    /**
     * 初始化ViewModel的id
     *
     * @return BR的id
     */
    abstract fun initVariableId(): Int

    /**
     * 初始化ViewModel
     *
     * @return 继承BaseViewModel的ViewModel
     */
    private fun initViewModel(): VM {

        return ViewModelProvider(
            if (isActivityViewModel) {
                requireActivity()
            } else {
                this
            }
        ).get(getVmClazz(this))
    }

    /**
     * 是否获取 activityViewModel
     */
    protected open val isActivityViewModel: Boolean
        get() = false

    private fun <T> getVmClazz(obj: Any): T {
        val type= (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
        return type[0] as T
    }


    override fun initData() {}
    override fun initViewObservable() {}
    val isBackPressed: Boolean
        get() = false

}