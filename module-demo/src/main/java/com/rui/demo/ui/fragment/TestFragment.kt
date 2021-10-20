package com.rui.demo.ui.fragment

import android.Manifest
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.permissionx.guolindev.PermissionX
import com.rui.demo.BR
import com.rui.demo.R
import com.rui.demo.databinding.TestFragmentHomeBinding
import com.rui.demo.ui.viewmodel.TestViewModel
import com.rui.mvvmlazy.base.fragment.BaseVmDbFragment
import com.rui.mvvmlazy.utils.common.KLog
import org.jetbrains.anko.support.v4.toast

/**
 * 测试入口页面
 */
class TestFragment : BaseVmDbFragment<TestFragmentHomeBinding, TestViewModel>() {
    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.test_fragment_home
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initData() {
        super.initData()
        binding.setPresenter(Presenter())
    }

    override fun initViewObservable() {
        super.initViewObservable()
    }

    /**
     * 封装布局中的点击事件儿;
     */
    inner class Presenter {
        fun bindingClick() {
            NavHostFragment
                .findNavController(this@TestFragment)
                .navigate(R.id.home_action_testfragment_to_bindingfragment)
        }

        fun netClick() {
            NavHostFragment
                .findNavController(this@TestFragment)
                .navigate(R.id.test_action_testfragment_to_test_networkfragment)

        }

        fun rvPaginationClick() {
            NavHostFragment
                .findNavController(this@TestFragment)
                .navigate(R.id.home_action_testfragment_to_home_paginationfragment)
        }

        fun listClick() {
            NavHostFragment
                .findNavController(this@TestFragment)
                .navigate(R.id.home_action_testfragment_to_home_listfragment)
        }

        fun rvMutiClick() {
            NavHostFragment
                .findNavController(this@TestFragment)
                .navigate(R.id.test_action_testfragment_to_test_mutifragment)
        }

        fun errorClick() {
            "你好".toInt()
        }

        fun permissionClick() {
            requestCameraPermissions()
        }

        fun dataBaseClick() {
            NavHostFragment
                .findNavController(this@TestFragment)
                .navigate(R.id.home_action_testfragment_to_home_roomsamplefragment)
        }

        fun rxHelperClick() {
            NavHostFragment
                .findNavController(this@TestFragment)
                .navigate(R.id.test_action_testfragment_to_test_shapeviewfragment)
        }

        fun downloadClick() {
            downFile("http://gdown.baidu.com/data/wisegame/dc8a46540c7960a2/baidushoujizhushou_16798087.apk")
        }

        fun titleBarClick() {
            startContainerActivity(TitleBarFragment::class.java.canonicalName)
        }

        fun viewPagerGroupClick() {
            startContainerActivity(ViewPagerGroupFragment::class.java.canonicalName)
        }

        fun dialogClick() {
            NavHostFragment
                .findNavController(this@TestFragment)
                .navigate(R.id.test_action_testfragment_to_test_dialogfragment)
        }

        fun btmTabClick() {
            NavHostFragment
                .findNavController(this@TestFragment)
                .navigate(R.id.test_action_testfragment_to_test_btmtabfragment)
        }
    }

    /**
     * 请求相机权限
     */
    private fun requestCameraPermissions() {
        PermissionX.init(activity)
            .permissions(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CAMERA,
                Manifest.permission.CALL_PHONE
            )
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "Core fundamental are based on these permissions",
                    "OK",
                    "Cancel"
                )
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    toast("All permissions are granted")
                } else {
                    toast("These permissions are denied: $deniedList")
                }
            }

    }

    private fun downFile(url: String) {
        val destFileDir: String = requireActivity().application.cacheDir.path
        KLog.e("destFileDir--$destFileDir")
        val destFileName = System.currentTimeMillis().toString() + ".apk"
        val progressDialog = ProgressDialog(activity)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.setTitle("正在下载...")
        progressDialog.setCancelable(false)
        progressDialog.show()
//        DownLoadManager.getInstance()
//            .load(url, object : ProgressCallBack<ResponseBody>(destFileDir, destFileName) {
//                override fun onStart() {
//                    super.onStart()
//                    KLog.e("下载--onStart")
//                }
//
//                override fun onSuccess(responseBody: ResponseBody) {
//                    KLog.e("下载--onSuccess")
//                    ToastUtils.showShort("文件下载完成！")
//                }
//
//                override fun progress(progress: Long, total: Long) {
//                    KLog.e("下载--progress")
//                    progressDialog.setMax(total.toInt())
//                    progressDialog.setProgress(progress.toInt())
//                }
//
//                override fun onError(e: Throwable) {
//                    e.printStackTrace()
//                    ToastUtils.showShort("文件下载失败！")
//                    progressDialog.dismiss()
//                }
//
//                override fun onCompleted() {
//                    progressDialog.dismiss()
//                    KLog.e("下载--onCompleted")
//                }
//            })
    }
}