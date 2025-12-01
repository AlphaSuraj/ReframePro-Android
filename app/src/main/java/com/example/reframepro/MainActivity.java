package com.example.reframepro;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.ReturnCode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_SELECT = 100;
    private static final int REQ_PERMS = 101;

    private Uri inputUri;
    private TextView tvStatus;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSelect = findViewById(R.id.btnSelect);
        Button btnProcess = findViewById(R.id.btnProcess);
        tvStatus = findViewById(R.id.tvStatus);
        videoView = findViewById(R.id.videoView);

        btnSelect.setOnClickListener(v -> selectVideo());
        btnProcess.setOnClickListener(v -> {
            if (inputUri == null) {
                tvStatus.setText("Please select a video first");
                return;
            }
            checkPermissionsAndProcess();
        });
    }

    private void selectVideo() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("video/*");
        startActivityForResult(Intent.createChooser(i, "Select Video"), REQ_SELECT);
    }

    private void checkPermissionsAndProcess() {
        List<String> need = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            need.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            need.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            need.add(Manifest.permission.RECORD_AUDIO);
        }
        if (need.size() > 0) {
            ActivityCompat.requestPermissions(this, need.toArray(new String[0]), REQ_PERMS);
        } else {
            processPipeline();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMS) {
            processPipeline();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SELECT && resultCode == Activity.RESULT_OK && data != null) {
            inputUri = data.getData();
            videoView.setVideoURI(inputUri);
            videoView.start();
            tvStatus.setText("Selected: " + inputUri.getPath());
        }
    }

    private void processPipeline() {
        tvStatus.setText("Processing...");

        // Step 1: Extract path and create output folders
        String inputPath = FileUtils.getRealPathFromUri(this, inputUri);
        if (inputPath == null) {
            tvStatus.setText("Failed to resolve input path");
            return;
        }

        File outDir = new File(getExternalFilesDir(null), "reframe_out");
        if (!outDir.exists()) outDir.mkdirs();

        // 1) Reframe to 9:16 1080x1920 with face centering
        // Use FaceCenterer to compute crop offset (centerX, centerY) in pixels
        FaceCenterer.FaceBox box = FaceCenterer.findFaceBoxSync(this, inputPath);
        // If no face, box will be null -> center crop

        String reframePath = new File(outDir, "reframe_1080x1920.mp4").getAbsolutePath();

        String cropFilter;
        if (box != null) {
            // compute crop center based on face center
            int targetW = 1080;
            int targetH = 1920;
            cropFilter = FaceCenterer.buildCropFilter(box, targetW, targetH);
        } else {
            // center scale+pad filter
            cropFilter = "scale=1080:1920:force_original_aspect_ratio=decrease,pad=1080:1920:(ow-iw)/2:(oh-ih)/2";
        }

        String cmdReframe = String.format("-i \"%s\" -vf \"%s\" -c:v libx264 -preset fast -crf 20 -c:a copy \"%s\"", inputPath, cropFilter, reframePath);

        FFmpegKit.executeAsync(cmdReframe, session -> {
            final ReturnCode rc = session.getReturnCode();
            runOnUiThread(() -> {
                if (rc != null && rc.isValueSuccess()) {
                    tvStatus.setText("Reframe done. Splitting...");
                    splitToParts(reframePath, outDir);
                    // Start STT in background
                    VoskTranscriber transcriber = new VoskTranscriber(this);
                    transcriber.transcribeFileAsync(reframePath, srt -> runOnUiThread(() -> tvStatus.setText("Captions ready: " + srt)));
                } else {
                    tvStatus.setText("FFmpeg failed: " + (rc == null ? "unknown" : rc.toString()));
                }
            });
        });
    }

    private void splitToParts(String sourcePath, File outDir) {
        // Use ffmpeg segmenter: 60s parts
        String outPattern = new File(outDir, "part_%03d.mp4").getAbsolutePath();
        String cmdSplit = String.format("-i \"%s\" -c copy -map 0 -f segment -segment_time 60 -reset_timestamps 1 \"%s\"", sourcePath, outPattern);

        FFmpegKit.executeAsync(cmdSplit, session -> {
            final ReturnCode rc = session.getReturnCode();
            runOnUiThread(() -> {
                if (rc != null && rc.isValueSuccess()) {
                    tvStatus.setText("Splitting done. Export folder: " + outDir.getAbsolutePath());
                } else {
                    tvStatus.setText("Split failed: " + (rc == null ? "unknown" : rc.toString()));
                }
            });
        });
    }
}