package com.authentication.firebaseauth.presentation.components.publishScreen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.authentication.firebaseauth.presentation.intents.FeedIntent
import com.authentication.firebaseauth.presentation.viewmodels.MyFeedVM

@Composable
fun PublishScreen(
    localImageUri: String, // The local phone path we get from the gallery!
    imageViewModel: MyFeedVM,
    onNavigateBack: () -> Unit
) {
    Log.d("NAV_TEST", "3. Publish Screen successfully opened! The URI is: $localImageUri")
    // 1. Create variables for your specific tags
    var tag1 by remember { mutableStateOf("") }
    var tag2 by remember { mutableStateOf("") }
    var tag3 by remember { mutableStateOf("") }

    val state by imageViewModel.state.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        // 2. Coil shows the image directly from the phone! (No Firebase needed yet)
        AsyncImage(
            model = localImageUri,
            contentDescription = "Preview",
            modifier = Modifier.fillMaxWidth().height(250.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        // 3.Tag boxes!
        OutlinedTextField(
            value = tag1, onValueChange = { tag1 = it },
            label = { Text("Tag 1 (e.g. Nature)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = tag2, onValueChange = { tag2 = it },
            label = { Text("Tag 2 (e.g. Dark)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = tag3, onValueChange = { tag3 = it },
            label = { Text("Tag 3 (e.g. Cars)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        // 4. The Publish Button
        Button(
            // Only click if at least first tag is filled
            enabled = tag1.isNotBlank() && !state.isLoading,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Bundle the separate tags into a list for the database!
                val finalTagList = listOf(tag1, tag2, tag3).filter { it.isNotBlank() }

                // Now we finally tell the ViewModel to upload it to Firebase!
                imageViewModel.onIntentEvent(
                    FeedIntent.UploadImage(localImageUri, finalTagList)
                )

                onNavigateBack() // Close this screen
            }
        ) {
            Text(if (state.isLoading) "Uploading..." else "Publish Image")
        }
    }
}