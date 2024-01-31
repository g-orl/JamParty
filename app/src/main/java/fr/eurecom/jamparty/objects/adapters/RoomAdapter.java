package fr.eurecom.jamparty.objects.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import java.util.ArrayList;

import fr.eurecom.jamparty.MainActivity;
import fr.eurecom.jamparty.R;
import fr.eurecom.jamparty.objects.Hasher;
import fr.eurecom.jamparty.objects.Room;
import fr.eurecom.jamparty.objects.RoomUserManager;
import fr.eurecom.jamparty.ui.home.HomeFragment;
public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    private List<Room> rooms;
    private NavController fragmentController;
    private boolean enableJoin;
    private Context context;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.room_name);
            imageView = itemView.findViewById(R.id.room_image);
        }
    }

    public RoomAdapter(List<Room> rooms, NavController fragmentController, boolean enableJoin, Context context) {
        this.rooms = rooms;
        this.fragmentController = fragmentController;
        this.enableJoin = enableJoin;
        this.context = context;
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
        // here you can set the callback method
        holder.textView.setText(room.getName());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!enableJoin) return;
                // TODO implement join functionality
                String hash = Hasher.hashString("");
                if (!hash.equals(room.getHash())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Enter room password");
                    // Set up the input
                    final EditText input = new EditText(context);
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);
                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String password = input.getText().toString();
                            String hash = Hasher.hashString(password);
                            if (room.getHash().equals(hash)) {
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("room", room);
                                if (RoomUserManager.userJoinRoom(MainActivity.getUser(), room, false) == RoomUserManager.OPERATION_OK)
                                    fragmentController.navigate(R.id.navigation_room, bundle);
                                else {
                                    Toast.makeText(context, "Room is full", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.e("PasswordHash", room.getHash()+" - "+hash);
                                Toast.makeText(context, "Wrong Password.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("room", room);
                    if (RoomUserManager.userJoinRoom(MainActivity.getUser(), room, false) == RoomUserManager.OPERATION_OK)
                        fragmentController.navigate(R.id.navigation_room, bundle);
                    else {
                        Toast.makeText(context, "Room is full", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }
}
