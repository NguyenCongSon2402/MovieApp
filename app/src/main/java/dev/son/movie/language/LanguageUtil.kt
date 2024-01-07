package com.mobiai.base.language

import android.content.Context
import android.content.res.Configuration
import android.text.TextUtils
import dev.son.movie.data.local.SharedPreferenceUtils
import java.util.*

object LanguageUtil {

    private fun saveLocale(lang: String?) {
        SharedPreferenceUtils.languageCode = lang
    }

    fun setupLanguage(context: Context) {
        var languageCode = SharedPreferenceUtils.languageCode
        if (TextUtils.isEmpty(languageCode)) languageCode = Locale.getDefault().language
// Kiểm tra nếu languageCode là rỗng thì đặt mặc định là "en" (tiếng Anh)
//        if (TextUtils.isEmpty(languageCode)) {
//            languageCode = "en"
//        }
        val config = Configuration()
        val locale = languageCode?.let { Locale(it) }
        if (locale != null) {
            Locale.setDefault(locale)
        }
        config.locale = locale
        context.resources
            .updateConfiguration(config, null)
    }

    fun changeLang(lang: String, context: Context) {
        if (lang.equals("", ignoreCase = true)) return
        val myLocale = Locale(lang)
        saveLocale(lang)
        Locale.setDefault(myLocale)
        val config = Configuration()
        config.locale = myLocale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

}
