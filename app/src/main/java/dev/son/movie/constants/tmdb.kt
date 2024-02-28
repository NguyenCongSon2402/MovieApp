package com.netflixclone.constants

import dev.son.movie.network.models.countries.Countries

const val BASE_IMG = "https://img.ophim12.cc/uploads/movies/"
const val BASE_IMG_HEADER = "https://img.ophim9.cc"
const val COUNTRIES = "Countries"
const val CATEGORIES = "Categories"

enum class ImageSize(val value: String) {
    NORMAL("w500"),
    ORIGINAL("original"),
}
