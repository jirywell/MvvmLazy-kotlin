package com.rui.mvvmlazy.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.customview.widget.ViewDragHelper
import android.view.MotionEvent
import android.view.View
import java.lang.IllegalArgumentException

/**
 * *******************************
 * *@Author
 * *date ：
 * *description:自定义拖动布局
 * *******************************
 */
class DragView(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    private val mDragger: ViewDragHelper
    private var mAutoBackView: View? = null
    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return mDragger.shouldInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        //点击位置坐标
        val downX = event.x.toInt()
        val downY = event.y.toInt()
        //悬浮区域左上角坐标
        val x = mAutoBackView!!.x.toInt()
        val y = mAutoBackView!!.y.toInt()
        //悬浮区域宽高
        val width = mAutoBackView!!.width
        val height = mAutoBackView!!.height
        return if (downX >= x && downY >= y && downX <= x + width && downY <= y + height) {
            //点击在悬浮区域内部进行事件拦截，否则不拦截
            try {
                mDragger.processTouchEvent(event)
                true
            } catch (e: IllegalArgumentException) {
                false
            }
        } else {
            false
        }
    }

    override fun computeScroll() {
        if (mDragger.continueSettling(true)) {
            invalidate()
        }
    }

    /**
     * onFinishInflate 当View中所有的子控件均被映射成xml后触发
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
        mAutoBackView = getChildAt(0)
    }

    init {
        mDragger = ViewDragHelper.create(this, 1.0f, object : ViewDragHelper.Callback() {
            override fun getViewHorizontalDragRange(child: View): Int {
                //返回可拖动的子视图的水平运动范围(以像素为单位)的大小。
                //对于不能垂直移动的视图，此方法应该返回0。
                return measuredWidth - child.measuredWidth
            }

            override fun getViewVerticalDragRange(child: View): Int {
                //返回可拖动的子视图的竖直运动范围(以像素为单位)的大小。
                //对于不能垂直移动的视图，此方法应该返回0。
                return measuredHeight - child.measuredHeight
            }

            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                //返回true表view示捕获当前touch到的
                return true
            }

            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                var left = left
                if (left > width - child.measuredWidth) {
                    //超出左侧边界处理
                    left = width - child.measuredWidth
                } else if (left < 0) {
                    //超出右侧边界处理
                    left = 0
                }
                return left
            }

            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                var top = top
                if (top > height - child.measuredHeight) {
                    //超出下边界处理
                    top = height - child.measuredHeight
                } else if (top < 0) {
                    //超出上边界处理
                    top = 0
                }
                return top
            }
        })
        mDragger.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT)
    }
}