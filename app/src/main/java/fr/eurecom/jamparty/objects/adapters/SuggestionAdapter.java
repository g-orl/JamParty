package fr.eurecom.jamparty.objects.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import fr.eurecom.jamparty.R;
import fr.eurecom.jamparty.objects.Room;
import fr.eurecom.jamparty.objects.Suggestion;
import fr.eurecom.jamparty.ui.fragments.RoomFragment;


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
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionAdapter.ViewHolder holder, int position) {
        Log.i("LIKE pos", String.valueOf(position));
        Suggestion suggestion = suggestions.get(position);
        if(suggestion == null) return;

        // here you can set the callback method
        holder.memorySongName.setText(suggestion.getName());
        holder.memorySongArtist.setText(suggestion.getAuthor());

        Glide.with(holder.itemView).load(suggestion.getImage_url()).into(holder.memorySongImage);

        View popupView = caller.getLayoutInflater().inflate(R.layout.suggestion_popup, null);
        // TODO fix on click listener
        holder.memorySongImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Create and configure the popup window
                PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setOutsideTouchable(true);
                // Show the popup window at the specified location
                popupWindow.showAsDropDown(v, 75, -v.getHeight()+75);
                // showPopupWindow(v, song);
                return true;
            }
        });

        popupView.findViewById(R.id.dislike_button).setOnClickListener(new View.OnClickListener() {
            private boolean isDisliked = false;
            @Override
            public void onClick(View v) {
                // dislike the song
                if (isDisliked) {
                    if(suggestion != null)
                        suggestion.upvote();
                    popupView.findViewById(R.id.dislike_button).setBackground(caller.getContext().getDrawable(R.drawable.thumb_nobg));
                } else {
                    if(suggestion != null)
                        suggestion.downvote();
                    popupView.findViewById(R.id.dislike_button).setBackground(caller.getContext().getDrawable(R.drawable.thumb_red_nobg));
                }
                isDisliked = !isDisliked;
                room.pushSongsToDb();
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

}
