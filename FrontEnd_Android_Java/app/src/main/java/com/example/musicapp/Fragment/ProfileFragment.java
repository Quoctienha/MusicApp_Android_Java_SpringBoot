package com.example.musicapp.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.musicapp.R;
import com.example.musicapp.activity.LoginActivity;
import com.example.musicapp.auth.TokenManager;

public class ProfileFragment extends Fragment
{
    private TextView tvUsername;
    private TextView tvEmail;

    public ProfileFragment()
    {
        // Yêu cầu bởi Fragment
    }

    public static ProfileFragment newInstance(String username, String email) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("username", username);
        args.putString("email", email);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo các TextView
        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);

        // Lấy dữ liệu từ Bundle
        Bundle args = getArguments();
        if (args != null) {
            String username = args.getString("username", "Username");
            String email = args.getString("email", "Email");
            Log.d("ProfileFragment", "Username: " + username + ", Email: " + email);
            tvUsername.setText(username);
            tvEmail.setText(email);
        }

        // Cài đặt ListView cho các tùy chọn
        ListView listView = view.findViewById(R.id.profile_options_list);
        String[] options = new String[]{
                "Favourites",
                "Downloads",
                "Languages",
                "Follow",
                "Log Out"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.list_item_profile, // Sử dụng layout tùy chỉnh
                R.id.text1, // Không cần id này vì layout chỉ có 1 TextView, nhưng để tương thích
                options
        );
        listView.setAdapter(adapter);

        // Xử lý sự kiện nhấn vào ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = options[position];
                if ("Log Out".equals(selectedItem)) {
                    // Hiển thị dialog xác nhận đăng xuất
                    showLogoutDialog();
                }
            }
        });
    }

    private void showLogoutDialog() {
        // Tạo dialog
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_logout);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Áp dụng kích thước cho dialog
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Xử lý nút đóng dialog
        ImageButton btnCloseDialog = dialog.findViewById(R.id.btn_close_dialog);
        btnCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Xử lý nút "Logout"
        Button btnConfirmLogout = dialog.findViewById(R.id.btn_confirm_logout);
        btnConfirmLogout.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                TokenManager tokenManager = new TokenManager(requireContext());
                tokenManager.getAccessToken();

                // Điều hướng về LoginActivity và kết thúc HomeActivity
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
                dialog.dismiss();
            }
        });

        // Hiển thị dialog
        dialog.show();
    }
}
