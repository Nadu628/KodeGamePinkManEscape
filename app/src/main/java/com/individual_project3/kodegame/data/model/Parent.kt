package com.individual_project3.kodegame.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parents")
data class Parent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    //parent info
    val firstName: String,
    val lastName: String,
    val dob: String, //store as MM/DD/YYYY or ISO-8601 string
    val email: String,
    val passwordHash: String,

    //child info
    val childFirstName: String,
    val childLastName: String,
    val childDOB: String
)
