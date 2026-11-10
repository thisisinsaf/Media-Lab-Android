# CSE 489 · Media & Broadcast Lab

A Kotlin Android app implementing **CSE 489 Mobile Application Development, Assignment 2**. The interface uses a navigation drawer with four destinations and follows the activity flow in the assignment.

## Open it in Android Studio Rabbit

1. Extract `CSE489_Media_Broadcast_Lab.zip` on your computer.
2. Launch **Android Studio Rabbit**. Select **Open** (not **New Project**).
3. Choose the **`CSE489_Media_Broadcast_Lab` folder** containing `settings.gradle.kts`.
4. Allow Gradle sync. The first sync needs **internet** to download the official Gradle distribution, Android Gradle Plugin, Kotlin and AndroidX/Material dependencies.
5. If prompted, install **Android SDK Platform 35**, **Build Tools**, and use **Gradle JDK 17 or later** (Android Studio's embedded JDK is usually fine).
6. Start an Android emulator (Android 13, 14 or 15 recommended) or connect a USB-debugging-enabled phone.
7. Select the **app** run configuration, then click **Run ▶**.

The app does not need a separate backend, database, third-party media service, or API key.

### If Gradle shows an error

- Ensure the folder you opened has **`settings.gradle.kts`** at its root (not the outer ZIP file or only the `app` folder).
- Check that Gradle is set to use Android Studio's embedded JDK 17+ under **Settings → Build, Execution, Deployment → Build Tools → Gradle**.
- Let Android Studio install any missing Android SDK packages.
- Disable Gradle offline mode for your *first* build.
- If your network restricts `services.gradle.org`, permit it or configure your own Gradle 8.13 installation.

**About the included Gradle launcher:** this ZIP contains `gradlew`, `gradlew.bat`, and a small auditable bootstrap JAR whose source is included in `gradle/wrapper/BootstrapSource.java.txt`. The build environment used to assemble this ZIP could not fetch the standard Gradle wrapper JAR. This bootstrap instead downloads the **official Gradle 8.13 distribution** and verifies the publisher's SHA-256 checksum (`20f1b11…e8710aed78`) before executing it. It is *not* the upstream Gradle wrapper JAR. After a successful sync, you can replace it with the standard wrapper by running `gradle wrapper --gradle-version 8.13` using Gradle installed locally.

## Features and assignment mapping

| Assignment item | Implemented here | Relevant code |
| --- | --- | --- |
| Navigation drawer | Menu with Broadcast Receiver, Image Scale, Video, Audio | `MainActivity.kt`, `drawer_menu.xml` |
| Fragments / Jetpack | Four independent fragment destinations hosted by the main activity | `BroadcastFragment.kt`, `ImageScaleFragment.kt`, `VideoFragment.kt`, `AudioFragment.kt` |
| Custom broadcast selection | Spinner → text-entry Activity 2 → receiver Activity 3 | `BroadcastFragment.kt`, `CustomMessageActivity.kt`, `BroadcastResultActivity.kt` |
| Custom broadcast receiver | Dynamically registers a real receiver, sends an app-scoped broadcast, shows received text | `BroadcastResultActivity.kt` |
| System battery receiver | Reads `ACTION_BATTERY_CHANGED` including percentage and charging status | `BatteryStatusActivity.kt` |
| Internet image scale | Downloads an HTTPS image and supports pinch zoom, pan and double-tap | `ImageScaleFragment.kt`, `ZoomableImageView.kt` |
| In-app video | Plays local `res/raw/sample_video.mp4` | `VideoFragment.kt` |
| In-app audio | Plays local `res/raw/sample_audio.mp3` with seek/pause/replay | `AudioFragment.kt` |

### Important: where media files come from

- **Image:** remote image, using default HTTPS endpoint `https://picsum.photos/id/1018/1200/800`; you may paste a different direct HTTPS image URL.
- **Video:** `app/src/main/res/raw/sample_video.mp4`, included in the APK. This original 8-second video has an H.264 video track and AAC audio track.
- **Audio:** `app/src/main/res/raw/sample_audio.mp3`, included in the APK. This is an original 16-second melody created for the demonstration.

The files are local on the device **after installing the APK**; you do not need to copy them into the emulator.

## Test each feature

1. Open the left navigation drawer and confirm all four menu items switch fragments.
2. On **Broadcast Receiver**, select **Custom broadcast receiver**, tap Continue, enter `Hello from CSE 489`, then tap Send. On Activity 3, verify it displays `Hello from CSE 489` with the received status. Tap Send broadcast again and edit the message.
3. Reopen Broadcast Receiver, select **System battery notification receiver** and Continue. Verify the battery percentage. On the emulator use **Extended Controls → Battery** to change the level; observe updates.
4. Open **Image Scale** while the emulator has internet. Wait for the image to load. Pinch to zoom, drag it, double-tap, and use Reset zoom. Paste another direct HTTPS photo URL and load it.
5. Open **Video Player**. Tap Play, Pause, Resume, Replay. The video plays even with the emulator offline.
6. Open **Audio Player**. Tap Play, Pause, Resume, seek to a different point, and replay. The audio plays even with the emulator offline.

See [docs/TEST_PLAN.md](docs/TEST_PLAN.md) for a checklist that can be used while demonstrating the app.

## Architecture

```
MainActivity (DrawerLayout + NavigationView)
├── BroadcastFragment (Spinner)
│   ├── CustomMessageActivity [step 2: text]
│   │   └── BroadcastResultActivity [step 3: real custom receiver]
│   └── BatteryStatusActivity [system battery receiver]
├── ImageScaleFragment (HTTPS image + ZoomableImageView)
├── VideoFragment (VideoView → local MP4)
└── AudioFragment (MediaPlayer → local MP3)
```

- **Language:** Kotlin
- **UI:** XML layouts, AndroidX fragments and Material Components
- **Build:** AGP 8.13.2, Gradle 8.13, Kotlin Gradle Plugin 2.3.10
- **SDK:** compile/target 35, minimum 24 (Android 7.0)
- **Permissions:** Internet only, for network images
- **Privacy:** no login, tracking, analytics, file permissions or user-generated content upload

The original MP3 and MP4 demonstration assets are included in source control. They do not require any external media rights or playback network access.

## Verification status

The project sources, XML files, APK resources, MP3/MP4 codec metadata, ZIP contents and bootstrap JAR were checked during packaging. **An actual Android Gradle build and runtime/emulator test were not possible in the packaging environment**, because it did not have Android SDK / Gradle repositories available. The final compile and on-device behavior must be checked in Android Studio Rabbit.
