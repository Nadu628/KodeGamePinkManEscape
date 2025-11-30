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

    @Query("select * from parents where email = :email limit 1 ")
    suspend fun findParentByEmail(email: String): Parent?

    @Query("select * from children where username = :username limit 1")
    suspend fun findChildByUsername(username: String): Child?

    @Query("select * from parents where firstName = :childFirst and lastName = :childLast and dob = :childDob")
    suspend fun findParentsByChildNameDob(childFirst: String, childLast: String, childDob: String): List<Parent>

    @Transaction
    @Query("select * from parents where id = :parentId")
    fun getParentWithChild(parentId: Long): Flow<ParentWithChild?>


}