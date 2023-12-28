package fr.eurecom.jamparty.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import fr.eurecom.jamparty.MainActivity;
import fr.eurecom.jamparty.Song;
import fr.eurecom.jamparty.SongAdapter;
import fr.eurecom.jamparty.SpotifyApiTask;
import fr.eurecom.jamparty.databinding.FragmentHomeBinding;
import fr.eurecom.jamparty.ui.fragments.CreateFragment;
import fr.eurecom.jamparty.ui.fragments.JoinFragment;

// mapper
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private ArrayList<Song> songs;
    private SongAdapter adapter;

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        songs = new ArrayList<>();
        adapter = new SongAdapter(getContext(), songs, this);
        binding.songList.setAdapter(adapter);

        ImageButton playButton = binding.playButton;
        ImageButton backButton = binding.backButton;
        ImageButton nextButton = binding.nextButton;
        Button exitButton = binding.buttonExit;

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeViewModel.setRoomName("");
                homeViewModel.setInRoom(false);
            }
        });

        binding.editTextText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                // clear the previous songs present in the array
                songs.clear();
                adapter.clear();

                String textTyped = binding.editTextText.getText().toString();
                if(textTyped.length() == 0) return;
                String spotifyEndpointUrl = "https://api.spotify.com/v1/search?q=" + textTyped + "&type=track&market=FR";

                // Execute the AsyncTask

                new SpotifyApiTask(new SpotifyApiTask.AsyncTaskListener() {
                    @Override
                    public void onTaskComplete(String result) {
                        // TODO show the retreived text in the text box
                        if(result != null){
                            try {
                                // Create an ObjectMapper
                                ObjectMapper objectMapper = new ObjectMapper();

                                // Parse JSON string to JsonNode
                                JsonNode jsonNode = objectMapper.readTree(result);

                                for(int i = 0; i< jsonNode.get("tracks").get("items").size(); i++){
                                    songs.add(new Song(jsonNode.get("tracks").get("items").get(i).get("name").asText()));
                                }

                                adapter.notifyDataSetChanged();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).execute(spotifyEndpointUrl);

                // Log.i("TEXT", binding.editTextText.getText().toString());
            }
        });
        binding.buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateFragment().show(getChildFragmentManager(), CreateFragment.TAG);
            }
        });

        binding.buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                homeViewModel.setInRoom(true);
//                homeViewModel.setRoomName("Room1");
                if (((MainActivity)getActivity()).getLocation()!=null){
                    new JoinFragment(homeViewModel).show(getChildFragmentManager(), JoinFragment.TAG);
                }
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Clicked play!", Toast.LENGTH_SHORT).show();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Clicked next!", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Clicked back!", Toast.LENGTH_SHORT).show();
            }
        });

        homeViewModel.getInRoom().observe(this.getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.buttonExit.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
                binding.playerArea.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
                binding.buttonCreate.setVisibility(aBoolean ? View.GONE : View.VISIBLE);
                binding.buttonJoin.setVisibility(aBoolean ? View.GONE : View.VISIBLE);
                binding.textRoomName.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
            }
        });

        homeViewModel.getRoomName().observe(this.getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.textRoomName.setText(s);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}