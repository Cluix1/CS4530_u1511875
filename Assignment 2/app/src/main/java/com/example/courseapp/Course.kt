package com.example.courseapp

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Stores a course's details and a stable identifier for editing and deletion. */
@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val department: String,
    val number: String,
    val location: String
) {
    val name: String get() = "$department $number"
}
