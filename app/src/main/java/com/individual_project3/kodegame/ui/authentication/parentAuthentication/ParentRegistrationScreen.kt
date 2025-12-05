package com.individual_project3.kodegame.ui.authentication.parentAuthentication

import android.app.DatePickerDialog
import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.NavController
import com.individual_project3.kodegame.R
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.Locale
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.individual_project3.kodegame.ui.authentication.AuthViewModel
import com.individual_project3.kodegame.ui.theme.CloudButtonTwo
import com.individual_project3.kodegame.ui.theme.CloudTextField



@Composable
fun ParentRegistrationScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
    ){
    val context = LocalContext.current

    //formater convert MM/DD/YYYY -> yyyy-mm-dd
    val inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    val dbFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun parseDobForDb(value: String): String? {
        return try {
            val parsed = LocalDate.parse(value, inputFormatter)
            parsed.format(dbFormatter)
        } catch (e: Exception) {
            null
        }
    }

    //helper to show DatePickerDialog and return formatted date
    fun showDatePicker(
        context: Context,
        initialText: String?,
        onDateSelected: (String) -> Unit
    ) {
        val now = Calendar.getInstance()

        val initDate = try {
            if (!initialText.isNullOrBlank()) {
                val parsed = LocalDate.parse(initialText, inputFormatter) // ⭐ FIX
                Calendar.getInstance().apply {
                    set(parsed.year, parsed.monthValue - 1, parsed.dayOfMonth)
                }
            } else now
        } catch (e: Exception) {
            now
        }

        val dpd = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val picked = LocalDate.of(year, month + 1, dayOfMonth)
                onDateSelected(picked.format(inputFormatter)) // ⭐ FIX — correct for UI
            },
            initDate.get(Calendar.YEAR),
            initDate.get(Calendar.MONTH),
            initDate.get(Calendar.DAY_OF_MONTH)
        )

        dpd.show()
    }


    //UI state for all input fields
    var parentFN by remember { mutableStateOf("") }
    var parentLN by remember { mutableStateOf("") }
    var parentDOB by remember { mutableStateOf("") }
    var childFN by remember { mutableStateOf("") }
    var childLN by remember { mutableStateOf("") }
    var childDOB by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    //errors
    var parentFNError by remember {mutableStateOf<String?>(null)}
    var parentLNError by remember {mutableStateOf<String?>(null)}
    var parentDOBError by remember {mutableStateOf<String?>(null)}
    var childFNError by remember {mutableStateOf<String?>(null)}
    var childLNError by remember {mutableStateOf<String?>(null)}
    var childDOBError by remember {mutableStateOf<String?>(null)}
    var emailError by remember {mutableStateOf<String?>(null)}
    var passwordError by remember {mutableStateOf<String?>(null)}

    //font
    val bubbleFont = FontFamily(Font(R.font.poppins_bold))
    //background
    val gradient = Brush.verticalGradient(colors = listOf(Color(0xffb3e5fc), Color(0xffb2ff59)))

    //validation helpers
    fun isValidEmail(value: String) = value.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(value).matches()

    //name 3-30 characters long
    fun isValidName(value: String): Boolean{
        val nameRegex = Regex("^[\\p{L}'\\-]{3,30}$")
        return nameRegex.matches(value.trim())
    }


    //validate all fields and set error messages; return true if everything is correct
    fun validateAll(): Boolean{
        var ok = true

        //parent name
        parentFNError = when{
            parentFN.isBlank() -> "Enter your first name"
            !isValidName(parentFN) -> "Must be 3-30 letters"
            else -> null
        }
        if(parentFNError != null) ok = false

        parentLNError = when{
            parentLN.isBlank() -> "Enter your last name"
            !isValidName(parentLN) -> "Must be 3-30 letters"
            else -> null
        }
        if(parentLNError != null) ok = false

        //parent dob -> must be 18+
        val parentDobDb = parseDobForDb(parentDOB)
        parentDOBError = when {
            parentDOB.isBlank() -> "Enter your date of birth"
            parentDobDb == null -> "Use MM/DD/YYYY"
            else -> null
        }
        if (parentDOBError != null) ok = false

        //child name
        childFNError = when{
            childFN.isBlank() -> "Enter your child's first name"
            !isValidName(childFN) -> "Must be 3-30 letters"
            else -> null
        }
        if(childFNError != null) ok = false

        childLNError = when{
            childLN.isBlank() -> "Enter your child's last name"
            !isValidName(childLN) -> "Must be 3-30 letters"
            else -> null
        }
        if(childLNError != null) ok = false

        //child DOB
        val childDobDb = parseDobForDb(childDOB)
        childDOBError = when {
            childDOB.isBlank() -> "Enter child's date of birth"
            childDobDb == null -> "Use MM/DD/YYYY"
            else -> null
        }
        if (childDOBError != null) ok = false

        //email
        emailError = when{
            email.isBlank() -> "Enter email"
            !isValidEmail(email) -> "Enter a valid email address"
            else -> null
        }
        if(emailError != null) ok = false

        //password
        passwordError = when{
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
        if(passwordError != null) ok = false

        return ok
    }

    //UI Layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
            contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            //Screen title
            Text(
                text = "Parent Registration",
                fontSize = 20.sp,
                fontFamily = bubbleFont,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            //TextFields
            //parent name
            CloudTextField(
                value = parentFN,
                onValueChange = {
                    parentFN = it
                    parentFNError = null
                },
                labelText = "First Name",
                isError = parentFNError != null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            CloudTextField(
                value = parentLN,
                onValueChange = {
                    parentLN = it
                    parentLNError = null
                },
                labelText = "Last Name",
                isError = parentLNError != null,
                errorText = parentLNError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            //parent dob
            CloudTextField(
                value = parentDOB,
                onValueChange = {
                    parentDOB = it
                    parentDOBError = null
                },
                labelText = "Date of Birth (MM/DD/YYYY)",
                isError = parentDOBError != null,
                errorText = parentDOBError,
                trailingIcon = {
                    IconButton(onClick = {
                        showDatePicker(context, parentDOB) { picked ->
                            parentDOB = picked
                        }
                    }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                    }

                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            //child name
            CloudTextField(
                value = childFN,
                onValueChange = {
                    childFN = it
                    childFNError = null
                },
                labelText = "Child's First Name",
                isError = childFNError != null,
                errorText = childFNError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            CloudTextField(
                value = childLN,
                onValueChange = {
                    childLN = it
                    childLNError = null
                },
                labelText = "Child's Last Name",
                isError = childLNError != null,
                errorText = childLNError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            //child dob
            CloudTextField(
                value = childDOB,
                onValueChange = {
                    childDOB = it
                    childDOBError = null
                },
                labelText = "Child's Date of Birth (MM/DD/YYYY)",
                isError = childDOBError != null,
                errorText = childDOBError,
                trailingIcon = {
                    IconButton(onClick = {
                        showDatePicker(context, childDOB) { picked ->
                            childDOB = picked
                        }
                    }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                    }

                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            //email
            CloudTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                labelText = "Email",
                isError = emailError != null,
                errorText = emailError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            //password
            CloudTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                labelText = "Password",
                isError = passwordError != null,
                errorText = passwordError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            //precheck to enable button
            val allFilled = remember(parentFN, parentLN, parentDOB, childFN, childLN, childDOB, email, password) {
                parentFN.isNotBlank() && parentLN.isNotBlank() && parentDOB.isNotBlank() &&
                        childFN.isNotBlank() && childLN.isNotBlank() && childDOB.isNotBlank() &&
                        email.isNotBlank() && password.length >= 6
            }
            //register button
            AnimatedVisibility(
                visible = allFilled,
                enter = slideInHorizontally(initialOffsetX = {it }),
                exit = fadeOut()
            ) {
                CloudButtonTwo(
                    text = "Register",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    onClick = {
                        if (!validateAll()) {
                            Toast.makeText(context, "Fix errors", Toast.LENGTH_SHORT).show()
                            return@CloudButtonTwo
                        }

                        //Convert DOBs AGAIN SAFELY
                        val parentDobDb = parseDobForDb(parentDOB)!!
                        val childDobDb = parseDobForDb(childDOB)!!

                        viewModel.registerParent(
                            parentFN.trim(),
                            parentLN.trim(),
                            parentDobDb,
                            email.trim(),
                            password.trim(),
                            childFN.trim(),
                            childLN.trim(),
                            childDobDb
                        ) { id ->
                            if (id != null) {
                                Toast.makeText(context, "Registered!", Toast.LENGTH_SHORT).show()
                                navController.navigate("pick_user_screen") {
                                    popUpTo("splash_screen") { inclusive = true }
                                }
                            }else{
                                Toast.makeText(context, "Please fix errors", Toast.LENGTH_SHORT ).show()
                            }
                        }

                    })


            }
            Spacer(modifier = Modifier.height(12.dp))
            CloudButtonTwo("Back") {
                navController.popBackStack()
            }

        }
    }
}
