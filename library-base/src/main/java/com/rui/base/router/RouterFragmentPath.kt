package com.rui.base.router

import com.rui.base.router.RouterActivityPath.Sign
import com.rui.base.router.RouterActivityPath.Mine
import com.rui.base.router.RouterFragmentPath.Home
import com.rui.base.router.RouterFragmentPath.Discover

/**
 * ******************************
 * 用于组件开发中，ARouter多Fragment跳转的统一路径注册
 * 在这里注册添加路由路径，需要清楚的写好注释，标明功能界面
 * *******************************
 */
class RouterFragmentPath {
    /**
     * 首页组件
     */
    object Home {
        private const val HOME = "/home"

        /*首页*/
        const val PAGER_HOME = HOME + "/Home"

        //推荐
        const val PAGER_RECOMMEND = HOME + PAGER_HOME + "/Recommend"

        //热门
        const val PAGER_HOT = HOME + PAGER_HOME + "/Hot"

        //关注
        const val PAGER_ATTENTION = HOME + PAGER_HOME + "/Attention"

        //附近
        const val PAGER_NEARBY = HOME + PAGER_HOME + "/NearBy"
    }

    /**
     * 资源组件
     */
    object Resource {
        private const val RESOURCE = "/resource"

        /*资源*/
        const val PAGER_RESOURCE = RESOURCE + "/Resource"
    }

    /**
     * 发现组件
     */
    object Discover {
        private const val DISCOVER = "/discover"

        /*发现*/
        const val PAGER_DISCOVER = DISCOVER + "/Discover"

        //我的圈子
        const val ME_CIRCLE = DISCOVER + "/meCircle"

        //全部圈子
        const val ALL_CIRCLE = DISCOVER + "/allCircle"
    }

    /**
     * 消息组件
     */
    object Message {
        private const val MESSAGE = "/message"

        /*消息*/
        const val PAGER_MESSAGE = MESSAGE + "/Message"
    }

    /**
     * 用户组件
     */
    object User {
        private const val USER = "/user"

        /*我的*/
        const val PAGER_MINE = USER + "/Mine"
    }
}