package com.example.musicapp.dto;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PageWrapper<T> {

    @SerializedName("content")
    private List<T> content;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("totalElements")
    private long totalElements;

    @SerializedName("size")
    private int size;

    @SerializedName("number") // Số trang hiện tại (từ 0)
    private int number;

    @SerializedName("first")
    private boolean first;

    @SerializedName("last")
    private boolean last;

    @SerializedName("numberOfElements") // Số phần tử trong trang hiện tại
    private int numberOfElements;

    @SerializedName("empty")
    private boolean empty;

    // --- Getters (Bắt buộc) ---
    public List<T> getContent() {
        return content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getSize() {
        return size;
    }

    public int getNumber() {
        return number;
    }

    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public boolean isEmpty() {
        return empty;
    }
}