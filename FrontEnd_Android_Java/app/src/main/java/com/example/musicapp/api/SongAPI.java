package com.example.musicapp.api;

import com.example.musicapp.dto.SongDTO;
// Đảm bảo bạn đã tạo lớp PageWrapper ở bước trước
import com.example.musicapp.dto.PageWrapper; // <<< THÊM IMPORT NÀY

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query; // <<< THÊM IMPORT NÀY

public interface SongAPI {
    @GET("/song")
    Call<List<SongDTO>> getAllSongs();

    @GET("/song/trending")
    Call<List<SongDTO>> getTop10Songs();

    @GET("/songs/search") // Đường dẫn phải khớp với endpoint backend
    Call<PageWrapper<SongDTO>> searchSongs(
            @Query("query") String query,
            @Query("type") String type,
            @Query("page") int page,
            @Query("size") int size
    );
    // --- KẾT THÚC PHẦN THÊM ---

    @PUT("/song/{id}/like")
    Call<Void> likeSong(@Path("id") Long songId);

    @PUT("/song/{id}/dislike")
    Call<Void> dislikeSong(@Path("id") Long songId);

    @PUT("/song/{id}/view")
    Call<Void> incrementView(@Path("id") Long songId);
}