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

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import fr.eurecom.jamparty.ui.home.HomeFragment;


public class SuggestionAdapter extends ArrayAdapter {

    private HomeFragment caller;

    public SuggestionAdapter(@NonNull Context context, ArrayList<Suggestion> suggestions, HomeFragment caller) {
        super(context, 0, suggestions);
        this.caller = caller;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Suggestion suggestion = (Suggestion) getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_suggestion, parent, false);
        }

        TextView nameTxt = convertView.findViewById(R.id.songName);
        TextView authorTxt = convertView.findViewById(R.id.songAuthor);
        ImageView image = convertView.findViewById(R.id.songImage);

        nameTxt.setText(suggestion.getSong().getName());
        authorTxt.setText(suggestion.getSong().getAuthor());
        // TODO save bitmap in the song so that there is no need to downlaod again
        Glide.with(caller.getView()).load(suggestion.getSong().getImage_url()).into(image);

        return convertView;
    }

}
