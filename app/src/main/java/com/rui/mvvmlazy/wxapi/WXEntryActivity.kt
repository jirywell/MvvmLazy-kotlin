package com.rui.mvvmlazy.wxapi

import android.app.Activity
import android.os.Bundle
import com.jeremyliao.liveeventbus.LiveEventBus
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler

/**
 * 微信回调处理
 */
class WXEntryActivity : Activity(), IWXAPIEventHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onReq(baseReq: BaseReq) {}
    override fun onResp(baseResp: BaseResp) {
        //Log.e("---接收到信息--",baseResp.openId);
        LiveEventBus.get("WXResp", BaseResp::class.java).post(baseResp)
        finish()
    }
}