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
            try {
                InputStream in = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder stringBuilder = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {

                    stringBuilder.append(line).append("\n");
                }

                return stringBuilder.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error fetching data from Spotify API", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        // Handle the result (JSON response) here

        if (result != null) {
            Log.d(TAG, "Spotify API response: " + result);
            // Parse and process the JSON response
        } else {
            Log.e(TAG, "Failed to fetch data from Spotify API");
        }
        listener.onTaskComplete(result);
    }
}

