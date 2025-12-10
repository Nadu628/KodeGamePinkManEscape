package com.individual_project3.kodegame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.individual_project3.kodegame.LocalizedString
import com.individual_project3.kodegame.R
import com.individual_project3.kodegame.ui.theme.CloudButtonTwo

@Composable
fun PauseMenuOverlay(
    onResume: () -> Unit,
    onLanguage: () -> Unit,
    onToggleMusic: () -> Unit,
    musicOn: Boolean,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(260.dp)
                .wrapContentHeight()
                .background(
                    Color.White.copy(alpha = 0.92f),
                    RoundedCornerShape(30.dp)
                )
                .shadow(12.dp, RoundedCornerShape(30.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                CloudButtonTwo(
                    text = LocalizedString(R.string.resume),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onResume
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(LocalizedString(R.string.language), fontSize = 18.sp)
                    IconButton(onClick = onLanguage) {
                        Icon(
                            painter = painterResource(R.drawable.ic_language),
                            contentDescription = "Language",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(LocalizedString(R.string.music), fontSize = 18.sp)
                    IconButton(onClick = onToggleMusic) {
                        Icon(
                            painter = painterResource(
                                if (musicOn) R.drawable.music_on
                                else R.drawable.music_off
                            ),
                            contentDescription = "Toggle Music",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                CloudButtonTwo(
                    text = LocalizedString(R.string.logout),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onLogout
                )
            }
        }
    }
}
