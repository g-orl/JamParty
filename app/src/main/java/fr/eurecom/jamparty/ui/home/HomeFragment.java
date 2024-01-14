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

public class HomeFragment extends Fragment {
    private Location location;
    private FirebaseDatabase database;
    public static final double MAX_DIST_IN_METERS = 1000;
    public static String TAG = "JoinRoomDialog";
    private FragmentHomeBinding binding;
    public NavController fragmentController;
    public LayoutInflater inflater;
    public ViewGroup container;

    public void enterRoom(String name){
        Bundle bundle = new Bundle();
        bundle.putString("room_name", name);
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

        DatabaseReference rooms = database.getReference("Rooms");
        Query query = rooms.orderByChild("owner/latitude")
                .startAt(latitude - 0.1)
                .endAt(latitude + 0.1);
        final ArrayList<Room> roomsArray = new ArrayList<>();

        final RoomAdapter adapter = new RoomAdapter(requireContext(), roomsArray, this);
        final ListView roomsList = view.findViewById(R.id.roomsPosition);
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
    }
}

