package com.example.musicapp.api;

import com.example.musicapp.dto.PlaylistDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface PlaylistAPI {

    @GET("playlists")
    Call<List<PlaylistDTO>> getAllPlaylists();

    @PUT("/playlists/{playlistId}/add-song/{songId}")
    Call<PlaylistDTO> addSongToPlaylist(@Path("playlistId") Long playlistId, @Path("songId") Long songId);

    @POST("playlists")
    Call<PlaylistDTO> createPlaylist(@Body PlaylistDTO playlistDTO);

    @PUT("playlists/{id}")
    Call<PlaylistDTO> updatePlaylist(@Path("id") Long id, @Body PlaylistDTO playlistDTO);

    @DELETE("playlists/{id}")
    Call<Void> deletePlaylist(@Path("id") Long id);
}
