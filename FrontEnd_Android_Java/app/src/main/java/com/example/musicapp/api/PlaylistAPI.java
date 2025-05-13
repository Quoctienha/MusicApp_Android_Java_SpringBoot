package com.example.musicapp.api;

import com.example.musicapp.dto.PlaylistDTO;
import com.example.musicapp.dto.SongDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface PlaylistAPI {

    @GET("/api/playlists")
    Call<List<PlaylistDTO>> getAllPlaylists();

    @GET("/api/playlists/{playlistId}/songs")
    Call<List<SongDTO>> getSongsInPlaylist(@Path("playlistId") Long playlistId);

    @PUT("/api/playlists/{playlistId}/add-song/{songId}")
    Call<PlaylistDTO> addSongToPlaylist(@Path("playlistId") Long playlistId, @Path("songId") Long songId);

    @PUT("/api/playlists/{playlistId}/remove-song/{songId}")
    Call<PlaylistDTO> removeSongFromPlaylist(@Path("playlistId") Long playlistId, @Path("songId") Long songId);


    @POST("/api/playlists")
    Call<PlaylistDTO> createPlaylist(@Body PlaylistDTO playlistDTO);

    @PUT("/api/playlists/{id}")
    Call<PlaylistDTO> updatePlaylist(@Path("id") Long id, @Body PlaylistDTO playlistDTO);

    @DELETE("/api/playlists/{id}")
    Call<Void> deletePlaylist(@Path("id") Long id);

    @GET("/api/playlists/{id}")
    Call<PlaylistDTO> getPlaylistById(@Path("id") long id);

}
