package com.authentication.firebaseauth.presentation.intents

import com.authentication.firebaseauth.data.WallpaperData

sealed class FeedIntent {
 object LoadFeed: FeedIntent()
 data class DeleteImage(val wallpaper: WallpaperData) : FeedIntent()
 data class UploadImage(val wallpaper: String, val finalTagList: List<String>) : FeedIntent()
}