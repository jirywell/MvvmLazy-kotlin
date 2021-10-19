/*
 * Copyright (C) 2018 jirui_zhao(jirui_zhao@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rui.mvvmlazy.utils.common

import android.os.SystemClock
import android.view.View
import java.util.*

/**
 * 快速点击工具类
 *
 * @author zjr
 * @since 2019-08-08 16:35
 */
class ClickUtils() {
    /**
     * 多次点击的监听
     */
    interface OnContinuousClickListener {
        /**
         * 多次点击
         */
        fun onContinuousClick()
    }

    /**
     * 点击返回退出监听
     */
    interface OnClick2ExitListener {
        /**
         * 再点击一次
         */
        fun onRetry()

        /**
         * 退出
         */
        fun onExit()
    }

    companion object {
        /**
         * 默认点击时间间期（毫秒）
         */
        private const val DEFAULT_INTERVAL_MILLIS: Long = 1000

        /**
         * 最近一次点击的时间
         */
        private var sLastClickTime: Long = 0

        /**
         * 最近一次点击的控件ID
         */
        private var sLastClickViewId = 0

        /**
         * 是否是快速点击
         *
         * @param v 点击的控件
         * @return true:是，false:不是
         */
        fun isFastDoubleClick(v: View): Boolean {
            return isFastDoubleClick(v, DEFAULT_INTERVAL_MILLIS)
        }

        /**
         * 是否是快速点击
         *
         * @param v              点击的控件
         * @param intervalMillis 时间间期（毫秒）
         * @return
         */
        fun isFastDoubleClick(v: View, intervalMillis: Long): Boolean {
            val time = System.currentTimeMillis()
            val viewId = v.id
            val timeD = time - sLastClickTime
            return if (0 < timeD && timeD < intervalMillis && viewId == sLastClickViewId) {
                true
            } else {
                sLastClickTime = time
                sLastClickViewId = viewId
                false
            }
        }
        //====================多次点击==========================//
        /**
         * 点击次数
         */
        private const val COUNTS = 5
        private var sHits = LongArray(COUNTS)

        /**
         * 规定有效时间
         */
        private const val DEFAULT_DURATION: Long = 1000

        /**
         * 一秒内连续点击5次
         *
         * @param listener 多次点击的监听
         */
        fun doClick(listener: OnContinuousClickListener?) {
            doClick(DEFAULT_DURATION, listener)
        }

        /**
         * 规定时间内连续点击5次
         *
         * @param duration 规定时间
         * @param listener 多次点击的监听
         */
        fun doClick(duration: Long, listener: OnContinuousClickListener?) {
            //每次点击时，数组向前移动一位
            System.arraycopy(sHits, 1, sHits, 0, sHits.size - 1)
            //为数组最后一位赋值
            sHits[sHits.size - 1] = SystemClock.uptimeMillis()
            if (sHits[0] >= SystemClock.uptimeMillis() - duration) {
                //重新初始化数组
                sHits = LongArray(COUNTS)
                listener?.onContinuousClick()
            }
        }
        //====================双击退出==========================//
        /**
         * 双击退出函数
         */
        private var sIsExit = false
        /**
         * 双击返回退出程序
         *
         * @param intervalMillis 按键间隔
         * @param listener       退出监听
         */
        /**
         * 双击返回退出程序
         */
        @JvmOverloads
        fun exitBy2Click(intervalMillis: Long = 2000, listener: OnClick2ExitListener? = null) {
            if (!sIsExit) {
                sIsExit = true
                // 准备退出
                if (listener != null) {
                    listener.onRetry()
                } else {
                    ToastUtils.Companion.showShort("再按一次退出程序")
                }
                val tExit = Timer()
                tExit.schedule(object : TimerTask() {
                    override fun run() {
                        // 取消退出
                        sIsExit = false
                    } // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
                }, intervalMillis)
            } else {
                listener?.onExit()
            }
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}