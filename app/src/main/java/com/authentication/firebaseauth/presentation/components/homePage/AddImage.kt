package com.authentication.firebaseauth.presentation.components.homePage

import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.authentication.firebaseauth.presentation.viewmodels.MyFeedVM
import android.content.Intent
import androidx.navigation.NavHostController
import com.authentication.firebaseauth.presentation.components.dumbActivity.Routes

@Composable
fun AddImage(navController: NavHostController, imageViewModel: MyFeedVM) {

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val uri = result.data?.data

        if (uri != null) {
            // 1. Save the URI to the Shared ViewModel!
            imageViewModel.imageUriToPublish = uri.toString()

            // 2. Navigate to the Publish Screen!
            navController.navigate("publish_screen") {
                // This removes 'AddImage' from the back queue so hitting the physical back button
                // on the Publish screen doesn't accidentally reopen the gallery!
                popUpTo(Routes.ADD_IMAGE) { inclusive = true }
            }
        } else {
            // 3. If the user hit "Cancel" in the gallery, don't leave them on a blank screen.
            // Send them back to the Home Page.
            navController.popBackStack()
        }
    }

    // Launch the gallery immediately when the user clicks the bottom bar icon
    LaunchedEffect(Unit) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }
}