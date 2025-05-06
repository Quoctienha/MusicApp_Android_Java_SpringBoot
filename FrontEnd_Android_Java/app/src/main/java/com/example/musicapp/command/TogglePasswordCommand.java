package com.example.musicapp.command;

import android.widget.EditText;
import android.widget.ImageButton;
import android.text.InputType;

import com.example.musicapp.R;

public class TogglePasswordCommand implements Command {

    private final EditText edtPassword;
    private final ImageButton btnTogglePassword;
    private boolean isPasswordVisible;

    public TogglePasswordCommand(EditText edtPassword, ImageButton btnTogglePassword, boolean isPasswordVisible) {
        this.edtPassword = edtPassword;
        this.btnTogglePassword = btnTogglePassword;
        this.isPasswordVisible = isPasswordVisible;
    }

    @Override
    public void execute() {
        if (isPasswordVisible) {
            // Ẩn mật khẩu
            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_eye);
            isPasswordVisible = false;
        } else {
            // Hiện mật khẩu
            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_eye_off);
            isPasswordVisible = true;
        }

        // Đặt con trỏ ở cuối
        edtPassword.setSelection(edtPassword.getText().length());
    }

}
