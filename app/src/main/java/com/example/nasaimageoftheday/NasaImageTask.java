package com.example.nasaimageoftheday;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NasaImageTask extends AsyncTask<String, Void, NasaImage> {
    private static final String TAG = "NasaImageTask";
    private static final String API_KEY = "NY8vCnv3ArNynhbhtJpZlhAZOAI8CghzcGpTUicn";
    private static final String API_BASE_URL = "https://api.nasa.gov/planetary/apod";

    private ProgressBar progressBar;
    private ListView listViewImages;
    private Context context;

    public NasaImageTask(ProgressBar progressBar, ListView listViewImages, Context context) {
        this.progressBar = progressBar;
        this.listViewImages = listViewImages;
        this.context = context;
    }

    @Override
    protected NasaImage doInBackground(String... params) {
        Log.d("NasaImageTask", "doInBackground: Task started");
        String selectedDate = params[0];
        String apiUrl = API_BASE_URL + "?api_key=" + API_KEY + "&date=" + selectedDate;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                reader.close();
                inputStream.close();

                JSONObject jsonObject = new JSONObject(responseBuilder.toString());
                NasaImage nasaImage = new NasaImage();
                nasaImage.setDate(jsonObject.getString("date"));
                nasaImage.setUrl(jsonObject.getString("url"));
                nasaImage.setHdurl(jsonObject.getString("hdurl"));
                return nasaImage;
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error fetching NASA image data: " + e.getMessage());
        }

        return null;
    }


    @Override
    protected void onPostExecute(NasaImage nasaImage) {
        Log.d("NasaImageTask", "onPostExecute: Task completed");
        progressBar.setVisibility(View.GONE);

        if (nasaImage != null) {
            listViewImages.setVisibility(View.VISIBLE);

            NasaImageAdapter adapter = (NasaImageAdapter) listViewImages.getAdapter();
            adapter.add(nasaImage);
        } else {
            Toast.makeText(context, "Failed to fetch NASA image data", Toast.LENGTH_SHORT).show();
        }
    }

}

