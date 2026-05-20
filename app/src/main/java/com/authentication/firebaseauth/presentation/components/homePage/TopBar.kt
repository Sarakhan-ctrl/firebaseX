package com.authentication.firebaseauth.presentation.components.homePage

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.authentication.firebaseauth.R
import com.authentication.firebaseauth.ui.theme.BlackBG

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }        // It keeps track of when the user's finger is touching the screen.
    TopAppBar(
        modifier = Modifier.height(68.dp),
        title = {
                Image(
                    painter = painterResource(id = R.drawable.xenox_svg_icon),
                    contentDescription = "Xenox logo",
                    Modifier
                        .size(84.dp)
                        .padding(start = 6.dp)
                        .clickable(interactionSource = interactionSource,
                            indication = null,onClick = {Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show()})
                )
        },
        actions = {
            Image(
                painter = painterResource(id = R.drawable.coin_svg_icon),
                contentDescription = "Xenox coin wallet",
                modifier = Modifier.size(55.dp).padding(end = 6.dp).clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show() })
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(BlackBG.value)
        )
    )
}