package com.example.musicapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.Adapter.SimpleTextAdapter;
import com.example.musicapp.R;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment
{
    private RecyclerView trendingArtistsRecycler;
    private RecyclerView trendingMusicRecycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        trendingArtistsRecycler = view.findViewById(R.id.recycler_trending_artists);
        trendingMusicRecycler = view.findViewById(R.id.recycler_trending_music);

        setupTrendingArtists();
        setupTrendingMusic();

        return view;
    }

    private void setupTrendingArtists() {
        List<String> artists = Arrays.asList("Artist 1", "Artist 2", "Artist 3");
        trendingArtistsRecycler.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        trendingArtistsRecycler.setAdapter(new SimpleTextAdapter(artists));
    }

    private void setupTrendingMusic() {
        List<String> songs = Arrays.asList("Song A", "Song B", "Song C");
        trendingMusicRecycler.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        trendingMusicRecycler.setAdapter(new SimpleTextAdapter(songs));
    }
}
