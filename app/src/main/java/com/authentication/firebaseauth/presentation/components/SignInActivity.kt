package com.authentication.firebaseauth.presentation.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.authentication.firebaseauth.ui.theme.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.authentication.firebaseauth.R
import com.authentication.firebaseauth.data.googleAuthUiClient.MyGoogleAuthUiClient
import com.authentication.firebaseauth.presentation.animations.logIn.MarqueeBackground
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(authClient: MyGoogleAuthUiClient, onSignInSuccess: () -> Unit)  // This is the navigation trigger!
{
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope() // Required for suspend functions
    MarqueeBackground()
    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.weight(0.7f))
        Image(
            painter = painterResource(id = R.drawable.login_xenox),
            contentDescription = "Xenox Login LOGO",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.drawable.xenox_svg_icon),
            contentDescription = "Xenox LOGO"
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Login to unlock all features",
            fontSize = 22.sp,
            color = Color(Stroke.value)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = painterResource(id = R.drawable.get_bonus_coin),
            contentDescription = "get bonus coin"
        )
        Spacer(modifier = Modifier.height(24.dp))


        Button(
            modifier = Modifier.fillMaxWidth(0.8f),
            enabled = !isLoading,
            onClick = {
                isLoading = true   // Turn the animation on
                // When the button is clicked, launch the background work
                coroutineScope.launch {
                    val result = authClient.signIn(context)
                    isLoading = false // Turn the animation off
                    if (result.data != null) {
                        // Success! Trigger the navigation to the next screen
                        Toast.makeText(context, "Welcome to Xenox!", Toast.LENGTH_SHORT)
                            .show()
                        onSignInSuccess()
                    } else {
                        // Failed or cancelled. Show the error.
                        Toast.makeText(context, "Login Failed!", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        )
        {
            if (isLoading) {
                // Show a tiny spinner inside the button
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = "Sign In With Google")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Skip",
            fontSize = 12.sp,
            fontStyle = FontStyle.Italic,
            color = Color(Stroke.value)
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}