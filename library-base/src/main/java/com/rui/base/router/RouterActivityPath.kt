package com.rui.base.router

/**
 * ******************************
 * *description: 用于组件开发中，ARouter单Activity跳转的统一路径注册
 * 在这里注册添加路由路径，需要清楚的写好注释，标明功能界面
 * *******************************
 */
class RouterActivityPath {
    /**
     * 主业务组件
     */
    object Main {
        private const val MAIN = "/main"

        /*主业务界面*/
        const val PAGER_MAIN = MAIN + "/Main"
        const val PAGER_TEST = MAIN + "/Test"

        //用户主界面
        const val USER_MAIN = MAIN + "/UserMain"
        const val PAGER_WEBVIEW = MAIN + "/webview"
        const val ABOUT_US = MAIN + "/aboutus"
    }

    /**
     * 身份验证组件
     */
    object Sign {
        private const val SIGN = "/sign"
        const val PAGER_ACCOUNT_LOGIN = SIGN + "/accountLogin"
        const val PAGER_MOBILE_LOGIN = SIGN + "/mobileLogin"
    }

    /**
     * 用户组件
     */
    object Mine {
        private const val MINE = "/mine"
        const val PAGER_OSNOTICE = MINE + "/OsNotice"
        const val POLICYPAGER = MINE + "/policyPage"
    }

    object Test {
        const val TEST = "/test"
        const val TESTPAGER = TEST + "/testPage"
    }
}