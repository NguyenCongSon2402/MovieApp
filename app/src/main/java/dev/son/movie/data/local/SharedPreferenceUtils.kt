package dev.son.movie.data.local

import dev.son.movie.TrackingApplication


object SharedPreferenceUtils {
    private const val LANGUAGE = "LANGUAGE"


    var languageCode: String?
        get() = TrackingApplication.instanceSharePreference.getValue(LANGUAGE, null)
        set(value) = TrackingApplication.instanceSharePreference.setValue(LANGUAGE, value)

}