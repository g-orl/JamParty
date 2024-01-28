package fr.eurecom.jamparty.objects.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import java.util.ArrayList;

import fr.eurecom.jamparty.R;
import fr.eurecom.jamparty.objects.Room;
import fr.eurecom.jamparty.ui.home.HomeFragment;
public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    private List<Room> rooms;
    private NavController fragmentController;
    private boolean enableJoin;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.room_name);
            imageView = itemView.findViewById(R.id.room_image);
        }
    }

    public RoomAdapter(List<Room> rooms, NavController fragmentController, boolean enableJoin) {
        this.rooms = rooms;
        this.fragmentController = fragmentController;
        this.enableJoin = enableJoin;
    }

    @NonNull
    @Override
    public RoomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.memory_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomAdapter.ViewHolder holder, int position) {
        Room room = rooms.get(position);
        if(room == null) return;
        // here you can set the callback method
        holder.textView.setText(room.getName());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO implement join functionality
                if(enableJoin) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("room", room);

                    fragmentController.navigate(R.id.navigation_room, bundle);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }
}
