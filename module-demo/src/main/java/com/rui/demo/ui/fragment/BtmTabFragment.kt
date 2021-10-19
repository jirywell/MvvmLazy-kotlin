package com.rui.demo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.rui.demo.BR
import com.rui.demo.R
import com.rui.demo.databinding.TestFragmentBtmtabBinding
import com.rui.demo.ui.fragment.tab.TabBar1Fragment
import com.rui.demo.ui.fragment.tab.TabBar2Fragment
import com.rui.demo.ui.fragment.tab.TabBar3Fragment
import com.rui.demo.ui.fragment.tab.TabBar4Fragment
import com.rui.demo.ui.viewmodel.BtmTabViewModel
import com.rui.mvvmlazy.base.fragment.BaseVmDbFragment
import java.util.*

class BtmTabFragment : BaseVmDbFragment<TestFragmentBtmtabBinding, BtmTabViewModel>() {
    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.test_fragment_btmtab
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initData() {
        super.initData()
        val list: MutableList<Fragment> = ArrayList()
        list.add(TabBar1Fragment.Companion.newInstance())
        list.add(TabBar2Fragment.Companion.newInstance())
        list.add(TabBar3Fragment.Companion.newInstance())
        list.add(TabBar4Fragment.Companion.newInstance())
        binding.tabLayout.configTabLayoutConfig {
            onSelectIndexChange = { fromIndex, selectIndexList, reselect, fromUser ->
                val toIndex = selectIndexList[0]
                showFragment(list[toIndex], if (fromIndex >= 0) list[fromIndex] else null)
            }
        }
    }

    private fun showFragment(showFragment: Fragment, hideFragment: Fragment?) {
        val transaction = childFragmentManager.beginTransaction()
        if (showFragment.isAdded) {
            transaction.show(showFragment)
            transaction.setMaxLifecycle(showFragment, Lifecycle.State.RESUMED)
        } else {
            transaction.add(R.id.frame_container_layout, showFragment)
        }
        if (hideFragment != null) {
            if (hideFragment.isAdded) {
                transaction.hide(hideFragment)
                transaction.setMaxLifecycle(showFragment, Lifecycle.State.STARTED)
            }
        }
        transaction.commitAllowingStateLoss()
    }
}