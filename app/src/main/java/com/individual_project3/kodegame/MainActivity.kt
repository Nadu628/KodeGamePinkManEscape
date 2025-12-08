package com.individual_project3.kodegame

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import com.individual_project3.kodegame.ui.Navigation
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // APPLY SAVED LOCALE BEFORE COMPOSE STARTS
        val appLocales = AppCompatDelegate.getApplicationLocales()
        if (appLocales.isEmpty) {
            // Optional: default language fallback
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.create(Locale("en"))
            )
        }

        setContent {
            AppWithLocaleSupport()
        }

        KodeGameApp.audio.startBackground(R.raw.sfx_game_music)
    }
}



@Composable
fun AppWithLocaleSupport() {
    CompositionLocalProvider(LocalAppLocale provides appLocaleState.value) {
        Navigation()
    }
}

@SuppressLint("LocalContextConfigurationRead")
@Suppress("DEPRECATION")
@Composable
fun LocalizedString(
    @StringRes id: Int,
    vararg formatArgs: Any
): String {
    val context = LocalContext.current
    val locale = LocalAppLocale.current

    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)

    val localizedContext = context.createConfigurationContext(config)

    return if (formatArgs.isNotEmpty())
        localizedContext.getString(id, *formatArgs)
    else
        localizedContext.getString(id)
}






