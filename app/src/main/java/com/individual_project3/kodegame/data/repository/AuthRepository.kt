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
        Log.d("Auth", "Registering parent: email=$email, hash=${hashPassword(passwordPlain.trim())}")
        val hashed = hashPassword(passwordPlain.trim())
        val parent = Parent(
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            dob = dob.trim(),
            email = email.trim(),
            passwordHash = hashed,
            childFirstName = childFN.trim(),
            childLastName = childLN.trim(),
            childDOB = childDOB.trim()
        )
        return dao.insertParent(parent)
    }

    //child register -> must select parent
    suspend fun registerChild(parentId: Long, firstName: String, lastName: String,
                              dob: String, username: String, passwordPlain: String): Long {
        val hashed = hashPassword(passwordPlain.trim())
        val child = Child(
            parentId = parentId,
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            dob = dob.trim(),
            username = username.trim(),
            passwordHash = hashed
        )
        return dao.insertChild(child)
    }

    //lookup parent by child's name and dob
    suspend fun lookupParentsForChild(first: String, last: String, dob: String): List<Parent>{
        return dao.findParentsByChildNameDob(first,last,dob)
    }

    //parent login: find parent by email and verify hashed password
    suspend fun loginParent(email: String, passwordPlain: String): Parent?{
        val parent = dao.findParentByEmail(email.trim()) ?: return null
        val hashed = hashPassword(passwordPlain.trim())
        Log.d("Auth", "Login attempt: email=$email, enteredHash=$hashed, storedHash=${parent.passwordHash}")
        return if (parent.passwordHash == hashed) parent else null
    }

    //child login: find child by username and password
    suspend fun loginChild(username: String, passwordPlain: String): Child?{
        val child = dao.findChildByUsername(username.trim()) ?: return null
        val hashed = hashPassword(passwordPlain.trim())
        return if(child.passwordHash == hashed) child else null
    }

    //observe parent with child for UI updates
    fun observeParentWithChildren(parentId: Long): Flow<ParentWithChild?> =
        dao.getParentWithChild(parentId)
}