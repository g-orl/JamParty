package fr.eurecom.jamparty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import fr.eurecom.jamparty.ui.fragments.RoomFragment;
import fr.eurecom.jamparty.ui.home.HomeFragment;

public class RoomAdapter extends ArrayAdapter {
    private HomeFragment caller;

    public RoomAdapter(@NonNull Context context, ArrayList<Room> contacts, HomeFragment homeFragment) {
        super(context, 0, contacts);
        this.caller = homeFragment;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Room room = (Room) getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_room, parent, false);
        }
        TextView nameTxt = convertView.findViewById(R.id.roomNameTxt);
        Button joinBtn = convertView.findViewById(R.id.joinBtn);

        nameTxt.setText(room.getName());


        joinBtn.setOnClickListener(v -> {
            caller.enterRoom(room.getName());

        });
        return convertView;
    }
}
