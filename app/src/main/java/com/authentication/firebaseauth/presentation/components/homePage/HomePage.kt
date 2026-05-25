package com.authentication.firebaseauth.presentation.components.homePage

import android.content.Intent
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.authentication.firebaseauth.R
import com.authentication.firebaseauth.data.WallpaperData
import com.authentication.firebaseauth.presentation.components.dumbActivity.Routes
import com.authentication.firebaseauth.presentation.intents.FeedIntent
import com.authentication.firebaseauth.presentation.viewmodels.MyFeedVM
import com.authentication.firebaseauth.ui.theme.BlackBG
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavHostController,imageViewModel: MyFeedVM) {

                                                                                                    //    val imageViewModel: MyFeedVM = viewModel()                                                    // 1.  Initializing viewmodel  .. doubtfull
    val myFeedState by imageViewModel.state.collectAsState()
    var currentData by remember { mutableStateOf<WallpaperData?>(null) }       // to extract the current state so it can be updated when required..(current value is null)

   /* LaunchedEffect(Unit) {
        imageViewModel.onIntentEvent(FeedIntent.LoadFeed)                                           // init is already used in VM!!
    } */

    val galleryLauncher=rememberLauncherForActivityResult(
         ActivityResultContracts.StartActivityForResult()){
            result->
        val uri=result.data?.data
        if(uri!=null && currentData!=null){
            imageViewModel.onIntentEvent(FeedIntent.DeleteImage(currentData!!))
            navController.navigate(Routes.PUBLISH_SCREEN)
//            imageViewModel.uploadImg(uri.toString(), listOf())
            currentData=null
        }
    }

    val hazeState = remember { HazeState() }                                              // 2.  The "Brain" that calculates the live blur
    Scaffold(topBar = {
        TopBar()
    })
    { paddingValues ->

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {                                                 // 2. A Box to stack the floating bar ON TOP of the scrolling feed

            // --- THE SCROLLABLE LIST ---
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(bottom = 12.dp),                                     // So the last item isn't hidden under the bar
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .haze(state = hazeState)
                    .background(Color(BlackBG.value))
            ) {
                items(myFeedState.imagesList){wallpaperData->
                    Box(modifier = Modifier.clickable{

                        currentData=wallpaperData
                        val intent=
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            galleryLauncher.launch(intent)
                    })
                    {
                        AsyncImage(model = wallpaperData.url ,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                            contentScale = ContentScale.Crop)
                        IconButton(onClick = {
                            imageViewModel.onIntentEvent(FeedIntent.DeleteImage(wallpaperData))
                        }){
                            Icon(painter = painterResource(id = R.drawable.delete), contentDescription = null)
                        }
                    }

                }
            }
            if (myFeedState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        // This dims the screen behind the spinner!
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 4.dp
                    )
                }
            }

            // --- THE FLOATING GLASS BAR ---
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter) // Pin to bottom
                    .padding(horizontal = 32.dp, vertical = 32.dp)
                    .fillMaxWidth()
                    .height(54.dp)
                    .hazeChild(                                                                     // 4. THE LIVE BLUR: Apply the calculated blur strictly inside the pill shape
                        state = hazeState,
                        shape = CircleShape,
                    )
                    .background(                                                                    // 5. The Frost Tint (Keep it very low opacity so the blur shines through!)
                        color = Color.Black.copy(alpha = 0.15f),
                        shape = CircleShape
                    )
                    .border(                                                                        // 6. The Crisp Glass Edge
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
                    .padding(vertical = 16.dp, horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.paint(painterResource(R.drawable.selected_bar))) {
                    Icon(
                        painterResource(R.drawable.wallpaper),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.clickable(onClick = { navController.navigate(Routes.IMAGE_SCREEN) })
                    )
                }
                
                Icon(
                    painterResource(R.drawable.star),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.clickable(onClick = { navController.navigate(Routes.APP_PAGES) })
                )
                Icon(
                    painterResource(R.drawable.search),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.clickable(onClick = { navController.navigate(Routes.APP_PAGES) })
                )
                Icon(
                    painterResource(R.drawable.user_account),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.clickable(onClick = { navController.navigate(Routes.APP_PAGES) })
                )
            }
            // --- THE FLOATING GLASS BAR ENDS HERE ---
        }
    }
}