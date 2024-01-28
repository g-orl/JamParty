package fr.eurecom.jamparty.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import fr.eurecom.jamparty.MainActivity;
import fr.eurecom.jamparty.R;
import fr.eurecom.jamparty.objects.Room;
import fr.eurecom.jamparty.objects.adapters.RoomAdapter;
import fr.eurecom.jamparty.objects.User;
import fr.eurecom.jamparty.databinding.FragmentHomeBinding;
import fr.eurecom.jamparty.ui.fragments.CreateFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HomeFragment extends Fragment {
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private Location location;
    private FirebaseDatabase database;
    public static final double MAX_DIST_IN_METERS = 1000;
    public static String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    public NavController fragmentController;
    public LayoutInflater inflater;
    public ViewGroup container;
    private View view;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        this.fragmentController = NavHostFragment.findNavController(this);
        this.database = FirebaseDatabase.getInstance(MainActivity.DATABASE_URL);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.buttonCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateFragment().show(getChildFragmentManager(), CreateFragment.TAG);
            }
        });
        if (MainActivity.isLoggedIn()) {
            setupLocationProvider();
        } else {
            fragmentController.navigate(R.id.navigation_profile);
        }
        return root;
    }

    private void setupLocationProvider() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Location Permission: ", "To be checked");
            ActivityCompat.requestPermissions(requireActivity(),
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
                    if(location == null || loc.distanceTo(location) > 5) {
                        location = loc;
                        onLocationChanged();
                    }
                }
            }
        };

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        this.view = view;
    }

    public void onLocationChanged() {
        ProgressBar spinner = view.findViewById(R.id.progressBar);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Log.i("LatLng", latitude + " " + longitude);

        final ArrayList<Room> roomsArray = new ArrayList<>();
        final RoomAdapter roomAdapter = new RoomAdapter(roomsArray, this.fragmentController, MainActivity.isLoggedIn());

        final RecyclerView recyclerView = view.findViewById(R.id.home_room_list);
        recyclerView.setAdapter(roomAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        DatabaseReference usersRef = database.getReference(MainActivity.USERS_TABLE);
        DatabaseReference roomsRef = database.getReference(MainActivity.ROOMS_TABLE);
        usersRef.orderByChild("latitude")
                .startAt(latitude - 0.1)
                .endAt(latitude + 0.1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        roomsArray.clear();

                        Log.i("Rooms", "Joined room search");
                        List<CompletableFuture<Void>> roomFutures = new ArrayList<>();

                        for(DataSnapshot userSnapshot : snapshot.getChildren()) {
                            User user = userSnapshot.getValue(User.class);
                            if(user != null && user.getOwnedRoomId() != null) {
                                double roomLat = userSnapshot.child("latitude").getValue(Double.class);
                                double roomLng = userSnapshot.child("longitude").getValue(Double.class);
                                float[] dist = new float[1];
                                Location.distanceBetween(location.getLatitude(), location.getLongitude(), roomLat, roomLng, dist);
                                if (dist[0] > MAX_DIST_IN_METERS) continue;

                                Log.i("Rooms", "Found room at " + roomLat + ", " + roomLng);
                                CompletableFuture<Void> roomFuture = new CompletableFuture<>();
                                roomsRef.child(user.getOwnedRoomId())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot roomSnapshot) {
                                                Room room = roomSnapshot.getValue(Room.class);
                                                roomsArray.add(room);
                                                roomFuture.complete(null);
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Log.e("Database Error", databaseError.getMessage());
                                                roomFuture.completeExceptionally(databaseError.toException()); // Complete with an exception
                                            }
                                        });
                                roomFutures.add(roomFuture);
                            }
                        }
                        // Wait for all CompletableFuture instances to complete
                        CompletableFuture<Void> allOf = CompletableFuture.allOf(roomFutures.toArray(new CompletableFuture[0]));
                        allOf.whenComplete((result, throwable) -> {
                            // This block is executed when all CompletableFuture instances are completed
                            roomAdapter.notifyDataSetChanged();
                            spinner.setVisibility(View.GONE);
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { Log.e("Database Error", databaseError.getMessage()); }
                });
        if (MainActivity.isLoggedIn()) {
            User user = MainActivity.getUser();
            user.setLatitude(latitude);
            user.setLongitude(longitude);
            user.pushLocationToDb();
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Location Permission: ", "To be checked");
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else
            Log.i("Location Permission: ", "GRANTED");
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
        if (location != null)
            onLocationChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Location Permission: ", "To be checked");
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else
            Log.i("Location Permission: ", "GRANTED");
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
        if (location != null)
            onLocationChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

}

