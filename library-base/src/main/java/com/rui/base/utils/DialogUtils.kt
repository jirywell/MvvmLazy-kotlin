package com.rui.base.utils

import android.content.Context
import android.view.View
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.AttachPopupView
import com.lxj.xpopup.impl.LoadingPopupView
import com.lxj.xpopup.interfaces.OnCancelListener
import com.lxj.xpopup.interfaces.OnConfirmListener
import com.lxj.xpopup.interfaces.OnInputConfirmListener
import com.lxj.xpopup.interfaces.OnSelectListener
import com.lxj.xpopupext.listener.CityPickerListener
import com.lxj.xpopupext.listener.CommonPickerListener
import com.lxj.xpopupext.listener.TimePickerListener
import com.lxj.xpopupext.popup.CityPickerPopup
import com.lxj.xpopupext.popup.CommonPickerPopup
import com.lxj.xpopupext.popup.TimePickerPopup
import com.rui.base.view.EditTextBottomPopup
import com.rui.base.view.HintPopup
import com.rui.mvvmlazy.base.AppManager.Companion.appManager
import java.lang.ref.WeakReference
import java.util.*

/**
 * 创建日期： 2020/6/22 15:10
 * 作者：     zjr
 * 文件名称： DialogUtils
 * 类说明：  常用弹框工具类
 */
object DialogUtils {
    private var mLoadingPopup: WeakReference<LoadingPopupView?>? = null

    /**
     * 确认弹框
     *
     * @param title           标题
     * @param content         内容
     * @param confirmListener 确认监听
     * @param cancelListener  取消监听
     */
    @JvmStatic
    fun showConfirmDialog(
        title: CharSequence?,
        content: CharSequence?,
        confirmListener: OnConfirmListener?,
        cancelListener: OnCancelListener?
    ) {
        XPopup.Builder(context)
            .hasNavigationBar(false)
            .isDestroyOnDismiss(true)
            .asConfirm(
                title, content,
                "取消", "确定",
                confirmListener, cancelListener, false
            ).show()
    }

    /**
     * 带输入框的弹窗
     *
     * @param title           标题
     * @param inputContent    输入框内容
     * @param confirmListener 取消监听
     */
    @JvmStatic
    fun showInputDialog(
        title: CharSequence?,
        inputContent: CharSequence?,
        confirmListener: OnInputConfirmListener?
    ) {
        XPopup.Builder(context)
            .hasStatusBarShadow(false) //.dismissOnBackPressed(false)
            .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
            .autoOpenSoftInput(true)
            .asInputConfirm(
                title, null, inputContent, null,
                confirmListener
            )
            .show()
    }

    /**
     * 完成提示
     *
     * @param msg 文本信息
     */
    @JvmStatic
    fun showHintFinish(msg: CharSequence?) {
        val hintPopup = HintPopup(context!!)
        hintPopup.setIcon(HintPopup.ICON_FINISH)
            .setMessage(msg)
        XPopup.Builder(context)
            .hasShadowBg(false)
            .asCustom(hintPopup)
            .show()
    }

    /**
     * 错误提示
     *
     * @param msg 文本
     */
    @JvmStatic
    fun showHintError(msg: CharSequence?) {
        val hintPopup = HintPopup(context!!)
        hintPopup.setIcon(HintPopup.ICON_ERROR)
            .setMessage(msg)
        XPopup.Builder(context)
            .hasShadowBg(false)
            .asCustom(hintPopup)
            .show()
    }

    /**
     * 警告提示
     *
     * @param msg 文本
     */
    @JvmStatic
    fun showHintWarning(msg: CharSequence?) {
        val hintPopup = HintPopup(context!!)
        hintPopup.setIcon(HintPopup.ICON_WARNING)
            .setMessage(msg)
        XPopup.Builder(context)
            .hasShadowBg(false)
            .asCustom(hintPopup)
            .show()
    }

    /**
     * 加载弹窗
     *
     * @param msg 文本
     * @return LoadingPopupView对象
     */
    @JvmStatic
    fun showLoading(msg: CharSequence?): LoadingPopupView? {
        val loadingPopup: LoadingPopupView?
        if (mLoadingPopup == null || mLoadingPopup!!.get() == null) {
            loadingPopup = XPopup.Builder(context)
                .dismissOnBackPressed(false)
                .asLoading(msg)
                .show() as LoadingPopupView
            mLoadingPopup = WeakReference(loadingPopup)
        } else {
            loadingPopup = mLoadingPopup!!.get()
            loadingPopup!!.setTitle(msg)
            loadingPopup.show()
        }
        return loadingPopup
    }

    /**
     * 取消进度框
     */
    @JvmStatic
    fun cancelLoading() {
        if (mLoadingPopup != null && mLoadingPopup!!.get()!!.isShow) {
            mLoadingPopup!!.get()!!.smartDismiss()
        }
        if (mLoadingPopup != null) mLoadingPopup!!.clear()
    }

    /**
     * 依附列表弹窗
     *
     * @param v              依附控件
     * @param data           列表数据
     * @param iconIds        图片数据
     * @param selectListener 选择监听
     */
    @JvmStatic
    fun showAttachList(
        v: View?,
        data: Array<String?>?,
        iconIds: IntArray?,
        selectListener: OnSelectListener?
    ) {
        val attachPopupView: AttachPopupView = XPopup.Builder(context)
            .hasShadowBg(false)
            .isClickThrough(true) //                         .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
            //                        .isDarkTheme(true)
            //                        .popupAnimation(PopupAnimation.ScrollAlphaFromTop) //NoAnimation表示禁用动画
            //                .isCenterHorizontal(true) //是否与目标水平居中对齐
            //                        .offsetY(60)
            //                        .offsetX(80)
            //                        .popupPosition(PopupPosition.Top) //手动指定弹窗的位置
            //                        .popupWidth(500)
            .atView(v) // 依附于所点击的View，内部会自动判断在上方或者下方显示
            .asAttachList(
                data,
                iconIds, selectListener, 0, 0
            )
        attachPopupView.show()
    }

    /**
     * 评论弹窗
     *
     * @param onInputConfirmListener 输入内容确认回调
     */
    @JvmStatic
    fun showCommentDialog(onInputConfirmListener: OnInputConfirmListener?) {
        XPopup.Builder(context)
            .hasStatusBarShadow(false)
            .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
            .autoOpenSoftInput(true)
            .asCustom(EditTextBottomPopup(context!!, onInputConfirmListener))
            .show()
    }

    /**
     * 底部列表弹窗
     *
     * @param title          标题
     * @param data           数据
     * @param selectListener 选择回调
     */
    fun showBtmListDialog(
        title: CharSequence?,
        data: Array<String?>?,
        selectListener: OnSelectListener?
    ) {
        showBtmListDialog(title, data, null, -1, selectListener)
    }

    /**
     * 底部列表弹窗(带选中状态)
     *
     * @param title           标题
     * @param data            数据
     * @param iconIds         图标
     * @param checkedPosition 选中位置
     * @param selectListener  选择回调
     */
    @JvmStatic
    fun showBtmListDialog(
        title: CharSequence?,
        data: Array<String?>?,
        iconIds: IntArray?,
        checkedPosition: Int,
        selectListener: OnSelectListener?
    ) {
        XPopup.Builder(context)
            .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
            .asBottomList(
                title, data,
                iconIds, checkedPosition, selectListener
            )
            .show()
    }

    /**
     * 时间,日期选择框
     */
    @JvmStatic
    fun showTimeDialog(mode: TimePickerPopup.Mode?, timePickerListener: TimePickerListener?) {
//        Calendar date = Calendar.getInstance();
//        date.set(2000, 5, 1);
//        Calendar date2 = Calendar.getInstance();
//        date2.set(2020, 5, 1);
        val popup = TimePickerPopup(context!!)
            .setMode(mode) //                        .setDefaultDate(date)  //设置默认选中日期
            //                        .setYearRange(1990, 1999) //设置年份范围
            //                        .setDateRange(date, date2) //设置日期范围
            .setTimePickerListener(timePickerListener)
        XPopup.Builder(context)
            .asCustom(popup)
            .show()
    }

    /**
     * 城市选择弹框
     *
     * @param cityPickerListener 选择回调
     */
    @JvmStatic
    fun showCityDialog(cityPickerListener: CityPickerListener?) {
        val popup = CityPickerPopup(context!!)
        popup.setCityPickerListener(cityPickerListener)
        XPopup.Builder(context)
            .asCustom(popup)
            .show()
    }

    /**
     * 通用列表框
     *
     * @param list 列表数据
     */
    @JvmStatic
    fun showNormalListDialog(
        list: ArrayList<String?>?,
        commonPickerListener: CommonPickerListener?
    ) {
        val popup = CommonPickerPopup(context!!)
        popup.setPickerData(list)
            .setCurrentItem(1)
        popup.setCommonPickerListener(commonPickerListener)
        XPopup.Builder(context)
            .asCustom(popup)
            .show()
    }

    /**
     * 获取当前页面上下文对象
     *
     * @return Context
     */
    val context: Context?
        get() = appManager.currentActivity()
}