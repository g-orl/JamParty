package fr.eurecom.jamparty;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import fr.eurecom.jamparty.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    private Location location;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    public static String DATABASE_URL = "https://jamparty-c5fc6-default-rtdb.europe-west1.firebasedatabase.app/";

    public static String ACCESS_TOKEN = "";
    public static final String CLIENT_ID = "576209ee8d91417fbfc0e5ee2df80982";
    public static String USER_ID = "576209ee8d91417fbfc0e5ee2df80982";
    public static final String REDIRECT_URI = "fr.eurecom.jamparty://logged";
    public static SpotifyAppRemote mSpotifyAppRemote;
    public static final int REQUEST_CODE = 1337;
    public static final String ROOMS_TABLE = "Rooms";
    public static final String USERS_TABLE = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        addDummyData(false);
        setupLocationProvider();
    }

    private void setupLocationProvider() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Location Permission: ", "To be checked");
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else
            Log.i("Location Permission: ", "GRANTED");
        locationRequest = new LocationRequest.Builder(10000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location loc : locationResult.getLocations()) {
                    location = loc;
                    //Log.i("LOCATION", "Updated to " + location.getLatitude() + ", " + location.getLongitude());
                }
            }
        };


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        // fusedLocationClient.requestLocationUpdates(locationRequest, this, null);

        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
        addDummyData(false);
    }

    private void addDummyData(boolean execute) {
        if(!execute) return;
        FirebaseDatabase db = FirebaseDatabase.getInstance(DATABASE_URL);
        DatabaseReference usersRef = db.getReference(USERS_TABLE);
        DatabaseReference roomsRef = db.getReference(ROOMS_TABLE);

        String key = "user"+usersRef.push().getKey();
        User user1 = new User(key, 43.5722, 7.1030);
        key = "user"+usersRef.push().getKey();
        User user2 = new User(key, 43.6144, 7.0711);
        key = "user"+usersRef.push().getKey();
        User user3 = new User(key, 43.5841, 7.1184);


        key = "room"+roomsRef.push().getKey();
        Room room1 = new Room(key, "ItaRoom", "chiave");
        key = "room"+roomsRef.push().getKey();
        Room room2 = new Room(key, "FraRoom", "chiave_in_francese");
        key = "room"+roomsRef.push().getKey();
        Room room3 = new Room(key, "EngRoom", "key");

        RoomUserManager.userJoinRoom(user1, room1, true);
        RoomUserManager.userJoinRoom(user2, room2, true);
        RoomUserManager.userJoinRoom(user3, room3, true);

        usersRef.child(user1.getId()).setValue(user1);
        usersRef.child(user2.getId()).setValue(user2);
        usersRef.child(user3.getId()).setValue(user3);

        roomsRef.child(room1.getId()).setValue(room1);
        roomsRef.child(room2.getId()).setValue(room2);
        roomsRef.child(room3.getId()).setValue(room3);
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Location Permission: ", "To be checked");
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else
            Log.i("Location Permission: ", "GRANTED");
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Location Permission: ", "To be checked");
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else
            Log.i("Location Permission: ", "GRANTED");
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    @Override
    public void onStop() {
        super.onStop();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void connected(){

        // get user id
        String spotifyEndpointUrl = "https://api.spotify.com/v1/me";

        // Execute the AsyncTask
        new SpotifyApiTask(new SpotifyApiTask.AsyncTaskListener() {
            @Override
            public void onTaskComplete(String result) {

                if(result != null){
                    try {
                        // Create an ObjectMapper
                        ObjectMapper objectMapper = new ObjectMapper();

                        // Parse JSON string to JsonNode
                        JsonNode jsonNode = objectMapper.readTree(result);

                        String id = jsonNode.get("id").asText();
                        MainActivity.USER_ID = id;
                        Toast.makeText(MainActivity.this, "User id: " + MainActivity.USER_ID, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).execute(spotifyEndpointUrl);

    }


    /*
    public void spawnJoin(View v) {
        if(location == null)
            return;
        JoinFragment floatingFragment = new JoinFragment();
        floatingFragment.show(getSupportFragmentManager(), "YourFloatingFragmentTag");
    }
    */
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);
            System.out.println(response.getAccessToken());
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response

                    ConnectionParams connectionParams =
                            new ConnectionParams.Builder(CLIENT_ID)
                                    .setRedirectUri(REDIRECT_URI)
                                    .showAuthView(true)
                                    .build();

                    SpotifyAppRemote.connect(this, connectionParams,
                            new Connector.ConnectionListener() {

                                @Override
                                public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                                    mSpotifyAppRemote = spotifyAppRemote;
                                    MainActivity.ACCESS_TOKEN = response.getAccessToken();
                                    // Now you can start interacting with App Remote
                                    connected();
                                }

                                @Override
                                public void onFailure(Throwable throwable) {
                                    Log.e("MainActivity", throwable.getMessage(), throwable);
                                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                                    // Something went wrong when attempting to connect! Handle errors here
                                }
                            });
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }

}