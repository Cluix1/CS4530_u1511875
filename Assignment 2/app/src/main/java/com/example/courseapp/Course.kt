package com.example.courseapp

/** Stores a course's details and a stable identifier for editing and deletion. */
data class Course(
    val id: Long,
    val department: String,
    val number: String,
    val location: String
) {
    val name: String get() = "$department $number"
}
