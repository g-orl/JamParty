package fr.eurecom.jamparty;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.database.ValueEventListener;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import fr.eurecom.jamparty.databinding.ActivityMainBinding;
import fr.eurecom.jamparty.objects.Hasher;
import fr.eurecom.jamparty.objects.Room;
import fr.eurecom.jamparty.objects.RoomUserManager;
import fr.eurecom.jamparty.objects.User;


public class MainActivity extends AppCompatActivity {
    public static String DATABASE_URL = "https://jamparty-c5fc6-default-rtdb.europe-west1.firebasedatabase.app/";

    public static String ACCESS_TOKEN = "";
    public static final String CLIENT_ID = "576209ee8d91417fbfc0e5ee2df80982";
    public static String USER_ID = null;
    public static final String REDIRECT_URI = "fr.eurecom.jamparty://logged";
    public static SpotifyAppRemote mSpotifyAppRemote;
    public static final int REQUEST_CODE = 1337;
    public static final String ROOMS_TABLE = "Rooms";
    public static final String USERS_TABLE = "Users";
    private static User logged_in_user = null;
    public static final FirebaseDatabase DB = FirebaseDatabase.getInstance(DATABASE_URL);
    public static final DatabaseReference USERS_REF = DB.getReference(USERS_TABLE);
    public static final DatabaseReference ROOMS_REF = DB.getReference(ROOMS_TABLE);


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
        Room room1 = new Room(key, "LucaRoom", Hasher.hashString("andrea"), 16, 1000 * 60 * 60 * 4);
        key = "room"+roomsRef.push().getKey();
        Room room2 = new Room(key, "EureRoom", Hasher.hashString(""), 16, 1000 * 60 * 60 * 24 * 5);
        key = "room"+roomsRef.push().getKey();
        Room room3 = new Room(key, "RoomGare", Hasher.hashString(""), 16, 1000 * 60 * 60 * 24 * 100);

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

    private void connected(){
        // get user id
        String spotifyEndpointUrl = "https://api.spotify.com/v1/me";
        // get navigator
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

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
                        USER_ID = id;
                        USERS_REF.child(USER_ID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                if (user == null) user = new User();
                                logged_in_user = user;
                                if (user.getId() == null) {
                                    user.setId(USER_ID);
                                    USERS_REF.child(USER_ID).setValue(user);
                                }
                                if (user.getCurrentRoomId() == null)
                                    navController.navigate(R.id.navigation_home);
                                else {
                                    ROOMS_REF.child(user.getCurrentRoomId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Room room = snapshot.getValue(Room.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putParcelable("room", room);
                                            RoomUserManager.userJoinRoom(MainActivity.getUser(), room, false);
                                            navController.navigate(R.id.navigation_room, bundle);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) { }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });

                        Toast.makeText(MainActivity.this, "Welcome " + MainActivity.USER_ID, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).execute(spotifyEndpointUrl);

    }

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

    public static boolean isLoggedIn() {
        return USER_ID != null;
    }

    public static void resetUserId() { USER_ID = null; }

    public static User getUser() { return logged_in_user; }
}