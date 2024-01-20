package fr.eurecom.jamparty;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.eurecom.jamparty.ui.fragments.MemoryFragment;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.ViewHolder> {
    private List<Room> rooms;
    private FragmentManager fragmentManager;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.song_image);
            textView = itemView.findViewById(R.id.song_name);
        }
    }

    public MemoryAdapter(List<Room> rooms, FragmentManager fragmentManager) {
        this.rooms = rooms;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public MemoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.memory_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoryAdapter.ViewHolder holder, int position) {
        Room room = rooms.get(position);
        // here you can set the callback method
        holder.textView.setText(room.getName());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MemoryFragment(room).show(fragmentManager, MemoryFragment.TAG);
                Toast.makeText(v.getContext(), String.format("Clicked on room %s", room.getName()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }
}
