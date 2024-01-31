package fr.eurecom.jamparty.objects.adapters;

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
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import fr.eurecom.jamparty.R;
import fr.eurecom.jamparty.SpotifyApiPostTask;
import fr.eurecom.jamparty.objects.Room;
import fr.eurecom.jamparty.objects.Song;
import fr.eurecom.jamparty.objects.Suggestion;
import fr.eurecom.jamparty.ui.fragments.RoomFragment;
import fr.eurecom.jamparty.ui.home.HomeFragment;


public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {

    private RoomFragment caller;
    private Room room;
    private ArrayList<Suggestion> suggestions;

    public SuggestionAdapter(ArrayList<Suggestion> suggestions, RoomFragment caller) {
        this.suggestions = suggestions;
        this.caller = caller;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
    public Room getRoom() { return room; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView memorySongImage;
        public TextView memorySongName;
        public TextView memorySongArtist;
        public View popupView;
        public Suggestion suggestion = null;

        public ViewHolder(View itemView) {
            super(itemView);
            memorySongName = itemView.findViewById(R.id.memory_song_name);
            memorySongArtist = itemView.findViewById(R.id.memory_song_artist);
            memorySongImage = itemView.findViewById(R.id.memory_song_image);
        }
    }

    @NonNull
    @Override
    public SuggestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.memory_song, parent, false);
        ViewHolder ret = new ViewHolder(view);
        ret.popupView = caller.getLayoutInflater().inflate(R.layout.suggestion_popup, null);
        ret.popupView.findViewById(R.id.dislike_button).setOnClickListener(new View.OnClickListener() {
            private boolean isDisliked = false;
            @Override
            public void onClick(View v) {
                // dislike the song
                if (isDisliked) {
                    if(ret.suggestion != null)
                        ret.suggestion.upvote();
                    ret.popupView.findViewById(R.id.dislike_button).setBackground(caller.getContext().getDrawable(R.drawable.thumb_nobg));
                } else {
                    if(ret.suggestion != null)
                        ret.suggestion.downvote();
                    ret.popupView.findViewById(R.id.dislike_button).setBackground(caller.getContext().getDrawable(R.drawable.thumb_red_nobg));
                }
                isDisliked = !isDisliked;
                room.pushSongsToDb();
            }
        });
        return ret;
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionAdapter.ViewHolder holder, int position) {
        Suggestion song = suggestions.get(position);
        if(song == null) return;

        // here you can set the callback method
        holder.memorySongName.setText(song.getName());
        holder.memorySongArtist.setText(song.getAuthor());

        Glide.with(holder.itemView).load(song.getImage_url()).into(holder.memorySongImage);

        // TODO fix on click listener

        /*
        thumbDown.setOnClickListener(new View.OnClickListener() {
            private boolean isDisliked = false;
            @Override
            public void onClick(View v) {
                if (isDisliked) {
                    suggestion.upvote();
                    thumbDown.setImageResource(R.drawable.thumb_nobg);
                } else {
                    suggestion.downvote();
                    thumbDown.setImageResource(R.drawable.thumb_red_nobg);
                }
                isDisliked = !isDisliked;
                room.pushSongsToDb();
            }
        });
         */

        // TODO add album image to song
        // TODO make image long clickable to dislike
        holder.memorySongImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Create and configure the popup window
                holder.suggestion = song;
                PopupWindow popupWindow = new PopupWindow(holder.popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setOutsideTouchable(true);
                // Show the popup window at the specified location
                popupWindow.showAsDropDown(v, 75, -v.getHeight()+75);
                //showPopupWindow(v, song);
                return true;
            }
        });
    }

    public void showPopupWindow(View anchorView, Suggestion suggestion) {
        // Create a popup window
        View popupView = caller.getLayoutInflater().inflate(R.layout.suggestion_popup, null);

        // Create and configure the popup window
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);

        // Show the popup window at the specified location
        popupWindow.showAsDropDown(anchorView, 75, -anchorView.getHeight()+75);
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

}
