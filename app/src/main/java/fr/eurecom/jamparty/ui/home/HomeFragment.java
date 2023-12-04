package fr.eurecom.jamparty.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import fr.eurecom.jamparty.databinding.FragmentHomeBinding;
import fr.eurecom.jamparty.ui.createroom.CreateFragment;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageButton playButton = binding.playButton;
        ImageButton backButton = binding.backButton;
        ImageButton nextButton = binding.nextButton;

        binding.buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateFragment().show(getChildFragmentManager(), CreateFragment.TAG);
            }
        });

        binding.buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeViewModel.setInRoom(true);
                homeViewModel.setRoomName("Room1");
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Clicked play!", Toast.LENGTH_SHORT).show();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Clicked next!", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Clicked back!", Toast.LENGTH_SHORT).show();
            }
        });

        homeViewModel.getInRoom().observe(this.getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.playerArea.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
                binding.buttonCreate.setVisibility(aBoolean ? View.GONE : View.VISIBLE);
                binding.buttonJoin.setVisibility(aBoolean ? View.GONE : View.VISIBLE);
                binding.textRoomName.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
            }
        });

        homeViewModel.getRoomName().observe(this.getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.textRoomName.setText(s);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}