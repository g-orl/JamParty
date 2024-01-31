package fr.eurecom.jamparty.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;

import fr.eurecom.jamparty.MainActivity;
import fr.eurecom.jamparty.R;
import fr.eurecom.jamparty.objects.Room;
import fr.eurecom.jamparty.objects.RoomUserManager;
import fr.eurecom.jamparty.objects.Song;
import fr.eurecom.jamparty.objects.SongTimer;
import fr.eurecom.jamparty.objects.User;
import fr.eurecom.jamparty.objects.adapters.SongAdapter;
import fr.eurecom.jamparty.SpotifyApiTask;
import fr.eurecom.jamparty.objects.Suggestion;
import fr.eurecom.jamparty.objects.adapters.SuggestionAdapter;
import fr.eurecom.jamparty.databinding.FragmentRoomBinding;

public class RoomFragment extends Fragment {
    public ArrayList<Song> songs;
    private FragmentRoomBinding binding;
    public NavController fragmentController;
    public SuggestionAdapter suggestionAdapter;

    public Room room;
    private Thread roomChecker;
    private boolean killThread = false;

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

        SongAdapter adapter = new SongAdapter(songs, this);
        suggestionAdapter = new SuggestionAdapter(room.getQueue(), this);
        suggestionAdapter.setRoom(room);
        binding.songList.setAdapter(adapter);
        binding.suggestions.setAdapter(suggestionAdapter);

        DatabaseReference roomRef = MainActivity.ROOMS_REF.child(this.room.getId());

        roomRef.addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String childKey = snapshot.getKey();
                if (childKey.equals("terminated")) {
                    boolean childValue = snapshot.getValue(Boolean.class);
                    if (childValue)
                        fragmentController.navigate(R.id.navigation_home);
                }
            }
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        roomRef.child("queue").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("CHILD", "Updating entire array list");
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    room.setQueue(childSnapshot.getValue(ArrayList.class));
                    suggestionAdapter.notifyDataSetChanged();
                }
            }
            @Override public void onCancelled(DatabaseError databaseError) { }
        });

        roomRef.child("queue").addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                Suggestion suggestion = dataSnapshot.getValue(Suggestion.class);
                Log.i("CHILD INDEX", "Added " + suggestion.getName() + " | size = "+room.getQueue().size());
                if (room.findSuggestion(suggestion) == null)
                    room.addToQueue(suggestion);
                suggestionAdapter.notifyDataSetChanged();
                // need to remove this suggestion from my suggestion queue
                //if (RoomUserManager.userOwnsRoom(MainActivity.getUser(), room)) {
                    Log.i("TIMER", "Starting timer for "+suggestion.getName()+" | "+suggestion.getAuthor());
                    SongTimer task = new SongTimer(room, room.findSuggestion(suggestion));
                    new Timer().schedule(task, 15000);
                //}
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                Suggestion suggestion = dataSnapshot.getValue(Suggestion.class);
                Log.i("CHILD INDEX", "Changed ("+")" + suggestion.getName() + " | size = "+room.getQueue().size());
                // BE AWARE THAT ALL SUGGESTION CHANGES
                Suggestion tmp = room.findSuggestion(suggestion);
                if (tmp != null) {
                    tmp.setAuthor(suggestion.getAuthor());
                    tmp.setName(suggestion.getName());
                    tmp.setUri(suggestion.getUri());
                    tmp.setUserId(suggestion.getUri());
                    tmp.setVotesDown(suggestion.getVotesDown());
                    tmp.setImage_url(suggestion.getImage_url());
                } else {
                    room.addToQueue(suggestion);
                }
                suggestionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Suggestion suggestion = dataSnapshot.getValue(Suggestion.class);
                Log.i("CHILD INDEX", "Remove " + suggestion.getName() + " | size = "+room.getQueue().size());
                // need to remove this suggestion from my suggestion queue
                room.removeFromQueue(suggestion);
                suggestionAdapter.notifyDataSetChanged();
                Log.i("CHILD INDEX", "Size end of remove = "+room.getQueue().size());
                // TODO save the song into the played songs
            }
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.i("TIMER", "MOVED PORCODDIOOOOOOO");
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { }
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
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) {
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

        binding.textRoomName.setText(room.getName());

        Toast endToast = Toast.makeText(requireContext(), "The room is terminated", Toast.LENGTH_SHORT);
        Runnable runThread = () -> {
            long sleepTime = 1000;
            while (!killThread) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (room == null) continue;
                User user = MainActivity.getUser();
                long currentTime = System.currentTimeMillis();
                long closeTime = room.getCloseTime();
                Log.i("ROOM TERM", String.valueOf(closeTime-currentTime));
                if (currentTime >= closeTime) {
                    RoomUserManager.userExitRoom(user, room);
                    endToast.show();
                    break;
                }
            }
        };
        roomChecker = new Thread(runThread);
        roomChecker.start();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        killThread = true;
        binding = null;
    }


    /*public void showPopupWindow(View anchorView, Suggestion suggestion) {
        // Create a popup window
        View popupView = getLayoutInflater().inflate(R.layout.suggestion_popup, null);

        // Create and configure the popup window
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);

        // Show the popup window at the specified location
        popupWindow.showAsDropDown(anchorView, 75, -anchorView.getHeight()+75);

        popupView.findViewById(R.id.dislike_button).setOnClickListener(new View.OnClickListener() {
            private boolean isDisliked = false;
            @Override
            public void onClick(View v) {
                // dislike the song
                if (isDisliked) {
                    suggestion.upvote();
                    popupView.findViewById(R.id.dislike_button).setBackground(getContext().getDrawable(R.drawable.thumb_nobg));
                } else {
                    suggestion.downvote();
                    popupView.findViewById(R.id.dislike_button).setBackground(getContext().getDrawable(R.drawable.thumb_red_nobg));
                }
                isDisliked = !isDisliked;
                room.pushSongsToDb();
            }
        });
    }*/
}
