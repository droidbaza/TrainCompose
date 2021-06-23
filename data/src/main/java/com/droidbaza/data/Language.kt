package com.droidbaza.data

import android.annotation.SuppressLint
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*


@JsonClass(generateAdapter = true)
data class Language(
    @Json(name = "english_name") val englishName: String,
    @Json(name = "iso_639_1") val iso6391: String,
    @Json(name = "name") val name: String
) {
    companion object {
        @SuppressLint("ConstantLocale")
        val default = Language(
            englishName = Locale.getDefault().displayLanguage,
            iso6391 = Locale.getDefault().language,
            name = Locale.getDefault().displayLanguage
        )
    }
}

inline val Language.flagUrl get() = "https://www.unknown.nu/flags/images/$iso6391-100"
