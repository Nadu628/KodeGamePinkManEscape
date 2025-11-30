package com.individual_project3.kodegame.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

//child entity references parentID to link to parent
@Entity(
    tableName = "children",
    foreignKeys = [ForeignKey(
        entity = Parent::class,
        parentColumns = ["id"],
        childColumns = ["parentId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("parentId")]
)
data class Child(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val parentId: Long,
    val firstName: String,
    val lastName: String,
    val dob: String,
    val username: String,
    val passwordHash: String
)