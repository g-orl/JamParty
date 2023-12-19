package fr.eurecom.jamparty.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.widget.EditText;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import fr.eurecom.jamparty.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static String TAG = "CreateRoomDialog";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateFragment newInstance(String param1, String param2) {
        CreateFragment fragment = new CreateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getEncoder().encodeToString(hashedBytes);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return null;
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
                        if (which == Dialog.BUTTON_POSITIVE){
                            // user wants to create the room
                            // TODO add room to Firebase
                            EditText name = (EditText) getDialog().findViewById(R.id.inputName);

                            EditText editTextPassword = (EditText) getDialog().findViewById(R.id.inputPassword);
                            String hashedPassword = hashPassword(editTextPassword.getText().toString());

                            EditText editTextParticipants = (EditText) getDialog().findViewById(R.id.inputMaxParticipants);
                            EditText editTextHours = (EditText) getDialog().findViewById(R.id.inputHours);
                            EditText editTextMinutes = (EditText) getDialog().findViewById(R.id.inputMinutes);

                            int maxParticipants = Integer.parseInt(editTextParticipants.getText().toString());
                            int hours = Integer.parseInt(editTextHours.getText().toString());
                            int minutes = Integer.parseInt(editTextMinutes.getText().toString());

                            System.out.println(String.format("User wants to create room:\nName: %s\nPassword: %s\nMax number of participants: %d\nDuration: %dh %dm", name.getText().toString(), editTextPassword.getText().toString(), maxParticipants, hours, minutes));
                        }
                    }
                })
                .create();
    }
}