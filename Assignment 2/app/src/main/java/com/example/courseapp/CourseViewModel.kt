package com.example.courseapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.Locale

/** Owns observable course data independently of recomposition and activity rotation. */
class CourseViewModel : ViewModel() {
    var courses by mutableStateOf<List<Course>>(emptyList())
        private set

    private var nextId = 1L

    /**
     * Validates and saves a new course, or replaces the course with the given identifier.
     *
     * @param courseId Identifier to edit, or null to add a course.
     * @return An error message for invalid input, or null after a successful save.
     */
    fun saveCourse(
        department: String,
        number: String,
        location: String,
        courseId: Long? = null
    ): String? {
        val cleanDepartment = department.trim().uppercase(Locale.ROOT)
        val cleanNumber = number.trim()
        val cleanLocation = location.trim()

        if (cleanDepartment.isEmpty() || cleanNumber.isEmpty() || cleanLocation.isEmpty()) {
            return "Enter a department, course number, and location."
        }
        if (!cleanNumber.all { it in '0'..'9' }) {
            return "The course number must contain only digits."
        }
        if (courseId != null && courses.none { it.id == courseId }) {
            return "This course no longer exists."
        }

        val course = Course(courseId ?: nextId++, cleanDepartment, cleanNumber, cleanLocation)
        courses = if (courseId == null) {
            courses + course
        } else {
            courses.map { if (it.id == courseId) course else it }
        }
        return null
    }

    /** Removes only the selected course, even if multiple courses have the same name. */
    fun deleteCourse(courseId: Long) {
        courses = courses.filterNot { it.id == courseId }
    }
}
