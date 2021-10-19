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
package com.rui.mvvmlazy.utils.display

import android.R
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.*
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import java.util.*

/**
 * ViewUtils
 *
 * **get view height**
 *  * [ViewUtils.getListViewHeightBasedOnChildren]
 *  * [ViewUtils.getAbsListViewHeightBasedOnChildren]
 *
 *
 * **set view height**
 *  * [ViewUtils.setViewHeight] set view height
 *  * [ViewUtils.setListViewHeightBasedOnChildren]
 *  * [ViewUtils.setAbsListViewHeightBasedOnChildren]
 *
 *
 * **set other info**
 *  * [ViewUtils.setSearchViewOnClickListener]
 *
 *
 *
 * <pre>
 * desc   : 控件工具类
 * author : zjr
 * time   : 2018/4/23 下午4:29
</pre> *
 */
class ViewUtils() {
    companion object {


        /**
         * set view height
         *
         * @param view
         * @param height
         */
        fun setViewHeight(view: View?, height: Int) {
            if (view == null) {
                return
            }
            val params = view.layoutParams
            params.height = height
        }


        /**
         * set SearchView OnClickListener
         *
         * @param v
         * @param listener
         */
        fun setSearchViewOnClickListener(v: View?, listener: View.OnClickListener?) {
            if (v is ViewGroup) {
                val group = v
                val count = group.childCount
                for (i in 0 until count) {
                    val child = group.getChildAt(i)
                    if (child is LinearLayout
                        || child is RelativeLayout
                    ) {
                        setSearchViewOnClickListener(child, listener)
                    }
                    if (child is TextView) {
                        child.isFocusable = false
                    }
                    child.setOnClickListener(listener)
                }
            }
        }

        /**
         * get descended views from parent.
         *
         * @param parent
         * @param filter          Type of views which will be returned.
         * @param includeSubClass Whether returned list will include views which are subclass of
         * filter or not.
         * @return
         */
        fun <T : View?> getDescendants(
            parent: ViewGroup,
            filter: Class<T>,
            includeSubClass: Boolean
        ): List<T> {
            val descendedViewList: MutableList<T> = ArrayList()
            val childCount = parent.childCount
            for (i in 0 until childCount) {
                val child = parent.getChildAt(i)
                val childsClass: Class<out View> = child.javaClass
                if (includeSubClass && filter.isAssignableFrom(childsClass)
                    || !includeSubClass && childsClass == filter
                ) {
                    descendedViewList.add(filter.cast(child))
                }
                if (child is ViewGroup) {
                    descendedViewList.addAll(
                        getDescendants(
                            child,
                            filter, includeSubClass
                        )
                    )
                }
            }
            return descendedViewList
        }

        /**
         * Helps determine if the app is running in a Tablet context.
         *
         * @param context
         * @return
         */
        fun isTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
        }

        /**
         * 获取activity最顶层的父布局
         *
         * @android:id/content 对应的控件是FrameLayout
         *
         * @param activity
         * @return
         */
        fun getContentView(activity: Activity): FrameLayout {
            val view = activity.window.decorView as ViewGroup
            return view.findViewById(R.id.content)
        }

        /**
         * View设备背景
         *
         * @param context
         * @param view
         * @param res
         */
        fun setBackground(context: Context, view: View?, res: Int) {
            val bmp = BitmapFactory.decodeResource(context.resources, res)
            val bd = BitmapDrawable(context.resources, bmp)
            view?.setBackgroundDrawable(bd)
        }

        /**
         * 释放图片资源
         *
         * @param v
         */
        fun recycleBackground(v: View) {
            val d = v.background
            v.setBackgroundResource(0) //别忘了把背景设为null，避免onDraw刷新背景时候出现used a recycled bitmap错误
            if (d != null && d is BitmapDrawable) {
                val bmp = d.bitmap
                if (bmp != null && !bmp.isRecycled) {
                    bmp.recycle()
                }
            }
            if (d != null) {
                d.callback = null
            }
        }

        /**
         * 遍历View,清除所有ImageView的缓存
         *
         * @param view
         */
        fun clearImageView(view: View) {
            if (view is ViewGroup) {
                val parent = view
                val count = parent.childCount
                for (i in 0 until count) {
                    clearImageView(parent.getChildAt(i))
                }
            } else if (view is ImageView) {
                clearImgMemory(view)
            }
        }

        /**
         * 清空图片的内存
         */
        fun clearImgMemory(imageView: ImageView) {
            val d = imageView.drawable
            if (d != null && d is BitmapDrawable) {
                val bmp = d.bitmap
                if (bmp != null && !bmp.isRecycled) {
                    bmp.recycle()
                }
            }
            imageView.setImageBitmap(null)
            if (d != null) {
                d.callback = null
            }
        }
        //=====================View 常用操作==============================//
        /**
         * 设置控件的可见度
         *
         * @param view   控件
         * @param isShow 是否可见
         */
        fun setVisibility(view: View?, isShow: Boolean) {
            if (view != null) {
                view.visibility = if (isShow) View.VISIBLE else View.GONE
            }
        }

        /**
         * 设置控件的可见度
         *
         * @param view       控件
         * @param visibility
         */
        fun setVisibility(view: View?, visibility: Int) {
            if (view != null) {
                view.visibility = visibility
            }
        }

        /**
         * 设置控件是否可用
         *
         * @param view    控件
         * @param enabled 是否可用
         */
        fun setEnabled(view: View?, enabled: Boolean) {
            if (view != null) {
                view.isEnabled = enabled
                if (view is EditText) {
                    view.setFocusable(enabled)
                    view.setFocusableInTouchMode(enabled)
                }
            }
        }

        /**
         * 设置控件的文字
         *
         * @param view 控件
         * @param text 文字
         */
        fun setText(view: TextView?, text: String?) {
            if (view != null) {
                view.text = text
            }
        }

        /**
         * 设置控件的文字
         *
         * @param view   控件
         * @param textId 文字资源
         */
        fun setText(view: TextView?, @StringRes textId: Int) {
            view?.setText(textId)
        }

        /**
         * 设置控件的文字颜色
         *
         * @param view    控件
         * @param colorId 文字颜色
         */
        fun textColorId(view: TextView?, @ColorRes colorId: Int) {
            view?.setTextColor(ContextCompat.getColor(view.context, colorId))
        }

        /**
         * 设置控件的图片资源
         *
         * @param view    控件
         * @param imageId 图片资源ID
         */
        fun setImageResource(view: ImageView?, @DrawableRes imageId: Int) {
            view?.setImageResource(imageId)
        }

        /**
         * 设置控件的图片资源
         *
         * @param view     控件
         * @param drawable 图片资源
         */
        fun setImageDrawable(view: ImageView?, drawable: Drawable?) {
            view?.setImageDrawable(drawable)
        }

        /**
         * 设置控件的图片资源
         *
         * @param view 控件
         * @param uri  图片资源
         */
        fun setImageURI(view: ImageView?, uri: Uri?) {
            view?.setImageURI(uri)
        }

        /**
         * 设置图片的等级
         *
         * @param view  控件
         * @param level 图片等级
         */
        fun setImageLevel(view: ImageView?, level: Int) {
            view?.setImageLevel(level)
        }

        /**
         * 给图片着色
         *
         * @param view 控件
         * @param tint 着色
         */
        fun setImageTint(view: ImageView?, tint: ColorStateList?) {
            if (view != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.imageTintList = tint
                }
            }
        }

        /**
         * 设置控件的选中状态
         *
         * @param view    控件
         * @param isCheck 是否选中
         */
        fun setChecked(view: CompoundButton?, isCheck: Boolean) {
            if (view != null) {
                view.isChecked = isCheck
            }
        }

        /**
         * 设置控件的选中监听
         *
         * @param view                  控件
         * @param checkedChangeListener 选中监听
         */
        fun setOnCheckedChangeListener(
            view: CompoundButton?,
            checkedChangeListener: CompoundButton.OnCheckedChangeListener?
        ) {
            view?.setOnCheckedChangeListener(checkedChangeListener)
        }

        /**
         * 设置控件的选中状态【静默】
         *
         * @param view                  控件
         * @param isCheck               是否选中
         * @param checkedChangeListener 选中监听
         */
        fun setCheckedSilent(
            view: CompoundButton?,
            isCheck: Boolean,
            checkedChangeListener: CompoundButton.OnCheckedChangeListener?
        ) {
            if (view != null) {
                view.setOnCheckedChangeListener(null)
                view.isChecked = isCheck
                view.setOnCheckedChangeListener(checkedChangeListener)
            }
        }
        //=====================设置Padding==============================//
        /**
         * 扩展点击区域的范围
         *
         * @param view       需要扩展的元素，此元素必需要有父级元素
         * @param expendSize 需要扩展的尺寸（以xp为单位的）
         */
        fun expendTouchArea(view: View?, expendSize: Int) {
            if (view != null) {
                val parentView = view.parent as View
                parentView.post { //屏幕的坐标原点在左上角
                    val rect = Rect()
                    view.getHitRect(rect) //如果太早执行本函数，会获取rect失败，因为此时UI界面尚未开始绘制，无法获得正确的坐标
                    rect.left -= expendSize
                    rect.top -= expendSize
                    rect.right += expendSize
                    rect.bottom += expendSize
                    parentView.touchDelegate = TouchDelegate(rect, view)
                }
            }
        }

        /**
         * 设置控件的padding
         *
         * @param view    控件
         * @param padding
         */
        fun setPadding(view: View?, padding: Int) {
            view?.setPadding(padding, padding, padding, padding)
        }

        /**
         * 对 View 设置 paddingLeft
         *
         * @param view  需要被设置的 View
         * @param value 设置的值
         */
        fun setPaddingLeft(view: View, value: Int) {
            if (value != view.paddingLeft) {
                view.setPadding(value, view.paddingTop, view.paddingRight, view.paddingBottom)
            }
        }

        /**
         * 对 View 设置 paddingTop
         *
         * @param view  需要被设置的 View
         * @param value 设置的值
         */
        fun setPaddingTop(view: View, value: Int) {
            if (value != view.paddingTop) {
                view.setPadding(view.paddingLeft, value, view.paddingRight, view.paddingBottom)
            }
        }

        /**
         * 对 View 设置 paddingRight
         *
         * @param view  需要被设置的 View
         * @param value 设置的值
         */
        fun setPaddingRight(view: View, value: Int) {
            if (value != view.paddingRight) {
                view.setPadding(view.paddingLeft, view.paddingTop, value, view.paddingBottom)
            }
        }

        /**
         * 对 View 设置 paddingBottom
         *
         * @param view  需要被设置的 View
         * @param value 设置的值
         */
        fun setPaddingBottom(view: View, value: Int) {
            if (value != view.paddingBottom) {
                view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, value)
            }
        }
        //=====================设置Margin==============================//
        /**
         * 设置view的margin<br></br>
         * 当且仅当 新的start,top,end,bottom 至少有之一 与原值不相同，为避免不必要的requestlayout
         *
         * @param view   控件
         * @param start  左侧距离
         * @param top    顶部距离
         * @param end    右侧距离
         * @param bottom 底部距离
         */
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        fun setViewMargin(view: View?, start: Int, top: Int, end: Int, bottom: Int) {
            val lp = getLayoutParams<MarginLayoutParams>(view, MarginLayoutParams::class.java)
            if (lp != null) {
                val oldStart = lp.marginStart
                val oldTop = lp.topMargin
                val oldEnd = lp.marginEnd
                val oldBottom = lp.bottomMargin
                if (oldStart != start || oldTop != top || oldEnd != end || oldBottom != bottom) {
                    lp.marginStart = start
                    lp.topMargin = top
                    lp.marginEnd = end
                    lp.bottomMargin = bottom
                    setLayoutParams(view, lp)
                }
            }
        }

        /**
         * 获取视图布局参数
         *
         * @param view  视图
         * @param clazz 待转换的类型
         * @param <T>   数据类型模板
         * @return 视图布局参数
        </T> */
        fun <T> getLayoutParams(view: View?, clazz: Class<out ViewGroup.LayoutParams?>?): T? {
            if (view == null || clazz == null) {
                return null
            }
            val layoutParams = view.layoutParams
            return if (clazz.isInstance(layoutParams)) {
                layoutParams as T
            } else null
        }

        /**
         * 设置视图布局参数
         *
         * @param view   视图
         * @param params 布局参数
         */
        fun setLayoutParams(view: View?, params: ViewGroup.LayoutParams?) {
            if (view != null && params != null) {
                view.layoutParams = params
            }
        }
    }

    /**
     * Don't let anyone instantiate this class.
     */
    init {
        throw Error("Do not need instantiate!")
    }
}