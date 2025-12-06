package com.individual_project3.kodegame.data.progress

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progress")
data class ProgressRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val childId: Int,        // foreign key to Child table
    val level: Int,
    val strawberries: Int,
    val completed: Boolean
)
