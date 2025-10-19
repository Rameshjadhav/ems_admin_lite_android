package com.ems.lite.admin.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyAsync extends AsyncTask<Void, Void, Bitmap> {

    @Override
    protected Bitmap doInBackground(Void... params) {

        try {
            String src = "http://vishwainfotech.co.in/api/Kunaljadhav/images/111.jpg";
            String shareUrl = Prefs.INSTANCE.getShareImageUrl();
            if (!TextUtils.isEmpty(shareUrl)) {
                src = shareUrl;
            }
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}