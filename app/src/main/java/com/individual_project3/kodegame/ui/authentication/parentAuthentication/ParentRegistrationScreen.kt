package com.individual_project3.kodegame.ui.authentication.parentAuthentication

import android.util.Patterns
import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.individual_project3.kodegame.ui.theme.CloudTextField

@Composable
fun ParentRegistrationScreen(
    navController: NavController,
    onRegister: ((parentFN: String, parentLN: String, parentDOB: String, childFN: String,
                  childLN: String, childDOB: String, email: String, password: String) -> Unit)
    ){
    val context = LocalContext.current

    //UI state for all input fields
    var parentFN by remember { mutableStateOf("") }
    var parentLN by remember { mutableStateOf("") }
    var parentDOB by remember { mutableStateOf("") }
    var childFN by remember { mutableStateOf("") }
    var childLN by remember { mutableStateOf("") }
    var childDOB by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    //validation error messages (null is no error)
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

    //Date formatter for MM/DD/YYYY
    val formatter = DateTimeFormatter.ofPattern("mm/dd/yyyy", Locale.US)

    //helper to show DatePickerDialog and return formatted date
    fun showDatePicker(initialText: String?, onDateSelected: (String)-> Unit){
        val now = Calendar.getInstance()
        val initDate = try{
            //try to parse existing text to use as initial date in the picker
            if(!initialText.isNullOrBlank()){
                val parsed = LocalDate.parse(initialText, formatter)
                Calendar.getInstance().apply{
                    set(parsed.year, parsed.monthValue - 1, parsed.dayOfMonth)
                }
            }else now
        }catch(e:Exception){
            now
        }

        //create and show platform DatePickerDialog
        val dpd = android.app.DatePickerDialog(
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

    //validation helpers
    fun isValidEmail(value: String) = value.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(value).matches()

    //name 3-30 characters long
    fun isValidName(value: String): Boolean{
        val nameRegex = Regex("^[\\p{L}'\\-]{3,30}$")
        return nameRegex.matches(value.trim())
    }

    //parse date string to LocalDate or return null
    fun parseDate(value: String): LocalDate?{
        return try{
            LocalDate.parse(value, formatter)
        }catch(e: DateTimeParseException){
            null
        }
    }

    //compute age in years from dob
    fun ageYears(dob: LocalDate): Int = Period.between(dob, LocalDate.now()).years

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
        val pDOB = parseDate(parentDOB)
        parentDOBError = when{
            parentDOB.isBlank() -> "Enter your date of birth"
            pDOB == null -> "Use MM/DD/YYYY or pick from calendar"
            ageYears(pDOB) < 18 -> "Parent must be at least 18 years old"
            else -> null
        }
        if(parentDOBError != null) ok = false

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
        val cDOB = parseDate(childDOB)
        childDOBError = when{
            childDOB.isBlank() -> "Enter your child's date of birth"
            cDOB == null -> "Use MM/DD/YYYY or pick from calendar"
            else -> null
        }
        if(childDOBError != null) ok = false

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
            verticalArrangement = Arrangement.Top,
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
                label = "First Name",
                isError = parentFNError != null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            CloudTextField(
                value = parentLN,
                onValueChange = {
                    parentLN = it;
                    parentLNError = null
                },
                label = "Last Name",
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
                label = "Date of Birth (MM/DD/YYYY)",
                isError = parentDOBError != null,
                errorText = parentDOBError,
                trailingIcon = {
                    IconButton(onClick = {showDatePicker(parentDOB) {parentDOB = it} }) {
                        Icon(painter = painterResource(id = android.R.drawable.ic_menu_my_calendar),
                            contentDescription = "pick date")
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
                label = "Child's First Name",
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
                label = "Child's Last Name",
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
                label = "Child's Date of Birth (MM/DD/YYYY)",
                isError = childDOBError != null,
                errorText = childDOBError,
                trailingIcon = {
                    IconButton(
                        onClick = {showDatePicker(childDOB) {childDOB = it}}) {
                        Icon(painter = painterResource(id = android.R.drawable.ic_menu_my_calendar),
                            contentDescription = "pick date")
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
                label = "Email",
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
                label = "Password",
                isError = passwordError != null,
                errorText = passwordError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            //prechect to enable button
            val allFilled = remember(parentFN, parentLN, parentDOB, childFN, childLN, childDOB, email, password) {
                parentFN.isNotBlank() && parentLN.isNotBlank() && parentDOB.isNotBlank() &&
                        childFN.isNotBlank() && childLN.isNotBlank() && childDOB.isNotBlank() &&
                        email.isNotBlank() && password.length >= 6
            }

            //register button
            Button(
                onClick = {
                    val ok = validateAll()
                    if(ok){
                        onRegister?.invoke(parentFN.trim(), parentLN.trim(), parentDOB.trim(), childFN.trim(), childLN.trim(), childDOB.trim(), email.trim(), password.trim())
                        navController.navigate("parent_login_screen")
                        Toast.makeText(context, "Registered successfully", Toast.LENGTH_SHORT)
                    }else{
                        Toast.makeText(context, "Please fix errors", Toast.LENGTH_SHORT)
                    }
                },
                enabled = allFilled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Register", style = TextStyle(fontFamily = bubbleFont))
            }

        }
    }
}
