package com.example.musicapp.Fragment;

import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.adapter.PlaylistAdapter;
import com.example.musicapp.api.PlaylistAPI;
import com.example.musicapp.dto.PlaylistDTO;
import com.example.musicapp.ultis.RetrofitService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistFragment extends Fragment implements PlaylistAdapter.OnPlaylistActionListener {

    private RecyclerView recyclerView;
    private PlaylistAdapter adapter;
    private List<PlaylistDTO> playlistList = new ArrayList<>();
    private PlaylistAPI playlistApi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        recyclerView = view.findViewById(R.id.playlistRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Retrofit  có TokenInterceptor, không cần truyền token thủ công
        playlistApi = RetrofitService.getInstance(requireContext()).createService(PlaylistAPI.class);

        adapter = new PlaylistAdapter(playlistList, this);
        recyclerView.setAdapter(adapter);

        loadPlaylists();

        view.findViewById(R.id.addPlaylistButton).setOnClickListener(v -> showAddDialog());

        return view;
    }

    @Override
    public void onPlaylistClick(PlaylistDTO playlist) {
        Bundle bundle = new Bundle();
        bundle.putLong("playlistId", playlist.getId());

        Fragment playlistDetailFragment = new PlaylistDetailFragment();
        playlistDetailFragment.setArguments(bundle);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, playlistDetailFragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadPlaylists() {
        playlistApi.getAllPlaylists().enqueue(new Callback<List<PlaylistDTO>>() {
            @Override
            public void onResponse(Call<List<PlaylistDTO>> call, Response<List<PlaylistDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    playlistList.clear();
                    playlistList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Unable to get playlist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PlaylistDTO>> call, Throwable t) {
                Toast.makeText(getContext(), "Error while connecting to playlist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddDialog() {
        EditText input = new EditText(getContext());
        new AlertDialog.Builder(requireContext())
                .setTitle("New playlist ")
                .setView(input)
                .setPositiveButton("Create", (dialog, which) -> {
                    PlaylistDTO dto = new PlaylistDTO();
                    dto.setName(input.getText().toString());
                    dto.setSongIds(new ArrayList<>());

                    playlistApi.createPlaylist(dto).enqueue(new Callback<PlaylistDTO>() {
                        @Override
                        public void onResponse(Call<PlaylistDTO> call, Response<PlaylistDTO> response) {
                            if (response.isSuccessful()) {
                                loadPlaylists();
                            } else {
                                Toast.makeText(getContext(), "Unable to create playlist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<PlaylistDTO> call, Throwable t) {
                            Toast.makeText(getContext(), "Unable to create playlist", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onRename(PlaylistDTO playlist) {
        EditText input = new EditText(getContext());
        input.setText(playlist.getName());

        new AlertDialog.Builder(requireContext())
                .setTitle("Rename playlist")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    playlist.setName(input.getText().toString());

                    playlistApi.updatePlaylist(playlist.getId(), playlist).enqueue(new Callback<PlaylistDTO>() {
                        @Override
                        public void onResponse(Call<PlaylistDTO> call, Response<PlaylistDTO> response) {
                            if (response.isSuccessful()) {
                                loadPlaylists();
                            } else {
                                Toast.makeText(getContext(), "Unable to change", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<PlaylistDTO> call, Throwable t) {
                            Toast.makeText(getContext(), "Error while changing playlist's name", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDelete(PlaylistDTO playlist) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete playlist")
                .setMessage("Are you sure?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    playlistApi.deletePlaylist(playlist.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                loadPlaylists();
                            } else {
                                Toast.makeText(getContext(), "Unable to delete playlist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getContext(), "Error while deleting playlist", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
