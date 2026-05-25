package com.authentication.firebaseauth.domain

import com.authentication.firebaseauth.data.WallpaperData

interface ImageRepository {
    suspend fun uploadImage(uriString: String, tags: List<String>): WallpaperData
    suspend fun fetchImg(): List<WallpaperData>
    suspend fun deleteImage(wallpaper: String)
}