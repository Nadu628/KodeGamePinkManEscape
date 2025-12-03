package com.individual_project3.kodegame.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.individual_project3.kodegame.R

@Composable
fun CloudButtonTwo(
    text: String,
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int? = null, // optional icon
    onClick: () -> Unit
) {
    val bubbleFont = FontFamily(Font(R.font.poppins_regular))

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.95f),
            contentColor = Color.Black
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
        modifier = modifier
            .height(64.dp)
            .widthIn(min = 96.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            if (iconRes != null) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = text,
                    modifier = Modifier
                        .size(28.dp)
                        .padding(end = 8.dp)
                )
            }
            Text(text = text, fontSize = 18.sp, fontFamily = bubbleFont)
        }
    }
}