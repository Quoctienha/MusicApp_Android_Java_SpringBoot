package com.example.musicapp.api;


import com.example.musicapp.dto.PageWrapper;

import com.example.musicapp.dto.SongDTO;

import com.example.musicapp.dto.SongRatingResponseDTO;


import java.util.List;


import retrofit2.Call;

import retrofit2.http.GET;

import retrofit2.http.PUT;

import retrofit2.http.Path;
import retrofit2.http.Query;

import retrofit2.http.Query;


public interface SongAPI {


    /**

     * Retrieves all songs.

     * Adjusted to match backend's @GetMapping("/") in SongController.

     */

    @GET("song/") // Đã sửa: Khớp với @RequestMapping("/song") và @GetMapping("/")

    Call<List<SongDTO>> getAllSongs();


    /**

     * Retrieves the top 10 trending songs.

     * Adjusted to match backend's SongController.

     */

    @GET("song/trending") // Đã sửa: Khớp với @RequestMapping("/song") và @GetMapping("/trending")

    Call<List<SongDTO>> getTop10Songs();


    /**

     * Searches for songs with pagination.

     * Adjusted to match backend's SongController.

     */

    @GET("song/search") // Đã sửa: Khớp với @RequestMapping("/song") và @GetMapping("/search")

    Call<PageWrapper<SongDTO>> searchSongs(

            @Query("query") String query,

            @Query("type") String type,

            @Query("page") int page,

            @Query("size") int size

    );


    /**

     * Likes a song.

     * Uses SongRatingResponseDTO.

     * Adjusted to match backend's SongController.

     * @param songId The ID of the song to like.

     * @return A Call object for the response.

     */

    @PUT("song/{id}/like") // Đã sửa: Khớp với @RequestMapping("/song") và @PutMapping("/{id}/like")

    Call<SongRatingResponseDTO> likeSong(@Path("id") Long songId);


    /**

     * Dislikes a song.

     * Uses SongRatingResponseDTO.

     * Adjusted to match backend's SongController.

     * @param songId The ID of the song to dislike.

     * @return A Call object for the response.

     */

    @PUT("song/{id}/dislike") // Đã sửa: Khớp với @RequestMapping("/song") và @PutMapping("/{id}/dislike")

    Call<SongRatingResponseDTO> dislikeSong(@Path("id") Long songId);


    /**

     * Increments the view count for a song.

     * Adjusted to match backend's SongController.

     * @param songId The ID of the song whose view count is to be incremented.

     * @return A Call object for the response.

     */

    @PUT("song/{id}/view") // Đã sửa: Khớp với @RequestMapping("/song") và @PutMapping("/{id}/view")

    Call<Void> incrementView(@Path("id") Long songId);


    /**

     * Gets the current user's rating (like/dislike/none) for a specific song.

     * Adjusted to match backend's SongController.

     * @param songId The ID of the song.

     * @return A Call object for the SongRatingResponseDTO.

     */

    @GET("song/{id}/rating") // Đã sửa: Khớp với @RequestMapping("/song") và @GetMapping("/{songId}/rating")

// Tên tham số {id} ở đây sẽ được Retrofit thay thế bằng giá trị của songId.

    Call<SongRatingResponseDTO> getUserRatingForSong(@Path("id") Long songId);

} 