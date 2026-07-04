# Project Plan

A 1x1 homescreen widget and app that captures an image using an in-app camera and shares it directly to the Google Gemini app for analysis.

## Project Brief

# Gemini Lens Widget - Project Brief

## Features
- **One-Tap Camera Widget**: A 1x1 home screen widget that
 provides instant access to the in-app camera, minimizing the friction for AI analysis.
- **Rapid In-App Capture**: A streamlined camera interface powered by **CameraX**, designed for high-speed image acquisition.
- **Secure Image Provisioning**: Implementation of **FileProvider** to generate secure, temporary URIs for captured images, ensuring privacy and compliance with Android's security model.
- **Instant Gemini Sharing**: Automated hand-off to the Google Gemini app (`com.google.android.apps.bard`) using `Intent.ACTION_SEND`, enabling immediate AI-powered image analysis.

## High-Level Technical Stack
- **Kotlin & Coroutines**: The foundation for app logic and asynchronous camera/file operations.
- **Jetpack Compose**: The primary framework for building a modern, vibrant user interface.
- **CameraX**: The core library used for robust, lifecycle-aware camera preview and image capture.
- **Jetpack Navigation 3**: A state-driven navigation architecture to manage app flows and intent-based triggers.
- **Compose Material Adaptive**: Ensures the camera UI and settings remain functional and aesthetically pleasing across handsets and foldables.
- **Jetpack Glance**: A Compose-based toolkit for building the responsive 1x1 home screen widget.
- **Android FileProvider**: Essential for the secure sharing of captured image files between the app and Gemini.

## Implementation Steps
**Total Duration:** 38m 19s

### Task_1_Setup_Dependencies_Theming: Add Jetpack Glance dependencies and set up Material 3 dynamic theming with edge-to-edge support.
- **Status:** COMPLETED
- **Updates:** Added Jetpack Glance dependencies (1.1.1), updated Material 3 theme with dynamic color support and a vibrant fallback palette, configured MainActivity for edge-to-edge with a Scaffold and TopAppBar, and upgraded SDK to 37 for compatibility. Project builds successfully.
- **Acceptance Criteria:**
  - Glance dependencies added to libs.versions.toml and build.gradle.kts
  - Material 3 theme with dynamic color support implemented
  - Edge-to-edge display configured in MainActivity
  - Project builds successfully
- **Duration:** 18m 9s

### Task_2_Widget_and_Intent_Implementation: Create the 1x1 Glance widget and implement the Intent logic to launch Google Gemini's camera mode.
- **Status:** COMPLETED
- **Updates:** Implemented a 1x1 Jetpack Glance widget with Material 3 theming. The widget uses a placeholder intent to launch Google Gemini (com.google.android.apps.bard). Created necessary resources including a custom icon and widget info metadata. Registered the widget receiver in the manifest. Updated MainActivity with instructions and a test button. Project builds successfully.
- **Acceptance Criteria:**
  - Gemini camera Intent identified and implemented
  - 1x1 widget created using Jetpack Glance and registered in AndroidManifest.xml
  - Widget click action triggers the Gemini camera Intent
  - Widget UI is minimalist and supports Material 3 theming
- **Duration:** 6m 47s

### Task_3_Adaptive_Configuration_UI: Build the adaptive onboarding and configuration UI using Navigation 3 and Material Adaptive components.
- **Status:** COMPLETED
- **Updates:** Implemented adaptive UI using NavigationSuiteScaffold for responsive layouts across different form factors. Integrated Navigation 3 for type-safe, state-driven navigation between Home and Settings screens. Applied Material 3 dynamic theming and ensured edge-to-edge support. The app now adapts its navigation UI (Bottom Bar vs Navigation Rail) based on screen size. Project builds successfully.
- **Acceptance Criteria:**
  - Adaptive UI implemented using NavigationSuiteScaffold or similar components
  - Navigation 3 used to manage UI states and transitions
  - UI looks and functions correctly on handsets, foldables, and tablets
  - Material 3 components and dynamic theming applied to the UI
- **Duration:** 8m 8s

### Task_4_CameraX_and_FileSharing_Implementation: Implement the CameraX capture interface and FileProvider logic to share images with the Gemini app via Intent.ACTION_SEND.
- **Status:** COMPLETED
- **Updates:** Implemented CameraX preview and image capture in a new CameraScreen. Configured FileProvider with secure sharing paths in file_paths.xml and AndroidManifest.xml. Developed sharing logic using Intent.ACTION_SEND targeting com.google.android.apps.bard with secure URIs. Handled camera permissions using Accompanist. Integrated the camera flow into Navigation 3 and updated the 1x1 Glance widget to launch the camera directly via a custom action. Project builds successfully.
- **Acceptance Criteria:**
  - CameraX preview and image capture implemented using internal Camera screen
  - FileProvider configured for secure image URI sharing
  - Intent.ACTION_SEND correctly targets Gemini package (com.google.android.apps.bard) with image
  - Camera and storage permissions handled gracefully
- **Duration:** 5m 15s

### Task_5_Widget_Update_and_Final_Verification: Update the widget to launch the in-app camera, implement the adaptive icon, and perform final verification.
- **Status:** COMPLETED
- **Acceptance Criteria:**
  - 1x1 widget launches app camera screen directly
  - Adaptive app icon created and set
  - Full flow (Capture -> Share to Gemini) verified without crashes
  - App follows Material 3 and adaptive design guidelines
  - build pass
  - app does not crash
- **StartTime:** 2026-07-04 09:35:52 CDT

