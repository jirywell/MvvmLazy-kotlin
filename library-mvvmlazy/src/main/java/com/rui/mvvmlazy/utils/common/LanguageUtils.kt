/*
 * Copyright (C) 2020 jirui_zhao(jirui_zhao@163.com)
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
 *
 */
package com.rui.mvvmlazy.utils.common

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

/**
 * 语言工具类
 *
 * @author zjr
 * @since 2020/6/7 11:27 PM
 */
class LanguageUtils() {
    companion object {
        /**
         * 中文
         */
        private const val CHINESE_LANGUAGE = "zh"

        /**
         * 阿拉伯语
         */
        private const val ARABIC_LANGUAGE = "ar"

        /**
         * 波斯语
         */
        private const val FARSI_LANGUAGE = "fa"

        /**
         * 希伯来文
         */
        private const val IW_LANGUAGE = "iw"

        /**
         * 乌尔都语
         */
        private const val URDU_LANGUAGE = "ur"

        /**
         *
         */
        private const val UG_LANGUAGE = "ug"

        /**
         * 英语
         */
        private const val EN_LANGUAGE = "en"
        //========系统级语言=======//
        /**
         * @return 获取系统设置的默认语言
         */
        val defaultLocale: Locale
            get() = Locale.getDefault()

        /**
         * 获取系统设置的默认语言代码
         *
         * @return 语言代码
         */
        val language: String
            get() = Locale.getDefault().language

        /**
         * 获取系统设置的默认语言所在国家的代码
         *
         * @return 语言所在国家的代码
         */
        val country: String
            get() = Locale.getDefault().country

        /**
         * @return 是否是中文
         */
        val isZh: Boolean
            get() {
                val lang = language
                return CHINESE_LANGUAGE == lang
            }

        /**
         * @return 是否是阿拉伯语
         */
        val isArabic: Boolean
            get() {
                val lang = language
                return ARABIC_LANGUAGE == lang
            }

        /**
         * @return 是否是英语
         */
        val isEn: Boolean
            get() {
                val lang = language
                return EN_LANGUAGE == lang
            }
        val isUrdu: Boolean
            get() {
                val lang = language
                return URDU_LANGUAGE == lang
            }

        /**
         * @return 是否是镜像语言
         */
        val isRTL: Boolean
            get() {
                val lang = language
                return ARABIC_LANGUAGE == lang || FARSI_LANGUAGE == lang || IW_LANGUAGE == lang || URDU_LANGUAGE == lang || UG_LANGUAGE == lang
            }
        //========应用级语言=======//
        /**
         * @return 获取当前应用设置的语言
         */
        fun getAppLocale(context: Context): Locale {
            return context.resources.configuration.locale
        }

        fun getI18N(context: Context): String {
            val locale = getAppLocale(context)
            return getLocaleLanguage(locale) + '_' + getLocaleCountry(locale)
        }

        private fun getLocaleLanguage(locale: Locale): String {
            var language = locale.language
            if (null != language && language.length > 2) {
                language = StringUtils.Companion.cutString(language, 0, 2)
            }
            return StringUtils.Companion.getStringTrim(language)
        }

        private fun getLocaleCountry(locale: Locale): String {
            var country = locale.country
            if (null != country && country.length > 2) {
                country = StringUtils.Companion.cutString(country, 0, 2)
            }
            return StringUtils.Companion.getStringTrim(country)
        }

        /**
         * @return 当前应用的语言是否是简体中文
         */
        fun isSimplifiedChinese(context: Context): Boolean {
            return Locale.SIMPLIFIED_CHINESE == getAppLocale(context)
        }

        /**
         * 设置应用语言为简体中文
         */
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        fun setSimplifiedChinese(context: Context) {
            setAppLocale(context, Locale.SIMPLIFIED_CHINESE)
        }

        /**
         * 设置应用所处的语言
         *
         * @param locale 需要切换的语言
         */
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        fun setAppLocale(context: Context, locale: Locale?) {
            val resource = context.resources
            val config = resource.configuration
            config.setLocale(locale)
            resource.updateConfiguration(config, null)
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}