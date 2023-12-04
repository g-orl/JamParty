package fr.eurecom.jamparty;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RoomAdapter extends ArrayAdapter {

    public RoomAdapter(@NonNull Context context, ArrayList<Room> contacts) {
        super(context, 0, contacts);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Room room = (Room) getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_room, parent, false);
        }
        TextView nameTxt = convertView.findViewById(R.id.roomNameTxt);
        Button joinBtn = convertView.findViewById(R.id.joinBtn);

        nameTxt.setText(room.getName());

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { }
        });
        return convertView;
    }
}
