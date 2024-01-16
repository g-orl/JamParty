package fr.eurecom.jamparty.ui.home;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import fr.eurecom.jamparty.MainActivity;
import fr.eurecom.jamparty.R;
import fr.eurecom.jamparty.Room;
import fr.eurecom.jamparty.RoomAdapter;
import fr.eurecom.jamparty.User;
import fr.eurecom.jamparty.databinding.FragmentHomeBinding;
import fr.eurecom.jamparty.ui.fragments.CreateFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HomeFragment extends Fragment {
    private Location location;
    private FirebaseDatabase database;
    public static final double MAX_DIST_IN_METERS = 1000;
    public static String TAG = "JoinRoomDialog";
    private FragmentHomeBinding binding;
    public NavController fragmentController;
    public LayoutInflater inflater;
    public ViewGroup container;

    public void enterRoom(String name, String id){
        Bundle bundle = new Bundle();
        bundle.putString("room_name", name);
        bundle.putString("room_id", id);

        fragmentController.navigate(R.id.navigation_room, bundle);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;

        this.fragmentController = NavHostFragment.findNavController(this);

        Activity activity = getActivity();
        if (activity != null && activity instanceof MainActivity)
            this.location = ((MainActivity) getActivity()).getLocation();
        this.database = FirebaseDatabase.getInstance(MainActivity.DATABASE_URL);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        binding.buttonCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateFragment().show(getChildFragmentManager(), CreateFragment.TAG);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        if (location == null)
            return;

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Log.i("LatLng", latitude + " " + longitude);

        final ArrayList<Room> roomsArray = new ArrayList<>();
        final RoomAdapter adapter = new RoomAdapter(requireContext(), roomsArray, this);
        final ListView roomsList = view.findViewById(R.id.roomsPosition);
        roomsList.setAdapter(adapter);

        DatabaseReference usersRef = database.getReference("Users");
        DatabaseReference roomsRef = database.getReference("Rooms");
        usersRef.orderByChild("latitude")
                .startAt(latitude - 0.1)
                .endAt(latitude + 0.1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        roomsArray.clear();
                        adapter.clear();
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
                            adapter.notifyDataSetChanged();
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { Log.e("Database Error", databaseError.getMessage()); }
                });
    }
}

