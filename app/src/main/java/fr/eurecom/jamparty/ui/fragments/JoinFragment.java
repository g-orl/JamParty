package fr.eurecom.jamparty.ui.fragments;

import fr.eurecom.jamparty.MainActivity;
import fr.eurecom.jamparty.R;
import fr.eurecom.jamparty.Room;
import fr.eurecom.jamparty.RoomAdapter;
import fr.eurecom.jamparty.User;
import fr.eurecom.jamparty.ui.home.HomeViewModel;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class JoinFragment extends DialogFragment {
    private Location location;
    private FirebaseDatabase database;
    public static final double MAX_DIST_IN_METERS = 1000;

    public static String TAG = "JoinRoomDialog";

    private HomeViewModel homeViewModel;

    public JoinFragment(HomeViewModel homeViewModel) {
        super(R.layout.fragment_join);
        this.homeViewModel = homeViewModel;
    }

    public HomeViewModel getHomeViewModel() {
        return homeViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();
        if (activity != null && activity instanceof MainActivity)
            this.location = ((MainActivity) getActivity()).getLocation();
        this.database = FirebaseDatabase.getInstance(MainActivity.DATABASE_URL);
    }

    /*@Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Button closeBtn = view.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(v -> dismiss());
        if (location == null)
            return;

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Log.i("LatLng", latitude + " " + longitude);

        DatabaseReference rooms = database.getReference("Rooms");
        Query query = rooms.orderByChild("owner/latitude")
                .startAt(latitude - 0.1)
                .endAt(latitude + 0.1);
        final ArrayList<Room> roomsArray = new ArrayList<>();
        final RoomAdapter adapter = new RoomAdapter(requireContext(), roomsArray, this);
        final ListView roomsList = view.findViewById(R.id.roomsList);
        roomsList.setAdapter(adapter);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                roomsArray.clear();
                adapter.clear();
                for (DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {
                    Log.i("Found: ", roomSnapshot.child("id").getValue(String.class));
                    double roomLat = roomSnapshot.child("owner/latitude").getValue(Double.class);
                    double roomLng = roomSnapshot.child("owner/longitude").getValue(Double.class);
                    float[] dist = new float[1];
                    Location.distanceBetween(location.getLatitude(), location.getLongitude(), roomLat, roomLng, dist);
                    if(dist[0] <= MAX_DIST_IN_METERS) {
                        User owner = new User(roomSnapshot.child("owner/id").getValue(String.class),
                                roomLat,
                                roomLng);
                        Room room = new Room(roomSnapshot.child("id").getValue(String.class),
                                roomSnapshot.child("name").getValue(String.class),
                                roomSnapshot.child("hash").getValue(String.class),
                                owner);
                        roomsArray.add(room);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Database Error", databaseError.getMessage());
            }
        });
    }*/
}
