package com.example.musicapp.dto;

import java.util.List;

public class PagedResponseDTO<T> {
    private List<T> content;
    private int page;
    private int totalPages;
    private long totalElements;
    private boolean isLast;

    // Constructor
    public PagedResponseDTO(List<T> content, int page, int totalPages, long totalElements, boolean isLast) {
        this.content = content;
        this.page = page;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.isLast = isLast;
    }

    // Getters
    public List<T> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public boolean isLast() {
        return isLast;
    }

    // Setters
    public void setContent(List<T> content) {
        this.content = content;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public void setLast(boolean last) {
        isLast = last;
    }
}
