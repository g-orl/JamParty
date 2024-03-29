package fr.eurecom.jamparty.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import fr.eurecom.jamparty.MainActivity;
import fr.eurecom.jamparty.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.buttonLogin.setVisibility(MainActivity.isLoggedIn() ? View.GONE : View.VISIBLE);
        binding.buttonLogout.setVisibility(MainActivity.isLoggedIn() ? View.VISIBLE : View.GONE);

        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthorizationRequest.Builder builder =
                        new AuthorizationRequest.Builder(MainActivity.CLIENT_ID, AuthorizationResponse.Type.TOKEN, MainActivity.REDIRECT_URI);

                builder.setScopes(new String[]{"user-library-read", "user-read-currently-playing", "user-read-playback-state", "user-read-private", "user-read-email", "user-modify-playback-state"});
                AuthorizationRequest request = builder.build();

                AuthorizationClient.openLoginActivity(getActivity(), MainActivity.REQUEST_CODE, request);
            }
        });

        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthorizationClient.clearCookies(getActivity());
                MainActivity.resetUserId();
                binding.buttonLogin.setVisibility(View.VISIBLE);
                binding.buttonLogout.setVisibility(View.GONE);
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