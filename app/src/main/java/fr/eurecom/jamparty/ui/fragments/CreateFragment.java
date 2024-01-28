package fr.eurecom.jamparty.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import fr.eurecom.jamparty.MainActivity;
import fr.eurecom.jamparty.R;
import fr.eurecom.jamparty.objects.Hasher;
import fr.eurecom.jamparty.objects.Room;
import fr.eurecom.jamparty.objects.RoomUserManager;
import fr.eurecom.jamparty.objects.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateFragment extends DialogFragment {

    public static String TAG = "CreateRoomDialog";
    private EditText txtName;
    private EditText txtPassword;
    private EditText txtMaxParticipants;
    private EditText txtDuration;
    private NavController fragmentController;

    public CreateFragment() {
        // Required empty public constructor
    }

    public static CreateFragment newInstance(String param1, String param2) {
        CreateFragment fragment = new CreateFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.fragmentController = NavHostFragment.findNavController(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        txtName = (EditText) getDialog().findViewById(R.id.inputName);
        txtPassword = (EditText) getDialog().findViewById(R.id.inputPassword);
        txtMaxParticipants = (EditText) getDialog().findViewById(R.id.inputMaxParticipants);
        txtDuration = (EditText) getDialog().findViewById(R.id.inputDuration);
        txtMaxParticipants.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                int value = Integer.parseInt(text);
                if (value < 1) {
                    txtMaxParticipants.setText("1");
                } else if (value > 256) {
                    txtMaxParticipants.setText("256");
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        txtDuration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                int value = Integer.parseInt(text);
                if (value < 1) {
                    txtDuration.setText("1");
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        System.out.println("View created");
//        return inflater.inflate(R.layout.fragment_create, container, false);
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        System.out.println("Dialog created");
        return new AlertDialog.Builder(requireContext())
                .setView(R.layout.fragment_create)
                .setMessage("Create a room!")
                .setPositiveButton(getString(R.string.create_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == Dialog.BUTTON_POSITIVE) {
                            // user wants to create the room
                            String name = txtName.getText().toString();
                            String password = txtPassword.getText().toString();
                            String maxParticipantsStr = txtMaxParticipants.getText().toString();
                            String durationStr = txtDuration.getText().toString();
                            if(name.isEmpty() || maxParticipantsStr.isEmpty() || durationStr.isEmpty()) {
                                Toast.makeText(requireContext(), "Please fill all the required fields.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String hash = Hasher.hashString(password);
                            int maxParticipants = Integer.parseInt(maxParticipantsStr);
                            int duration = Integer.parseInt(durationStr);
                            int durationMillis = duration * 60 * 1000;
                            DatabaseReference roomsRef = MainActivity.ROOMS_REF;
                            String id = "room" + roomsRef.push().getKey();
                            Room room = new Room(id, name, hash, maxParticipants, durationMillis);
                            room.pushToDb();

                            Bundle bundle = new Bundle();
                            bundle.putParcelable("room", room);
                            RoomUserManager.userJoinRoom(MainActivity.getUser(), room, true);
                            fragmentController.navigate(R.id.navigation_room, bundle);
                        }
                    }
                })
                .create();
    }
}