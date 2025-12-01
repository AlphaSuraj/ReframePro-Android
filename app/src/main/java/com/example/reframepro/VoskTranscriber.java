package com.example.reframepro;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.RecognitionListener;
import org.vosk.android.SpeechService;

public class VoskTranscriber {
    private Context ctx;
    public VoskTranscriber(Context c){ ctx = c; }

    public interface Callback { void onResult(String srtPath); }

    public void transcribeFileAsync(String videoPath, Callback cb) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    // 1) Extract audio to wav using ffmpeg
                    File outWav = new File(ctx.getCacheDir(), "audio_tmp.wav");
                    String cmd = String.format("-i \"%s\" -vn -acodec pcm_s16le -ar 16000 -ac 1 \"%s\"", videoPath, outWav.getAbsolutePath());
                    com.arthenica.ffmpegkit.FFmpegKit.execute(cmd);

                    // 2) Init Vosk model from assets
                    File modelDir = new File(ctx.getFilesDir(), "vosk-model");
                    if (!modelDir.exists()) {
                        // Copy from assets (large). Assume user placed model in files/vosk-model manually as described in README.
                        return "Please place Vosk model under app files/vosk-model";
                    }

                    Model model = new Model(modelDir.getAbsolutePath());
                    Recognizer recognizer = new Recognizer(model, 16000.0f);

                    java.io.InputStream ais = new java.io.FileInputStream(outWav);
                    byte[] b = new byte[4096];
                    int len;
                    StringBuilder srtBuilder = new StringBuilder();
                    int idx = 1; int startMs = 0;
                    while ((len = ais.read(b)) > 0) {
                        if (recognizer.acceptWaveForm(b, len)) {
                            String res = recognizer.getResult();
                            JSONObject j = new JSONObject(res);
                            String text = j.optString("text");
                            if (text != null && text.trim().length()>0) {
                                srtBuilder.append(idx++).append("\n");
                                srtBuilder.append("00:00:")
                                        .append(String.format("%02d", startMs/1000)).append(",000 --> ")
                                        .append("00:00:")
                                        .append(String.format("%02d", (startMs+4000)/1000)).append(",000\n");
                                srtBuilder.append(text).append("\n\n");
                                startMs += 4000;
                            }
                        }
                    }
                    ais.close();

                    File srt = new File(ctx.getExternalFilesDir(null), "captions.srt");
                    FileOutputStream fos = new FileOutputStream(srt);
                    fos.write(srtBuilder.toString().getBytes());
                    fos.close();

                    return srt.getAbsolutePath();
                } catch (Exception e) { e.printStackTrace(); return "transcribe error: " + e.getMessage(); }
            }

            @Override
            protected void onPostExecute(String s) {
                if (cb != null) cb.onResult(s);
            }
        }.execute(videoPath);
    }
}