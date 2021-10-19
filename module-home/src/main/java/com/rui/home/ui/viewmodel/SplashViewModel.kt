package com.rui.home.ui.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import android.view.Gravity
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.XPopup
import com.rui.base.router.RouterActivityPath
import com.rui.home.R
import com.rui.home.utils.TextBackClickUtils
import com.rui.mvvmlazy.base.AppManager
import com.rui.mvvmlazy.base.BaseViewModel
import com.rui.mvvmlazy.base.appContext
import com.rui.mvvmlazy.utils.common.ToastUtils
import com.rui.mvvmlazy.utils.data.SPUtils

class SplashViewModel : BaseViewModel() {
    override fun initData() {
        super.initData()
        if (!SPUtils.instance.getBoolean("showWelDialog", false)) {
            val content = appContext.getString(R.string.home_welcome_dialog)
            val verification = SpannableStringBuilder()
            verification.append(content)
            val dialog = XPopup.Builder(AppManager.appManager.currentActivity())
                .hasNavigationBar(false)
                .isDestroyOnDismiss(true)
                .asConfirm("用户协议和隐私政策", verification,
                    "暂不使用", "同意",
                    {
                        SPUtils.instance.put("showWelDialog", true)
                        Handler(Looper.myLooper()!!).postDelayed({
                            ARouter.getInstance().build(RouterActivityPath.Test.TESTPAGER)
                                .navigation()
                            finish()
                        }, 1500)
                    }, {
                        finish()
                    }, false
                )
            dialog.show()
            val tvTip = dialog.contentTextView
            tvTip.gravity = Gravity.START
            TextBackClickUtils.setBackClick(
                tvTip,
                verification,
                "#2D6BF5",
                content.indexOf("《用户服务协议》"),
                content.indexOf("《用户服务协议》") + 8
            ) { view: View, charSequence: CharSequence ->
                ToastUtils.showShort("跳转《用户服务协议》")
            }
            TextBackClickUtils.setBackClick(
                tvTip,
                verification,
                "#2D6BF5",
                content.indexOf("《隐私政策》"),
                content.indexOf("《隐私政策》") + 6
            ) { view: View, charSequence: CharSequence ->
                ToastUtils.showShort("跳转《隐私政策》")
            }

        } else {
            Handler(Looper.myLooper()!!).postDelayed({
                ARouter.getInstance().build(RouterActivityPath.Test.TESTPAGER).navigation()
                finish()
            }, 1500)
        }
    }
}