package com.authentication.firebaseauth.presentation.components.homePage

import android.content.Intent
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.authentication.firebaseauth.presentation.viewmodels.MyFeedVM

@Composable
fun AddImage(navController: NavHostController ,imageViewModel: MyFeedVM) {

    val context = LocalContext.current
    val feedState by imageViewModel.state.collectAsState()                                // watch the state

    val galleryLauncher = rememberLauncherForActivityResult(  // open gallery to fetch images
        ActivityResultContracts.StartActivityForResult()                                   // used to open the gallery and get the image
    ) {
        result ->
        val uri = result.data?.data                                                            // get the uri of the image
        if (uri != null) {                                                                          // if the uri is not null, upload the image
            imageViewModel.uploadImg(uri.toString())

        }
    }
    LaunchedEffect(feedState.isLoading) {
        if (!feedState.isLoading && feedState.imagesList.isNotEmpty()) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(Unit) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)   // intent to open the gallery
        galleryLauncher.launch(intent)                                                       //  launch the intent
    }
}