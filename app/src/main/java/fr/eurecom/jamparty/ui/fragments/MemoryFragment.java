package fr.eurecom.jamparty.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.eurecom.jamparty.MemoryAdapter;
import fr.eurecom.jamparty.R;
import fr.eurecom.jamparty.Room;
import fr.eurecom.jamparty.Song;
import fr.eurecom.jamparty.SongMemoryAdapter;
import fr.eurecom.jamparty.Suggestion;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MemoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemoryFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static String TAG = "MemoryRoomDialog";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Room room;

    public MemoryFragment() {
        // Required empty public constructor
    }

    public MemoryFragment(Room room){
        this.room = room;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MemoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MemoryFragment newInstance(String param1, String param2) {
        MemoryFragment fragment = new MemoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        System.out.println("Dialog created");
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.fragment_memory, null);
        TextView memoryRoomName = view.findViewById(R.id.memory_room_name);
        memoryRoomName.setText(room.getName());
        RecyclerView recyclerView = view.findViewById(R.id.memory_song_list);
        SongMemoryAdapter songMemoryAdapter = new SongMemoryAdapter(this.room.getQueue(), getChildFragmentManager());

        recyclerView.setAdapter(songMemoryAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .setPositiveButton(getString(R.string.share_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == Dialog.BUTTON_POSITIVE){
                            // user wants to create the room
                            // TODO open dialog to share the memories to social media
                        }
                    }
                })
                .create();
    }
}