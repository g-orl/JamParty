package fr.eurecom.jamparty.objects.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import fr.eurecom.jamparty.R;
import fr.eurecom.jamparty.objects.Song;
import fr.eurecom.jamparty.objects.Suggestion;

public class SongMemoryAdapter extends RecyclerView.Adapter<SongMemoryAdapter.ViewHolder> {
    private List<Song> songs;
    private FragmentManager fragmentManager;

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

    public SongMemoryAdapter(List<Song> songs, FragmentManager fragmentManager) {
        this.songs = songs;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public SongMemoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.memory_song, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SongMemoryAdapter.ViewHolder holder, int position) {
        Song song = songs.get(position);
        if(song == null) return;
        // here you can set the callback method

        holder.memorySongName.setText(song.getName());
        holder.memorySongArtist.setText(song.getAuthor());

        Glide.with(holder.itemView).load(song.getImage_url()).into(holder.memorySongImage);
        // TODO add album image to song
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }
}
