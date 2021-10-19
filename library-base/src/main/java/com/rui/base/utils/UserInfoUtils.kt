package com.rui.base.utils

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Base64
import com.rui.base.entity.LoginUnNormalBean
import com.rui.base.global.SPKeyGlobal
import com.rui.mvvmlazy.base.appContext
import java.io.*

class UserInfoUtils() {
    private val share: SharedPreferences
    private val editor: SharedPreferences.Editor
    private val SHARED_NAME = "spname" //sp的文件名 自定义

    /**
     * 保存对象
     * 针对复杂类型存储<对象>
     * 注意：要保存的对象必须序列化
     *
     * @param key
     * @param bean
    </对象> */
    private fun setUserInfo(key: String, bean: LoginUnNormalBean) {
        //创建字节数组输出流
        val baos = ByteArrayOutputStream()
        /*
        对象输出流
        ObjectOutputStream 是实现序列化的关键类，它可以将对象转换为二进制流
         */
        var out: ObjectOutputStream? = null
        try {
            //然后通过将字对象进行64转码，写入key值为key的sp中
            out = ObjectOutputStream(baos)
            //将对象写进该流中
            out.writeObject(bean)
            //将二进制数据转换为字符串
            val objectVal = String(Base64.encode(baos.toByteArray(), Base64.DEFAULT))
            editor.putString(key, objectVal)
            editor.commit()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (baos != null) {
                    baos.close()
                }
                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun setUserInfo(`object`: LoginUnNormalBean) {
        setUserInfo(SPKeyGlobal.USER_INFO, `object`)
    }

    val userInfo: LoginUnNormalBean?
        get() = getUserInfo(SPKeyGlobal.USER_INFO)

    /**
     * 根据字符串key获取对象
     *
     * @param key
     * @param
     * @return UserInfoEntity
     */
    private fun getUserInfo(key: String): LoginUnNormalBean? {
        if (share.contains(key)) {
            //获取该key对象的字符串值
            val objectVal = share.getString(key, null)
            //将字符串转换为二进制数据
            val buffer = Base64.decode(objectVal, Base64.DEFAULT)
            //一样通过读取字节流，创建字节流输入流，写入对象并作强制转换
            val bais = ByteArrayInputStream(buffer)
            var ois: ObjectInputStream? = null
            try {
                ois = ObjectInputStream(bais)
                return ois.readObject() as LoginUnNormalBean
            } catch (e: StreamCorruptedException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } finally {
                try {
                    if (bais != null) {
                        bais.close()
                    }
                    ois?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    var account: String?
        get() = share.getString(SPKeyGlobal.ACCOUNT, null)
        set(account) {
            editor.putString(SPKeyGlobal.ACCOUNT, account)
            editor.commit()
        }
    var psd: String?
        get() = share.getString(SPKeyGlobal.PSD, null)
        set(psd) {
            editor.putString(SPKeyGlobal.PSD, psd)
            editor.commit()
        }
    var phone: String?
        get() = share.getString(SPKeyGlobal.PHONE, null)
        set(phone) {
            editor.putString(SPKeyGlobal.PHONE, phone)
            editor.commit()
        }
    var auth: String?
        get() = share.getString(SPKeyGlobal.AUTH, "")
        set(data) {
            editor.putString(SPKeyGlobal.AUTH, data)
            editor.commit()
        }

    fun remove(key: String?) {
        editor.remove(key)
        editor.commit()
    }

    /**
     * 判断用户是否登录，用户未登录，直接跳转到登录界面
     */
    val isLogin: Boolean
        get() = !(null == userInfo || TextUtils.isEmpty(userInfo!!.access_token))

    /**
     * 判断用户是否登录，不跳转
     */
    val isLogin2: Boolean
        get() = !(null == userInfo || TextUtils.isEmpty(userInfo!!.access_token))

    /**
     * 退出登录
     */
    fun logOut() {
        remove(SPKeyGlobal.USER_INFO)
        remove(SPKeyGlobal.AUTH)
    }

    companion object {
        /**
         * 单例模式
         */
        var instance //单例模式 双重检查锁定
                : UserInfoUtils? = null
            get() {
                if (field == null) {
                    synchronized(UserInfoUtils::class.java) {
                        if (field == null) {
                            field = UserInfoUtils()
                        }
                    }
                }
                return field
            }
            private set
    }

    init {
        share = appContext.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)
        editor = share.edit()
    }
}