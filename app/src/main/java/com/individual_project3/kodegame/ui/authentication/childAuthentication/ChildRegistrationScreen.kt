package com.individual_project3.kodegame.ui.authentication.childAuthentication

import android.app.DatePickerDialog
import androidx.navigation.NavController
import com.individual_project3.kodegame.ui.authentication.AuthViewModel
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import android.content.Context
import android.widget.Toast
import java.time.LocalDate
import java.util.Calendar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardOptions
import com.individual_project3.kodegame.data.model.Parent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.input.KeyboardType
import com.individual_project3.kodegame.KodeGameApp
import com.individual_project3.kodegame.R
import com.individual_project3.kodegame.assets.audio.AudioManager
import com.individual_project3.kodegame.ui.theme.CloudButtonTwo
import com.individual_project3.kodegame.ui.theme.CloudTextField
import java.time.format.DateTimeParseException
import java.util.Locale


fun showDatePicker(
    context: Context,
    initialText: String?,
    onDateSelected: (String) -> Unit
) {
    val inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    val now = Calendar.getInstance()

    val initDate = try {
        if (!initialText.isNullOrBlank()) {
            val parsed = LocalDate.parse(initialText, inputFormatter)
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
            val formatted = picked.format(inputFormatter)
            onDateSelected(formatted)
        },
        initDate.get(Calendar.YEAR),
        initDate.get(Calendar.MONTH),
        initDate.get(Calendar.DAY_OF_MONTH)
    )

    dpd.show()
}


@Composable
fun ChildRegistrationScreen(navController: NavController, viewModel: AuthViewModel){
    val context = LocalContext.current

    val audio = KodeGameApp.audio

    LaunchedEffect(Unit) {
        audio.loadSfx(R.raw.sfx_button_click)
    }

    //formatters
    val inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    val dbFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    //helper to convert MM/DD/YYYY -> yyyy-mm-dd
    fun parseDobForDb(value: String): String? {
        return try {
            val parsed = LocalDate.parse(value, inputFormatter)
            parsed.format(dbFormatter)
        } catch (e: Exception) {
            null
        }
    }

    //fields
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var username by remember {mutableStateOf("")}
    var password by remember { mutableStateOf("") }

    //errors
    var firstNameError by remember {mutableStateOf<String?>(null)}
    var lastNameError by remember {mutableStateOf<String?>(null)}
    var dobError by remember {mutableStateOf<String?>(null)}
    var usernameError by remember {mutableStateOf<String?>(null)}
    var passwordError by remember {mutableStateOf<String?>(null)}
    var parentSelectionError by remember { mutableStateOf<String?>(null) }

    //parent selection
    var expanded by remember{mutableStateOf(false)}
    var selectedParent: Parent? by remember{mutableStateOf(null)}
    var matchingParents by remember{mutableStateOf(listOf<Parent>())}

    val bubbleFont = FontFamily(Font(R.font.poppins_bold))
    val gradient = Brush.verticalGradient(colors = listOf(Color(0xffb3e5fc), Color(0xffb2ff59)))

    //query parent when child enters name and dob
    LaunchedEffect(firstName, lastName, dob) {
        val dobDb = parseDobForDb(dob)
        if (firstName.isNotBlank() && lastName.isNotBlank() && dobDb != null) {
            viewModel.lookupParentsForChild(firstName.trim(), lastName.trim(), dobDb) { parents ->
                matchingParents = parents
            }
        } else {
            matchingParents = emptyList()
            selectedParent = null
        }
    }

    //name 3-30 letters
    fun isValidName(value: String): Boolean{
        val nameRegex = Regex("^[\\p{L}'\\-]{3,30}$")
        return nameRegex.matches(value.trim())
    }

    //username 3-30 letters, numbers only
    fun isValidUsername(value: String): Boolean {
        val userRegex = Regex("^[A-Za-z0-9]{3,30}$")
        return userRegex.matches(value.trim())
    }

    //validate inputs
    fun validateChildAll(): Boolean {
        var ok = true

        firstNameError = when {
            firstName.isBlank() -> "Enter your first name"
            !isValidName(firstName) -> "Must be 3-30 characters"
            else -> null
        }.also { if (it != null) ok = false }

        lastNameError = when {
            lastName.isBlank() -> "Enter your last name"
            !isValidName(lastName) -> "Must be 3-30 characters"
            else -> null
        }.also { if (it != null) ok = false }

        val dobDb = parseDobForDb(dob)
        dobError = when {
            dob.isBlank() -> "Enter your date of birth"
            dobDb == null -> "Use MM/DD/YYYY"
            else -> null
        }.also { if (it != null) ok = false }

        usernameError = when {
            username.isBlank() -> "Enter a username"
            !isValidUsername(username) -> "Must be 3-30 letters or numbers"
            else -> null
        }.also { if (it != null) ok = false }

        passwordError = when {
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }.also { if (it != null) ok = false }

        parentSelectionError = when {
            selectedParent == null -> "Select a parent"
            else -> null
        }.also { if (it != null) ok = false }

        return ok
    }

    //UI Layout
    Box(
        modifier = Modifier.fillMaxSize().background(gradient),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Child Registration", fontSize = 24.sp, fontFamily = bubbleFont)

            Spacer(modifier = Modifier.height(18.dp))

            //TextFields
            //child name
            CloudTextField(
                value = firstName,
                onValueChange = {
                    firstName = it
                    firstNameError = null
                },
                labelText = "First Name",
                isError = firstNameError != null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            CloudTextField(
                value = lastName,
                onValueChange = {
                    lastName = it
                    lastNameError = null
                },
                labelText = "Last Name",
                isError = lastNameError != null,
                errorText = lastNameError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            //parent dob
            CloudTextField(
                value = dob,
                onValueChange = {
                    dob = it
                    dobError = null
                },
                labelText = "Date of Birth (MM/DD/YYYY)",
                isError = dobError != null,
                errorText = dobError,
                trailingIcon = {
                    IconButton(onClick = {
                        audio.play(R.raw.sfx_button_click)
                        showDatePicker(context, dob) { picked ->
                            dob = picked
                        }
                    }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                    }

                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            //parent dropbox
            Box {
                CloudButtonTwo(
                    text = selectedParent?.let { "Parent: ${it.firstName} ${it.lastName}" }
                        ?: "Select Parent",
                    onClick = { expanded = true; audio.play(R.raw.sfx_button_click) },
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    matchingParents.forEach { parent ->
                        DropdownMenuItem(
                            text = { Text("${parent.firstName} ${parent.lastName}") },
                            onClick = {
                                audio.play(R.raw.sfx_button_click)
                                selectedParent = parent
                                parentSelectionError = null
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Show error under dropdown
            if (parentSelectionError != null) {
                Text(
                    text = parentSelectionError!!,
                    color = Color.Red,
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )
            }
            if(parentSelectionError != null){
                Text(parentSelectionError!!, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(12.dp))

            //username
            CloudTextField(
                value = username,
                onValueChange = {
                    username = it
                    usernameError = null
                },
                labelText = "Username",
                isError = usernameError != null,
                errorText = usernameError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

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

            val allFilled = firstName.isNotBlank() && lastName.isNotBlank() && dob.isNotBlank() &&
                    username.isNotBlank() && password.isNotBlank() && selectedParent != null


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
                        audio.play(R.raw.sfx_button_click)
                        if (!validateChildAll()) {
                            Toast.makeText(context, "Fix errors", Toast.LENGTH_SHORT).show()
                            return@CloudButtonTwo
                        }

                        val dobDb = parseDobForDb(dob)!!

                        viewModel.registerChild(
                            parentId = selectedParent!!.id,
                            first = firstName.trim(),
                            last = lastName.trim(),
                            dob = dobDb,
                            username = username.trim(),
                            password = password.trim()
                        ) { id ->
                            if (id != null) {
                                Toast.makeText(context, "Child Registered", Toast.LENGTH_SHORT).show()
                                navController.navigate("child_login_screen") {
                                    popUpTo("child_registration_screen") { inclusive = true }
                                }
                            }
                        }
                    }
                )
            }
            Spacer(Modifier.height(12.dp))

            CloudButtonTwo("Back") { navController.popBackStack(); audio.play(R.raw.sfx_button_click) }
        }
    }

}