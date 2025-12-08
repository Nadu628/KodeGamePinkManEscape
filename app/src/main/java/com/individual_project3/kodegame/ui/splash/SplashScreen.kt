package com.individual_project3.kodegame.ui.splash


import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.os.LocaleListCompat
import com.individual_project3.kodegame.R
import androidx.navigation.NavController
import com.individual_project3.kodegame.assets.audio.AudioManager
import com.individual_project3.kodegame.ui.theme.bubbleFont
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.compose.ui.res.stringResource


@Composable
fun SplashScreen(navController: NavController) {
    val bubbleFont = FontFamily(Font(R.font.poppins_bold))
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xffb3e5fc), Color(0xffb2ff59))
    )

    var showWelcome by remember { mutableStateOf(true) }
    var showPrompt by remember { mutableStateOf(false) }
    var fadeOut by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (fadeOut) 0f else 1f,
        animationSpec = tween(600),
        label = "fade"
    )

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showLanguageDialog by remember { mutableStateOf(false) }
    val audio = remember { AudioManager(context) }

// load just click sound
    LaunchedEffect(Unit) {
        audio.loadSfx(R.raw.sfx_button_click)
    }


    LaunchedEffect(Unit) {
        delay(3000)
        showWelcome = false
        showPrompt = true
    }

    fun handleTap() {
        if (!showPrompt) return
        audio.play(R.raw.sfx_button_click)
        fadeOut = true

        scope.launch {
            delay(700)
            navController.navigate("pick_user_screen") {
                popUpTo("splash_screen") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .alpha(alpha)
            .clickable { handleTap() },
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            IdleCharacter(
                modifier = Modifier.size(90.dp),
                frameDelayMs = 150L
            )

            Spacer(Modifier.height(32.dp))

            when {
                showWelcome -> Text(
                    stringResource(R.string.welcome),
                    fontSize = 26.sp,
                    fontFamily = bubbleFont,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                showPrompt -> Text(
                    stringResource(R.string.touch_screen),
                    fontSize = 24.sp,
                    fontFamily = bubbleFont,
                    color = Color.White
                )
            }
        }
        if (showLanguageDialog) {
            LanguagePickerDialog(
                onDismiss = { showLanguageDialog = false },
                onLanguageSelected = { locale ->
                    updateAppLocale(locale)
                    showLanguageDialog = false
                }
            )
        }

    }
}


@Composable
fun LanguagePickerButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            stringResource(R.string.language),
            color = Color.Black,
            fontSize = 16.sp,
            fontFamily = bubbleFont
        )
    }
}


@Composable
fun LanguagePickerDialog(
    onDismiss: () -> Unit,
    onLanguageSelected: (Locale) -> Unit
) {
    val languages = listOf(
        "English" to Locale("en"),
        "French" to Locale("fr"),
        "Spanish" to Locale("es"),
        "Haitian Creole" to Locale("ht"),
        "Japanese" to Locale("ja")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.choose_language)) },

        text = {
            Column {
                languages.forEach { (label, locale) ->
                    Text(
                        text = label,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onLanguageSelected(locale) },
                        fontSize = 18.sp
                    )
                }
            }
        },

        confirmButton = {}
    )
}


fun updateAppLocale(locale: Locale) {
    AppCompatDelegate.setApplicationLocales(
        LocaleListCompat.create(locale)
    )
}



