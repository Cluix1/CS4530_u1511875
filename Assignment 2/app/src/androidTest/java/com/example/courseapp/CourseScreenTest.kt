package com.example.courseapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.hasScrollToIndexAction
import org.junit.Rule
import org.junit.Test

/** Exercises the actual activity, including its ViewModel across activity recreation. */
class CourseScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun addEditCancelDeleteAndConfirmDelete() {
        addCourse("4530")
        composeRule.onNodeWithText("CS 4530").performClick()
        composeRule.onNodeWithText("Department: CS").assertIsDisplayed()
        composeRule.onNodeWithText("Number: 4530").assertIsDisplayed()
        composeRule.onNodeWithText("Location: WEB 1250").assertIsDisplayed()
        composeRule.activityRule.scenario.recreate()
        composeRule.onNodeWithText("Location: WEB 1250").assertIsDisplayed()
        composeRule.onNodeWithText("Edit course").performClick()
        composeRule.onNodeWithText("Location").performTextReplacement("MEB 3225")
        composeRule.onNodeWithText("Save").performClick()
        composeRule.onNodeWithText("Location: MEB 3225").assertIsDisplayed()
        composeRule.onNodeWithText("Delete course").performClick()
        composeRule.onNodeWithText("Cancel").performClick()
        composeRule.onNodeWithText("Location: MEB 3225").assertIsDisplayed()
        composeRule.onNodeWithText("Delete course").performClick()
        composeRule.onNodeWithText("Delete", substring = false).performClick()
        composeRule.onNodeWithText("No courses yet. Add a course to get started.").assertIsDisplayed()
    }

    @Test
    fun listScrollsToCoursesOutsideInitialViewport() {
        repeat(15) { addCourse((4000 + it).toString()) }
        composeRule.onNode(hasScrollToIndexAction()).performScrollToIndex(14)
        composeRule.onNodeWithText("CS 4014").performClick()
        composeRule.onNodeWithText("Number: 4014").assertIsDisplayed()
    }

    private fun addCourse(number: String) {
        composeRule.onNodeWithText("Add course").performClick()
        composeRule.onNodeWithText("Department").performTextInput("CS")
        composeRule.onNodeWithText("Course number").performTextInput(number)
        composeRule.onNodeWithText("Location").performTextInput("WEB 1250")
        composeRule.onNodeWithText("Save").performClick()
    }
}
