package com.example.musicapp.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.musicapp.Fragment.HomeFragment;
import com.example.musicapp.Fragment.PlaylistFragment;
import com.example.musicapp.Fragment.PremiumFragment;
import com.example.musicapp.Fragment.ProfileFragment;
import com.example.musicapp.Fragment.SearchFragment;
import com.example.musicapp.Fragment.SubsciptionFragment;
import com.example.musicapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Default screen
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        ImageButton searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(v -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new SearchFragment())
                    .addToBackStack(null)
                    .commit();
        });


        bottomNavigationView.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        Fragment selectedFragment = null;
                        int id = item.getItemId();

                        if (id == R.id.nav_home) {
                            selectedFragment = new HomeFragment();
                        } else if (id == R.id.nav_profile) {
                            selectedFragment = new ProfileFragment();   // ← no args
                        } else if (id == R.id.nav_premium) {
                            selectedFragment = new PremiumFragment();
                        } else if (id == R.id.nav_subscribed) {
                            selectedFragment = new SubsciptionFragment();
                        } else if (id == R.id.nav_playlist) {
                            selectedFragment = new PlaylistFragment();
                        }

                        if (selectedFragment != null) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, selectedFragment)
                                    .commit();
                            return true;
                        }
                        return false;
                    }
                });
    }
}
