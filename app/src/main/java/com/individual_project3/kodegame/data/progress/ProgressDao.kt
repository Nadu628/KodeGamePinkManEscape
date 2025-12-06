package com.individual_project3.kodegame.data.progress

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProgressDao {

    @Insert
    suspend fun insertRecord(record: ProgressRecord)

    @Query("SELECT * FROM progress WHERE childId = :id ORDER BY level ASC")
    suspend fun getProgressForChild(id: Int): List<ProgressRecord>
}

