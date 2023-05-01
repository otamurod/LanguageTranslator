package com.otamurod.languagetranslator.ui.screen

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

private var imageUri = mutableStateOf<Uri?>(null)
private var textChanged = mutableStateOf("Scanned text will appear here..")
private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

@Composable
fun TextRecognitionScreen() {
    //to select image, let's start new intent
    val selectImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            imageUri.value = uri
        })

    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .weight(3f)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                IconButton(
                    modifier = Modifier.align(Alignment.BottomStart),
                    onClick = {
                        selectImage.launch("image/*")
                    }) {
                    Icon(
                        Icons.Filled.Add,
                        "add",
                        tint = Color.Blue
                    )
                }

                if (imageUri.value != null) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = rememberImagePainter(
                            data = imageUri.value
                        ),
                        contentDescription = "image"
                    )
                    IconButton(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        onClick = {
                            val image = InputImage.fromFilePath(context, imageUri.value!!)
                            recognizeText(image)
                        }) {
                        Icon(
                            Icons.Filled.Search,
                            "scan",
                            tint = Color.Blue
                        )
                    }
                }

            }
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .weight(3f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFFE1F5FE))
            ) {
                val scroll = rememberScrollState(0)
                SelectionContainer(
                    modifier = Modifier
                        .background(Color(0xFFE1F5FE))
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .verticalScroll(scroll),
                        text = textChanged.value
                    )
                }
            }
        }
    }
}

fun recognizeText(image: InputImage) {
    recognizer.process(image)
        .addOnSuccessListener {
            textChanged.value = it.text
        }
        .addOnFailureListener {
            Log.e("TEXT_REC", it.message.toString())
        }
}