package com.example.musicapp.activity;


import android.content.Context;

import android.os.Bundle;

import android.os.Handler;

import android.util.Log;

import android.view.MenuItem;

import android.view.View;

import android.view.inputmethod.InputMethodManager;

import android.widget.ImageView;

import android.widget.Toast;


import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;

import androidx.fragment.app.FragmentTransaction;


import com.example.musicapp.Fragment.HomeFragment;

import com.example.musicapp.Fragment.PlaylistFragment;

import com.example.musicapp.Fragment.PremiumFragment;

import com.example.musicapp.Fragment.ProfileFragment;

import com.example.musicapp.Fragment.SearchFragment;

import com.example.musicapp.Fragment.SubsciptionFragment;

import com.example.musicapp.R;

import com.example.musicapp.dto.SongDTO;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.navigation.NavigationBarView;


import java.util.List;


public class HomeActivity extends AppCompatActivity {


    private BottomNavigationView bottomNavigationView;

    private ImageView searchIconButton;


    private static final String TAG = "HomeActivity";

    private static final String HOME_FRAGMENT_TAG = "HOME_FRAGMENT_TAG";

    private static final String SEARCH_FRAGMENT_TAG = "SEARCH_FRAGMENT_TAG";

    private static final String PLAYLIST_FRAGMENT_TAG = "PLAYLIST_FRAGMENT_TAG";

    private static final String SUBSCRIPTION_FRAGMENT_TAG = "SUBSCRIPTION_FRAGMENT_TAG";

    private static final String PREMIUM_FRAGMENT_TAG = "PREMIUM_FRAGMENT_TAG";

    private static final String PROFILE_FRAGMENT_TAG = "PROFILE_FRAGMENT_TAG";


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);


        bottomNavigationView = findViewById(R.id.bottom_navigation);

        searchIconButton = findViewById(R.id.search_icon_button);


// Default screen

        if (savedInstanceState == null) {

            loadFragmentInternal(new HomeFragment(), HOME_FRAGMENT_TAG, false, true);

        }


        setupBottomNavigationListener();

        setupSearchIconListener();

    }

    private void setupSearchIconListener() {

        if (searchIconButton != null) {

            searchIconButton.setOnClickListener(v -> {

                Log.d(TAG, "Search icon button clicked. Opening SearchFragment.");

                openSearchFragment();

            });

        } else {

            Log.w(TAG, "searchIconButton is null in HomeActivity. Check activity_home.xml.");

        }

    }


    private void setupBottomNavigationListener() {

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = null;

                String selectedTag = null;

                boolean addToBackStack = true;


                int itemId = item.getItemId();

                FragmentManager fm = getSupportFragmentManager();

                Fragment currentFragment = fm.findFragmentById(R.id.fragment_container);


                if (itemId == R.id.nav_home) {

                    if (!(currentFragment instanceof HomeFragment)) {

                        selectedFragment = findOrCreateFragment(fm, HOME_FRAGMENT_TAG, HomeFragment.class);

                        selectedTag = HOME_FRAGMENT_TAG;

                        addToBackStack = false;

                    } else {

                        return true;

                    }

                } else if (itemId == R.id.nav_subscribed) {

                    if (!(currentFragment instanceof SubsciptionFragment)) {

                        selectedFragment = findOrCreateFragment(fm, SUBSCRIPTION_FRAGMENT_TAG, SubsciptionFragment.class);

                        selectedTag = SUBSCRIPTION_FRAGMENT_TAG;

                    } else {

                        return true;

                    }

                } else if (itemId == R.id.nav_playlist) {

                    if (!(currentFragment instanceof PlaylistFragment)) {

                        selectedFragment = findOrCreateFragment(fm, PLAYLIST_FRAGMENT_TAG, PlaylistFragment.class);

                        selectedTag = PLAYLIST_FRAGMENT_TAG;

                    } else {

                        return true;

                    }

                } else if (itemId == R.id.nav_premium) {

                    if (!(currentFragment instanceof PremiumFragment)) {

                        selectedFragment = findOrCreateFragment(fm, PREMIUM_FRAGMENT_TAG, PremiumFragment.class);

                        selectedTag = PREMIUM_FRAGMENT_TAG;

                    } else {

                        return true;

                    }

                } else if (itemId == R.id.nav_profile) {

                    if (!(currentFragment instanceof ProfileFragment)) {

                        selectedFragment = findOrCreateFragment(fm, PROFILE_FRAGMENT_TAG, ProfileFragment.class);

                        selectedTag = PROFILE_FRAGMENT_TAG;

                    } else {

                        return true;

                    }

                }


                if (selectedFragment != null) {

                    if (currentFragment instanceof SearchFragment) {

                        fm.popBackStack(SEARCH_FRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    }

                    loadFragmentInternal(selectedFragment, selectedTag, addToBackStack, false);

                    return true;

                }

                return false;

            }

        });

    }


    private <T extends Fragment> T findOrCreateFragment(FragmentManager fm, String tag, Class<T> fragmentClass) {

        Fragment fragment = fm.findFragmentByTag(tag);

        if (fragment == null) {

            try {

                fragment = fragmentClass.newInstance();

                Log.d(TAG, "Creating new instance of " + fragmentClass.getSimpleName() + " with tag: " + tag);

            } catch (IllegalAccessException | java.lang.InstantiationException e) {

                Log.e(TAG, "Error creating fragment: " + fragmentClass.getSimpleName(), e);

                return null;

            }

        } else {

            Log.d(TAG, "Reusing existing instance of " + fragmentClass.getSimpleName() + " with tag: " + tag);

        }

        if (fragmentClass.isInstance(fragment)) {

            return fragmentClass.cast(fragment);

        } else {

            Log.e(TAG, "Fragment with tag " + tag + " is not of type " + fragmentClass.getSimpleName() + ". Forcing new instance.");

            try {

                fragment = fragmentClass.newInstance();

                return fragmentClass.cast(fragment);

            } catch (IllegalAccessException | java.lang.InstantiationException e) {

                Log.e(TAG, "Error creating fragment: " + fragmentClass.getSimpleName(), e);

                return null;

            }

        }

    }


    public void openSearchFragment() {

        SearchFragment searchFragment = new SearchFragment();

        loadFragmentInternal(searchFragment, SEARCH_FRAGMENT_TAG, true, false);

        Log.d(TAG, "Opening SearchFragment. Initial query: none");

    }


    private void loadFragmentInternal(Fragment fragment, String tag, boolean addToBackStack, boolean isInitialLoad) {

        if (fragment == null) {

            Log.e(TAG, "Attempting to load a null fragment. Tag: " + tag);

            return;

        }

        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction transaction = fm.beginTransaction();


        transaction.replace(R.id.fragment_container, fragment, tag);


        if (addToBackStack && !isInitialLoad) {

            transaction.addToBackStack(tag);

            Log.d(TAG, "Fragment " + tag + " added to back stack.");

        }


        try {

            transaction.commit();

            Log.d(TAG, "Fragment transaction committed for " + tag);

        } catch (IllegalStateException e) {

            Log.e(TAG, "Could not commit fragment transaction for " + tag, e);

        }

    }


    @Override

    public void onBackPressed() {

        FragmentManager fm = getSupportFragmentManager();

        Fragment searchFragmentInstance = fm.findFragmentByTag(SEARCH_FRAGMENT_TAG);


        if (searchFragmentInstance != null && searchFragmentInstance.isVisible()) {

            Log.d(TAG, "Back pressed: SearchFragment is visible, popping it.");

            fm.popBackStack(SEARCH_FRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            updateBottomNavSelectionAfterBackPress(fm);

            return;

        }


        if (fm.getBackStackEntryCount() > 0) {

            Log.d(TAG, "Back pressed: Popping general back stack.");

            fm.popBackStackImmediate();

            updateBottomNavSelectionAfterBackPress(fm);

        } else {

            Fragment currentFragment = fm.findFragmentById(R.id.fragment_container);

            if (currentFragment instanceof HomeFragment) {

                Log.d(TAG, "Back pressed: On HomeFragment with empty back stack. Exiting app.");

                super.onBackPressed();

            } else {

                Log.w(TAG, "Back pressed: Not on HomeFragment and empty back stack. Defaulting to HomeFragment.");

                loadFragmentInternal(findOrCreateFragment(fm, HOME_FRAGMENT_TAG, HomeFragment.class), HOME_FRAGMENT_TAG, false, true);

                bottomNavigationView.setSelectedItemId(R.id.nav_home);

            }

        }

    }


    private void updateBottomNavSelectionAfterBackPress(FragmentManager fm) {

        new Handler().postDelayed(() -> {

            Fragment currentVisibleFragment = fm.findFragmentById(R.id.fragment_container);

            Log.d(TAG, "Updating BottomNav selection after back press. Current fragment: " +

                    (currentVisibleFragment != null ? currentVisibleFragment.getClass().getSimpleName() : "null"));


            if (currentVisibleFragment instanceof HomeFragment) {

                if (bottomNavigationView.getSelectedItemId() != R.id.nav_home) {

                    bottomNavigationView.setSelectedItemId(R.id.nav_home);

                }

            } else if (currentVisibleFragment instanceof SubsciptionFragment) {

                if (bottomNavigationView.getSelectedItemId() != R.id.nav_subscribed) {

                    bottomNavigationView.setSelectedItemId(R.id.nav_subscribed);

                }

            } else if (currentVisibleFragment instanceof PlaylistFragment) {

                if (bottomNavigationView.getSelectedItemId() != R.id.nav_playlist) {

                    bottomNavigationView.setSelectedItemId(R.id.nav_playlist);

                }

            } else if (currentVisibleFragment instanceof PremiumFragment) {

                if (bottomNavigationView.getSelectedItemId() != R.id.nav_premium) {

                    bottomNavigationView.setSelectedItemId(R.id.nav_premium);

                }

            } else if (currentVisibleFragment instanceof ProfileFragment) {

                if (bottomNavigationView.getSelectedItemId() != R.id.nav_profile) {

                    bottomNavigationView.setSelectedItemId(R.id.nav_profile);

                }

            }

        }, 50);

    }


    private void hideKeyboard() {

        View view = this.getCurrentFocus();

        if (view == null) {

            view = new View(this);

        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null && view.getWindowToken() != null) {

            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        } else {

            Log.w(TAG, "Could not hide keyboard, no focus or imm is null.");

        }

    }

}