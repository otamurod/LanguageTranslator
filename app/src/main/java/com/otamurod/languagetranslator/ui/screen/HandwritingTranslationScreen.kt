package com.otamurod.languagetranslator.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun HandwritingTranslationScreen() {
    Column(
        modifier = Modifier.fillMaxSize().background(color = Color.Magenta),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Add a Canvas Composable for drawing
        // Add a Button Composable for initiating translation
        Text("HandwritingTranslationScreen")
    }
}