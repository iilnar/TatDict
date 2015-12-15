package com.example.ilnarsabirzyanov.tatdict;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    private downloadTask downloadTask;
    private ProgressDialog progressDialog;

    public SettingsFragment() {
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.settings_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        view.getRootView().findViewById(R.id.toolbarView).setVisibility(View.GONE);
        view.getRootView().findViewById(R.id.text).setVisibility(View.GONE);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Downloading..");
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        if (downloadTask != null && downloadTask.getStatus() == AsyncTask.Status.RUNNING) {
            progressDialog.show();
        }
        getView().findViewById(R.id.updateDB).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadTask = new downloadTask(getActivity());
                        downloadTask.execute("https://www.dropbox.com/s/2i6ta2gb6ae65po/rus_to_tat.file?raw=1", "rus_to_tat.file",
                                "https://www.dropbox.com/s/cwdf5saxc27i43z/tat_to_rus.file?dl=1", "tat_to_rus.file");
                    }
                }
        );
    }

    class downloadTask extends AsyncTask<String, Integer, Boolean> {
        Activity activity;
        private Exception m_error;

        downloadTask(Activity activity) {
            this.activity = activity;
//            progressDialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        private File download(String link, String fileName) {
            HttpURLConnection httpURLConnection;
            InputStream inputStream = null;
            int totalSize;
            int downloadSize;
            byte[] buffer;
            int bufferLength;
            File file = null;
            URL url;

            FileOutputStream fileOutputStream = null;
            try {
                url = new URL(link);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                int responseCode = httpURLConnection.getResponseCode();
                totalSize = httpURLConnection.getContentLength();

                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                file = new File(activity.getExternalFilesDir(null), fileName + ".tmp");
                fileOutputStream = new FileOutputStream(file);
                inputStream = httpURLConnection.getInputStream();

                downloadSize = 0;

                buffer = new byte[1024];
                while ((bufferLength = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bufferLength);
                    downloadSize += bufferLength;
                    publishProgress(downloadSize, totalSize);
                }
            } catch (IOException e) {
                e.printStackTrace();
                m_error = e;
                if (file != null) {
                    file.deleteOnExit();
                }
                file = null;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return file;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            File rusToTat = download(params[0], params[1]);
            boolean b;
            if (rusToTat == null) {
                return false;
            }
            File tatToRus = download(params[2], params[3]);
            if (tatToRus == null) {
                rusToTat.delete();
                return false;
            }
            File f = activity.getFilesDir();
            new File(activity.getExternalFilesDir(null), params[1]).delete();
            rusToTat.renameTo(new File(activity.getExternalFilesDir(null), params[1]));
            new File(activity.getExternalFilesDir(null), params[3]).delete();
            tatToRus.renameTo(new File(activity.getExternalFilesDir(null), params[3]));
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress((int) ((values[0] / (float) values[1]) * 100));
        }

        @Override
        protected void onPostExecute(Boolean res) {
            progressDialog.hide();
            if (m_error != null) {
                m_error.printStackTrace();
                Toast.makeText(activity, "Произошла ошибка.", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(activity, "Словари скачаны.", Toast.LENGTH_SHORT).show();
            //file.delete();
        }
    }

}
