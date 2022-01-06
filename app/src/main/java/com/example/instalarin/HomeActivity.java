package com.example.instalarin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.instalarin.Fragments.HomeFragment;
import com.example.instalarin.Fragments.NotificationFragment;
import com.example.instalarin.Fragments.ProfileFragment;
import com.example.instalarin.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navHome :
                        selectorFragment = new HomeFragment();
                        break;

                    case   R.id.navSearch:
                        selectorFragment = new SearchFragment();
                        break;

                    case   R.id.navAdd:
                        selectorFragment = null;
                        startActivity(new Intent(HomeActivity.this, PostActivity.class));
                        break;

                    case   R.id.navHeart:
                        selectorFragment = new NotificationFragment();
                        break;

                    case   R.id.navProfile:
                        selectorFragment = new ProfileFragment();
                        break;
                }

                if(selectorFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,selectorFragment).commit();
                }

                return true;

            }
        });

        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            String profileId = intent.getString("publisherId");
            System.out.println(profileId+"   heeere");

            getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId", profileId).apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new ProfileFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.navProfile);
        } else {
            System.out.println("failed");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer , new HomeFragment()).commit();
        }

    }
}