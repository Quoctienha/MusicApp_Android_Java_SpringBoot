package com.example.musicapp.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.musicapp.R;
import com.example.musicapp.api.UserAPI;
import com.example.musicapp.auth.TokenManager;
import com.example.musicapp.dto.EditProfileRequestDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditProfileFragment extends Fragment {

    private EditText edtFullName, edtPhone;
    private Button   btnSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup parent, @Nullable Bundle s) {
        return inf.inflate(R.layout.fragment_edit_profile, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        edtFullName = v.findViewById(R.id.edt_fullname);
        edtPhone    = v.findViewById(R.id.edt_phone);
        btnSave     = v.findViewById(R.id.btn_save_profile);

        btnSave.setOnClickListener(x -> save());
    }

    private void save() {
        String name  = edtFullName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
            Toast.makeText(getContext(),"Fill all fields",Toast.LENGTH_SHORT).show();
            return;
        }

        String token = new TokenManager(requireContext()).getAccessToken();
        Retrofit rt  = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EditProfileRequestDTO req = new EditProfileRequestDTO(name, phone);
        rt.create(UserAPI.class)
                .updateProfile("Bearer " + token, req)
                .enqueue(new Callback<Void>() {
                    @Override public void onResponse(Call<Void> c, Response<Void> r) {
                        if (r.isSuccessful()) {
                            Toast.makeText(getContext(),"Updated",Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            Toast.makeText(getContext(),"Fail "+r.code(),Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onFailure(Call<Void> c, Throwable t) {
                        Toast.makeText(getContext(),"Network",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
