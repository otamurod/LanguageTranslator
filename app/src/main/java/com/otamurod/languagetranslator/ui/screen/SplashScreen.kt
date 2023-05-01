package com.otamurod.languagetranslator.ui.screen

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.otamurod.languagetranslator.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Add animated logo here
        Image(
            painterResource(id = R.drawable.splash_screen_logo),
            contentDescription = "Text Translation",
            modifier = Modifier
                .height(240.dp)
                .width(240.dp)
                .scale(scale.value)
        )

        Spacer(modifier = Modifier.height(35.dp))

        Text(
            "Language Translator",
            modifier = Modifier.scale(scale.value),
            color = Color(0xFF311B92),
            fontSize = 40.sp,
            fontFamily = FontFamily.Cursive
        )

        // Animation
        LaunchedEffect(key1 = true) {
            scale.animateTo(
                targetValue = 1.0f,
                // tween Animation
                animationSpec = tween(
                    durationMillis = 1500,
                    easing = {
                        OvershootInterpolator(2.5f).getInterpolation(it)
                    })
            )
            // Set the delay time
            delay(2000L)
            navController.popBackStack()
            navController.navigate("main")
        }
    }
}