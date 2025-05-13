// SongAPI.java
package com.example.musicapp.api;

import com.example.musicapp.dto.PagedResponseDTO;
import com.example.musicapp.dto.SongDTO;
import com.example.musicapp.dto.SongRatingResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SongAPI {
    @GET("/api/song")
    Call<List<SongDTO>> getAllSongs();

    @GET("/api/song/trending")
    Call<List<SongDTO>> getTop10Songs();

    @PUT("/api/song/{id}/like")
    Call<SongRatingResponseDTO> likeSong(@Path("id") Long songId);

    @PUT("/api/song/{id}/dislike")
    Call<SongRatingResponseDTO> dislikeSong(@Path("id") Long songId);

    @PUT("/api/song/{id}/view")
    Call<Void> incrementView(@Path("id") Long songId);

    @GET("/api/song/{id}/rating")
    Call<SongRatingResponseDTO> getUserRatingForSong(@Path("id") Long songId);

    @GET("/api/song/search")
    Call<PagedResponseDTO<SongDTO>> searchSongs(
            @Query("keyword") String keyword,
            @Query("mode") String mode,
            @Query("page") int page,
            @Query("size") int size
    );

}