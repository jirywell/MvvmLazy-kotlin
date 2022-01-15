package com.rui.mvvmlazy.binding.viewadapter.recyclerview

import android.graphics.Color
import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.fondesa.recyclerviewdivider.dividerBuilder
import com.fondesa.recyclerviewdivider.staggeredDividerBuilder

/**
 * Created by zjr on 2020/6/16.
 */
object LineManagers {
    //瀑布流分割线
    @JvmStatic
    fun staggeredDivider(): LineManagerFactory {
        return staggeredDivider(Color.parseColor("#E6E6E6"), 1)
    }

    @JvmStatic
    fun staggeredDivider(
        color: Int,
        size: Int
    ): LineManagerFactory {
        return object : LineManagerFactory {
            override fun create(recyclerView: RecyclerView) {
                recyclerView.context.staggeredDividerBuilder()
                    .color(color)
                    .size(size, TypedValue.COMPLEX_UNIT_DIP)
                    .hideSideDividers()
                    .build()
                    .addTo(recyclerView)
            }
        }
    }

    //线型分割线 ,Grid分割线
    @JvmStatic
    fun divider(): LineManagerFactory {
        return divider(Color.parseColor("#E6E6E6"), 1)
    }

    @JvmStatic
    fun divider(color: Int, size: Int): LineManagerFactory {
        return object : LineManagerFactory {
            override fun create(recyclerView: RecyclerView) {
                recyclerView.context.dividerBuilder()
                    .color(color)
                    .size(
                        size,
                        TypedValue.COMPLEX_UNIT_DIP
                    ) //                        .showFirstDivider()
                    //                        .showLastDivider()
                    //                        .showSideDividers()
                    .build()
                    .addTo(recyclerView)
            }
        }
    }

    interface LineManagerFactory {
        fun create(recyclerView: RecyclerView)
    }
}