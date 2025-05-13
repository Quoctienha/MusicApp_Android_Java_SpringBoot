package com.example.musicapp.command;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.musicapp.activity.LoginActivity;
import com.example.musicapp.api.RegisterCustomerAPI;
import com.example.musicapp.dto.RegisterRequestDTO;
import com.example.musicapp.dto.RegisterResponseDTO;
import com.example.musicapp.ultis.RetrofitService;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterCommand implements Command {

    private final Context context;
    private final RegisterRequestDTO request;

    public RegisterCommand(Context context, RegisterRequestDTO request) {
        this.context = context;
        this.request = request;
    }

    @Override
    public void execute() {
        RegisterCustomerAPI registerCustomerAPI = RetrofitService.getInstance(context).createService(RegisterCustomerAPI.class);
        registerCustomerAPI.registerCustomer(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RegisterResponseDTO> call, @NonNull Response<RegisterResponseDTO> response) {
                if (response.body() != null) {
                    RegisterResponseDTO registerResponse = response.body();
                    if (Objects.equals(registerResponse.getStatus(), "Success")) {
                        Toast.makeText(context, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        //Đến trang login
                        Command navigateCommand = new NavigateToActivityCommand(
                                context,
                                LoginActivity.class,
                                android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                        );
                        navigateCommand.execute();
                    } else {
                        Toast.makeText(context, "Fail to register: " + registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Fail to register: Nill response from server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegisterResponseDTO> call, @NonNull Throwable t) {
                Toast.makeText(context, "Unable to connect to server " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.getLogger(RegisterCommand.class.getName()).log(Level.SEVERE, "Error: ", t);
            }
        });
    }
}
