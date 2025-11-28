package com.individual_project3.kodegame.data.db

import androidx.room.*
import com.individual_project3.kodegame.data.model.Child
import com.individual_project3.kodegame.data.model.Parent
import com.individual_project3.kodegame.data.model.ParentWithChild
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthDao{
    //insert parent -> returns generated id
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertParent(parent: Parent): Long

    //insert child
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChild(child: Child): Long

    //convience transaction: insert parent then its child, returning parentId
    //ensures atomicity
    @Transaction
    suspend fun insertParentWithChildren(parent: Parent, children: List<Child>): Long{
        val parentId = insertParent(parent)
        children.forEach{insertChild(it.copy(parentId = parentId))}
        return parentId
    }

    //fetch parent with their child
    @Transaction
    @Query("select * from parents where id = :parentId")
    fun getParentWithChild(parentId : Long): Flow<ParentWithChild?>

    //find parent by email
    @Query("select * from parents where email = :email limit 1")
    suspend fun findParentByEmail(email: String): Parent?

    //find child by first/last/dob (used for child login)
    @Query("select * from children where firstName = :first and lastName = :last and dob = :dob limit 1")
    suspend fun findChildByNameDob(first: String, last: String, dob: String): Child?

    //get parent by id
    @Query("select * from parents where id = :id limit 1")
    suspend fun getParentById(id: Long): Parent?
}