package com.example.musicapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicapp.R;
import com.example.musicapp.dto.ArtistDTO;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private List<ArtistDTO> artistList;

    public ArtistAdapter(List<ArtistDTO> artistList) {
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_artist, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        ArtistDTO artist = artistList.get(position);
        holder.artistName.setText(artist.getStageName());
    }

    @Override
    public int getItemCount() {
        return artistList != null ? artistList.size() : 0;
    }

    static class ArtistViewHolder extends RecyclerView.ViewHolder {
        TextView artistName;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            artistName = itemView.findViewById(R.id.artist_name);
        }
    }
}
