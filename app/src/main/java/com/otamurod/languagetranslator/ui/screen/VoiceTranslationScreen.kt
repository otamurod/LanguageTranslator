package com.otamurod.languagetranslator.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.otamurod.languagetranslator.R
import com.otamurod.languagetranslator.ui.model.MLKitModelStatus
import com.otamurod.languagetranslator.ui.model.SpeechRecognizerContract
import com.otamurod.languagetranslator.ui.theme.Purple700
import java.util.*

@Preview
@Composable
fun PreviewVoiceTranslationScreen() {
    VoiceTranslationScreen()
}

private var translation = mutableStateOf("Translation...")
private const val TAG = "VoiceTranslationScreen"
private lateinit var context: Context
private var textToSpeech: TextToSpeech? = null

@Composable
fun VoiceTranslationScreen() {
    context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission Accepted: Do something
            Log.d(TAG, "PERMISSION GRANTED")

        } else {
            // Permission Denied: Do something
            Log.d(TAG, "PERMISSION DENIED")
        }
    }

    val speechRecognizerLauncher =
        rememberLauncherForActivityResult(contract = SpeechRecognizerContract(), onResult = {
            translation.value = it.toString()
        })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFE1F5FE)),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(elevation = 4.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp, 10.dp, 15.dp, 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Create DropdownMenu for selecting source language
                LanguageMenu("From", true)

                Image(
                    painterResource(id = R.drawable.ic_arrow_24),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(2.dp)
                        .width(32.dp)
                        .height(48.dp)
                )
                // Create DropdownMenu for selecting target language
                LanguageMenu("To", false)
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = Purple700)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon for Microphone
            Column(
                modifier = Modifier.wrapContentHeight(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(painterResource(id = R.drawable.ic_mic),
                    contentDescription = "Voice Translation",
                    modifier = Modifier
                        .padding(6.dp)
                        .height(120.dp)
                        .width(120.dp)
                        .graphicsLayer {
                            clip = true
                            shape = RoundedCornerShape(20.dp)
                        }
                        .clickable {
                            //check whether the permission is granted
                            if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.RECORD_AUDIO
                                )
                            ) {
                                //start recording
                                speechRecognizerLauncher.launch(Unit)
                            } else {
                                //request permission
                                launcher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        })
                Text(
                    text = "Speak",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 20.dp)
                )
            }
            //Translate Button
            Button(modifier = Modifier.align(Alignment.Bottom), onClick = {
                translatorBuilder(context)
            }) {
                Text(text = "Translate")
            }
            // Icon for Speaker
            Column(
                modifier = Modifier.wrapContentHeight(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(painterResource(id = R.drawable.ic_speaker),
                    contentDescription = "Voice Translation",
                    modifier = Modifier
                        .padding(6.dp)
                        .height(120.dp)
                        .width(120.dp)
                        .graphicsLayer {
                            clip = true
                            shape = RoundedCornerShape(20.dp)
                        }
                        .clickable {
                            textToSpeech(context)
                        })
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Listen",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 20.dp)
                )
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = Purple700)
        )
        val scroll = rememberScrollState(0)
        SelectionContainer(
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFFE1F5FE))
        ) {
            Text(
                text = translation.value,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 5.dp, 10.dp)
                    .background(Color.White)
                    .verticalScroll(scroll)
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
        )
    }
}

fun startTranslation() {
    translator.translate(translation.value).addOnSuccessListener { translated ->
        Log.d(TAG, "translate: $translated")
        translation.value = translated
    }.addOnFailureListener { it.printStackTrace() }

    //result
    textToSpeech(context)
}

fun translatorBuilder(context: Context) {
    val translatorOptions = TranslatorOptions.Builder().setSourceLanguage(sourceLanguageCode)
        .setTargetLanguage(targetLanguageCode).build()
    translator = Translation.getClient(translatorOptions)
    Log.d(TAG, "VoiceTranslationScreen: Built!!!")
    isModelDownloaded(context)
    translate(translation.value)
}

fun isModelDownloaded(context: Context) {
    Log.d(TAG, "VoiceTranslationScreen: Checking...")
    toast(context, MLKitModelStatus.CheckingDownload)
    val downloadConditions = DownloadConditions.Builder().build()

    translator.downloadModelIfNeeded(downloadConditions).addOnSuccessListener {
        toast(context, MLKitModelStatus.Downloaded)
        startTranslation()
    }.addOnCompleteListener {

    }.addOnFailureListener {
        it.printStackTrace()
    }
}

fun textToSpeech(context: Context) {
    textToSpeech = TextToSpeech(
        context
    ) {
        if (it == TextToSpeech.SUCCESS) {
            textToSpeech?.let { txtToSpeech ->
                txtToSpeech.language = Locale.forLanguageTag(targetLanguageCode)
                txtToSpeech.setSpeechRate(1.0f)
                txtToSpeech.speak(
                    translation.value,
                    TextToSpeech.QUEUE_ADD,
                    null,
                    null
                )
            }
        }
    }
}