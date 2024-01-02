package fr.eurecom.jamparty;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

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

import fr.eurecom.jamparty.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    private Location location;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    public static String DATABASE_URL = "https://jamparty-c5fc6-default-rtdb.europe-west1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // addDummyData();
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
                    Log.i("LOCATION", "Updated to " + location.getLatitude() + ", " + location.getLongitude());
                }
            }
        };


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        // fusedLocationClient.requestLocationUpdates(locationRequest, this, null);

        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
        addDummyData(true);
    }

    private void addDummyData(boolean execute) {
        if(!execute) return;
        FirebaseDatabase db = FirebaseDatabase.getInstance(DATABASE_URL);
        DatabaseReference usersRef = db.getReference("UsersNew");
        DatabaseReference roomsRef = db.getReference("RoomsNew");

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

}