package com.example.musicapp.api;

import com.example.musicapp.dto.ArtistDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ArtistAPI {
    @GET("/artist/top")
    Call<List<ArtistDTO>> getTopArtists();
}
