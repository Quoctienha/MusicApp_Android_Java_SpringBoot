package com.example.musicapp.api;

import com.example.musicapp.dto.EditProfileRequestDTO;
import com.example.musicapp.dto.UserProfileResponseDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface UserAPI {

    @GET("api/user/get-profile")            // <-- new path
    Call<UserProfileResponseDTO> getProfile();

    @PUT("api/user/edit-profile")           // <-- new path
    Call<Void> updateProfile(@Body EditProfileRequestDTO dto);
}
