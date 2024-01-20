package fr.eurecom.jamparty.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import fr.eurecom.jamparty.MainActivity;
import fr.eurecom.jamparty.R;
import fr.eurecom.jamparty.objects.Room;
import fr.eurecom.jamparty.objects.Song;
import fr.eurecom.jamparty.objects.adapters.SongAdapter;
import fr.eurecom.jamparty.SpotifyApiTask;
import fr.eurecom.jamparty.objects.Suggestion;
import fr.eurecom.jamparty.objects.adapters.SuggestionAdapter;
import fr.eurecom.jamparty.databinding.FragmentRoomBinding;

public class RoomFragment  extends Fragment {
    private ArrayList<Song> songs;
    public ArrayList<Suggestion> suggestions;
    private FragmentRoomBinding binding;
    public NavController fragmentController;
    private SuggestionAdapter suggestionAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentRoomBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.fragmentController = NavHostFragment.findNavController(this);

        songs = new ArrayList<>();
        suggestions = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance(MainActivity.DATABASE_URL);
        DatabaseReference rooms = database.getReference("Rooms");

        SongAdapter adapter = new SongAdapter(getContext(), songs, this);
        suggestionAdapter = new SuggestionAdapter(getContext(), suggestions, this);
        binding.songList.setAdapter(adapter);
        binding.suggestions.setAdapter(suggestionAdapter);

        String room_id = getArguments().getString("room_id");

        rooms.child(room_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Room room = snapshot.getValue(Room.class);
                suggestionAdapter.setRoom(room);
                suggestions.addAll(room.getQueue());
                suggestionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Database Error", error.getMessage());
            }
        });

        /*ImageButton playButton = binding.playButton;
        ImageButton backButton = binding.backButton;
        ImageButton nextButton = binding.nextButton;*/
        Button exitButton = binding.buttonExit;

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO go back to home fragment
                fragmentController.navigate(R.id.navigation_home);
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
                                Song temp;
                                for(int i = 0; i< jsonNode.get("tracks").get("items").size(); i++){
                                    temp = new Song(
                                            jsonNode.get("tracks").get("items").get(i).get("name").asText(),
                                            jsonNode.get("tracks").get("items").get(i).get("artists").get(0).get("name").asText(),
                                            jsonNode.get("tracks").get("items").get(i).get("uri").asText());

                                    temp.setImage_url(jsonNode.get("tracks").get("items").get(i).get("album").get("images").get(0).get("url").asText());

                                    songs.add(temp);
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

        /*playButton.setOnClickListener(new View.OnClickListener() {
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
        });*/

        binding.textRoomName.setText(getArguments().getString("room_name"));

        return root;
    }

    public void addSuggestion(Suggestion suggestion) {
        suggestions.add(suggestion);
        suggestionAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
