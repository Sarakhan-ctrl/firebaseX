package com.authentication.firebaseauth.presentation.states

import com.authentication.firebaseauth.data.WallpaperData

data class FeedState(
    val isLoading: Boolean = false,             // for spinner
    val imagesList: List<WallpaperData> = emptyList(),    // stateflow: Holds value & whenever an image is added, it notifies the page to add that new image
    val error: String? = null
)
