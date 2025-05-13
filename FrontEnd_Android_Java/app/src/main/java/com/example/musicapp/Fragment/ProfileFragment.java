package com.example.musicapp.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.musicapp.R;
import com.example.musicapp.api.UserAPI;
import com.example.musicapp.auth.TokenManager;
import com.example.musicapp.command.Command;
import com.example.musicapp.command.CommandInvoker;
import com.example.musicapp.command.LogoutCommand;
import com.example.musicapp.dto.UserProfileResponseDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.musicapp.ultis.RetrofitService;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvEmail, tvFullName, tvPhone , tvMembership;
    // If you add these to XML:
    // private TextView tvFullName, tvPhone;

    // lifecycle

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             @Nullable ViewGroup parent,
                             @Nullable Bundle state) {
        return inf.inflate(R.layout.fragment_profile, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);

        tvUsername = v.findViewById(R.id.tvUsername);
        tvEmail    = v.findViewById(R.id.tvEmail);
        tvFullName = v.findViewById(R.id.tvFullName);
        tvPhone    = v.findViewById(R.id.tvPhone);
        tvMembership = v.findViewById(R.id.tvMembership);

        setupList(v);
        loadProfile();
    }

    @Override public void onResume() {
        super.onResume();
        loadProfile();
    }

    // list & navigation -

    private void setupList(View root) {
        ListView list = root.findViewById(R.id.profile_options_list);
        String[] opts = {
                "Edit Profile",
                "Log Out"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.list_item_profile,
                R.id.text1,
                opts);
        list.setAdapter(adapter);

        list.setOnItemClickListener((p, item, pos, id) -> {
            String sel = opts[pos];
            if ("Edit Profile".equals(sel)) {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new EditProfileFragment())
                        .addToBackStack(null)
                        .commit();
            } else if ("Log Out".equals(sel)) {
                showLogoutDialog();
            }
        });
    }


    // networking

    private void loadProfile() {

        // shared Retrofit service
        UserAPI api = RetrofitService
                .getInstance(requireContext())
                .createService(UserAPI.class);


        api.getProfile().enqueue(new Callback<UserProfileResponseDTO>() {
            @Override public void onResponse(Call<UserProfileResponseDTO> c,
                                             Response<UserProfileResponseDTO> r) {
                if (r.isSuccessful() && r.body()!=null) {
                    UserProfileResponseDTO d = r.body();

                    boolean hasFull  = d.getFullName()!=null && !d.getFullName().isEmpty();
                    boolean hasPhone = d.getPhone()!=null    && !d.getPhone()   .isEmpty();

                    if (hasFull && hasPhone) {
                        tvFullName.setText(d.getFullName());
                        tvPhone   .setText(d.getPhone());
                        tvFullName.setVisibility(View.VISIBLE);
                        tvPhone   .setVisibility(View.VISIBLE);
                        tvUsername.setVisibility(View.GONE);
                        tvEmail   .setVisibility(View.GONE);
                    } else {
                        tvUsername.setText(d.getUsername());
                        tvEmail   .setText(d.getEmail());
                        tvUsername.setVisibility(View.VISIBLE);
                        tvEmail   .setVisibility(View.VISIBLE);
                        tvFullName.setVisibility(View.GONE);
                        tvPhone   .setVisibility(View.GONE);
                    }
                    tvMembership.setText(d.getMembership());

                } else {
                    Log.e("Profile", "GET /profile failed " + r.code());
                }
            }
            @Override public void onFailure(Call<UserProfileResponseDTO> c, Throwable t) {
                Log.e("Profile", "network", t);
            }
        });
    }
    // logout

    private void showLogoutDialog() {
        Dialog dlg = new Dialog(requireContext());
        dlg.setContentView(R.layout.dialog_logout);
        dlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dlg.findViewById(R.id.btn_close_dialog).setOnClickListener(v -> dlg.dismiss());

        Button ok = dlg.findViewById(R.id.btn_confirm_logout);
        ok.setOnClickListener(v -> {
            // Khởi tạo và thực thi LogoutCommand
            Command logoutCommand = new LogoutCommand(requireContext());

            // Sử dụng CommandInvoker để thực thi LogoutCommand
            CommandInvoker invoker = new CommandInvoker();
            invoker.setCommand(logoutCommand);
            invoker.executeCommand();

            // Đóng dialog
            dlg.dismiss();
        });
        dlg.show();
    }
}
