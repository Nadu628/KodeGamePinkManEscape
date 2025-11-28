package com.individual_project3.kodegame.data.repository

import com.individual_project3.kodegame.data.db.AuthDao
import com.individual_project3.kodegame.data.model.Child
import com.individual_project3.kodegame.data.model.Parent
import com.individual_project3.kodegame.data.model.ParentWithChild
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest

class AuthRepository(private val dao: AuthDao){
    //sha-256 hashing with a static
    private fun hashPassword(password: String, salt: String = "staticSalt"): String{
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest((salt + password).toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") {"%02x".format(it)}
    }

    //register a parent and child in single operation
    //parent passwordHash contains the plain password and replace with the hashed value before inserting
    suspend fun registerParentWithChild(parent: Parent, child: Child): Long {
        val parentToInsert = parent.copy(passwordHash = hashPassword(parent.passwordHash))
        return dao.insertParentWithChildren(parentToInsert, listOf(child))
    }

    //parent login: find parent by email and verify hashed password
    suspend fun loginParent(email: String, passwordPlain: String): Parent?{
        val parent = dao.findParentByEmail(email) ?: return null
        val hashed = hashPassword(passwordPlain)
        return if (parent.passwordHash == hashed) parent else null
    }

    //child login: find child by name + dob and return child + parentId
    suspend fun loginChildByNameDob(first: String, last: String, dob: String): Pair<Child,Long>?{
        val child = dao.findChildByNameDob(first,last,dob) ?: return null
        return Pair(child, child.parentId)
    }

    //observe parent with child for UI updates
    fun observeParentWithChildren(parentId: Long): Flow<ParentWithChild?> =
        dao.getParentWithChild(parentId)
}