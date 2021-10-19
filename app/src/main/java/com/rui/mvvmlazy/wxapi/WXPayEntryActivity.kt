package com.rui.mvvmlazy.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.jeremyliao.liveeventbus.LiveEventBus
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler

class WXPayEntryActivity : Activity(), IWXAPIEventHandler {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onReq(req: BaseReq) {}
    override fun onResp(resp: BaseResp) {
        LiveEventBus.get("WXResp", BaseResp::class.java).post(resp)
        finish()
    }

    companion object {
        private const val TAG = "MicroMsg.SDKSample.WXPayEntryActivity"
    }
}