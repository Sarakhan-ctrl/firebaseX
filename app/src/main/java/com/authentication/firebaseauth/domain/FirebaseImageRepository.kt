package com.authentication.firebaseauth.domain

import android.net.Uri
import com.authentication.firebaseauth.data.WallpaperData
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseImageRepository : ImageRepository {

    // We moved your Firebase storage reference here!
    private val firebaseStorage = Firebase.storage.reference

    override suspend fun uploadImage(uriString: String): WallpaperData {
        // 1. Convert the pure Kotlin String back into an Android Uri
        val uri = Uri.parse(uriString)

        val fileName= UUID.randomUUID().toString()                                           // store the file name in a variable -> UUID generate code for each image so make sure its random and doesn't repeat or override another image"s code
        val fileLocation= firebaseStorage.child(fileName)                 // store the file location in a variable//    (.child("images")-> look for a folder, if not then create an empty one so i can store in it..)
        // 2. Do the Firebase heavy lifting
        fileLocation.putFile(uri).await()                                                           // put the file  in that location and wait till it finishes
        val fileUrl= fileLocation.downloadUrl.await()                                          // get the url of the file


        // 3. Return the clean data class back to the ViewModel
        return WallpaperData(fileName, fileUrl.toString())
    }

    override suspend fun fetchImg(): List<WallpaperData> {
        val allImages = firebaseStorage.listAll().await()
        val imgUrls = allImages.items.mapNotNull {
            try {
                val url = it.downloadUrl.await().toString()
                WallpaperData(
                    it.name,
                    url
                )                                 // name is from WallpaperData class
            } catch (e: Exception) {
                null
            }
        }
        return imgUrls
    }

    override suspend fun deleteImage(wallpaper: String) {
        firebaseStorage.child(wallpaper).delete().await()
    }
}