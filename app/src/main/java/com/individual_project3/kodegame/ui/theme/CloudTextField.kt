package com.individual_project3.kodegame.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.individual_project3.kodegame.R

val bubbleFont = FontFamily(Font(R.font.poppins_regular))

@Composable
fun CloudTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorText: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    height: Dp = 64.dp,
    shapeRadius: Dp = 28.dp,
    backgroundColors: List<Color> = listOf(Color(0xFFFFFFFF), Color(0xFFF7FBFF)),
){
    Column(modifier = modifier){
        //outer cloud container w/ shadow and gradient background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(shapeRadius))
                .background(
                    brush = Brush.verticalGradient(backgroundColors),
                    shape = RoundedCornerShape(shapeRadius)
                )
        ){
            //OutlinedTextField but remove its default
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textStyle = TextStyle(
                    fontFamily = bubbleFont,
                    fontSize = 16.sp,
                    color = Color.Black
                ),
                label = {
                    Text(
                        text = label,
                        style = TextStyle(fontFamily = bubbleFont, fontSize = 14.sp)
                    )
                },
                singleLine = singleLine,
                trailingIcon = trailingIcon,
                isError = isError,
                //make field look "flat" so outer box looks like a cloud
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color(0xff4a90e2),
                    unfocusedIndicatorColor = Color(0xffb0bec5),
                    cursorColor = Color.Black,
                    errorIndicatorColor = MaterialTheme.colorScheme.error
                ),
                keyboardOptions = keyboardOptions,
                visualTransformation = if(label.contains("Password", ignoreCase = true)) PasswordVisualTransformation() else VisualTransformation.None,

            )
        }

        //inline error text using same font
        if(isError && !errorText.isNullOrBlank()){
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                style = TextStyle(fontFamily = bubbleFont, fontSize = 12.sp),
                modifier = Modifier
                    .padding(start = 8.dp, top = 6.dp)
                    .fillMaxWidth()
            )
        }
    }
}
