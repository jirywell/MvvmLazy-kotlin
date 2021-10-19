package com.rui.mvvmlazy.http.cookie.store

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.internal.and
import java.io.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by zjr on 2020/5/13.
 */
class PersistentCookieStore(context: Context) : CookieStore {
    private val cookies: HashMap<String, ConcurrentHashMap<String?, Cookie>>
    private val cookiePrefs: SharedPreferences
    private fun getCookieToken(cookie: Cookie?): String {
        return cookie!!.name + "@" + cookie.domain
    }

    /** 根据当前url获取所有需要的cookie,只返回没有过期的cookie  */
    override fun loadCookie(url: HttpUrl): List<Cookie> {
        val ret = ArrayList<Cookie>()
        if (cookies.containsKey(url.host)) {
            val urlCookies: Collection<Cookie> = cookies[url.host]!!.values
            for (cookie in urlCookies) {
                if (isCookieExpired(cookie)) {
                    removeCookie(url, cookie)
                } else {
                    ret.add(cookie)
                }
            }
        }
        return ret
    }

    /** 将url的所有Cookie保存在本地  */
    override fun saveCookie(url: HttpUrl, urlCookies: List<Cookie>) {
        if (!cookies.containsKey(url.host)) {
            cookies[url.host] = ConcurrentHashMap()
        }
        for (cookie in urlCookies) {
            //当前cookie是否过期
            if (isCookieExpired(cookie)) {
                removeCookie(url, cookie)
            } else {
                saveCookie(url, cookie, getCookieToken(cookie))
            }
        }
    }

    override fun saveCookie(url: HttpUrl, cookie: Cookie) {
        if (!cookies.containsKey(url.host)) {
            cookies[url.host] = ConcurrentHashMap()
        }
        //当前cookie是否过期
        if (isCookieExpired(cookie)) {
            removeCookie(url, cookie)
        } else {
            saveCookie(url, cookie, getCookieToken(cookie))
        }
    }

    /**
     * 保存cookie，并将cookies持久化到本地,数据结构为
     * Url.host -> Cookie1.name,Cookie2.name,Cookie3.name
     * cookie_Cookie1.name -> CookieString
     * cookie_Cookie2.name -> CookieString
     */
    private fun saveCookie(url: HttpUrl, cookie: Cookie, name: String) {
        //内存缓存
        cookies[url.host]!![name] = cookie
        //文件缓存
        val prefsWriter = cookiePrefs.edit()
        prefsWriter.putString(url.host, TextUtils.join(",", cookies[url.host]!!.keys))
        prefsWriter.putString(
            COOKIE_NAME_PREFIX + name,
            encodeCookie(SerializableHttpCookie(cookie))
        )
        prefsWriter.apply()
    }

    /** 根据url移除当前的cookie  */
    override fun removeCookie(url: HttpUrl, cookie: Cookie?): Boolean {
        val name = getCookieToken(cookie)
        return if (cookies.containsKey(url.host) && cookies[url.host]!!.containsKey(name)) {
            //内存移除
            cookies[url.host]!!.remove(name)
            //文件移除
            val prefsWriter = cookiePrefs.edit()
            if (cookiePrefs.contains(COOKIE_NAME_PREFIX + name)) {
                prefsWriter.remove(COOKIE_NAME_PREFIX + name)
            }
            prefsWriter.putString(url.host, TextUtils.join(",", cookies[url.host]!!.keys))
            prefsWriter.apply()
            true
        } else {
            false
        }
    }

    override fun removeCookie(url: HttpUrl): Boolean {
        return if (cookies.containsKey(url.host)) {
            //文件移除
            val cookieNames: Set<String?> = cookies[url.host]!!.keys
            val prefsWriter = cookiePrefs.edit()
            for (cookieName in cookieNames) {
                if (cookiePrefs.contains(COOKIE_NAME_PREFIX + cookieName)) {
                    prefsWriter.remove(COOKIE_NAME_PREFIX + cookieName)
                }
            }
            prefsWriter.remove(url.host).apply()
            //内存移除
            cookies.remove(url.host)
            true
        } else {
            false
        }
    }

    override fun removeAllCookie(): Boolean {
        val prefsWriter = cookiePrefs.edit()
        prefsWriter.clear().apply()
        cookies.clear()
        return true
    }

    /** 获取所有的cookie  */
    override val allCookie: List<Cookie>
        get() {
            val ret: MutableList<Cookie> = ArrayList()
            for (key in cookies.keys) ret.addAll(cookies[key]!!.values)
            return ret
        }

    override fun getCookie(url: HttpUrl): List<Cookie> {
        val ret: MutableList<Cookie> = ArrayList()
        val mapCookie: Map<String?, Cookie>? = cookies[url.host]
        if (mapCookie != null) ret.addAll(mapCookie.values)
        return ret
    }

    /**
     * cookies 序列化成 string
     *
     * @param cookie 要序列化的cookie
     * @return 序列化之后的string
     */
    private fun encodeCookie(cookie: SerializableHttpCookie?): String? {
        if (cookie == null) return null
        val os = ByteArrayOutputStream()
        try {
            val outputStream = ObjectOutputStream(os)
            outputStream.writeObject(cookie)
        } catch (e: IOException) {
            Log.d(LOG_TAG, "IOException in encodeCookie", e)
            return null
        }
        return byteArrayToHexString(os.toByteArray())
    }

    /**
     * 将字符串反序列化成cookies
     *
     * @param cookieString cookies string
     * @return cookie object
     */
    private fun decodeCookie(cookieString: String): Cookie? {
        val bytes = hexStringToByteArray(cookieString)
        val byteArrayInputStream = ByteArrayInputStream(bytes)
        var cookie: Cookie? = null
        try {
            val objectInputStream = ObjectInputStream(byteArrayInputStream)
            cookie = (objectInputStream.readObject() as SerializableHttpCookie).getCookie()
        } catch (e: IOException) {
            Log.d(LOG_TAG, "IOException in decodeCookie", e)
        } catch (e: ClassNotFoundException) {
            Log.d(LOG_TAG, "ClassNotFoundException in decodeCookie", e)
        }
        return cookie
    }

    /**
     * 二进制数组转十六进制字符串
     *
     * @param bytes byte array to be converted
     * @return string containing hex values
     */
    private fun byteArrayToHexString(bytes: ByteArray): String {
        val sb = StringBuilder(bytes.size * 2)
        for (element in bytes) {
            val v: Int = element and 0xff
            if (v < 16) {
                sb.append('0')
            }
            sb.append(Integer.toHexString(v))
        }
        return sb.toString().toUpperCase(Locale.US)
    }

    /**
     * 十六进制字符串转二进制数组
     *
     * @param hexString string of hex-encoded values
     * @return decoded byte array
     */
    private fun hexStringToByteArray(hexString: String): ByteArray {
        val len = hexString.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(
                hexString[i],
                16
            ) shl 4) + Character.digit(hexString[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    companion object {
        private const val LOG_TAG = "PersistentCookieStore"
        private const val COOKIE_PREFS = "habit_cookie" //cookie使用prefs保存
        private const val COOKIE_NAME_PREFIX = "cookie_" //cookie持久化的统一前缀

        /** 当前cookie是否过期  */
        private fun isCookieExpired(cookie: Cookie): Boolean {
            return cookie.expiresAt < System.currentTimeMillis()
        }
    }

    init {
        cookiePrefs = context.getSharedPreferences(COOKIE_PREFS, Context.MODE_PRIVATE)
        cookies = HashMap()

        //将持久化的cookies缓存到内存中,数据结构为 Map<Url.host, Map<Cookie.name, Cookie>>
        val prefsMap = cookiePrefs.all
        for ((key, value) in prefsMap) {
            if (value != null && !key.startsWith(COOKIE_NAME_PREFIX)) {
                //获取url对应的所有cookie的key,用","分割
                val cookieNames = TextUtils.split(value as String?, ",")
                for (name in cookieNames) {
                    //根据对应cookie的Key,从xml中获取cookie的真实值
                    val encodedCookie = cookiePrefs.getString(COOKIE_NAME_PREFIX + name, null)
                    if (encodedCookie != null) {
                        val decodedCookie = decodeCookie(encodedCookie)
                        if (decodedCookie != null) {
                            if (!cookies.containsKey(key)) cookies[key] = ConcurrentHashMap()
                            cookies[key]!![name] = decodedCookie
                        }
                    }
                }
            }
        }
    }
}