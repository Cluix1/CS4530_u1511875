package com.example.courseapp

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface CourseStore {
    val courses: Flow<List<Course>>
    suspend fun save(course: Course)
    suspend fun delete(courseId: Long)
}

/** Single app-wide middle layer between the ViewModel and Room. */
class CourseRepository private constructor(private val dao: CourseDao) : CourseStore {
    override val courses: Flow<List<Course>> = dao.observeAll()

    override suspend fun save(course: Course) {
        if (course.id == 0L) dao.insert(course) else dao.update(course)
    }

    override suspend fun delete(courseId: Long) = dao.deleteById(courseId)

    companion object {
        @Volatile private var instance: CourseRepository? = null

        fun getInstance(context: Context): CourseRepository = instance ?: synchronized(this) {
            instance ?: CourseRepository(CourseDatabase.getInstance(context).courseDao())
                .also { instance = it }
        }
    }
}

/** Default isolated store keeps validation tests independent from Android and disk. */
internal class InMemoryCourseStore : CourseStore {
    private val state = MutableStateFlow<List<Course>>(emptyList())
    override val courses: Flow<List<Course>> = state
    private var nextId = 1L

    override suspend fun save(course: Course) {
        val saved = if (course.id == 0L) course.copy(id = nextId++) else course
        state.value = if (course.id == 0L) state.value + saved else
            state.value.map { if (it.id == course.id) saved else it }
    }

    override suspend fun delete(courseId: Long) {
        state.value = state.value.filterNot { it.id == courseId }
    }
}
