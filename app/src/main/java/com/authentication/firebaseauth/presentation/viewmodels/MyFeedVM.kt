package com.authentication.firebaseauth.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.authentication.firebaseauth.data.WallpaperData
import com.authentication.firebaseauth.presentation.states.FeedState
import com.authentication.firebaseauth.presentation.intents.FeedIntent
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

// logic like CRUD
class MyFeedVM: ViewModel() {
    private val firebaseStorage= Firebase.storage.reference   // initializing firebase storage
    private val _state=MutableStateFlow(FeedState())
    val state=_state.asStateFlow()

    fun uploadImg(uri: Uri){                                                                         // Uniform Resource Identifier: a path pointing to where the image is present on the users phone (it handles the path to firebase and says "go fetch it")
        val fileName= UUID.randomUUID().toString()                                           // store the file name in a variable -> UUID generate code for each image so make sure its random and doesn't repeat or override another image"s code
        val fileLocation= firebaseStorage.child(fileName)                 // store the file location in a variable//    (.child("images")-> look for a folder, if not then create an empty one so i can store in it..)
        viewModelScope.launch {
            _state.value=state.value.copy(isLoading = true, error = null)
            try {
                _state.update { it.copy(isLoading = true) }                                          // show spinner
                fileLocation.putFile(uri).await()                                                    // put the file  in that location and wait till it finishes
                val fileUrl= fileLocation.downloadUrl.await()                                   // get the url of the file
                val newImage= WallpaperData(fileName,fileUrl.toString())
                _state.update {
                    it->it.copy(imagesList=listOf(newImage)+it.imagesList)                         // add the file to the stateflow and at top
                }

            }catch (e:Exception){
                _state.update { it.copy(error = e.message) }
            }finally {
                                                                                                     //Whether it succeeds or fails, turn off the loading spinner!
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

    init {
        onIntentEvent(FeedIntent.LoadFeed)
    }
    fun onIntentEvent(intent: FeedIntent){
        when(intent){
            is FeedIntent.LoadFeed->fetchImg()
            is FeedIntent.DeleteImage -> deleteImg(intent.wallpaper)
        }
    }
    fun fetchImg() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val allImages = firebaseStorage.listAll().await()
                                                                                                    //                val imgUrls = allImgs.items.map {
                                                                                                    //                    it.downloadUrl.await()
                                                                                                    //                }
                val imgUrls = allImages.items.mapNotNull {
                   try {
                       val url=it.downloadUrl.await().toString()
                       WallpaperData(it.name,url)                                 // name is from WallpaperData class
                   }catch (e:Exception){
                       null
                   }
                }
                _state.update {
                    it.copy(imagesList = imgUrls.sortedByDescending { it.name }) }

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
                firebaseStorage.child(wallpaper.name).delete().await()
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