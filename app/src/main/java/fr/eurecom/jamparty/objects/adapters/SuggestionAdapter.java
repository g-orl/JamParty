package fr.eurecom.jamparty.objects.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import fr.eurecom.jamparty.R;
import fr.eurecom.jamparty.objects.Room;
import fr.eurecom.jamparty.objects.Suggestion;
import fr.eurecom.jamparty.ui.fragments.RoomFragment;


public class SuggestionAdapter extends ArrayAdapter {

    private RoomFragment caller;
    private Room room;

    public SuggestionAdapter(@NonNull Context context, ArrayList<Suggestion> suggestions, RoomFragment caller) {
        super(context, 0, suggestions);
        this.caller = caller;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Suggestion suggestion = (Suggestion) getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_suggestion, parent, false);
        }

        TextView nameTxt = convertView.findViewById(R.id.songName);
        TextView authorTxt = convertView.findViewById(R.id.songAuthor);
        ImageView image = convertView.findViewById(R.id.songImage);
        ImageView thumbDown = convertView.findViewById(R.id.thumbDownImageView);

        nameTxt.setText(suggestion.getName());
        authorTxt.setText(suggestion.getAuthor());
        // TODO save bitmap in the song so that there is no need to downlaod again
        Glide.with(caller.getView()).load(suggestion.getImage_url()).into(image);

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

        return convertView;
    }

}
