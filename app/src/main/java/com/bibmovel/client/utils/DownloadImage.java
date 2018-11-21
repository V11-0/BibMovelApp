package com.bibmovel.client.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.bibmovel.client.BookDetailsActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;

/**
 * Created by vinibrenobr11 on 18/11/18 at 20:50
 */
public class DownloadImage extends Observable {

    public DownloadImage(String file_name, BookDetailsActivity observer) {

        addObserver(observer);

        Download download = new Download();
        download.execute(file_name);
    }

    private class Download extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {

            String path = strings[0];

            try {
                URL url = new URL(Values.Path.COVER_URL + path);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();

                Log.d("URL", "Connected to " + http.getURL());

                InputStream stream = http.getInputStream();

                Bitmap bitmap = BitmapFactory.decodeStream(stream);

                stream.close();
                http.disconnect();

                return bitmap;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            setChanged();
            notifyObservers(bitmap);
        }
    }
}
