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
import com.individual_project3.kodegame.R
import com.individual_project3.kodegame.ui.authentication.CloudButton
import com.individual_project3.kodegame.ui.theme.CloudTextField
import java.time.format.DateTimeParseException
import java.util.Locale


fun showDatePicker(
    context: Context,
    initialText: String?,
    onDateSelected: (String) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val now = Calendar.getInstance()

    val initDate = try {
        if (!initialText.isNullOrBlank()) {
            val parsed = LocalDate.parse(initialText, formatter)
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
            onDateSelected(picked.format(formatter))
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

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var username by remember {mutableStateOf("")}
    var password by remember { mutableStateOf("") }

    var firstNameError by remember {mutableStateOf<String?>(null)}
    var lastNameError by remember {mutableStateOf<String?>(null)}
    var dobError by remember {mutableStateOf<String?>(null)}
    var usernameError by remember {mutableStateOf<String?>(null)}
    var passwordError by remember {mutableStateOf<String?>(null)}
    var parentSelectionError by remember { mutableStateOf<String?>(null) }


    var expanded by remember{mutableStateOf(false)}
    var selectedParent: Parent? by remember{mutableStateOf(null)}
    var matchingParents by remember{mutableStateOf(listOf<Parent>())}

    val bubbleFont = FontFamily(Font(R.font.poppins_bold))
    val gradient = Brush.verticalGradient(colors = listOf(Color(0xffb3e5fc), Color(0xffb2ff59)))

    val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US)

    //query parent when child enters name and dob
    LaunchedEffect(firstName, lastName, dob) {
        if(firstName.isNotBlank() && lastName.isNotBlank() && dob.isNotBlank()){
            viewModel.lookupParentsForChild(firstName.trim(), lastName.trim(), dob.trim()){ parents ->
                matchingParents = parents
            }
        }else{
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

    //parse date string to LocalDate or return null
    fun parseDate(value: String): LocalDate? {
        return try {
            LocalDate.parse(value, formatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }



    fun validateChildAll(): Boolean{
        var ok = true
        //child FN
        firstNameError = when{
            firstName.isBlank() -> "Enter your first name"
            !isValidName(firstName) -> "Must be 3-30 characters"
            else -> null
        }
        if (firstNameError != null) ok = false

        // child last name
        lastNameError = when {
            lastName.isBlank() -> "Enter your last name"
            !isValidName(lastName) -> "Must be 3-30 letters"
            else -> null
        }
        if (lastNameError != null) ok = false

        // child DOB
        val cDOB = parseDate(dob)
        dobError = when {
            dob.isBlank() -> "Enter your date of birth"
            cDOB == null -> "Use MM/DD/YYYY or pick from calendar"
            else -> null
        }
        if (dobError != null) ok = false

        // username
        usernameError = when {
            username.isBlank() -> "Enter a username"
            !isValidUsername(username) -> "Must be 3-30 letters or numbers"
            else -> null
        }
        if (usernameError != null) ok = false

        // password
        passwordError = when {
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
        if (passwordError != null) ok = false

        // parent selection (must be linked)
        parentSelectionError = when {
            selectedParent == null -> "Select a parent to link"
            else -> null
        }
        if (parentSelectionError != null) ok = false

        return ok

    }

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
            Box{
                CloudButton(
                    text = selectedParent?.let{"Parent: ${it.firstName} ${it.lastName}"} ?: "Select Parent",
                    onClick = {expanded = true},
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(expanded = expanded, onDismissRequest = {expanded = false}) {
                    matchingParents.forEach { parent ->
                        DropdownMenuItem(
                            text = {Text("${parent.firstName} ${parent.lastName}")},
                            onClick = {
                                selectedParent = parent
                                expanded = false
                            }
                        )
                    }
                }

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
                CloudButton(
                    text = "Register",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    onClick = {
                        val ok = validateChildAll()
                        if (ok) {
                            selectedParent?.let { parent ->
                                viewModel.registerChild(
                                    parentId = parent.id,
                                    first = firstName.trim(),
                                    last = lastName.trim(),
                                    dob = dob.trim(),
                                    username = username.trim(),
                                    password = password.trim()
                                ) { childId ->
                                    if (childId != null) {
                                        Toast.makeText(
                                            context,
                                            "Child Registered",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigate("child_login_screen")
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Registration failed",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                }
                            }
                        }
                    })
                Spacer(modifier = Modifier.height(12.dp))
                CloudButton("Back") {
                    navController.popBackStack()
                }
            }

        }
    }

}