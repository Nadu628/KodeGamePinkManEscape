package com.individual_project3.kodegame.data.repository

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
        val parent = Parent(
            firstName = firstName,
            lastName = lastName,
            dob = dob,
            email = email,
            passwordHash = hashPassword(passwordPlain),
            childFirstName = childFN,
            childLastName = childLN,
            childDOB = childDOB
        )
        return dao.insertParent(parent)
    }

    //child register -> must select parent
    suspend fun registerChild(parentId: Long, firstName: String, lastName: String,
                              dob: String, username: String, passwordPlain: String): Long {
        val child = Child(
            parentId = parentId,
            firstName = firstName,
            lastName = lastName,
            dob = dob,
            username = username,
            passwordHash = hashPassword(passwordPlain)
        )
        return dao.insertChild(child)
    }

    //lookup parent by child's name and dob
    suspend fun lookupParentsForChild(first: String, last: String, dob: String): List<Parent>{
        return dao.findParentsByChildNameDob(first,last,dob)
    }

    //parent login: find parent by email and verify hashed password
    suspend fun loginParent(email: String, passwordPlain: String): Parent?{
        val parent = dao.findParentByEmail(email) ?: return null
        val hashed = hashPassword(passwordPlain)
        return if (parent.passwordHash == hashed) parent else null
    }

    //child login: find child by username and password
    suspend fun loginChild(username: String, passwordPlain: String): Child?{
        val child = dao.findChildByUsername(username) ?: return null
        val hashed = hashPassword(passwordPlain)
        return if(child.passwordHash == hashed) child else null
    }

    //observe parent with child for UI updates
    fun observeParentWithChildren(parentId: Long): Flow<ParentWithChild?> =
        dao.getParentWithChild(parentId)
}