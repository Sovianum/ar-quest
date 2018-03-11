package com.google.ar.core.examples.java.helloar.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileOutputStream;
import java.io.IOException;

public class FileWorker {

    public FileWorker() {

    }

    public String getInternalFileNameFromContext(Context context, String filename) {
        return context.getFilesDir().getPath() + "/" + filename;
    }

    public void writeImage(String filename, Bitmap bitmap) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); 
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap readImage(String filename) {
        return BitmapFactory.decodeFile(filename);
    }
}
