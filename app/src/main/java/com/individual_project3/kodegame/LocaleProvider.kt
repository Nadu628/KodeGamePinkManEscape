package com.individual_project3.kodegame


import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

val LocalAppLocale = staticCompositionLocalOf { Locale("en") }

var appLocaleState = mutableStateOf(Locale("en"))
