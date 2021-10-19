package com.rui.home.utils

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import com.rui.mvvmlazy.base.appContext

/**
 * Author ：贾卫星
 * Date: 2020/6/4 00049:58
 * Description: 设置text背景色和点击事件工具类
 */
object TextBackClickUtils {
    var defaultColorString = "#000000"
    fun setBackClick(
        tv: TextView,
        spannableStringBuilder: SpannableStringBuilder,
        colorString: String?,
        start: Int,
        end: Int,
        textClickListener: (View, CharSequence) -> Unit
    ) {
        setTextClick(tv, spannableStringBuilder, start, end, textClickListener)
        setTextBackGround(tv, spannableStringBuilder, start, end, colorString)
    }

    /**
     * 设置textView 部分内容的点击事件
     * @param tv    TextView  控件
     * @param spannableStringBuilder   显示的文字
     * @param start                     开始位置
     * @param end                       结束位置
     * @param textClickListener           监听类
     */
    fun setTextClick(
        tv: TextView,
        spannableStringBuilder: SpannableStringBuilder,
        start: Int,
        end: Int,
        textClickListener: (View, CharSequence) -> Unit
    ) {
        //设置部分文字点击事件
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                textClickListener.invoke(widget, spannableStringBuilder.subSequence(start, end))
            }

            //去除连接下划线
            override fun updateDrawState(ds: TextPaint) {
                /**set textColor */
                ds.color = ds.linkColor
                /**Remove the underline */
                ds.isUnderlineText = false
            }
        }
        tv.highlightColor = appContext.resources.getColor(android.R.color.transparent)
        spannableStringBuilder.setSpan(
            clickableSpan,
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv.text = spannableStringBuilder
    }

    /**
     * 设置tv 的背景色
     * @param tv                        TextView  控件
     * @param spannableStringBuilder     显示的文字
     * @param start                         开始位置
     * @param end                           结束位置
     * @param colorString                   设置字体颜色
     */
    fun setTextBackGround(
        tv: TextView,
        spannableStringBuilder: SpannableStringBuilder,
        start: Int,
        end: Int,
        colorString: String?
    ) {
        //设置部分文字颜色
        val foregroundColorSpan =
            ForegroundColorSpan(Color.parseColor(if (colorString == null || colorString.isEmpty()) defaultColorString else colorString))
        spannableStringBuilder.setSpan(
            foregroundColorSpan,
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        //配置给TextView
        tv.movementMethod = LinkMovementMethod.getInstance()
        tv.text = spannableStringBuilder
    }

}