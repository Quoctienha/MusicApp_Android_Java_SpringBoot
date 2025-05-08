package com.example.musicapp.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
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
import com.example.musicapp.ultis.RetrofitService;

public class EditProfileFragment extends Fragment {

    private EditText edtFullName, edtPhone;
    private Button   btnSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             @Nullable ViewGroup parent,
                             @Nullable Bundle s) {
        return inf.inflate(R.layout.fragment_edit_profile, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        edtFullName = v.findViewById(R.id.edt_fullname);
        edtPhone    = v.findViewById(R.id.edt_phone);
        btnSave     = v.findViewById(R.id.btn_save_profile);

        // restrict phone field to digits only
        edtPhone.setKeyListener(DigitsKeyListener.getInstance("0123456789"));

        btnSave.setOnClickListener(x -> save());
    }

    /* ---------------- save logic ---------------- */

    private void save() {
        String name  = edtFullName.getText().toString().trim();
        String phone = edtPhone   .getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(),"Full name is required",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidPhone(phone)) {
            Toast.makeText(getContext(),"Phone must be exactly 10 digits",Toast.LENGTH_SHORT).show();
            return;
        }

        // 1️⃣  Shared Retrofit service (token auto-added by interceptor)
        UserAPI api = RetrofitService
                .getInstance(requireContext())
                .createService(UserAPI.class);

        EditProfileRequestDTO req = new EditProfileRequestDTO(name, phone);

        // 2️⃣  Call PUT /api/user/profile without header arg
        api.updateProfile(req).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> c, Response<Void> r) {
                if (r.isSuccessful()) {
                    Toast.makeText(getContext(),"Profile updated",Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(),"Update failed: "+r.code(),Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<Void> c, Throwable t) {
                Toast.makeText(getContext(),"Network error",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* ---------------- validation helper ---------------- */

    private boolean isValidPhone(String phone) {
        // exactly 10 digits
        return phone != null && phone.matches("^\\d{10}$");
    }
}
