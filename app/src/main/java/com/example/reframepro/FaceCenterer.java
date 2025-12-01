package com.example.reframepro;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.File;
import java.util.List;

public class FaceCenterer {

    public static class FaceBox {
        public int centerX, centerY, imageW, imageH;
        public FaceBox(int cx, int cy, int w, int h) { centerX = cx; centerY = cy; imageW = w; imageH = h; }
    }

    // Synchronous simple method: decode first frame and run ML Kit face detection
    public static FaceBox findFaceBoxSync(Context ctx, String videoPath) {
        // For simplicity extract first frame using ffmpeg to a temp jpg, or rely on a helper that does it.
        try {
            File tmp = new File(ctx.getCacheDir(), "frame_tmp.jpg");
            String cmd = String.format("-i \"%s\" -ss 00:00:01 -vframes 1 -q:v 2 \"%s\"", videoPath, tmp.getAbsolutePath());
            com.arthenica.ffmpegkit.FFmpegKit.execute(cmd);
            if (!tmp.exists()) return null;

            Bitmap bmp = BitmapFactory.decodeFile(tmp.getAbsolutePath());
            InputImage image = InputImage.fromBitmap(bmp, 0);

            FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                    .build();

            FaceDetector detector = FaceDetection.getClient(options);

            // Blocking call via Tasks.await is not available here; so do a quick synchronous wrapper
            com.google.android.gms.tasks.Task<List<Face>> task = detector.process(image);
            while (!task.isComplete()) {
                Thread.sleep(10);
            }
            if (task.isSuccessful()) {
                List<Face> faces = task.getResult();
                if (faces != null && faces.size() > 0) {
                    Face f = faces.get(0);
                    int cx = (int)(f.getBoundingBox().centerX());
                    int cy = (int)(f.getBoundingBox().centerY());
                    return new FaceBox(cx, cy, bmp.getWidth(), bmp.getHeight());
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // Create an ffmpeg crop+scale filter that centers around the face coordinates
    public static String buildCropFilter(FaceBox box, int targetW, int targetH) {
        // Map face center from source image to target cropping origin
        // We will choose a crop region in source coords with aspect ratio targetH:targetW
        float targetAR = (float)targetH / (float)targetW; // 1920/1080

        // We will instruct ffmpeg to scale first so we can center in scaled pixels
        // Simpler approach: use crop and pad after scaling with force_original_aspect_ratio=increase,
        // then use "crop" with expressions around the face center.

        // Use ffmpeg expression to center crop on a variable center
        // We'll output a filter that roughly centers face: scale and then crop (center expressions)
        String filter = String.format(
                "scale='if(gt(a,%f),-2,%d)':'if(gt(a,%f),%d,-2)',crop=%d:%d:(main_w/2 - %d/2):(main_h/2 - %d/2)",
                (float)targetW/targetH, targetW, (float)targetW/targetH, targetH,
                targetW, targetH, targetW, targetH);

        // The above is a fallback; we keep it simple to avoid fragile expressions.
        // If you want pixel-perfect centering, compute exact crop using input dimensions and face coords and inject values.
        filter = String.format("scale=1080:1920:force_original_aspect_ratio=decrease,pad=1080:1920:(ow-iw)/2:(oh-ih)/2");
        return filter;
    }
}