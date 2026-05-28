package com.authentication.firebaseauth.data


import android.annotation.SuppressLint
import android.content.Context
import androidx.core.net.toUri
import com.authentication.firebaseauth.domain.ImageRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.UUID

// 1. We added 'context' here so the phone can read the image file!
class FirebaseImageRepository(@SuppressLint("StaticFieldLeak") private val context: Context) : ImageRepository {

    // Notice: firebaseStorage is GONE. We only have Firestore now!
    private val firestore = Firebase.firestore
    private val collectionName = "wallpapers"

    override suspend fun uploadImage(uriString: String, tags: List<String>): WallpaperData {
        return withContext(Dispatchers.IO) {
            val uri = uriString.toUri()
            val fileName = UUID.randomUUID().toString()

            // 1. Read the image from the phone
            val inputStream = context.contentResolver.openInputStream(uri)
            val imageBytes = inputStream?.readBytes() ?: throw Exception("Could not read image")

            // 2. Pack the box for Cloudinary using your VIP pass
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("upload_preset", "Xenox_app") // Your Cloudinary Preset!
                .addFormDataPart(
                    "file",
                    fileName,
                    imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                )
                .build()

            // 3. Send it to Cloudinary!
            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/drms0qzmy/image/upload") // Your Cloud Name!
                .post(requestBody)
                .build()

            val client = OkHttpClient()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                throw Exception("Cloudinary Upload Failed!")
            }

            // 4. Get the URL Receipt from Cloudinary
            val responseData = response.body?.string()
            val jsonObject = JSONObject(responseData ?: "")
            val fileUrl = jsonObject.getString("secure_url")

            // 5. Save the data to Firestore Database (This remains unchanged!)
            val newWallpaper = WallpaperData(
                name = fileName,
                url = fileUrl,
                tags = tags,
                trendScore = 0
            )
            firestore.collection(collectionName).document(fileName).set(newWallpaper).await()

            return@withContext newWallpaper
        }
    }

    override suspend fun fetchImg(): List<WallpaperData> {
        // This stays exactly the same! Firestore is still handling our data.
        val snapshot = try {
            firestore.collection(collectionName).get().await()
        } catch (_: Exception) {
            return emptyList()
        }
        return snapshot.documents.mapNotNull { document ->
            document.toObject(WallpaperData::class.java)
        }
    }

    override suspend fun deleteImage(wallpaper: String) {
        // We only delete the database entry now.
        // Cloudinary keeps the image, but since you have 25GB free, we don't worry about it!
        firestore.collection(collectionName).document(wallpaper).delete().await()
    }
}