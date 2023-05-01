package com.otamurod.languagetranslator.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Preview
@Composable
fun PreviewMainScreen() {
    MainScreen()
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        // Toolbar
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Language Translator",
                        fontSize = 24.sp,
                        fontFamily = FontFamily.Cursive
                    )
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.White,
                elevation = 10.dp
            )
        },
        // Bottom Navigation Bar
        bottomBar = {
            BottomNavigation(backgroundColor = Color(0xffffffff),
                modifier = Modifier
                    .graphicsLayer {
                        clip = true
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                        shadowElevation = 2.2f
                    }
                    .height(70.dp),
                elevation = 8.dp) {
                BottomNavigationItem(icon = {
                    Image(
                        painterResource(id = com.otamurod.languagetranslator.R.drawable.ic_translate),
                        contentDescription = "Text Translation",
                        modifier = Modifier.padding(6.dp)
                    )
                }, label = {
                    Text(
                        "Text",
                        modifier = Modifier.padding(4.dp),
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Default,
                        fontStyle = FontStyle.Italic
                    )
                }, selected = true, onClick = {
                    navController.popBackStack()
                    navController.navigate("textTranslation")
                })
                BottomNavigationItem(icon = {
                    Image(
                        painterResource(id = com.otamurod.languagetranslator.R.drawable.ic_voice),
                        contentDescription = "Voice Translation",
                        modifier = Modifier.padding(6.dp)
                    )
                }, label = {
                    Text(
                        "Speak",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Default,
                        fontStyle = FontStyle.Italic
                    )
                }, selected = false, onClick = {
                    navController.popBackStack()
                    navController.navigate("voiceTranslation")
                })
                BottomNavigationItem(icon = {
                    Image(
                        painterResource(id = com.otamurod.languagetranslator.R.drawable.ic_image),
                        contentDescription = "Image Scanner",
                        modifier = Modifier.padding(6.dp)
                    )
                },
                    label = {
                        Text(
                            "Scan",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Default,
                            fontStyle = FontStyle.Italic
                        )
                    },
                    selected = false,
                    onClick = {
                        navController.popBackStack()
                        navController.navigate("handwritingTranslation")
                    })
            }
        }) {
        NavHost(navController, startDestination = "textTranslation") {
            composable("textTranslation") {
                TextTranslationScreen()
            }
            composable("voiceTranslation") {
                VoiceTranslationScreen()
            }
            composable("handwritingTranslation") {
                TextRecognitionScreen()
            }
        }
    }
}
