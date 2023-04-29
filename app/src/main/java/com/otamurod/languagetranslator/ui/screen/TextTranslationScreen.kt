package com.otamurod.languagetranslator.ui.screen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.otamurod.languagetranslator.R
import com.otamurod.languagetranslator.ui.model.MLKitModelStatus
import com.otamurod.languagetranslator.ui.model.ModelLanguage
import com.otamurod.languagetranslator.ui.theme.Purple700
import java.util.*

private var languageArrayList: ArrayList<ModelLanguage>? = null
var sourceLanguageCode = "ko"
var sourceLanguageTitle = "Korean"
var targetLanguageCode = "en"
var targetLanguageTitle = "English"
private const val TAG = "TextTranslationScreen"
private val outputText = mutableStateOf("Translation...")
lateinit var translator: Translator

@Preview
@Composable
fun PreviewTextTranslationScreen() {
    TextTranslationScreen()
}

@Composable
fun TextTranslationScreen() {
    val context = LocalContext.current

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

        // Creating a variable to store the editable TextField value
        var inputText by remember { mutableStateOf("") }

        Box(contentAlignment = Alignment.Center) {

            Column {
                // Add an EditText Composable for entering text to translate
                TextField(value = inputText,
                    onValueChange = {
                        inputText = it
                    },
                    label = { Text("Type...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(275.dp),
                    textStyle = TextStyle(fontSize = 18.sp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        placeholderColor = Color.LightGray,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    placeholder = {
                        Text(
                            text = "Enter text",
                            fontSize = 18.sp,
                        )
                    })

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(color = Color(0xFFE1F5FE))
                )
                val scroll = rememberScrollState(0)
                SelectionContainer(modifier = Modifier.background(color = Color(0xFFE1F5FE))) {
                    Text(
                        text = outputText.value,
                        fontSize = 20.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(275.dp)
                            .padding(16.dp)
                            .verticalScroll(scroll)
                    )
                }
            }

            // Add a Button Composable for initiating translation
            Button(onClick = {
                buildTranslator(context, inputText)
                Log.d(TAG, "TextTranslationScreen: clicked")
            }) {
                Text(text = "Translate")
            }
        }
    }
}

fun loadAvailableLanguages() {
    languageArrayList = ArrayList()
    val languageCodeList = TranslateLanguage.getAllLanguages()
    for (languageCode in languageCodeList) {
        val languageTitle = Locale(languageCode).displayLanguage
        val modelLanguage = ModelLanguage(languageCode, languageTitle)
        languageArrayList!!.add(modelLanguage)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LanguageMenu(label: String, isForSource: Boolean) {
    loadAvailableLanguages()
    val options = mutableListOf<String>()
    for (language in languageArrayList!!) {
        options.add(language.languageTitle)
    }
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(if (isForSource) sourceLanguageTitle else targetLanguageTitle) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {
        expanded = !expanded
    }, modifier = Modifier
        .width(160.dp)
        .graphicsLayer {
            clip = true
            shape = RoundedCornerShape(15.dp)
        }) {
        TextField(readOnly = true,
            value = selectedOptionText,
            onValueChange = { },
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                textColor = Color.Black, backgroundColor = Color(
                    0xFFB2EBF2
                )
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            options.forEach { selectionOption ->
                DropdownMenuItem(onClick = {
                    selectedOptionText = selectionOption
                    expanded = false
                    if (isForSource) {
                        val selectedLanguage =
                            languageArrayList!!.filter { language -> language.languageTitle == selectionOption }
                        sourceLanguageCode = selectedLanguage[0].languageCode
                        sourceLanguageTitle = selectedLanguage[0].languageTitle
                        Log.d(
                            TAG, "LanguageMenu: source: $sourceLanguageCode :: $sourceLanguageTitle"
                        )
                    } else {
                        val selectedLanguage =
                            languageArrayList!!.filter { language -> language.languageTitle == selectionOption }
                        targetLanguageCode = selectedLanguage[0].languageCode
                        targetLanguageTitle = selectedLanguage[0].languageTitle
                        Log.d(
                            TAG, "LanguageMenu: target: $targetLanguageCode :: $targetLanguageTitle"
                        )
                    }
                }) {
                    Text(text = selectionOption) //show selected option
                }
            }
        }
    }
}

fun buildTranslator(context: Context, inputText: String) {
    val translatorOptions = TranslatorOptions.Builder().setSourceLanguage(sourceLanguageCode)
        .setTargetLanguage(targetLanguageCode).build()
    translator = Translation.getClient(translatorOptions)
    Log.d(TAG, "TextTranslationScreen: Built!!!")
    checkIfModelIsDownloaded(context, inputText)
    translate(inputText)
}

fun checkIfModelIsDownloaded(context: Context, inputText: String) {
    Log.d(TAG, "TextTranslationScreen: Checking...")
    toast(context, MLKitModelStatus.CheckingDownload)
    val downloadConditions = DownloadConditions.Builder().build()

    translator.downloadModelIfNeeded(downloadConditions).addOnSuccessListener {
        toast(context, MLKitModelStatus.Downloaded)
        translate(inputText)

    }.addOnCompleteListener {

    }.addOnFailureListener {
        it.printStackTrace()
    }
}

fun toast(context: Context, status: MLKitModelStatus) {
    Toast.makeText(context, "$status", Toast.LENGTH_SHORT).show()
}

fun translate(inputText: String) {
    translator.translate(inputText).addOnSuccessListener { translated ->
        Log.d(TAG, "translate: $translated")
        outputText.value = translated
    }.addOnFailureListener { it.printStackTrace() }
}