package com.example.parcelapp.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;


public class ImageLoaderHelper {

    public Uri getImageUri (Context ctx, Bitmap btm) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        btm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(ctx.getContentResolver(), btm, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI (Uri uri, Context ctx) {
        String path = "";
        if(ctx.getContentResolver() != null) {
            Cursor cursor = ctx.getContentResolver().query(uri, null, null, null, null);
            if(cursor != null) {
                cursor.moveToFirst();
                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(index);
                cursor.close();
            }
        }
        return path;
    }
}
