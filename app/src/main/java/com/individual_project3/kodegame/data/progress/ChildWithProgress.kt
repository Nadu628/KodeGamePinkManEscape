package com.individual_project3.kodegame.data.progress

import androidx.room.ColumnInfo

data class ChildWithProgress(

    @ColumnInfo(name = "childId")
    val childId: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "totalStrawberries")
    val totalStrawberries: Int,

    @ColumnInfo(name = "totalLevels")
    val totalLevels: Int
)
