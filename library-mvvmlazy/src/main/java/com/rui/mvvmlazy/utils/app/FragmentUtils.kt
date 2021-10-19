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
package com.rui.mvvmlazy.utils.app

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.AnimRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.rui.mvvmlazy.utils.common.KLog
import java.util.*

/**
 * <pre>
 * desc   : Fragment 相关工具类
 * author : zjr
 * time   : 2018/4/28 上午12:27
</pre> *
 */
class FragmentUtils() {
    private class Args(
        val id: Int, val tag: String?,
        val isHide: Boolean, val isAddStack: Boolean
    ) {
        constructor(id: Int, isHide: Boolean, isAddStack: Boolean) : this(
            id,
            null,
            isHide,
            isAddStack
        ) {
        }
    }

    class FragmentNode(val fragment: Fragment, val next: List<FragmentNode>?) {
        override fun toString(): String {
            return (fragment.javaClass.simpleName
                    + "->"
                    + if (next == null || next.isEmpty()) "no child" else next.toString())
        }
    }

    interface OnBackClickListener {
        fun onBackClick(): Boolean
    }

    companion object {
        private const val TYPE_ADD_FRAGMENT = 0x01
        private const val TYPE_SHOW_FRAGMENT = 0x01 shl 1
        private const val TYPE_HIDE_FRAGMENT = 0x01 shl 2
        private const val TYPE_SHOW_HIDE_FRAGMENT = 0x01 shl 3
        private const val TYPE_REPLACE_FRAGMENT = 0x01 shl 4
        private const val TYPE_REMOVE_FRAGMENT = 0x01 shl 5
        private const val TYPE_REMOVE_TO_FRAGMENT = 0x01 shl 6
        private const val ARGS_ID = "args_id"
        private const val ARGS_IS_HIDE = "args_is_hide"
        private const val ARGS_IS_ADD_STACK = "args_is_add_stack"
        private const val ARGS_TAG = "args_tag"

        /**
         * Add fragment.
         *
         * @param fm          The manager of fragment.
         * @param add         The fragment will be add.
         * @param containerId The id of container.
         * @param isHide      True to hide, false otherwise.
         */
        fun add(
            fm: FragmentManager,
            add: Fragment,
            @IdRes containerId: Int,
            isHide: Boolean
        ) {
            add(fm, add, containerId, null, isHide, false)
        }

        /**
         * Add fragment.
         *
         * @param fm          The manager of fragment.
         * @param add         The fragment will be add.
         * @param containerId The id of container.
         * @param isHide      True to hide, false otherwise.
         * @param isAddStack  True to add fragment in stack, false otherwise.
         */
        fun add(
            fm: FragmentManager,
            add: Fragment,
            @IdRes containerId: Int,
            isHide: Boolean,
            isAddStack: Boolean
        ) {
            add(fm, add, containerId, null, isHide, isAddStack)
        }

        /**
         * Add fragment.
         *
         * @param fm          The manager of fragment.
         * @param add         The fragment will be add.
         * @param containerId The id of container.
         * @param enterAnim   An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim    An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         */
        fun add(
            fm: FragmentManager,
            add: Fragment,
            @IdRes containerId: Int,
            @AnimRes enterAnim: Int,
            @AnimRes exitAnim: Int
        ) {
            add(fm, add, containerId, null, false, enterAnim, exitAnim, 0, 0)
        }

        /**
         * Add fragment.
         *
         * @param fm          The manager of fragment.
         * @param containerId The id of container.
         * @param add         The fragment will be add.
         * @param isAddStack  True to add fragment in stack, false otherwise.
         * @param enterAnim   An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim    An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         */
        fun add(
            fm: FragmentManager,
            add: Fragment,
            @IdRes containerId: Int,
            isAddStack: Boolean,
            @AnimRes enterAnim: Int,
            @AnimRes exitAnim: Int
        ) {
            add(fm, add, containerId, null, isAddStack, enterAnim, exitAnim, 0, 0)
        }

        /**
         * Add fragment.
         *
         * @param fm           The manager of fragment.
         * @param containerId  The id of container.
         * @param add          The fragment will be add.
         * @param enterAnim    An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim     An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         * @param popEnterAnim An animation or animator resource ID used for the enter animation on the
         * view of the fragment being readded or reattached caused by
         * popBackStack() or similar methods.
         * @param popExitAnim  An animation or animator resource ID used for the enter animation on the
         * view of the fragment being removed or detached caused by
         * popBackStack() or similar methods.
         */
        fun add(
            fm: FragmentManager,
            add: Fragment,
            @IdRes containerId: Int,
            @AnimRes enterAnim: Int,
            @AnimRes exitAnim: Int,
            @AnimRes popEnterAnim: Int,
            @AnimRes popExitAnim: Int
        ) {
            add(fm, add, containerId, null, false, enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }

        /**
         * Add fragment.
         *
         * @param fm           The manager of fragment.
         * @param containerId  The id of container.
         * @param add          The fragment will be add.
         * @param isAddStack   True to add fragment in stack, false otherwise.
         * @param enterAnim    An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim     An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         * @param popEnterAnim An animation or animator resource ID used for the enter animation on the
         * view of the fragment being readded or reattached caused by
         * popBackStack() or similar methods.
         * @param popExitAnim  An animation or animator resource ID used for the enter animation on the
         * view of the fragment being removed or detached caused by
         * popBackStack() or similar methods.
         */
        fun add(
            fm: FragmentManager,
            add: Fragment,
            @IdRes containerId: Int,
            isAddStack: Boolean,
            @AnimRes enterAnim: Int,
            @AnimRes exitAnim: Int,
            @AnimRes popEnterAnim: Int,
            @AnimRes popExitAnim: Int
        ) {
            add(
                fm,
                add,
                containerId,
                null,
                isAddStack,
                enterAnim,
                exitAnim,
                popEnterAnim,
                popExitAnim
            )
        }

        /**
         * Add fragment.
         *
         * @param fm             The manager of fragment.
         * @param add            The fragment will be add.
         * @param containerId    The id of container.
         * @param sharedElements A View in a disappearing Fragment to match with a View in an
         * appearing Fragment.
         */
        fun add(
            fm: FragmentManager,
            add: Fragment,
            @IdRes containerId: Int,
            vararg sharedElements: View
        ) {
            add(fm, add, containerId, null, false, *sharedElements)
        }

        /**
         * Add fragment.
         *
         * @param fm             The manager of fragment.
         * @param add            The fragment will be add.
         * @param containerId    The id of container.
         * @param isAddStack     True to add fragment in stack, false otherwise.
         * @param sharedElements A View in a disappearing Fragment to match with a View in an
         * appearing Fragment.
         */
        fun add(
            fm: FragmentManager,
            add: Fragment,
            @IdRes containerId: Int,
            isAddStack: Boolean,
            vararg sharedElements: View
        ) {
            add(fm, add, containerId, null, isAddStack, *sharedElements)
        }

        /**
         * Add fragment.
         *
         * @param fm          The manager of fragment.
         * @param adds        The fragments will be add.
         * @param containerId The id of container.
         * @param showIndex   The index of fragment will be shown.
         */
        fun add(
            fm: FragmentManager,
            adds: List<Fragment>,
            @IdRes containerId: Int,
            showIndex: Int
        ) {
            add(fm, adds.toTypedArray(), containerId, null, showIndex)
        }

        /**
         * Add fragment.
         *
         * @param fm          The manager of fragment.
         * @param adds        The fragments will be add.
         * @param containerId The id of container.
         * @param showIndex   The index of fragment will be shown.
         */
        fun add(
            fm: FragmentManager,
            adds: Array<Fragment>,
            @IdRes containerId: Int,
            showIndex: Int
        ) {
            add(fm, adds, containerId, null, showIndex)
        }
        /**
         * Add fragment.
         *
         * @param fm          The manager of fragment.
         * @param add         The fragment will be add.
         * @param containerId The id of container.
         * @param tag         The tag of fragment.
         * @param isHide      True to hide, false otherwise.
         * @param isAddStack  True to add fragment in stack, false otherwise.
         */
        /**
         * Add fragment.
         *
         * @param fm          The manager of fragment.
         * @param add         The fragment will be add.
         * @param containerId The id of container.
         */
        /**
         * Add fragment.
         *
         * @param fm          The manager of fragment.
         * @param add         The fragment will be add.
         * @param containerId The id of container.
         * @param tag         The tag of fragment.
         */
        /**
         * Add fragment.
         *
         * @param fm          The manager of fragment.
         * @param add         The fragment will be add.
         * @param containerId The id of container.
         * @param tag         The tag of fragment.
         * @param isHide      True to hide, false otherwise.
         */
        @JvmOverloads
        fun add(
            fm: FragmentManager,
            add: Fragment,
            @IdRes containerId: Int,
            tag: String? = null,
            isHide: Boolean = false,
            isAddStack: Boolean = false
        ) {
            putArgs(add, Args(containerId, tag, isHide, isAddStack))
            operateNoAnim(fm, TYPE_ADD_FRAGMENT, null, add)
        }

        /**
         * Add fragment.
         *
         * @param fm          The manager of fragment.
         * @param add         The fragment will be add.
         * @param containerId The id of container.
         * @param tag         The tag of fragment.
         * @param enterAnim   An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim    An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         */
        fun add(
            fm: FragmentManager,
            add: Fragment,
            @IdRes containerId: Int,
            tag: String?,
            @AnimRes enterAnim: Int,
            @AnimRes exitAnim: Int
        ) {
            add(fm, add, containerId, tag, false, enterAnim, exitAnim, 0, 0)
        }

        /**
         * Add fragment.
         *
         * @param fm           The manager of fragment.
         * @param add          The fragment will be add.
         * @param containerId  The id of container.
         * @param tag          The tag of fragment.
         * @param enterAnim    An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim     An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         * @param popEnterAnim An animation or animator resource ID used for the enter animation on the
         * view of the fragment being readded or reattached caused by
         * popBackStack() or similar methods.
         * @param popExitAnim  An animation or animator resource ID used for the enter animation on the
         * view of the fragment being removed or detached caused by
         * popBackStack() or similar methods.
         */
        fun add(
            fm: FragmentManager,
            add: Fragment,
            @IdRes containerId: Int,
            tag: String?,
            @AnimRes enterAnim: Int,
            @AnimRes exitAnim: Int,
            @AnimRes popEnterAnim: Int,
            @AnimRes popExitAnim: Int
        ) {
            add(fm, add, containerId, tag, false, enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }
        /**
         * Add fragment.
         *
         * @param fm           The manager of fragment.
         * @param add          The fragment will be add.
         * @param containerId  The id of container.
         * @param tag          The tag of fragment.
         * @param isAddStack   True to add fragment in stack, false otherwise.
         * @param enterAnim    An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim     An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         * @param popEnterAnim An animation or animator resource ID used for the enter animation on the
         * view of the fragment being readded or reattached caused by
         * popBackStack() or similar methods.
         * @param popExitAnim  An animation or animator resource ID used for the enter animation on the
         * view of the fragment being removed or detached caused by
         * popBackStack() or similar methods.
         */
        /**
         * Add fragment.
         *
         * @param fm          The manager of fragment.
         * @param add         The fragment will be add.
         * @param containerId The id of container.
         * @param tag         The tag of fragment.
         * @param isAddStack  True to add fragment in stack, false otherwise.
         * @param enterAnim   An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim    An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         */
        @JvmOverloads
        fun add(
            fm: FragmentManager,
            add: Fragment,
            @IdRes containerId: Int,
            tag: String?,
            isAddStack: Boolean,
            @AnimRes enterAnim: Int,
            @AnimRes exitAnim: Int,
            @AnimRes popEnterAnim: Int = 0,
            @AnimRes popExitAnim: Int = 0
        ) {
            val ft = fm.beginTransaction()
            putArgs(add, Args(containerId, tag, false, isAddStack))
            addAnim(ft, enterAnim, exitAnim, popEnterAnim, popExitAnim)
            operate(TYPE_ADD_FRAGMENT, fm, ft, null, add)
        }

        /**
         * Add fragment.
         *
         * @param fm             The manager of fragment.
         * @param add            The fragment will be add.
         * @param tag            The tag of fragment.
         * @param containerId    The id of container.
         * @param sharedElements A View in a disappearing Fragment to match with a View in an
         * appearing Fragment.
         */
        fun add(
            fm: FragmentManager,
            add: Fragment,
            @IdRes containerId: Int,
            tag: String?,
            vararg sharedElements: View
        ) {
            add(fm, add, containerId, tag, false, *sharedElements)
        }

        /**
         * Add fragment.
         *
         * @param fm             The manager of fragment.
         * @param add            The fragment will be add.
         * @param containerId    The id of container.
         * @param isAddStack     True to add fragment in stack, false otherwise.
         * @param sharedElements A View in a disappearing Fragment to match with a View in an
         * appearing Fragment.
         */
        fun add(
            fm: FragmentManager,
            add: Fragment,
            @IdRes containerId: Int,
            tag: String?,
            isAddStack: Boolean,
            vararg sharedElements: View
        ) {
            val ft = fm.beginTransaction()
            putArgs(add, Args(containerId, tag, false, isAddStack))
            addSharedElement(ft, *sharedElements)
            operate(TYPE_ADD_FRAGMENT, fm, ft, null, add)
        }

        /**
         * Add fragment.
         *
         * @param fm          The manager of fragment.
         * @param adds        The fragments will be add.
         * @param containerId The id of container.
         * @param showIndex   The index of fragment will be shown.
         */
        fun add(
            fm: FragmentManager,
            adds: List<Fragment>,
            @IdRes containerId: Int,
            tags: Array<String?>?,
            showIndex: Int
        ) {
            add(fm, adds.toTypedArray(), containerId, tags, showIndex)
        }

        /**
         * Add fragment.
         *
         * @param fm          The manager of fragment.
         * @param adds        The fragments will be add.
         * @param containerId The id of container.
         * @param showIndex   The index of fragment will be shown.
         */
        fun add(
            fm: FragmentManager,
            adds: Array<Fragment>,
            @IdRes containerId: Int,
            tags: Array<String?>?,
            showIndex: Int
        ) {
            if (tags == null) {
                var i = 0
                val len = adds.size
                while (i < len) {
                    putArgs(adds[i], Args(containerId, null, showIndex != i, false))
                    ++i
                }
            } else {
                var i = 0
                val len = adds.size
                while (i < len) {
                    putArgs(adds[i], Args(containerId, tags[i], showIndex != i, false))
                    ++i
                }
            }
            operateNoAnim(fm, TYPE_ADD_FRAGMENT, null, *adds)
        }

        /**
         * Show fragment.
         *
         * @param show The fragment will be show.
         */
        fun show(show: Fragment) {
            putArgs(show, false)
            operateNoAnim(show.fragmentManager, TYPE_SHOW_FRAGMENT, null, show)
        }

        /**
         * Show fragment.
         *
         * @param fm The manager of fragment.
         */
        fun show(fm: FragmentManager) {
            val fragments = getFragments(fm)
            for (show in fragments) {
                putArgs(show, false)
            }
            operateNoAnim(
                fm,
                TYPE_SHOW_FRAGMENT,
                null,
                *fragments.toTypedArray()
            )
        }

        /**
         * Hide fragment.
         *
         * @param hide The fragment will be hide.
         */
        fun hide(hide: Fragment) {
            putArgs(hide, true)
            operateNoAnim(hide.fragmentManager, TYPE_HIDE_FRAGMENT, null, hide)
        }

        /**
         * Hide fragment.
         *
         * @param fm The manager of fragment.
         */
        fun hide(fm: FragmentManager) {
            val fragments = getFragments(fm)
            for (hide in fragments) {
                putArgs(hide, true)
            }
            operateNoAnim(
                fm,
                TYPE_HIDE_FRAGMENT,
                null,
                *fragments.toTypedArray()
            )
        }

        /**
         * Show fragment then hide other fragment.
         *
         * @param showIndex The index of fragment will be shown.
         * @param fragments The fragments will be hide.
         */
        fun showHide(showIndex: Int, fragments: List<Fragment>) {
            showHide(fragments[showIndex], fragments)
        }

        /**
         * Show fragment then hide other fragment.
         *
         * @param show The fragment will be show.
         * @param hide The fragment will be hide.
         */
        fun showHide(show: Fragment, hide: List<Fragment>) {
            for (fragment in hide) {
                putArgs(fragment, fragment !== show)
            }
            operateNoAnim(
                show.fragmentManager, TYPE_SHOW_HIDE_FRAGMENT, show,
                *hide.toTypedArray()
            )
        }

        /**
         * Show fragment then hide other fragment.
         *
         * @param showIndex The index of fragment will be shown.
         * @param fragments The fragment will be hide.
         */
        fun showHide(showIndex: Int, vararg fragments: Fragment) {
            showHide(fragments[showIndex], *fragments)
        }

        /**
         * Show fragment then hide other fragment.
         *
         * @param show The fragment will be show.
         * @param hide The fragment will be hide.
         */
        fun showHide(show: Fragment, vararg hide: Fragment) {
            for (fragment in hide) {
                putArgs(fragment, fragment !== show)
            }
            operateNoAnim(show.fragmentManager, TYPE_SHOW_HIDE_FRAGMENT, show, *hide)
        }

        /**
         * Show fragment then hide other fragment.
         *
         * @param show The fragment will be show.
         * @param hide The fragment will be hide.
         */
        fun showHide(
            show: Fragment,
            hide: Fragment
        ) {
            putArgs(show, false)
            putArgs(hide, true)
            operateNoAnim(show.fragmentManager, TYPE_SHOW_HIDE_FRAGMENT, show, hide)
        }

        /**
         * Replace fragment.
         *
         * @param srcFragment  The source of fragment.
         * @param destFragment The destination of fragment.
         * @param isAddStack   True to add fragment in stack, false otherwise.
         */
        fun replace(
            srcFragment: Fragment,
            destFragment: Fragment,
            isAddStack: Boolean
        ) {
            replace(srcFragment, destFragment, null, isAddStack)
        }

        /**
         * Replace fragment.
         *
         * @param srcFragment  The source of fragment.
         * @param destFragment The destination of fragment.
         * @param enterAnim    An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim     An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         */
        fun replace(
            srcFragment: Fragment,
            destFragment: Fragment,
            @AnimRes enterAnim: Int,
            @AnimRes exitAnim: Int
        ) {
            replace(srcFragment, destFragment, null, false, enterAnim, exitAnim, 0, 0)
        }

        /**
         * Replace fragment.
         *
         * @param srcFragment  The source of fragment.
         * @param destFragment The destination of fragment.
         * @param isAddStack   True to add fragment in stack, false otherwise.
         * @param enterAnim    An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim     An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         */
        fun replace(
            srcFragment: Fragment,
            destFragment: Fragment,
            isAddStack: Boolean,
            @AnimRes enterAnim: Int,
            @AnimRes exitAnim: Int
        ) {
            replace(srcFragment, destFragment, null, isAddStack, enterAnim, exitAnim, 0, 0)
        }

        /**
         * Replace fragment.
         *
         * @param srcFragment  The source of fragment.
         * @param destFragment The destination of fragment.
         * @param enterAnim    An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim     An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         * @param popEnterAnim An animation or animator resource ID used for the enter animation on the
         * view of the fragment being readded or reattached caused by
         * popBackStack() or similar methods.
         * @param popExitAnim  An animation or animator resource ID used for the enter animation on the
         * view of the fragment being removed or detached caused by
         * popBackStack() or similar methods.
         */
        fun replace(
            srcFragment: Fragment,
            destFragment: Fragment,
            @AnimRes enterAnim: Int,
            @AnimRes exitAnim: Int,
            @AnimRes popEnterAnim: Int,
            @AnimRes popExitAnim: Int
        ) {
            replace(
                srcFragment, destFragment, null, false,
                enterAnim, exitAnim, popEnterAnim, popExitAnim
            )
        }

        /**
         * Replace fragment.
         *
         * @param srcFragment  The source of fragment.
         * @param destFragment The destination of fragment.
         * @param isAddStack   True to add fragment in stack, false otherwise.
         * @param enterAnim    An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim     An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         * @param popEnterAnim An animation or animator resource ID used for the enter animation on the
         * view of the fragment being readded or reattached caused by
         * popBackStack() or similar methods.
         * @param popExitAnim  An animation or animator resource ID used for the enter animation on the
         * view of the fragment being removed or detached caused by
         * popBackStack() or similar methods.
         */
        fun replace(
            srcFragment: Fragment,
            destFragment: Fragment,
            isAddStack: Boolean,
            @AnimRes enterAnim: Int,
            @AnimRes exitAnim: Int,
            @AnimRes popEnterAnim: Int,
            @AnimRes popExitAnim: Int
        ) {
            replace(
                srcFragment, destFragment, null, isAddStack,
                enterAnim, exitAnim, popEnterAnim, popExitAnim
            )
        }

        /**
         * Replace fragment.
         *
         * @param srcFragment    The source of fragment.
         * @param destFragment   The destination of fragment.
         * @param sharedElements A View in a disappearing Fragment to match with a View in an
         * appearing Fragment.
         */
        fun replace(
            srcFragment: Fragment,
            destFragment: Fragment,
            vararg sharedElements: View?
        ) {
            replace(srcFragment, destFragment, null, false, *sharedElements)
        }

        /**
         * Replace fragment.
         *
         * @param srcFragment    The source of fragment.
         * @param destFragment   The destination of fragment.
         * @param isAddStack     True to add fragment in stack, false otherwise.
         * @param sharedElements A View in a disappearing Fragment to match with a View in an
         * appearing Fragment.
         */
        fun replace(
            srcFragment: Fragment,
            destFragment: Fragment,
            isAddStack: Boolean,
            vararg sharedElements: View?
        ) {
            replace(srcFragment, destFragment, null, isAddStack, *sharedElements)
        }

        /**
         * Replace fragment.
         *
         * @param fm          The manager of fragment.
         * @param containerId The id of container.
         * @param fragment    The new fragment to place in the container.
         * @param isAddStack  True to add fragment in stack, false otherwise.
         */
        fun replace(
            fm: FragmentManager,
            fragment: Fragment,
            @IdRes containerId: Int,
            isAddStack: Boolean
        ) {
            replace(fm, fragment, containerId, null, isAddStack)
        }

        /**
         * Replace fragment.
         *
         * @param fm          The manager of fragment.
         * @param containerId The id of container.
         * @param fragment    The new fragment to place in the container.
         * @param enterAnim   An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim    An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         */
        fun replace(
            fm: FragmentManager,
            fragment: Fragment,
            @IdRes containerId: Int,
            @AnimRes enterAnim: Int,
            @AnimRes exitAnim: Int
        ) {
            replace(fm, fragment, containerId, null, false, enterAnim, exitAnim, 0, 0)
        }

        /**
         * Replace fragment.
         *
         * @param fm          The manager of fragment.
         * @param containerId The id of container.
         * @param fragment    The new fragment to place in the container.
         * @param isAddStack  True to add fragment in stack, false otherwise.
         * @param enterAnim   An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim    An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         */
        fun replace(
            fm: FragmentManager,
            fragment: Fragment,
            @IdRes containerId: Int,
            isAddStack: Boolean,
            @AnimRes enterAnim: Int,
            @AnimRes exitAnim: Int
        ) {
            replace(fm, fragment, containerId, null, isAddStack, enterAnim, exitAnim, 0, 0)
        }

        /**
         * Replace fragment.
         *
         * @param fm           The manager of fragment.
         * @param containerId  The id of container.
         * @param fragment     The new fragment to place in the container.
         * @param enterAnim    An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim     An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         * @param popEnterAnim An animation or animator resource ID used for the enter animation on the
         * view of the fragment being readded or reattached caused by
         * popBackStack() or similar methods.
         * @param popExitAnim  An animation or animator resource ID used for the enter animation on the
         * view of the fragment being removed or detached caused by
         * popBackStack() or similar methods.
         */
        fun replace(
            fm: FragmentManager,
            fragment: Fragment,
            @IdRes containerId: Int,
            @AnimRes enterAnim: Int,
            @AnimRes exitAnim: Int,
            @AnimRes popEnterAnim: Int,
            @AnimRes popExitAnim: Int
        ) {
            replace(
                fm, fragment, containerId, null, false,
                enterAnim, exitAnim, popEnterAnim, popExitAnim
            )
        }

        /**
         * Replace fragment.
         *
         * @param fm           The manager of fragment.
         * @param containerId  The id of container.
         * @param fragment     The new fragment to place in the container.
         * @param isAddStack   True to add fragment in stack, false otherwise.
         * @param enterAnim    An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim     An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         * @param popEnterAnim An animation or animator resource ID used for the enter animation on the
         * view of the fragment being readded or reattached caused by
         * popBackStack() or similar methods.
         * @param popExitAnim  An animation or animator resource ID used for the enter animation on the
         * view of the fragment being removed or detached caused by
         * popBackStack() or similar methods.
         */
        fun replace(
            fm: FragmentManager,
            fragment: Fragment,
            @IdRes containerId: Int,
            isAddStack: Boolean,
            @AnimRes enterAnim: Int,
            @AnimRes exitAnim: Int,
            @AnimRes popEnterAnim: Int,
            @AnimRes popExitAnim: Int
        ) {
            replace(
                fm, fragment, containerId, null, isAddStack,
                enterAnim, exitAnim, popEnterAnim, popExitAnim
            )
        }

        /**
         * Replace fragment.
         *
         * @param fm             The manager of fragment.
         * @param containerId    The id of container.
         * @param fragment       The new fragment to place in the container.
         * @param sharedElements A View in a disappearing Fragment to match with a View in an
         * appearing Fragment.
         */
        fun replace(
            fm: FragmentManager,
            fragment: Fragment,
            @IdRes containerId: Int,
            vararg sharedElements: View?
        ) {
            replace(fm, fragment, containerId, null, false, *sharedElements)
        }

        /**
         * Replace fragment.
         *
         * @param fm             The manager of fragment.
         * @param containerId    The id of container.
         * @param fragment       The new fragment to place in the container.
         * @param isAddStack     True to add fragment in stack, false otherwise.
         * @param sharedElements A View in a disappearing Fragment to match with a View in an
         * appearing Fragment.
         */
        fun replace(
            fm: FragmentManager,
            fragment: Fragment,
            @IdRes containerId: Int,
            isAddStack: Boolean,
            vararg sharedElements: View?
        ) {
            replace(fm, fragment, containerId, null, isAddStack, *sharedElements)
        }
        /**
         * Replace fragment.
         *
         * @param srcFragment  The source of fragment.
         * @param destFragment The destination of fragment.
         * @param destTag      The destination of fragment's tag.
         * @param isAddStack   True to add fragment in stack, false otherwise.
         */
        /**
         * Replace fragment.
         *
         * @param srcFragment  The source of fragment.
         * @param destFragment The destination of fragment.
         */
        /**
         * Replace fragment.
         *
         * @param srcFragment  The source of fragment.
         * @param destFragment The destination of fragment.
         * @param destTag      The destination of fragment's tag.
         */
        @JvmOverloads
        fun replace(
            srcFragment: Fragment,
            destFragment: Fragment,
            destTag: String? = null,
            isAddStack: Boolean = false
        ) {
            val args = getArgs(srcFragment)
            replace(srcFragment.parentFragmentManager, destFragment, args.id, destTag, isAddStack)
        }

        /**
         * Replace fragment.
         *
         * @param srcFragment  The source of fragment.
         * @param destFragment The destination of fragment.
         * @param destTag      The destination of fragment's tag.
         * @param enterAnim    An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim     An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         */
        fun replace(
            srcFragment: Fragment,
            destFragment: Fragment,
            destTag: String?,
            @AnimRes enterAnim: Int,
            @AnimRes exitAnim: Int
        ) {
            replace(srcFragment, destFragment, destTag, false, enterAnim, exitAnim, 0, 0)
        }

        /**
         * Replace fragment.
         *
         * @param srcFragment  The source of fragment.
         * @param destFragment The destination of fragment.
         * @param destTag      The destination of fragment's tag.
         * @param enterAnim    An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim     An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         * @param popEnterAnim An animation or animator resource ID used for the enter animation on the
         * view of the fragment being readded or reattached caused by
         * popBackStack() or similar methods.
         * @param popExitAnim  An animation or animator resource ID used for the enter animation on the
         * view of the fragment being removed or detached caused by
         * popBackStack() or similar methods.
         */
        fun replace(
            srcFragment: Fragment,
            destFragment: Fragment,
            destTag: String?,
            @AnimRes enterAnim: Int,
            @AnimRes exitAnim: Int,
            @AnimRes popEnterAnim: Int,
            @AnimRes popExitAnim: Int
        ) {
            replace(
                srcFragment, destFragment, destTag, false,
                enterAnim, exitAnim, popEnterAnim, popExitAnim
            )
        }
        /**
         * Replace fragment.
         *
         * @param srcFragment  The source of fragment.
         * @param destFragment The destination of fragment.
         * @param destTag      The destination of fragment's tag.
         * @param isAddStack   True to add fragment in stack, false otherwise.
         * @param enterAnim    An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim     An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         * @param popEnterAnim An animation or animator resource ID used for the enter animation on the
         * view of the fragment being readded or reattached caused by
         * popBackStack() or similar methods.
         * @param popExitAnim  An animation or animator resource ID used for the enter animation on the
         * view of the fragment being removed or detached caused by
         * popBackStack() or similar methods.
         */
        /**
         * Replace fragment.
         *
         * @param srcFragment  The source of fragment.
         * @param destFragment The destination of fragment.
         * @param destTag      The destination of fragment's tag.
         * @param isAddStack   True to add fragment in stack, false otherwise.
         * @param enterAnim    An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim     An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         */
        @JvmOverloads
        fun replace(
            srcFragment: Fragment,
            destFragment: Fragment,
            destTag: String?,
            isAddStack: Boolean,
            @AnimRes enterAnim: Int,
            @AnimRes exitAnim: Int,
            @AnimRes popEnterAnim: Int = 0,
            @AnimRes popExitAnim: Int = 0
        ) {
            val args = getArgs(srcFragment)
            replace(
                srcFragment.parentFragmentManager, destFragment, args.id, destTag, isAddStack,
                enterAnim, exitAnim, popEnterAnim, popExitAnim
            )
        }

        /**
         * Replace fragment.
         *
         * @param srcFragment    The source of fragment.
         * @param destFragment   The destination of fragment.
         * @param destTag        The destination of fragment's tag.
         * @param sharedElements A View in a disappearing Fragment to match with a View in an
         * appearing Fragment.
         */
        fun replace(
            srcFragment: Fragment,
            destFragment: Fragment,
            destTag: String?,
            vararg sharedElements: View?
        ) {
            replace(srcFragment, destFragment, destTag, false, *sharedElements)
        }

        /**
         * Replace fragment.
         *
         * @param srcFragment    The source of fragment.
         * @param destFragment   The destination of fragment.
         * @param destTag        The destination of fragment's tag.
         * @param isAddStack     True to add fragment in stack, false otherwise.
         * @param sharedElements A View in a disappearing Fragment to match with a View in an
         * appearing Fragment.
         */
        fun replace(
            srcFragment: Fragment,
            destFragment: Fragment,
            destTag: String?,
            isAddStack: Boolean,
            vararg sharedElements: View?
        ) {
            val args = getArgs(srcFragment)
            replace(
                srcFragment.parentFragmentManager,
                destFragment,
                args.id,
                destTag,
                isAddStack,
                *sharedElements
            )
        }
        /**
         * Replace fragment.
         *
         * @param fm          The manager of fragment.
         * @param fragment    The new fragment to place in the container.
         * @param containerId The id of container.
         * @param destTag     The destination of fragment's tag.
         * @param isAddStack  True to add fragment in stack, false otherwise.
         */
        /**
         * Replace fragment.
         *
         * @param fm          The manager of fragment.
         * @param fragment    The new fragment to place in the container.
         * @param containerId The id of container.
         */
        /**
         * Replace fragment.
         *
         * @param fm          The manager of fragment.
         * @param fragment    The new fragment to place in the container.
         * @param containerId The id of container.
         * @param destTag     The destination of fragment's tag.
         */
        @JvmOverloads
        fun replace(
            fm: FragmentManager,
            fragment: Fragment,
            @IdRes containerId: Int,
            destTag: String? = null,
            isAddStack: Boolean = false
        ) {
            val ft = fm.beginTransaction()
            putArgs(fragment, Args(containerId, destTag, false, isAddStack))
            operate(TYPE_REPLACE_FRAGMENT, fm, ft, null, fragment)
        }

        /**
         * Replace fragment.
         *
         * @param fm          The manager of fragment.
         * @param fragment    The new fragment to place in the container.
         * @param containerId The id of container.
         * @param destTag     The destination of fragment's tag.
         * @param enterAnim   An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim    An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         */
        fun replace(
            fm: FragmentManager,
            fragment: Fragment,
            @IdRes containerId: Int,
            destTag: String?,
            @AnimRes enterAnim: Int,
            @AnimRes exitAnim: Int
        ) {
            replace(fm, fragment, containerId, destTag, false, enterAnim, exitAnim, 0, 0)
        }

        /**
         * Replace fragment.
         *
         * @param fm           The manager of fragment.
         * @param fragment     The new fragment to place in the container.
         * @param containerId  The id of container.
         * @param destTag      The destination of fragment's tag.
         * @param enterAnim    An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim     An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         * @param popEnterAnim An animation or animator resource ID used for the enter animation on the
         * view of the fragment being readded or reattached caused by
         * popBackStack() or similar methods.
         * @param popExitAnim  An animation or animator resource ID used for the enter animation on the
         * view of the fragment being removed or detached caused by
         * popBackStack() or similar methods.
         */
        fun replace(
            fm: FragmentManager,
            fragment: Fragment,
            @IdRes containerId: Int,
            destTag: String?,
            @AnimRes enterAnim: Int,
            @AnimRes exitAnim: Int,
            @AnimRes popEnterAnim: Int,
            @AnimRes popExitAnim: Int
        ) {
            replace(
                fm, fragment, containerId, destTag, false,
                enterAnim, exitAnim, popEnterAnim, popExitAnim
            )
        }
        /**
         * Replace fragment.
         *
         * @param fm           The manager of fragment.
         * @param fragment     The new fragment to place in the container.
         * @param containerId  The id of container.
         * @param destTag      The destination of fragment's tag.
         * @param isAddStack   True to add fragment in stack, false otherwise.
         * @param enterAnim    An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim     An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         * @param popEnterAnim An animation or animator resource ID used for the enter animation on the
         * view of the fragment being readded or reattached caused by
         * popBackStack() or similar methods.
         * @param popExitAnim  An animation or animator resource ID used for the enter animation on the
         * view of the fragment being removed or detached caused by
         * popBackStack() or similar methods.
         */
        /**
         * Replace fragment.
         *
         * @param fm          The manager of fragment.
         * @param fragment    The new fragment to place in the container.
         * @param containerId The id of container.
         * @param destTag     The destination of fragment's tag.
         * @param isAddStack  True to add fragment in stack, false otherwise.
         * @param enterAnim   An animation or animator resource ID used for the enter animation on the
         * view of the fragment being added or attached.
         * @param exitAnim    An animation or animator resource ID used for the exit animation on the
         * view of the fragment being removed or detached.
         */
        @JvmOverloads
        fun replace(
            fm: FragmentManager,
            fragment: Fragment,
            @IdRes containerId: Int,
            destTag: String?,
            isAddStack: Boolean,
            @AnimRes enterAnim: Int,
            @AnimRes exitAnim: Int,
            @AnimRes popEnterAnim: Int = 0,
            @AnimRes popExitAnim: Int = 0
        ) {
            val ft = fm.beginTransaction()
            putArgs(fragment, Args(containerId, destTag, false, isAddStack))
            addAnim(ft, enterAnim, exitAnim, popEnterAnim, popExitAnim)
            operate(TYPE_REPLACE_FRAGMENT, fm, ft, null, fragment)
        }

        /**
         * Replace fragment.
         *
         * @param fm             The manager of fragment.
         * @param fragment       The new fragment to place in the container.
         * @param containerId    The id of container.
         * @param destTag        The destination of fragment's tag.
         * @param sharedElements A View in a disappearing Fragment to match with a View in an
         * appearing Fragment.
         */
        fun replace(
            fm: FragmentManager,
            fragment: Fragment,
            @IdRes containerId: Int,
            destTag: String?,
            vararg sharedElements: View?
        ) {
            replace(fm, fragment, containerId, destTag, false, *sharedElements)
        }

        /**
         * Replace fragment.
         *
         * @param fm             The manager of fragment.
         * @param fragment       The new fragment to place in the container.
         * @param containerId    The id of container.
         * @param destTag        The destination of fragment's tag.
         * @param isAddStack     True to add fragment in stack, false otherwise.
         * @param sharedElements A View in a disappearing Fragment to match with a View in an
         * appearing Fragment.
         */
        fun replace(
            fm: FragmentManager,
            fragment: Fragment,
            @IdRes containerId: Int,
            destTag: String?,
            isAddStack: Boolean,
            vararg sharedElements: View?
        ) {
            val ft = fm.beginTransaction()
            putArgs(fragment, Args(containerId, destTag, false, isAddStack))
            addSharedElement(ft, *sharedElements as Array<out View>)
            operate(TYPE_REPLACE_FRAGMENT, fm, ft, null, fragment)
        }
        /**
         * Pop fragment.
         *
         * @param fm          The manager of fragment.
         * @param isImmediate True to pop immediately, false otherwise.
         */
        /**
         * Pop fragment.
         *
         * @param fm The manager of fragment.
         */
        @JvmOverloads
        fun pop(
            fm: FragmentManager,
            isImmediate: Boolean = true
        ) {
            if (isImmediate) {
                fm.popBackStackImmediate()
            } else {
                fm.popBackStack()
            }
        }
        /**
         * Pop to fragment.
         *
         * @param fm            The manager of fragment.
         * @param popClz        The class of fragment will be popped to.
         * @param isIncludeSelf True to include the fragment, false otherwise.
         * @param isImmediate   True to pop immediately, false otherwise.
         */
        /**
         * Pop to fragment.
         *
         * @param fm            The manager of fragment.
         * @param popClz        The class of fragment will be popped to.
         * @param isIncludeSelf True to include the fragment, false otherwise.
         */
        @JvmOverloads
        fun popTo(
            fm: FragmentManager,
            popClz: Class<out Fragment?>,
            isIncludeSelf: Boolean,
            isImmediate: Boolean = true
        ) {
            if (isImmediate) {
                fm.popBackStackImmediate(
                    popClz.name,
                    if (isIncludeSelf) FragmentManager.POP_BACK_STACK_INCLUSIVE else 0
                )
            } else {
                fm.popBackStack(
                    popClz.name,
                    if (isIncludeSelf) FragmentManager.POP_BACK_STACK_INCLUSIVE else 0
                )
            }
        }
        /**
         * Pop all fragments.
         *
         * @param fm The manager of fragment.
         */
        /**
         * Pop all fragments.
         *
         * @param fm The manager of fragment.
         */
        @JvmOverloads
        fun popAll(fm: FragmentManager, isImmediate: Boolean = true) {
            while (fm.backStackEntryCount > 0) {
                if (isImmediate) {
                    fm.popBackStackImmediate()
                } else {
                    fm.popBackStack()
                }
            }
        }

        /**
         * Remove fragment.
         *
         * @param remove The fragment will be removed.
         */
        fun remove(remove: Fragment) {
            operateNoAnim(remove.fragmentManager, TYPE_REMOVE_FRAGMENT, null, remove)
        }

        /**
         * Remove to fragment.
         *
         * @param removeTo      The fragment will be removed to.
         * @param isIncludeSelf True to include the fragment, false otherwise.
         */
        fun removeTo(removeTo: Fragment, isIncludeSelf: Boolean) {
            operateNoAnim(
                removeTo.fragmentManager, TYPE_REMOVE_TO_FRAGMENT,
                if (isIncludeSelf) removeTo else null, removeTo
            )
        }

        /**
         * Remove all fragments.
         *
         * @param fm The manager of fragment.
         */
        fun removeAll(fm: FragmentManager) {
            val fragments = getFragments(fm)
            operateNoAnim(
                fm,
                TYPE_REMOVE_FRAGMENT,
                null,
                *fragments.toTypedArray()
            )
        }

        private fun putArgs(fragment: Fragment, args: Args) {
            var bundle = fragment.arguments
            if (bundle == null) {
                bundle = Bundle()
                fragment.arguments = bundle
            }
            bundle.putInt(ARGS_ID, args.id)
            bundle.putBoolean(ARGS_IS_HIDE, args.isHide)
            bundle.putBoolean(ARGS_IS_ADD_STACK, args.isAddStack)
            bundle.putString(ARGS_TAG, args.tag)
        }

        private fun putArgs(fragment: Fragment, isHide: Boolean) {
            var bundle = fragment.arguments
            if (bundle == null) {
                bundle = Bundle()
                fragment.arguments = bundle
            }
            bundle.putBoolean(ARGS_IS_HIDE, isHide)
        }

        private fun getArgs(fragment: Fragment): Args {
            val bundle = fragment.arguments
            return Args(
                bundle!!.getInt(ARGS_ID, fragment.id),
                bundle.getBoolean(ARGS_IS_HIDE),
                bundle.getBoolean(ARGS_IS_ADD_STACK)
            )
        }

        private fun operateNoAnim(
            fm: FragmentManager?,
            type: Int,
            src: Fragment?,
            vararg dest: Fragment
        ) {
            val ft = fm!!.beginTransaction()
            operate(type, fm, ft, src, *dest)
        }

        private fun operate(
            type: Int,
            fm: FragmentManager?,
            ft: FragmentTransaction,
            src: Fragment?,
            vararg dest: Fragment
        ) {
            if (src != null && src.isRemoving) {
                KLog.e("FragmentUtils", src.javaClass.name + " is isRemoving")
                return
            }
            var name: String
            var args: Bundle?
            when (type) {
                TYPE_ADD_FRAGMENT -> for (fragment in dest) {
                    args = fragment.arguments
                    if (args == null) {
                        return
                    }
                    name = args.getString(ARGS_TAG, fragment.javaClass.name)
                    val fragmentByTag = fm!!.findFragmentByTag(name)
                    if (fragmentByTag != null && fragmentByTag.isAdded) {
                        ft.remove(fragmentByTag)
                    }
                    ft.add(args.getInt(ARGS_ID), fragment, name)
                    if (args.getBoolean(ARGS_IS_HIDE)) {
                        ft.hide(fragment)
                    }
                    if (args.getBoolean(ARGS_IS_ADD_STACK)) {
                        ft.addToBackStack(name)
                    }
                }
                TYPE_HIDE_FRAGMENT -> for (fragment in dest) {
                    ft.hide(fragment)
                }
                TYPE_SHOW_FRAGMENT -> for (fragment in dest) {
                    ft.show(fragment)
                }
                TYPE_SHOW_HIDE_FRAGMENT -> {
                    ft.show(src!!)
                    for (fragment in dest) {
                        if (fragment !== src) {
                            ft.hide(fragment)
                        }
                    }
                }
                TYPE_REPLACE_FRAGMENT -> {
                    args = dest[0].arguments
                    if (args == null) {
                        return
                    }
                    name = args.getString(ARGS_TAG, dest[0].javaClass.name)
                    ft.replace(args.getInt(ARGS_ID), dest[0], name)
                    if (args.getBoolean(ARGS_IS_ADD_STACK)) {
                        ft.addToBackStack(name)
                    }
                }
                TYPE_REMOVE_FRAGMENT -> for (fragment in dest) {
                    if (fragment !== src) {
                        ft.remove(fragment)
                    }
                }
                TYPE_REMOVE_TO_FRAGMENT -> {
                    var i = dest.size - 1
                    while (i >= 0) {
                        val fragment = dest[i]
                        if (fragment === dest[0]) {
                            if (src != null) {
                                ft.remove(fragment)
                            }
                            break
                        }
                        ft.remove(fragment)
                        --i
                    }
                }
            }
            ft.commitAllowingStateLoss()
        }

        private fun addAnim(
            ft: FragmentTransaction,
            enter: Int,
            exit: Int,
            popEnter: Int,
            popExit: Int
        ) {
            ft.setCustomAnimations(enter, exit, popEnter, popExit)
        }

        private fun addSharedElement(
            ft: FragmentTransaction,
            vararg views: View
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (view in views) {
                    ft.addSharedElement(view, view.transitionName)
                }
            }
        }

        /**
         * Return the top fragment.
         *
         * @param fm The manager of fragment.
         * @return the top fragment
         */
        fun getTop(fm: FragmentManager): Fragment? {
            return getTopIsInStack(fm, false)
        }

        /**
         * Return the top fragment in stack.
         *
         * @param fm The manager of fragment.
         * @return the top fragment in stack
         */
        fun getTopInStack(fm: FragmentManager): Fragment? {
            return getTopIsInStack(fm, true)
        }

        private fun getTopIsInStack(
            fm: FragmentManager,
            isInStack: Boolean
        ): Fragment? {
            val fragments = getFragments(fm)
            for (i in fragments.indices.reversed()) {
                val fragment = fragments[i]
                if (isInStack) {
                    if (fragment.requireArguments().getBoolean(ARGS_IS_ADD_STACK)) {
                        return fragment
                    }
                } else {
                    return fragment
                }
            }
            return null
        }

        /**
         * Return the top fragment which is shown.
         *
         * @param fm The manager of fragment.
         * @return the top fragment which is shown
         */
        fun getTopShow(fm: FragmentManager): Fragment? {
            return getTopShowIsInStack(fm, false)
        }

        /**
         * Return the top fragment which is shown in stack.
         *
         * @param fm The manager of fragment.
         * @return the top fragment which is shown in stack
         */
        fun getTopShowInStack(fm: FragmentManager): Fragment? {
            return getTopShowIsInStack(fm, true)
        }

        private fun getTopShowIsInStack(
            fm: FragmentManager,
            isInStack: Boolean
        ): Fragment? {
            val fragments = getFragments(fm)
            for (i in fragments.indices.reversed()) {
                val fragment = fragments[i]
                if (fragment.isResumed && fragment.isVisible && fragment.userVisibleHint
                ) {
                    if (isInStack) {
                        if (fragment.requireArguments().getBoolean(ARGS_IS_ADD_STACK)) {
                            return fragment
                        }
                    } else {
                        return fragment
                    }
                }
            }
            return null
        }

        /**
         * Return the fragments in manager.
         *
         * @param fm The manager of fragment.
         * @return the fragments in manager
         */
        fun getFragments(fm: FragmentManager): List<Fragment> {
            val fragments = fm.fragments
            return if (fragments == null || fragments.isEmpty()) {
                emptyList()
            } else fragments
        }

        /**
         * Return the fragments in stack in manager.
         *
         * @param fm The manager of fragment.
         * @return the fragments in stack in manager
         */
        fun getFragmentsInStack(fm: FragmentManager): List<Fragment> {
            val fragments = getFragments(fm)
            val result: MutableList<Fragment> = ArrayList()
            for (fragment in fragments) {
                if (fragment.requireArguments().getBoolean(ARGS_IS_ADD_STACK)) {
                    result.add(fragment)
                }
            }
            return result
        }

        /**
         * Return all fragments in manager.
         *
         * @param fm The manager of fragment.
         * @return all fragments in manager
         */
        fun getAllFragments(fm: FragmentManager): List<FragmentNode> {
            return getAllFragments(fm, ArrayList())
        }

        private fun getAllFragments(
            fm: FragmentManager,
            result: MutableList<FragmentNode>
        ): List<FragmentNode> {
            val fragments = getFragments(fm)
            for (i in fragments.indices.reversed()) {
                val fragment = fragments[i]
                if (fragment != null) {
                    result.add(
                        FragmentNode(
                            fragment,
                            getAllFragments(
                                fragment.childFragmentManager,
                                ArrayList()
                            )
                        )
                    )
                }
            }
            return result
        }

        /**
         * Return all fragments in stack in manager.
         *
         * @param fm The manager of fragment.
         * @return all fragments in stack in manager
         */
        fun getAllFragmentsInStack(fm: FragmentManager): List<FragmentNode> {
            return getAllFragmentsInStack(fm, ArrayList())
        }

        private fun getAllFragmentsInStack(
            fm: FragmentManager,
            result: MutableList<FragmentNode>
        ): List<FragmentNode> {
            val fragments = getFragments(fm)
            for (i in fragments.indices.reversed()) {
                val fragment = fragments[i]
                if (fragment.requireArguments().getBoolean(ARGS_IS_ADD_STACK)) {
                    result.add(
                        FragmentNode(
                            fragment,
                            getAllFragmentsInStack(
                                fragment.childFragmentManager,
                                ArrayList()
                            )
                        )
                    )
                }
            }
            return result
        }

        /**
         * Find fragment.
         *
         * @param fm      The manager of fragment.
         * @param findClz The class of fragment will be found.
         * @return the fragment matches class
         */
        fun findFragment(
            fm: FragmentManager,
            findClz: Class<out Fragment?>
        ): Fragment? {
            return fm.findFragmentByTag(findClz.name)
        }

        /**
         * Find fragment.
         *
         * @param fm  The manager of fragment.
         * @param tag The tag of fragment will be found.
         * @return the fragment matches class
         */
        fun findFragment(
            fm: FragmentManager,
            tag: String
        ): Fragment? {
            return fm.findFragmentByTag(tag)
        }

        /**
         * Dispatch the back press for fragment.
         *
         * @param fragment The fragment.
         * @return `true`: the fragment consumes the back press<br></br>`false`: otherwise
         */
        fun dispatchBackPress(fragment: Fragment): Boolean {
            return (fragment.isResumed
                    && fragment.isVisible
                    && fragment.userVisibleHint
                    && fragment is OnBackClickListener
                    && (fragment as OnBackClickListener).onBackClick())
        }

        /**
         * Dispatch the back press for fragment.
         *
         * @param fm The manager of fragment.
         * @return `true`: the fragment consumes the back press<br></br>`false`: otherwise
         */
        fun dispatchBackPress(fm: FragmentManager): Boolean {
            val fragments = getFragments(fm)
            if (fragments == null || fragments.isEmpty()) {
                return false
            }
            for (i in fragments.indices.reversed()) {
                val fragment = fragments[i]
                if (fragment != null && fragment.isResumed
                    && fragment.isVisible
                    && fragment.userVisibleHint
                    && fragment is OnBackClickListener
                    && (fragment as OnBackClickListener).onBackClick()
                ) {
                    return true
                }
            }
            return false
        }

        /**
         * Set background color for fragment.
         *
         * @param fragment The fragment.
         * @param color    The background color.
         */
        fun setBackgroundColor(
            fragment: Fragment,
            @ColorInt color: Int
        ) {
            val view = fragment.view
            view?.setBackgroundColor(color)
        }

        /**
         * Set background resource for fragment.
         *
         * @param fragment The fragment.
         * @param resId    The resource id.
         */
        fun setBackgroundResource(
            fragment: Fragment,
            @DrawableRes resId: Int
        ) {
            val view = fragment.view
            view?.setBackgroundResource(resId)
        }

        /**
         * Set background color for fragment.
         *
         * @param fragment   The fragment.
         * @param background The background.
         */
        fun setBackground(fragment: Fragment, background: Drawable?) {
            val view = fragment.view ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.background = background
            } else {
                view.setBackgroundDrawable(background)
            }
        }

        /**
         * Return the simple name of fragment.
         *
         * @param fragment The fragment.
         * @return the simple name of fragment
         */
        fun getSimpleName(fragment: Fragment?): String {
            return if (fragment == null) "null" else fragment.javaClass.simpleName
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}