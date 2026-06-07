package com.authentication.firebaseauth.data

data class WallpaperData(
    val name: String="",
    val url:String="",
    val tags: List<String> = emptyList(),
    val trendScore: Int = 0
)
