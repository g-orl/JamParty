package fr.eurecom.jamparty;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SpotifyApiPostTask extends AsyncTask<String, Void, String> {

    public interface AsyncTaskListener {
        void onTaskComplete(String result);
    }

    private AsyncTaskListener listener;
    private static final String TAG = SpotifyApiPostTask.class.getSimpleName();

    public SpotifyApiPostTask(AsyncTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        if (params == null || params.length == 0) {
            return null;
        }

        String trackUri = params[0];

        try {
            // Construct the request URL with the track URI as a query parameter
            URL url = new URL("https://api.spotify.com/v1/me/player/queue?uri=" + trackUri);

            // Open the connection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Set the request method and headers
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Authorization", "Bearer " + MainActivity.ACCESS_TOKEN);

            // Get the response code
            int responseCode = urlConnection.getResponseCode();

            // Read and process the response
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.i("GOOD_HTTP", urlConnection.getResponseMessage());
                try (InputStream inputStream = urlConnection.getInputStream()) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    return response.toString();
                }
            } else {
                Log.i("ERROR_HTTP", urlConnection.getResponseMessage());
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error adding track to Spotify queue", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        // Handle the result (JSON response) here
        listener.onTaskComplete(result);
    }
}
