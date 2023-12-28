package fr.eurecom.jamparty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import fr.eurecom.jamparty.ui.home.HomeFragment;

public class SongAdapter extends ArrayAdapter {

    private HomeFragment caller;

    public SongAdapter(@NonNull Context context, ArrayList<Song> songs, HomeFragment caller) {
        super(context, 0, songs);
        this.caller = caller;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Song song = (Song) getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_song, parent, false);
        }
        TextView nameTxt = convertView.findViewById(R.id.songName);

        nameTxt.setText(song.getName());

        return convertView;
    }
}
