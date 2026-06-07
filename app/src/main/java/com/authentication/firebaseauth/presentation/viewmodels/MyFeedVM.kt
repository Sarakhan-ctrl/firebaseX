package com.authentication.firebaseauth.presentation.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.authentication.firebaseauth.data.WallpaperData
import com.authentication.firebaseauth.data.FirebaseImageRepository
import com.authentication.firebaseauth.domain.ImageRepository
import com.authentication.firebaseauth.presentation.states.FeedState
import com.authentication.firebaseauth.presentation.intents.FeedIntent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// logic like CRUD
class MyFeedVM(application: Application): AndroidViewModel(application) {
    var imageUriToPublish by mutableStateOf<String>("")

    private val repository = FirebaseImageRepository(application.applicationContext)
    private val _state=MutableStateFlow(FeedState())
    val state=_state.asStateFlow()

    init {
        onIntentEvent(FeedIntent.LoadFeed)
    }
    fun onIntentEvent(intent: FeedIntent){
        when(intent){
            is FeedIntent.LoadFeed->fetchImage()
            is FeedIntent.DeleteImage -> deleteImg(intent.wallpaper)
            is FeedIntent.UploadImage -> uploadImg(intent.wallpaper,intent.finalTagList)
        }
    }

    fun uploadImg(uriString: String, tags: List<String>){                                                               // Uniform Resource Identifier: a path pointing to where the image is present on the users phone (it handles the path to firebase and says "go fetch it")

        viewModelScope.launch {
            _state.value=state.value.copy(isLoading = true, error = null)
            try {
                val newImage= repository.uploadImage(uriString,tags)
                _state.update {
                    it->it.copy(
                    imagesList=listOf(newImage)+it.imagesList,
                    isLoading = false
                    )                                                                               // add the file to the stateflow and at top
                }
            }catch (e:Exception){
                _state.update { it.copy(error = e.message) }
            }finally {                                                                              //Whether it succeeds or fails, turn off the loading spinner!
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

   /* fun uploadImg(uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(1000)
            val fakeImage = WallpaperData(
                name = "fake_image_${UUID.randomUUID()}",
                url = uri.toString() // Coil will read this local phone path perfectly!
            )
            // 3. Add it to the UI
            _state.update { currentState ->
                currentState.copy(
                    imagesList = listOf(fakeImage) + currentState.imagesList,
                    isLoading = false
                )
            }
        }
    }*/

    fun fetchImage() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val newImage= repository.fetchImg()
                _state.update {
                    it.copy(
                        imagesList = newImage.sortedByDescending { img -> img.name },
                        isLoading = false
                    )
                }
            }catch (e:Exception){
                _state.update { it.copy(error = e.message) }
            }
            finally {
                _state.update { it.copy(isLoading = false, error = null) }
            }
        }
    }

    /* fun fetchImg() {
        viewModelScope.launch {
            // 1. Show the loading spinner
            _state.update { it.copy(isLoading = true, error = null) }

            // 2. Pretend we are checking the internet for half a second
            delay(500)

            // 3. Return an empty list (since we are just testing locally)
            _state.update {
                it.copy(
                    isLoading = false,
                    imagesList = emptyList()
                )
            }
        }
    }*/
    fun deleteImg(wallpaper: WallpaperData){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                repository.deleteImage(wallpaper.name)
                _state.update {
                    it.copy(imagesList = it.imagesList.filter { it.name != wallpaper.name })
                }
            }catch (e:Exception){
                _state.update { it.copy(error = e.message) }
            }finally {
                _state.update { it.copy(isLoading = false, error = null) }
            }
        }
    }
}