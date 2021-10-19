package com.rui.mvvmlazy.utils.display

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */
/**
 * 屏幕相关的操作类
 */
object ScreenUtils {
    /**
     * 获取宽度
     *
     * @param mContext 上下文
     * @return 宽度值，px
     */
    fun getWidth(mContext: Context): Int {
        val displayMetrics = DisplayMetrics()
        (mContext.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            .defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    /**
     * 获取高度
     *
     * @param mContext 上下文
     * @return 高度值，px
     */
    fun getHeight(mContext: Context): Int {
        val displayMetrics = DisplayMetrics()
        (mContext.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            .defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    /**
     * 是否在屏幕右侧
     *
     * @param mContext 上下文
     * @param xPos     位置的x坐标值
     * @return true：是。
     */
    fun isInRight(mContext: Context, xPos: Int): Boolean {
        return xPos > getWidth(mContext) / 2
    }

    /**
     * 是否在屏幕左侧
     *
     * @param mContext 上下文
     * @param xPos     位置的x坐标值
     * @return true：是。
     */
    fun isInLeft(mContext: Context, xPos: Int): Boolean {
        return xPos < getWidth(mContext) / 2
    }

    /**
     * 是否在View的右侧
     * @param view      要判断的View
     * @param xPos      位置x坐标
     * @return          true:是 false:不是
     */
    fun isInRight(view: View, xPos: Int): Boolean {
        return xPos > view.measuredWidth / 2
    }

    /**
     * 是否在View的左侧
     * @param view      要判断的View
     * @param xPos      位置x坐标
     * @return          true:是 false:不是
     */
    fun isInLeft(view: View, xPos: Int): Boolean {
        return xPos < view.measuredWidth / 2
    }

    fun getNavigationBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId =
            resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }
}