package com.rui.mvvmlazy.binding.viewadapter.recyclerview

import androidx.annotation.IntDef
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * A collection of factories to create RecyclerView LayoutManagers so that you can easily set them
 * in your layout.
 */
object LayoutManagers {
    const val HORIZONTAL = 0
    const val VERTICAL = 1

    /**
     * A [LinearLayoutManager].
     */
    @JvmStatic
    fun linear(): LayoutManagerFactory {
        return object : LayoutManagerFactory {
            override fun create(recyclerView: RecyclerView): RecyclerView.LayoutManager {
                return LinearLayoutManager(recyclerView.context)
            }
        }
    }

    /**
     * A [LinearLayoutManager].
     */
    @JvmStatic
    fun linear(canVertScroll: Boolean): LayoutManagerFactory {
        return object : LayoutManagerFactory {
            override fun create(recyclerView: RecyclerView): RecyclerView.LayoutManager {
                return object : LinearLayoutManager(recyclerView.context) {
                    override fun canScrollVertically(): Boolean {
                        return canVertScroll
                    }
                }
            }
        }
    }

    /**
     * A [LinearLayoutManager] with the given orientation and reverseLayout.
     */
    @JvmStatic
    fun linear(@Orientation orientation: Int, reverseLayout: Boolean): LayoutManagerFactory {
        return object : LayoutManagerFactory {
            override fun create(recyclerView: RecyclerView): RecyclerView.LayoutManager {
                return LinearLayoutManager(recyclerView.context, orientation, reverseLayout)
            }
        }
    }

    /**
     * A [LinearLayoutManager] with the given orientation and reverseLayout.
     */
    @JvmStatic
    fun linearVertical(
        @Orientation orientation: Int,
        canVertical: Boolean
    ): LayoutManagerFactory {
        return object : LayoutManagerFactory {
            override fun create(recyclerView: RecyclerView): RecyclerView.LayoutManager {
                return object : LinearLayoutManager(recyclerView.context, orientation, false) {
                    override fun canScrollVertically(): Boolean {
                        return canVertical
                    }
                }
            }
        }
    }

    /**
     * A [GridLayoutManager] with the given spanCount.
     */
    @JvmStatic
    fun grid(spanCount: Int): LayoutManagerFactory {
        return object : LayoutManagerFactory {
            override fun create(recyclerView: RecyclerView): RecyclerView.LayoutManager {
                return GridLayoutManager(recyclerView.context, spanCount)
            }
        }
    }

    /**
     * A [GridLayoutManager] with the given spanCount.
     *
     * @param spanCount      列数
     * @param canHorScroll   能否水平移动
     * @param canVertiScroll 能否垂直移动
     * @return
     */
    @JvmStatic
    fun grid(
        spanCount: Int,
        canHorScroll: Boolean,
        canVertiScroll: Boolean
    ): LayoutManagerFactory {
        return object : LayoutManagerFactory {
            override fun create(recyclerView: RecyclerView): RecyclerView.LayoutManager {
                return object : GridLayoutManager(recyclerView.context, spanCount) {
                    override fun canScrollHorizontally(): Boolean {
                        return canHorScroll
                    }

                    override fun canScrollVertically(): Boolean {
                        return canVertiScroll
                    }
                }
            }
        }
    }

    /**
     * A [GridLayoutManager] with the given spanCount, orientation and reverseLayout.
     */
    @JvmStatic
    fun grid(
        spanCount: Int,
        @Orientation orientation: Int,
        reverseLayout: Boolean
    ): LayoutManagerFactory {
        return object : LayoutManagerFactory {
            override fun create(recyclerView: RecyclerView): RecyclerView.LayoutManager {
                return GridLayoutManager(
                    recyclerView.context,
                    spanCount,
                    orientation,
                    reverseLayout
                )
            }
        }
    }

    /**
     * A [StaggeredGridLayoutManager] with the given spanCount and orientation.
     */
    @JvmStatic
    fun staggeredGrid(spanCount: Int, @Orientation orientation: Int): LayoutManagerFactory {
        return object : LayoutManagerFactory {
            override fun create(recyclerView: RecyclerView): RecyclerView.LayoutManager {
                return StaggeredGridLayoutManager(spanCount, orientation)
            }
        }
    }

    interface LayoutManagerFactory {
        fun create(recyclerView: RecyclerView): RecyclerView.LayoutManager
    }

    @IntDef(HORIZONTAL, VERTICAL)
    @Retention(RetentionPolicy.SOURCE)
    annotation class Orientation

}