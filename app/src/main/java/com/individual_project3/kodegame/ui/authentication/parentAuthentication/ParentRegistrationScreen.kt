package com.individual_project3.kodegame.ui.authentication.parentAuthentication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ParentRegistrationScreen(
    navController: NavController,
    onRegister: ((parentName: String, parentDOB: String, childName: String, childDOB: String,
               email: String, password: String) -> Unit)
    ){
    val context = LocalContext.current

    //UI state for all input fields
    var parentFN by remember { mutableStateOf("") }
    var parentLN by remember { mutableStateOf("") }
    var parentDOB by remember { mutableStateOf("") }
    var childName by remember { mutableStateOf("") }
    var childDOB by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    //validation error messages (null is no error)
    var parentFNError by remember {mutableStateOf<String?>(null)}
    var parentLNError by remember {mutableStateOf<String?>(null)}
    var childNameError by remember {mutableStateOf<String?>(null)}
    var childDOBError by remember {mutableStateOf<String?>(null)}
    var emailError by remember {mutableStateOf<String?>(null)}
    var passwordError by remember {mutableStateOf<String?>(null)}

    //Date formatter for MM/DD/YYYY
    val formatter = DateTimeFormatter.ofPattern("mm/dd/yyyy", Locale.US)

    //helper to show DatePickerDialog and return formatted date
    fun showDatePicker(initialText: String?, onDateSelected: (String)-> Unit){

    }

}
