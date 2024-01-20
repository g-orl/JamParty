package fr.eurecom.jamparty;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fr.eurecom.jamparty.ui.fragments.RoomFragment;
import fr.eurecom.jamparty.ui.home.HomeFragment;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private List<Song> songs;
    private RoomFragment caller;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView memorySongImage;
        public TextView memorySongName;
        public TextView memorySongArtist;

        public ViewHolder(View itemView) {
            super(itemView);
            memorySongName = itemView.findViewById(R.id.memory_song_name);
            memorySongArtist = itemView.findViewById(R.id.memory_song_artist);
            memorySongImage = itemView.findViewById(R.id.memory_song_image);
        }
    }

    public SongAdapter(List<Song> songs, RoomFragment caller) {
        this.songs = songs;
        this.caller = caller;
    }

    @NonNull
    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.memory_song, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SongAdapter.ViewHolder holder, int position) {
        Song song = songs.get(position);
        // here you can set the callback method
        holder.memorySongName.setText(song.getName());
        holder.memorySongArtist.setText(song.getAuthor());

        Glide.with(holder.itemView).load(song.getImage_url()).into(holder.memorySongImage);
        // TODO add album image to song

        holder.memorySongImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance(MainActivity.DATABASE_URL);
                String roomId = caller.getArguments().get("room_id").toString();


                Toast.makeText(caller.getContext(), "Added: " + song.getName() + " radio", Toast.LENGTH_SHORT).show();
                // TODO substitute with string
                DatabaseReference rooms = database.getReference("Rooms");
                rooms.child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Room room = snapshot.getValue(Room.class);
                        room.addToQueue(new Suggestion(song.getName(), song.getAuthor(), song.getUri(), MainActivity.USER_ID));
                        // need to send changes to db
                        rooms.child(roomId).setValue(room);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Database Error", error.getMessage());
                    }
                });

                caller.suggestions.add(new Suggestion(song.getName(), song.getAuthor(), song.getUri(), MainActivity.USER_ID));
                caller.suggestionAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }
}
