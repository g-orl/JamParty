package fr.eurecom.jamparty;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.eurecom.jamparty.ui.fragments.MemoryFragment;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.ViewHolder> {
    private List<Room> rooms;
    private NavController controller;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.room_image);
            textView = itemView.findViewById(R.id.room_name);
        }
    }

    public MemoryAdapter(List<Room> rooms, NavController controller) {
        this.rooms = rooms;
        this.controller = controller;
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
                Bundle bundle = new Bundle();
                bundle.putParcelable("room", room);
                controller.navigate(R.id.navigation_memory, bundle);

                // Toast.makeText(v.getContext(), String.format("Clicked on room %s", room.getName()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }
}
