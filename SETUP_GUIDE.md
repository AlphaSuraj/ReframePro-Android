# ReframePro Setup Guide

## Quick Start Checklist

- [ ] Clone/download the repository
- [ ] Add FFmpegKit library
- [ ] Add Vosk library  
- [ ] Download and place Vosk model
- [ ] Build and run

---

## Detailed Setup Steps

### 1. Get the Project

**Option A: Clone with Git**
```bash
git clone https://github.com/AlphaSuraj/ReframePro-Android.git
cd ReframePro-Android
```

**Option B: Download ZIP**
- Go to https://github.com/AlphaSuraj/ReframePro-Android
- Click "Code" → "Download ZIP"
- Extract to your desired location

---

### 2. Add Required Libraries

#### FFmpegKit (Required)

**Method 1: Gradle Dependency (Recommended for Android Studio)**

Edit `app/build.gradle` and uncomment:
```gradle
implementation 'com.arthenica:ffmpeg-kit-full:4.5.LTS'
```

**Method 2: Local AAR (For AIDE or offline builds)**

1. Download FFmpegKit AAR from: https://github.com/arthenica/ffmpeg-kit/releases
2. Get `ffmpeg-kit-full-4.5.LTS.aar`
3. Create `app/libs/` folder if it doesn't exist
4. Place the AAR file in `app/libs/`
5. In `app/build.gradle`, add:
```gradle
implementation files('libs/ffmpeg-kit-full-4.5.LTS.aar')
```

#### Vosk (Required for Captions)

**Method 1: Local AAR**

1. Download from: https://alphacephei.com/vosk/android
2. Get `vosk-android-0.3.xx.aar`
3. Place in `app/libs/`
4. In `app/build.gradle`, add:
```gradle
implementation files('libs/vosk-android-0.3.xx.aar')
```

**Method 2: Maven (if available)**
```gradle
implementation 'com.alphacephei:vosk-android:0.3.32'
```

---

### 3. Add Vosk Model (Required for Captions)

#### Download Model

1. Visit: https://alphacephei.com/vosk/models
2. Download **vosk-model-small-en-us-0.15** (~40MB)
3. Extract the ZIP file

#### Place Model in Project

**Option A: In Assets (Recommended)**
```
app/src/main/assets/vosk-model/
├── am/
├── conf/
├── graph/
├── ivector/
└── README
```

**Option B: On Device (Manual)**

After installing the app, manually copy to:
```
/data/data/com.example.reframepro/files/vosk-model/
```

---

### 4. Build the Project

#### Using Android Studio

1. Open Android Studio
2. File → Open → Select `ReframePro-Android` folder
3. Wait for Gradle sync to complete
4. Click "Sync Project with Gradle Files" if needed
5. Build → Make Project
6. Run → Run 'app'

#### Using AIDE (On Android)

1. Open AIDE app
2. Open Project → Navigate to `ReframePro-Android`
3. Wait for indexing
4. Click Run button (▶️)
5. AIDE will build and install the APK

#### Using Command Line

```bash
cd ReframePro-Android
./gradlew assembleDebug
# APK will be in: app/build/outputs/apk/debug/app-debug.apk
```

---

### 5. Install and Test

1. Install the APK on your Android device
2. Grant required permissions:
   - Storage access
   - Audio recording (for STT)
3. Select a short test video (1-2 minutes recommended)
4. Tap "Process" and wait
5. Check output in: `/Android/data/com.example.reframepro/files/reframe_out/`

---

## Troubleshooting

### Build Errors

**"Cannot resolve symbol FFmpegKit"**
- Ensure FFmpegKit AAR is in `app/libs/` or Gradle dependency is added
- Sync Gradle files
- Clean and rebuild project

**"Cannot resolve symbol Vosk"**
- Add Vosk AAR to `app/libs/`
- Check `build.gradle` has correct implementation line

**"Manifest merger failed"**
- Check AndroidManifest.xml for conflicts
- Ensure all permissions are properly declared

### Runtime Errors

**"FFmpeg failed"**
- Check logcat for detailed error
- Verify input video is accessible
- Ensure storage permissions granted

**"Please place Vosk model"**
- Verify model is in correct location
- Check folder structure matches exactly
- Model folder should contain `am/`, `conf/`, `graph/` subdirectories

**"Failed to resolve input path"**
- Grant storage permissions in Settings
- Try selecting video from different location
- Use Android 10+ scoped storage compatible paths

---

## Library Download Links

### FFmpegKit
- **GitHub**: https://github.com/arthenica/ffmpeg-kit
- **Releases**: https://github.com/arthenica/ffmpeg-kit/releases
- **Docs**: https://github.com/arthenica/ffmpeg-kit/wiki/Android

### Vosk
- **Website**: https://alphacephei.com/vosk/
- **Android**: https://alphacephei.com/vosk/android
- **Models**: https://alphacephei.com/vosk/models
- **GitHub**: https://github.com/alphacep/vosk-api

### ML Kit
- **Docs**: https://developers.google.com/ml-kit/vision/face-detection/android
- **Already included via Gradle**

---

## Minimum Requirements

- **Android Version**: 7.0 (API 24) or higher
- **Storage**: 500MB+ free space
- **RAM**: 2GB+ recommended
- **Processor**: Quad-core or better for smooth processing

---

## Development Environment

### Android Studio
- **Version**: Arctic Fox (2020.3.1) or newer
- **Gradle**: 7.4.2
- **JDK**: 11 or newer

### AIDE
- **Version**: 3.2.x or newer
- **Note**: May need to manually add AARs to `libs/` folder

---

## Next Steps

After successful setup:

1. **Test with short videos** (30-60 seconds) first
2. **Check output quality** and adjust settings if needed
3. **Monitor performance** on your device
4. **Customize** the app for your specific needs

---

## Getting Help

- **Issues**: https://github.com/AlphaSuraj/ReframePro-Android/issues
- **FFmpegKit Docs**: https://github.com/arthenica/ffmpeg-kit/wiki
- **Vosk Docs**: https://alphacephei.com/vosk/
- **ML Kit Docs**: https://developers.google.com/ml-kit

---

**Happy coding! 🚀**