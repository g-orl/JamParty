package fr.eurecom.jamparty.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import fr.eurecom.jamparty.MainActivity;
import fr.eurecom.jamparty.objects.adapters.MemoryAdapter;
import fr.eurecom.jamparty.objects.Room;
import fr.eurecom.jamparty.objects.adapters.RoomAdapter;
import fr.eurecom.jamparty.objects.User;
import fr.eurecom.jamparty.databinding.FragmentDashboardBinding;


public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    private NavController controller;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        controller = NavHostFragment.findNavController(this);

        RecyclerView recyclerView = binding.recyclerView;
        ArrayList<Room> rooms = new ArrayList<>();
        MemoryAdapter memoryAdapter = new MemoryAdapter(rooms, controller);
        recyclerView.setAdapter(memoryAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        User loggedInUser = MainActivity.getUser();
        if (loggedInUser != null) {
            ArrayList<String> history = loggedInUser.getRoomIdsHistory();
            MainActivity.ROOMS_REF.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot roomSnapshot : snapshot.getChildren()) {
                        Room room = roomSnapshot.getValue(Room.class);
                        if (history.contains(room.getId()))
                            rooms.add(room);
                    }
                    // sort in reverse order
                    rooms.sort((r1, r2) -> (int)(r2.getCreationTime()-r1.getCreationTime()));
                    memoryAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Database Error", error.getMessage());
                }
            });
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

