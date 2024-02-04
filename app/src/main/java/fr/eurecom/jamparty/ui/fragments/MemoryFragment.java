package fr.eurecom.jamparty.ui.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.eurecom.jamparty.R;
import fr.eurecom.jamparty.objects.Room;
import fr.eurecom.jamparty.objects.adapters.SongMemoryAdapter;
import fr.eurecom.jamparty.databinding.FragmentMemoryBinding;

public class MemoryFragment extends Fragment {

    private Room room;
    private FragmentMemoryBinding binding;
    public NavController fragmentController;

    public MemoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();

        if(arguments != null){
            // TODO check compatibility of android!
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this.room = arguments.getParcelable("room", Room.class);
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.binding = FragmentMemoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        this.fragmentController = NavHostFragment.findNavController(this);

        Log.i("BACK STACK", fragmentController.getCurrentBackStackEntry().toString());


        TextView memoryRoomName = binding.memoryRoomName;
        memoryRoomName.setText(room.getName());
        RecyclerView recyclerView = binding.memorySongList;
        SongMemoryAdapter songMemoryAdapter = new SongMemoryAdapter(this.room.getPlayed(), getChildFragmentManager());

        recyclerView.setAdapter(songMemoryAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        binding.memoryShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = Bitmap.createBitmap(root.getWidth(), root.getHeight(), Bitmap.Config.ARGB_8888);

                binding.memoryShareButton.setVisibility(View.GONE);

                // Create a Canvas with the Bitmap
                Canvas canvas = new Canvas(bitmap);

                canvas.drawColor(Color.WHITE);
                // Draw the view onto the Canvas
                root.draw(canvas);

                // now bitmap containes the image

                String imagePath = MediaStore.Images.Media.insertImage(
                        requireContext().getContentResolver(),
                        bitmap.copy(Bitmap.Config.ARGB_8888, true),
                        String.format("Memory-Room-%s", room.getId()),
                        "ImageDescription"
                );

                binding.memoryShareButton.setVisibility(View.VISIBLE);

                // Create an intent to share the image
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imagePath));

                // Create a chooser to allow the user to pick the sharing app
                Intent chooserIntent = Intent.createChooser(shareIntent, "Share Image");

                // Start the chooser
                startActivity(chooserIntent);
            }
        });
        binding.memoryCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.navigation_dashboard);
            }
        });
        return root;
    }



}