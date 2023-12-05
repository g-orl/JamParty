package fr.eurecom.jamparty.ui.fragments;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import fr.eurecom.jamparty.R;

public class CreateRoom extends Fragment {

    public void createRoom(View view){
        EditText textName = (EditText) view.findViewById(R.id.editName);
        String name = textName.getText().toString().trim();

        EditText textParticipants = (EditText) view.findViewById(R.id.editParticipants);
        String maxParticipantsStr = textParticipants.getText().toString().trim();
        int maxParticipants;
        try {
            maxParticipants = Integer.parseInt(maxParticipantsStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter a valid number for participants", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText textCode = (EditText) view.findViewById(R.id.editCode);
        String entryCode = textCode.getText().toString().trim();

        //Room room = new Room(name, maxParticipants, entryCode);
    }

}
