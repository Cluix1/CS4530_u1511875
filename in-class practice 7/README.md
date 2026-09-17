# In-class practice 7: ThemePractice

This Android Compose project demonstrates a custom Material 3 light and dark
palette with a deep teal seed (`#006A60`). Open `ThemePractice` in Android Studio,
run the `app` configuration, and use the switch to change themes. The Select and
Reset buttons demonstrate themed controls and state.

The color roles in `Color.kt` were generated from the seed with Google's
`@material/material-color-utilities` package, matching the Material Theme
Builder's color generation workflow. The interactive site was unavailable in
this environment, so `material-theme.zip` is a local Compose theme package,
not a direct download from the site's Export button.

The `snapshots` folder contains design previews of the light, dark, and selected
states. They are labeled previews because an Android SDK and emulator were not
available to capture running app screenshots here.
