package com.example.reframepro;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class FileUtils {
    
    public static String getRealPathFromUri(Context context, Uri uri) {
        String result = null;
        String[] projection = { MediaStore.Video.Media.DATA };
        
        try {
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                cursor.moveToFirst();
                result = cursor.getString(columnIndex);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Fallback to uri path if query fails
        if (result == null) {
            result = uri.getPath();
        }
        
        return result;
    }
}