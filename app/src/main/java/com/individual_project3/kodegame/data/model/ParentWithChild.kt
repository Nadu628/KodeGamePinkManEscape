package com.individual_project3.kodegame.data.model

import androidx.room.Embedded
import androidx.room.Relation

//relationship wrapper to fetch a parent and their children in one query
data class ParentWithChild(
    @Embedded val parent: Parent,
    @Relation(parentColumn = "id", entityColumn = "parentId")
    val children: List<Child>
)