package com.example.ilnarsabirzyanov.tatdict;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Ilnar Sabirzyanov on 09.11.2015.
 */
public class SettingsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceStet) {
        getView().findViewById(R.id.updateDB).setOnClickListener(
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
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
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
                File file;
                FileOutputStream fileOutputStream;
                try {
                    url = new URL(params[0]);
                    httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.connect();

                    //file = File.createTempFile("base", "tmp");
                    file = new File(getActivity().getExternalFilesDir(null), "tat_to_rus.file");
                    fileOutputStream = new FileOutputStream(file);
                    inputStream = httpURLConnection.getInputStream();

                    totalSize = httpURLConnection.getContentLength();
                    downloadSize = 0;

                    buffer = new byte[1024];
                    while ((bufferLength = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bufferLength);
                        downloadSize += bufferLength;
                        publishProgress(downloadSize, totalSize);
                    }
                    fileOutputStream.close();
                    inputStream.close();
                    return file;
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
