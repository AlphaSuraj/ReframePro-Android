# ReframePro — Full Offline Android Video Reframing App

**Complete Android project for video reframing, face-centered cropping, splitting, and offline captions**

## Features

✅ **9:16 Reframing** - Converts videos to 1080×1920 portrait format  
✅ **Face-Centered Cropping** - Uses ML Kit on-device face detection to auto-center faces  
✅ **60-Second Splitting** - Automatically splits videos into 60-second segments (`part_001.mp4`, `part_002.mp4`, etc.)  
✅ **Offline Captions** - Generates SRT subtitles using Vosk speech-to-text (fully offline)  
✅ **Fully Offline** - All processing happens on-device using FFmpeg  
✅ **Simple UI** - Select, process, preview, and export with ease

---

## Requirements

### Development Tools
- **AIDE** (Android IDE for mobile development) or **Android Studio** (desktop)
- Android 7.0+ (API 24+)

### Libraries

You need to add these dependencies:

1. **FFmpegKit Android** - For video processing
   - Download: [FFmpegKit Releases](https://github.com/arthenica/ffmpeg-kit/releases)
   - Place `ffmpeg-kit-full.aar` in `app/libs/` OR add Gradle dependency:
   ```gradle
   implementation 'com.arthenica:ffmpeg-kit-full:4.5.LTS'
   ```

2. **Vosk Android** - For offline speech-to-text
   - Download: [Vosk Android](https://alphacephei.com/vosk/android)
   - Place `vosk-android.aar` in `app/libs/` OR add dependency

3. **ML Kit Face Detection** - Already included in `build.gradle`:
   ```gradle
   implementation 'com.google.mlkit:face-detection:16.1.5'
   ```

### Assets

**Vosk Model** (for offline captions):
- Download a small English model: [vosk-model-small-en-us-0.15](https://alphacephei.com/vosk/models)
- Extract and place in: `app/src/main/assets/vosk-model/` or manually copy to device at `/data/data/com.example.reframepro/files/vosk-model/`

---

## Project Structure

```
ReframePro-Android/
├── app/
│   ├── build.gradle                    # App-level Gradle config
│   ├── src/main/
│   │   ├── AndroidManifest.xml         # Permissions & app config
│   │   ├── java/com/example/reframepro/
│   │   │   ├── MainActivity.java       # Main UI & processing pipeline
│   │   │   ├── FaceCenterer.java       # ML Kit face detection
│   │   │   ├── VoskTranscriber.java    # Offline speech-to-text
│   │   │   ├── FFmpegHelper.java       # FFmpeg utilities
│   │   │   └── FileUtils.java          # URI to path conversion
│   │   └── res/
│   │       ├── layout/activity_main.xml # UI layout
│   │       └── values/strings.xml       # String resources
│   └── libs/                            # Place .aar files here
├── build.gradle                         # Root Gradle config
├── settings.gradle
└── README.md
```

---

## Setup Instructions

### Option 1: AIDE (On Android Device)

1. **Clone or download** this repository
2. Open AIDE app and import the project
3. **Add libraries**:
   - Download `ffmpeg-kit-full.aar` and `vosk-android.aar`
   - Place them in `app/libs/` folder
4. **Add Vosk model**:
   - Download and extract Vosk model
   - Place in `app/src/main/assets/vosk-model/`
5. **Build & Run** - AIDE will compile and install the APK

### Option 2: Android Studio (Desktop)

1. **Clone the repository**:
   ```bash
   git clone https://github.com/AlphaSuraj/ReframePro-Android.git
   cd ReframePro-Android
   ```

2. **Open in Android Studio**

3. **Add dependencies** (choose one):
   - **Local AARs**: Place in `app/libs/` and uncomment in `build.gradle`
   - **Maven**: Uncomment these lines in `app/build.gradle`:
     ```gradle
     implementation 'com.arthenica:ffmpeg-kit-full:4.5.LTS'
     // Add Vosk dependency if available
     ```

4. **Add Vosk model** to `app/src/main/assets/vosk-model/`

5. **Sync Gradle** and **Run** on device/emulator

---

## Usage

1. **Launch the app**
2. **Tap "Select Video"** - Choose a video from your device
3. **Tap "Process"** - The app will:
   - Detect faces and reframe to 9:16 (1080×1920)
   - Split into 60-second segments
   - Generate offline captions (SRT file)
4. **Check output** in `/Android/data/com.example.reframepro/files/reframe_out/`:
   - `part_001.mp4`, `part_002.mp4`, etc.
   - `captions.srt`

---

## Permissions

The app requires:
- `READ_EXTERNAL_STORAGE` - To access videos
- `WRITE_EXTERNAL_STORAGE` - To save processed videos
- `RECORD_AUDIO` - For Vosk speech recognition

---

## Technical Details

### Video Processing Pipeline

1. **Face Detection** (`FaceCenterer.java`)
   - Extracts first frame using FFmpeg
   - Uses ML Kit to detect face coordinates
   - Calculates optimal crop center

2. **Reframing** (`MainActivity.java`)
   - FFmpeg command with dynamic crop filter
   - Scales to 1080×1920 with face-centered crop
   - Outputs H.264 encoded MP4

3. **Splitting** (`MainActivity.java`)
   - FFmpeg segment muxer
   - 60-second chunks with reset timestamps
   - Copy codec (no re-encoding)

4. **Captioning** (`VoskTranscriber.java`)
   - Extracts audio to 16kHz WAV
   - Vosk processes audio offline
   - Generates SRT subtitle file

---

## Performance Notes

- **Processing time**: Depends on video length and device CPU/GPU
- **Battery usage**: Video processing is intensive - test with short clips first
- **Storage**: Ensure sufficient space for output files
- **Recommended**: Start with 1-2 minute test videos

---

## Troubleshooting

### "FFmpeg failed"
- Ensure `ffmpeg-kit.aar` is properly added
- Check logcat for detailed error messages

### "Please place Vosk model"
- Verify model is in correct location: `app/src/main/assets/vosk-model/`
- Model folder should contain `am/`, `conf/`, `graph/` subdirectories

### "Failed to resolve input path"
- Grant storage permissions
- Try selecting video from different location

### Build errors
- Sync Gradle files
- Clean and rebuild project
- Verify all dependencies are added

---

## Future Enhancements

- [ ] Multi-frame face tracking for better centering
- [ ] Custom output resolution options
- [ ] Batch processing multiple videos
- [ ] Progress indicators for long videos
- [ ] Export to social media directly
- [ ] Custom caption styling

---

## License

MIT License - Feel free to use and modify

---

## Credits

- **FFmpegKit**: [arthenica/ffmpeg-kit](https://github.com/arthenica/ffmpeg-kit)
- **Vosk**: [alphacep/vosk-api](https://github.com/alphacep/vosk-api)
- **ML Kit**: [Google ML Kit](https://developers.google.com/ml-kit)

---

## Support

For issues or questions:
- Open an issue on GitHub
- Check FFmpegKit and Vosk documentation
- Review Android logcat for detailed errors

---

**Built with ❤️ for offline video processing**