package fr.eurecom.jamparty.objects.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
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
import fr.eurecom.jamparty.objects.Suggestion;
import fr.eurecom.jamparty.ui.fragments.RoomFragment;


public class SongAdapter extends ArrayAdapter {

    private RoomFragment caller;

    public SongAdapter(@NonNull Context context, ArrayList<Song> songs, RoomFragment caller) {
        super(context, 0, songs);
        this.caller = caller;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Song song = (Song) getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_song, parent, false);
        }
        TextView nameTxt = convertView.findViewById(R.id.songName);
        TextView authorTxt = convertView.findViewById(R.id.songAuthor);
        ImageView image = convertView.findViewById(R.id.songImage);
        EditText editText = caller.getView().findViewById(R.id.editTextText);

        nameTxt.setText(song.getName());
        authorTxt.setText(song.getAuthor());
        Glide.with(caller.getView()).load(song.getImage_url()).into(image);

        convertView.findViewById(R.id.songContainer).setOnClickListener(v -> {
            // add the clicked song to the suggestion queue

            FirebaseDatabase database = FirebaseDatabase.getInstance(MainActivity.DATABASE_URL);
            String roomId = this.caller.getArguments().get("room_id").toString();


            Toast.makeText(parent.getContext(), "Added: " + song.getName() + " radio", Toast.LENGTH_SHORT).show();
            // TODO substitute with string
            DatabaseReference rooms = database.getReference("Rooms");
            rooms.child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Room room = snapshot.getValue(Room.class);
                    if(room != null) {
                        room.addToQueue(new Suggestion(song.getName(), song.getAuthor(), song.getUri(), MainActivity.USER_ID));
                        // need to send changes to db
                        rooms.child(roomId).setValue(room);
                    } else {
                        Log.e("Room", "Room is null when adding a song");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Database Error", error.getMessage());
                }
            });

            this.caller.addSuggestion(new Suggestion(song.getName(), song.getAuthor(), song.getUri(), MainActivity.USER_ID));

            editText.setText("");
            this.clear();
        });

        return convertView;
    }

}
