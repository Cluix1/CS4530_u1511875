package com.example.courseapp

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/** Observes the activity's ViewModel and connects user actions to its course operations. */
@Composable
fun CourseScreen(courseViewModel: CourseViewModel = viewModel()) {
    var selectedId by rememberSaveable { mutableStateOf<Long?>(null) }
    var showEditor by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirmation by rememberSaveable { mutableStateOf(false) }
    val selectedCourse = courseViewModel.courses.find { it.id == selectedId }

    BackHandler(enabled = selectedId != null && !showEditor && !showDeleteConfirmation) {
        selectedId = null
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Course App", style = MaterialTheme.typography.headlineLarge)
            if (selectedCourse == null) {
                Button(onClick = { showEditor = true }) {
                    Text("Add course")
                }
                if (courseViewModel.courses.isEmpty()) {
                    Text("No courses yet. Add a course to get started.")
                }
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(courseViewModel.courses, key = { it.id }) { course ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedId = course.id }
                        ) {
                            Text(
                                text = course.name,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(20.dp)
                            )
                        }
                    }
                }
            } else {
                CourseDetails(
                    course = selectedCourse,
                    onBack = { selectedId = null },
                    onEdit = { showEditor = true },
                    onDelete = { showDeleteConfirmation = true }
                )
            }
        }
    }

    if (showEditor) {
        CourseEditor(
            course = selectedCourse,
            onDismiss = { showEditor = false },
            onSave = { department, number, location ->
                courseViewModel.saveCourse(department, number, location, selectedCourse?.id)
            }
        )
    }

    if (showDeleteConfirmation && selectedCourse != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete course?") },
            text = { Text("Delete ${selectedCourse.name} from your courses?") },
            confirmButton = {
                TextButton(onClick = {
                    courseViewModel.deleteCourse(selectedCourse.id)
                    showDeleteConfirmation = false
                    selectedId = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/** Displays all fields for the selected course and its available actions. */
@Composable
private fun CourseDetails(course: Course, onBack: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Course details", style = MaterialTheme.typography.headlineSmall)
        Text("Department: ${course.department}")
        Text("Number: ${course.number}")
        Text("Location: ${course.location}")
        Button(onClick = onEdit, modifier = Modifier.fillMaxWidth()) {
            Text("Edit course")
        }
        OutlinedButton(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
            Text("Delete course")
        }
        TextButton(onClick = onBack) {
            Text("Back to courses")
        }
    }
}

/** Keeps draft input across rotation and commits it only when ViewModel validation succeeds. */
@Composable
private fun CourseEditor(
    course: Course?,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> String?
) {
    var department by rememberSaveable { mutableStateOf(course?.department.orEmpty()) }
    var number by rememberSaveable { mutableStateOf(course?.number.orEmpty()) }
    var location by rememberSaveable { mutableStateOf(course?.location.orEmpty()) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (course == null) "Add course" else "Edit course") },
        text = {
            Column(
                modifier = Modifier.imePadding().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it; error = null },
                    label = { Text("Department") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it; error = null },
                    label = { Text("Course number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it; error = null },
                    label = { Text("Location") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                error = onSave(department, number, location)
                if (error == null) {
                    onDismiss()
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
