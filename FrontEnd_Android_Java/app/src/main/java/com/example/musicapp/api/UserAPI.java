package com.example.musicapp.api;

import com.example.musicapp.dto.EditProfileRequestDTO;
import com.example.musicapp.dto.UserProfileResponseDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;

public interface UserAPI {
    @GET("api/user/profile")
    Call<UserProfileResponseDTO> getProfile(@Header("Authorization") String token);

    @PUT("api/user/profile")
    Call<Void> updateProfile(@Header("Authorization") String token,
                             @Body EditProfileRequestDTO dto);
}
