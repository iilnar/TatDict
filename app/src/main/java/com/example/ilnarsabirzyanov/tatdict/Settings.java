package com.example.ilnarsabirzyanov.tatdict;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViewById(R.id.updateDB).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "http://my-files.ru/Save/2bcqj0/rus.txt";
                        downloadDictionary(url);
                    }
                }
        );
    }

    private void downloadDictionary(String url) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        new AsyncTask<String, Integer, File>() {
            private Exception m_error = null;

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("Downloading..");
                progressDialog.setCancelable(false);
                progressDialog.setMax(100);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
            }

            @Override
            protected File doInBackground(String... params) {
                URL url;
                HttpURLConnection httpURLConnection;
                InputStream inputStream;
                int totalSize;
                int downloadSize;
                byte[] buffer;
                int bufferLength;
                File file = null;
                FileOutputStream fileOutputStream = null;
                try {
                    url = new URL(params[0]);
                    httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.connect();

                    //file = File.createTempFile("base", "tmp");
                    file = new File(getExternalFilesDir(null), "tat_to_rus.file");
                    fileOutputStream = new FileOutputStream(file);
                    inputStream = httpURLConnection.getInputStream();

                    totalSize = httpURLConnection.getContentLength();
                    downloadSize = 0;

                    buffer = new byte[1024];
                    bufferLength = 0;

                    while ((bufferLength = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bufferLength);
                        downloadSize += bufferLength;
                        publishProgress(downloadSize, totalSize);
                    }
                    fileOutputStream.close();
                    inputStream.close();
                    return file;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    m_error = e;
                } catch (IOException e) {
                    e.printStackTrace();
                    m_error = e;
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                progressDialog.setProgress((int) ((values[0] / (float) values[1]) * 100));
            }

            @Override
            protected void onPostExecute(File file) {
                if (m_error != null) {
                    m_error.printStackTrace();
                    return;
                }
                progressDialog.hide();
                //file.delete();
            }
        }.execute(url);
    }
}
