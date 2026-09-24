# Assignment 2: Course App

A single-activity Jetpack Compose application for adding, viewing, editing, and deleting courses.

## Open and run in Android Studio

1. Choose **File > Open** and select this **Assignment 2** folder, which contains `settings.gradle.kts`. Open it as a separate project from Assignment 1.
2. Let Gradle sync and download dependencies. Use Android Studio's bundled JDK under **Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK**.
3. Install the Android SDK platform requested by sync (API 37), if prompted. This project follows Assignment 1's SDK and Gradle setup.
4. In **Tools > Device Manager**, create/start a phone emulator (API 24 or newer), or connect a phone with USB debugging enabled.
5. Select the **app** run configuration and your device, then click **Run**.

No API keys, account setup, or database configuration are required.

## Manual testing checklist

- Launch: an empty list and **Add course** button appear.
- Add `CS`, `4530`, `WEB 1250`: the list row displays only `CS 4530`.
- Tap the row: department, number, and location all appear.
- Edit the course, save, and return to the list: the updated name/details appear.
- Edit again and cancel: the saved values remain unchanged.
- Try saving blank/whitespace fields or a nonnumeric course number: an error appears without changing the course list.
- Add enough courses to fill the screen and scroll to the last one.
- Add two courses with the same name but different locations; deleting one must leave the other.
- Delete a course: cancel first to keep it, then confirm to remove it. Delete the last course to see the empty state.
- Rotate while viewing details or entering a draft: saved courses, selection, and draft text survive. Android's Back button returns from details or dismisses a dialog.
- Check the form in landscape with the keyboard visible; its fields can scroll.

Course data is stored in memory in the ViewModel. It survives recomposition and configuration changes, but is not persisted after force-stop/process death or a fresh application session. Disk persistence is not required by this assignment.

## Architecture and rubric

- `Course.kt`: immutable model with department, number, location, and stable ID.
- `CourseViewModel.kt`: observable course list, input validation, and add/edit/delete operations. The list setter is private, so the UI changes data through ViewModel methods.
- `MainActivity.kt`: the only activity; hosts Compose.
- `CourseScreen.kt`: obtains the lifecycle-managed ViewModel using `viewModel()`, observes its Compose state, renders names with `LazyColumn`, and provides detail/editor views. Draft input and navigation use `rememberSaveable`.
- Editing is included for extra credit. No fragments or XML layouts are used.

The project uses four-space Kotlin indentation, descriptive names, and documentation comments like Assignment 1, with Compose layout patterns from JetPackDemo.

## Automated checks and submission

From the Android Studio terminal in this folder, on Windows:

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
# Requires a running emulator or connected device:
.\gradlew.bat :app:connectedDebugAndroidTest
# Run last, before submitting:
.\gradlew.bat clean
```

On macOS/Linux, use `./gradlew` instead of `.\gradlew.bat` (run `chmod +x gradlew` if needed).

Unit tests cover validation, editing, and deletion identity. Device tests cover add/detail/edit/delete, activity recreation, and list scrolling. You can also run either test class using its green gutter arrow in Android Studio.

After any further changes, run the checks and `clean`, commit your source changes, and push to GitHub. Verify that GitHub shows the Assignment 2 folder on the branch you submit, then submit the repository link through your course's submission system. Build output and the machine-specific `local.properties` file are intentionally ignored.

References: [Compose compiler setup](https://developer.android.com/develop/ui/compose/setup-compose-dependencies-and-compiler), [ViewModel state ownership](https://developer.android.com/develop/ui/compose/state-hoisting), and [LazyColumn](https://developer.android.com/develop/ui/compose/lists).
