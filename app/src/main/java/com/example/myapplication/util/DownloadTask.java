package com.example.myapplication.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask extends AsyncTask<String, Void, Void> {

    private final Context context;

    public DownloadTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... strings) {
        String fileUrl = strings[0];
        String fileName = "downloaded_music.mp3"; // You can change the file name as needed

        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // Check if the download is successful (HTTP status code 200)
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get the InputStream from the connection
                InputStream inputStream = connection.getInputStream();

                // Create a FileOutputStream to save the downloaded file to internal storage
                FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);

                // Buffer size for downloading
                byte[] buffer = new byte[1024];
                int len;

                // Read from the InputStream and write to the FileOutputStream
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }

                // Close the streams
                outputStream.close();
                inputStream.close();
            } else {
                Log.e("DownloadTask", "Server returned HTTP " + connection.getResponseCode());
            }

            // Disconnect the HttpURLConnection
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
