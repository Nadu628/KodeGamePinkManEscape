package com.individual_project3.kodegame.data.progress

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "child_progress")
data class ChildProgressEntity(
    @PrimaryKey val childId: String,
    val totalStrawberries: Int,
    val levelsCompleted: Int
)