package com.individual_project3.kodegame.data.repository

import android.util.Log
import com.individual_project3.kodegame.data.db.AuthDao
import com.individual_project3.kodegame.data.model.Child
import com.individual_project3.kodegame.data.model.Parent
import com.individual_project3.kodegame.data.model.ParentWithChild
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest

class AuthRepository(private val dao: AuthDao){
    //hash password before saving
    private fun hashPassword(password: String, salt: String = "staticSalt"): String{
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest((salt + password).toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") {"%02x".format(it)}
    }

    //parent registers with child info
    suspend fun registerParent(firstName: String, lastName: String, dob: String,
                               email: String, passwordPlain: String, childFN: String,
                               childLN: String, childDOB: String): Long {
        Log.d("REGISTER_PARENT", "---- Registering Parent ----")
        Log.d("REGISTER_PARENT", "Parent: $firstName $lastName")
        Log.d("REGISTER_PARENT", "DOB: $dob")
        Log.d("REGISTER_PARENT", "Email: $email")
        Log.d("REGISTER_PARENT", "Child: $childFN $childLN, DOB=$childDOB")
        Log.d("REGISTER_PARENT", "PasswordHash=${hashPassword(passwordPlain.trim())}")
        Log.d("REGISTER_PARENT", "--------------------------------")
        Log.d("Auth", "Registering parent: email=$email, hash=${hashPassword(passwordPlain.trim())}")
        val hashed = hashPassword(passwordPlain.trim())
        val parent = Parent(
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            dob = dob,
            email = email.trim(),
            passwordHash = hashed,
            childFirstName = childFN.trim(),
            childLastName = childLN.trim(),
            childDOB = childDOB
        )
        return dao.insertParent(parent)
    }

    //child register -> must select parent
    suspend fun registerChild(parentId: Long, firstName: String, lastName: String,
                              dob: String, username: String, passwordPlain: String): Long {
        Log.d("REGISTER_CHILD", "---- Registering Child ----")
        Log.d("REGISTER_CHILD", "Child: $firstName $lastName, DOB=$dob")
        Log.d("REGISTER_CHILD", "Username: $username")
        Log.d("REGISTER_CHILD", "ParentId: $parentId")
        Log.d("REGISTER_CHILD", "PasswordHash=${hashPassword(passwordPlain.trim())}")
        Log.d("REGISTER_CHILD", "--------------------------------")
        val hashed = hashPassword(passwordPlain.trim())
        val child = Child(
            parentId = parentId,
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            dob = dob,// already ISO
            username = username.trim(),
            passwordHash = hashed
        )
        return dao.insertChild(child)
    }

    //lookup parent by child's name and dob
    suspend fun lookupParentsForChild(first: String, last: String, dob: String): List<Parent> {
        return dao.findParentsByChildNameDob(first, last, dob)
    }

    //parent login: find parent by email and verify hashed password
    suspend fun loginParent(email: String, passwordPlain: String): Parent?{
        Log.d("LOGIN_PARENT", "Email Entered: '$email'")
        val parent = dao.findParentByEmail(email.trim()) ?: return null
        Log.d("LOGIN_PARENT", "DB Email: ${parent?.email}")
        Log.d("LOGIN_PARENT", "DB Hash: ${parent?.passwordHash}")
        val hashed = hashPassword(passwordPlain.trim())
        Log.d("Auth", "Login attempt: email=$email, enteredHash=$hashed, storedHash=${parent.passwordHash}")
        return if (parent.passwordHash == hashed) parent else null
    }

    //child login: find child by username and password
    suspend fun loginChild(username: String, passwordPlain: String): Child?{
        Log.d("LOGIN_CHILD", "---- Child Login Attempt ----")
        Log.d("LOGIN_CHILD", "Username Entered: '$username'")
        val child = dao.findChildByUsername(username.trim()) ?: return null
        Log.d("LOGIN_CHILD", "DB Username: ${child?.username}")
        Log.d("LOGIN_CHILD", "DB Hash: ${child?.passwordHash}")
        val hashed = hashPassword(passwordPlain.trim())
        Log.d("Auth", "Login attempt: email=$username, enteredHash=$hashed, storedHash=${child.passwordHash}")
        return if(child.passwordHash == hashed) child else null
    }

    //observe parent with child for UI updates
    fun observeParentWithChildren(parentId: Long): Flow<ParentWithChild?> =
        dao.getParentWithChild(parentId)
}