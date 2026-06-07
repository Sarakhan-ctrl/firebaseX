package com.authentication.firebaseauth.presentation.components.dumbActivity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.authentication.firebaseauth.R
import com.authentication.firebaseauth.data.googleAuthUiClient.MyGoogleAuthUiClient
import com.authentication.firebaseauth.presentation.components.homePage.HomePage
import com.authentication.firebaseauth.presentation.components.SignInScreen
import com.authentication.firebaseauth.presentation.components.homePage.AddImage
import com.authentication.firebaseauth.presentation.components.publishScreen.PublishScreen
import com.authentication.firebaseauth.presentation.viewmodels.MyFeedVM

@Composable
fun Navigation() {
    val context = LocalContext.current.applicationContext
    val sharedViewModel: MyFeedVM = viewModel()
    val webClientId = stringResource(id = R.string.web_client_id)
    val credentialManager = CredentialManager.create(context)

     val googleAuthUiClient= remember {                                           // Initialize your Bouncer here so the whole app can use it
        MyGoogleAuthUiClient(
            webClientId= webClientId,
            credentialManager= credentialManager
        )
    }

    val currentUser=googleAuthUiClient.getSignedInUser()
    val navController = rememberNavController()
    val startingFolder=if(currentUser!=null) Routes.APP_PAGES else Routes.AUTH_GRAPH
    NavHost(
        navController = navController,
        startDestination = startingFolder,                                                          // Where the app starts
    ) {
        // grp 1
        navigation(
            startDestination = Routes.SIGN_IN,
            route =Routes.AUTH_GRAPH                                                                // The name of this group
        ) {
            composable(Routes.SIGN_IN) {
                SignInScreen(
                    authClient = googleAuthUiClient,
                    onSignInSuccess = {
                        navController.navigate(Routes.MAIN_FEED) {         // When login succeeds, navigate to the main feed!
                            popUpTo(Routes.AUTH_GRAPH) { inclusive = true }                   // Clear the back stack so they can't hit "back" to go to the login screen
                        }
                    }
                )
            }
        }

        // grp 2
        navigation(
            startDestination = Routes.MAIN_FEED,
            route = Routes.APP_PAGES                                                                 // The name of this group
        ) {
            composable(Routes.MAIN_FEED) {
                HomePage(navController,imageViewModel = sharedViewModel)
            }
            composable(Routes.ADD_IMAGE) {
                AddImage(navController,imageViewModel = sharedViewModel)
            }
            composable("publish_screen")
            {
                PublishScreen(localImageUri = sharedViewModel.imageUriToPublish,
                    imageViewModel = sharedViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
