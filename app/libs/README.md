# Libraries Folder

Place the following AAR files in this directory:

## Required Libraries

### 1. FFmpegKit
- **File**: `ffmpeg-kit-full-4.5.LTS.aar` (or latest version)
- **Download**: https://github.com/arthenica/ffmpeg-kit/releases
- **Size**: ~50-80MB (varies by architecture)
- **Purpose**: Video processing, reframing, splitting

### 2. Vosk Android
- **File**: `vosk-android-0.3.xx.aar` (latest version)
- **Download**: https://alphacephei.com/vosk/android
- **Size**: ~5-10MB
- **Purpose**: Offline speech-to-text for captions

## Installation

1. Download the AAR files from the links above
2. Place them in this `app/libs/` directory
3. Ensure `app/build.gradle` includes:
   ```gradle
   implementation fileTree(dir: 'libs', include: ['*.jar','*.aar'])
   ```
4. Sync Gradle and rebuild project

## Alternative: Maven Dependencies

Instead of local AARs, you can use Gradle dependencies (requires internet):

```gradle
// In app/build.gradle
dependencies {
    implementation 'com.arthenica:ffmpeg-kit-full:4.5.LTS'
    implementation 'com.alphacephei:vosk-android:0.3.32'
}
```

## Notes

- This folder is ignored by Git (see `.gitignore`)
- AAR files are large and should not be committed to version control
- Each developer needs to download and place these files locally
- For CI/CD, use Maven dependencies instead of local AARs