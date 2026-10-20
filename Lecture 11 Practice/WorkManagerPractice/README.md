# Lecture 11 Practice — WorkManager

Android Jetpack Compose + WorkManager practice implementing a Blur-O-Matic workflow.

## Run
1. Open `WorkManagerPractice` in Android Studio.
2. Allow Gradle sync (JDK 17; Android SDK 35).
3. Run the `app` module on an emulator or connected Android device.
4. Choose a blur amount and tap **Start**.
5. Wait for the chain of CleanupWorker → BlurWorker → SaveImageToFileWorker to finish. The result is rendered in the app.

The sample image is generated in code, then blurred in the background. WorkManager runs a unique sequence with input/output data and observes status.

## Evidence still required
See `screenshots/README.md`. Actual running app, code, and blurred-result screenshots must be taken on a working device.

Reference: https://developer.android.com/codelabs/basic-android-kotlin-compose-workmanager#0

**Note:** Prepared project; Android Studio build and device execution have not been verified here.
