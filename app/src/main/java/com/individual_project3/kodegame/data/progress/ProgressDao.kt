package com.individual_project3.kodegame.data.progress

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.individual_project3.kodegame.data.progress.ChildWithProgress

@Dao
interface ProgressDao {

    @Insert
    suspend fun insertRecord(record: ProgressRecord)

    @Query("SELECT * FROM progress WHERE childId = :id ORDER BY level ASC")
    suspend fun getProgressForChild(id: Int): List<ProgressRecord>

    @Query("""
        SELECT c.id AS childId,
               (c.firstName || ' ' || c.lastName) AS name,
               COALESCE(SUM(p.strawberries), 0) AS totalStrawberries,
               COALESCE(SUM(CASE WHEN p.completed = 1 THEN 1 ELSE 0 END), 0) AS totalLevels
        FROM children c
        LEFT JOIN progress p ON p.childId = c.id
        WHERE c.parentId = :parentId
        GROUP BY c.id
    """)
    suspend fun loadChildrenProgress(parentId: Int): List<ChildWithProgress>

}

