package com.authentication.firebaseauth.presentation.animations.logIn

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.authentication.firebaseauth.R
import com.authentication.firebaseauth.ui.theme.BlackBG

@Composable
fun MarqueeBackground(){

    val imgList1 = listOf(R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4)
    val imgList2 = listOf(R.drawable.img5, R.drawable.img6, R.drawable.img7, R.drawable.img8)

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val dynamicCardWidth = screenWidth / 3.5f
    val dynamicCardHeight = screenHeight / 4.0f


    val totalWidthOfSet1 = (dynamicCardWidth.value) * imgList1.size
    val totalWidthOfSet2 = (dynamicCardWidth.value) * imgList2.size

    val infiniteTransition = rememberInfiniteTransition(label = "marquee")             //<----------

    val animatedMasterNumber1 by infiniteTransition.animateFloat(              //<----------
        initialValue = 0f,
        targetValue = -totalWidthOfSet1,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset1"
    )

    val animatedMasterNumber2 by infiniteTransition.animateFloat(
        initialValue = -totalWidthOfSet2,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset2"
    )
    Box(modifier = Modifier.fillMaxSize().background(Color(BlackBG.value))) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().scale(1.1f).rotate(-3.5f)
        )
        {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .wrapContentWidth(unbounded = true, align = Alignment.Start)
                    .offset {
                        IntOffset(
                            x = animatedMasterNumber1.dp.roundToPx(),
                            y = 0
                        )
                    }     //<----------
            ) {
                imgList1.forEach { image ->
                    ImageCard(
                        image,
                        modifier = Modifier.width(dynamicCardWidth).height(dynamicCardHeight)
                    )
                }
                imgList1.forEach { image ->
                    ImageCard(
                        image,
                        modifier = Modifier.width(dynamicCardWidth).height(dynamicCardHeight)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .wrapContentWidth(unbounded = true, align = Alignment.Start)
                    .offset {
                        IntOffset(
                            x = animatedMasterNumber2.dp.roundToPx(),
                            y = 0
                        )
                    }       //<----------
            ) {
                imgList2.forEach { image ->
                    ImageCard(
                        image,
                        modifier = Modifier.width(dynamicCardWidth).height(dynamicCardHeight)
                    )
                }
                imgList2.forEach { image ->
                    ImageCard(
                        image,
                        modifier = Modifier.width(dynamicCardWidth).height(dynamicCardHeight)
                    )
                }
            }
        }
    }
}