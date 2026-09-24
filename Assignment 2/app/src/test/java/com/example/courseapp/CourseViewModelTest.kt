package com.example.courseapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Verifies validation and course identity independently of the UI. */
class CourseViewModelTest {
    @Test
    fun addCourseTrimsFieldsAndNormalizesDepartment() {
        val viewModel = CourseViewModel()
        assertNull(viewModel.saveCourse(" cs ", " 4530 ", " WEB 1250 "))
        val course = viewModel.courses.single()
        assertEquals("CS 4530", course.name)
        assertEquals("WEB 1250", course.location)
    }

    @Test
    fun invalidInputDoesNotAddCourses() {
        val viewModel = CourseViewModel()
        assertNotNull(viewModel.saveCourse(" ", "4530", "WEB"))
        assertNotNull(viewModel.saveCourse("CS", "", "WEB"))
        assertNotNull(viewModel.saveCourse("CS", "4530", " "))
        assertNotNull(viewModel.saveCourse("CS", "45x0", "WEB"))
        assertTrue(viewModel.courses.isEmpty())
    }

    @Test
    fun editPreservesIdentityAndInvalidEditPreservesOriginal() {
        val viewModel = CourseViewModel()
        viewModel.saveCourse("CS", "4530", "WEB")
        val original = viewModel.courses.single()
        assertNotNull(viewModel.saveCourse("CS", "abc", "MEB", original.id))
        assertEquals(original, viewModel.courses.single())
        assertNull(viewModel.saveCourse("MATH", "1210", "LCB", original.id))
        assertEquals(Course(original.id, "MATH", "1210", "LCB"), viewModel.courses.single())
    }

    @Test
    fun deletingOneDuplicateDoesNotDeleteTheOtherOrReuseItsId() {
        val viewModel = CourseViewModel()
        viewModel.saveCourse("CS", "4530", "WEB")
        viewModel.saveCourse("CS", "4530", "MEB")
        val first = viewModel.courses.first()
        val second = viewModel.courses.last()
        viewModel.deleteCourse(first.id)
        assertEquals(listOf(second), viewModel.courses)
        viewModel.saveCourse("CS", "3500", "WEB")
        assertNotEquals(first.id, viewModel.courses.last().id)
        assertNotNull(viewModel.saveCourse("CS", "4530", "WEB", first.id))
        assertEquals(2, viewModel.courses.size)
    }
}
