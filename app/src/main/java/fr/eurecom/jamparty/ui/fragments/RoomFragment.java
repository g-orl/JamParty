package fr.eurecom.jamparty.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import fr.eurecom.jamparty.objects.RoomUserManager;
import fr.eurecom.jamparty.objects.Song;
import fr.eurecom.jamparty.objects.User;
import fr.eurecom.jamparty.objects.adapters.SongAdapter;
import fr.eurecom.jamparty.SpotifyApiTask;
import fr.eurecom.jamparty.objects.Suggestion;
import fr.eurecom.jamparty.objects.adapters.SuggestionAdapter;
import fr.eurecom.jamparty.databinding.FragmentRoomBinding;
import fr.eurecom.jamparty.ui.home.HomeFragment;

public class RoomFragment extends Fragment implements ThreadCompleteListener {
    public ArrayList<Song> songs;
    private FragmentRoomBinding binding;
    public NavController fragmentController;
    public SuggestionAdapter suggestionAdapter;
    public Room room;
    private PopupWindow popupWindow;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        this.songs = new ArrayList<>();
        binding = FragmentRoomBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.room = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            this.room = getArguments().getParcelable("room", Room.class);
        }

        this.fragmentController = NavHostFragment.findNavController(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance(MainActivity.DATABASE_URL);
        DatabaseReference rooms = database.getReference("Rooms");

        SongAdapter adapter = new SongAdapter(songs, this);
        suggestionAdapter = new SuggestionAdapter(room.getQueue(), this);
        binding.songList.setAdapter(adapter);
        binding.suggestions.setAdapter(suggestionAdapter);

        rooms.child(this.room.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // TODO: update adapters and other stuff here!!!
                Room room = snapshot.getValue(Room.class);
                suggestionAdapter.setRoom(room);

                suggestionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Database Error", error.getMessage());
            }
        });

        LinearLayoutManager songLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.songList.setLayoutManager(songLayoutManager);

        LinearLayoutManager suggestionLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.suggestions.setLayoutManager(suggestionLayoutManager);


        Button exitButton = binding.buttonExit;

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO go back to home fragment
                fragmentController.navigate(R.id.navigation_home);
                RoomUserManager.userExitRoom(MainActivity.getUser(), room);

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
                                            jsonNode.get("tracks").get("items").get(i).get("uri").asText(),
                                            jsonNode.get("tracks").get("items").get(i).get("album").get("images").get(0).get("url").asText()
                                            );

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

        binding.textRoomName.setText(room.getName());

        Toast endToast = Toast.makeText(requireContext(), "The room is terminated", Toast.LENGTH_SHORT);
        class RoomChecker extends Thread {
            private ThreadCompleteListener listener;

            public RoomChecker(ThreadCompleteListener listener) {
                this.listener = listener;
            }

            @Override
            public void run() {
                long sleepTime = 1000;
                while (true) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (room == null) continue;
                    User user = MainActivity.getUser();
                    long currentTime = System.currentTimeMillis();
                    long closeTime = room.getCloseTime();
                    Log.i("RoomEnd", String.valueOf(closeTime-currentTime));
                    if (currentTime >= closeTime) {
                        if (RoomUserManager.userOwnsRoom(user, room)) {
                            room.setTerminated(true);
                            room.pushTerminatedToDb();
                        }
                        RoomUserManager.userExitRoom(user, room);
                        endToast.show();
                        break;
                    }
                }
                if (listener != null) {
                    listener.notifyThreadComplete(this);
                }
            }
        }
        RoomChecker roomChecker = new RoomChecker(this);
        roomChecker.start();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public void showPopupWindow(View anchorView, Suggestion suggestion) {
        // Create a popup window
        View popupView = getLayoutInflater().inflate(R.layout.suggestion_popup, null);

        // Create and configure the popup window
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(getContext().getDrawable(R.drawable.popup_background));

        // Show the popup window at the specified location
        popupWindow.showAsDropDown(anchorView, 0, -anchorView.getHeight());

        popupView.findViewById(R.id.dislike_button).setOnClickListener(new View.OnClickListener() {
            private boolean isDisliked = false;
            @Override
            public void onClick(View v) {
                // dislike the song
                if (isDisliked) {
                    suggestion.upvote();
                    popupWindow.setBackgroundDrawable(getContext().getDrawable(R.drawable.thumb_nobg));
                } else {
                    suggestion.downvote();
                    popupWindow.setBackgroundDrawable(getContext().getDrawable(R.drawable.thumb_red_nobg));
                }
                isDisliked = !isDisliked;
                room.pushSongsToDb();
            }
        });
    }

    @Override
    public void notifyThreadComplete(Thread thread) {
        fragmentController.navigate(R.id.navigation_home);
    }
}
