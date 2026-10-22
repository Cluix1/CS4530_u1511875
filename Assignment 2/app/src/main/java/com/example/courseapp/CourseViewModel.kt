package com.example.courseapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.Locale

/** Owns observable UI state and delegates durable changes to the repository. */
class CourseViewModel(private val repository: CourseStore = InMemoryCourseStore()) : ViewModel() {
    var courses by mutableStateOf<List<Course>>(emptyList())
        private set

    init {
        viewModelScope.launch {
            repository.courses.collect { courses = it }
        }
    }

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
        val existing = courseId?.let { id -> courses.find { it.id == id } }
        if (courseId != null && existing == null) return "This course no longer exists."

        viewModelScope.launch {
            repository.save(Course(courseId ?: 0L, cleanDepartment, cleanNumber, cleanLocation))
        }
        return null
    }

    fun deleteCourse(courseId: Long) {
        viewModelScope.launch { repository.delete(courseId) }
    }

    class Factory(private val repository: CourseRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CourseViewModel::class.java)) {
                return CourseViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
