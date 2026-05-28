//package com.authentication.firebaseauth.data
//
//import androidx.core.net.toUri
//import com.authentication.firebaseauth.domain.ImageRepository
//import com.google.firebase.Firebase
//import com.google.firebase.firestore.firestore
//import com.google.firebase.storage.storage
//import kotlinx.coroutines.tasks.await
//import java.util.UUID
//
//class FirebaseImageRepository : ImageRepository {
//
//    // We moved your Firebase storage reference here!
//    private val firebaseStorage = Firebase.storage.reference
//    // 1. We add the Firestore database reference!
//    private val firestore = Firebase.firestore
//    // The name of our "Drawer" in the database
//    private val collectionName = "wallpapers"                                                       // give a name to the document in the database
//
//    override suspend fun uploadImage(uriString: String, tags: List<String>): WallpaperData {
//        // fire storage
//        val uri = uriString.toUri()
//        val fileName= UUID.randomUUID().toString()                                           // store the file name in a variable -> UUID generate code for each image so make sure its random and doesn't repeat or override another image"s code
//        val fileLocation= firebaseStorage.child(fileName)                // store the file location in a variable//    (.child("images")-> look for a folder, if not then create an empty one so i can store in it..)
//        fileLocation.putFile(uri).await()                                                           // put the file  in that location and wait till it finishes
//        val fileUrl= fileLocation.downloadUrl.await().toString()                             // get the url of the file
//
//        // fire store
//        val newWallpaper = WallpaperData(
//            name = fileName,
//            url = fileUrl,
//            tags = tags,
//            trendScore = 0
//        )
//        firestore.collection(collectionName).document(fileName).set(newWallpaper).await()
//        // 3. Return the clean data class back to the ViewModel
//        return (newWallpaper)
//    }
//
//    override suspend fun fetchImg(): List<WallpaperData> {
//       /* val allImages = firebaseStorage.listAll().await()
//        val imgUrls = allImages.items.mapNotNull {
//            try {
//                val url = it.downloadUrl.await().toString()
//                WallpaperData(
//                    it.name,
//                    url
//                )                                 // name is from WallpaperData class
//            } catch (e: Exception) {
//                null
//            }
//        }*/
//
//        val snapshot =
//            try {
//                firestore.collection(collectionName).get().await()
//            }catch (_: Exception){
//                return emptyList()
//            }
//        return snapshot.documents.mapNotNull { document ->
//            document.toObject(WallpaperData::class.java)
//        }
//    }
//
//    override suspend fun deleteImage(wallpaper: String) {
//        firebaseStorage.child(wallpaper).delete().await()
//        firestore.collection(collectionName).document(wallpaper).delete().await()
//    }
//}