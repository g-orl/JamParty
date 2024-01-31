package fr.eurecom.jamparty;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class SpotifyApiTask extends AsyncTask<String, String, String> {

    public interface AsyncTaskListener{
        void onTaskComplete(String result);
    }

    private AsyncTaskListener listener;

    private static final String TAG = SpotifyApiTask.class.getSimpleName();

    public SpotifyApiTask(AsyncTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        String urlString = params[0];

        try {
            URL url = new URL(urlString);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", "Bearer " + MainActivity.ACCESS_TOKEN);
            urlConnection.setInstanceFollowRedirects(true);

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read and process the response
                try (InputStream inputStream = urlConnection.getInputStream()) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    System.out.println("Queue response: " + response.toString());
                    urlConnection.disconnect();
                    return response.toString();
                }
            } else {
                // Read and log the error response
                try (InputStream errorStream = urlConnection.getErrorStream()) {
                    if (errorStream != null) {
                        BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
                        String line;
                        StringBuilder errorResponse = new StringBuilder();
                        while ((line = errorReader.readLine()) != null) {
                            errorResponse.append(line);
                        }
                        System.err.println("Error response: " + errorResponse.toString());
                        urlConnection.disconnect();
                        return errorResponse.toString();
                    } else {
                        System.err.println("No error response stream available.");
                        urlConnection.disconnect();
                        return null;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        // Handle the result (JSON response) here
        listener.onTaskComplete(result);
    }
}

