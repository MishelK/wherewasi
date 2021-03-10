package com.kdkvit.wherewasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.kdkvit.wherewasi.fragments.MainFragment;
import com.kdkvit.wherewasi.fragments.WelcomeFragment;
import com.kdkvit.wherewasi.utils.SharedPreferencesUtils;

import java.util.UUID;

import models.User;

import static actions.actions.sendUserToBe;

public class MainActivity extends AppCompatActivity {

    public static User user;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);


        //Init user from local storage
        user = SharedPreferencesUtils.getUser(this);
        if(user==null){ //User not exists -> welcome page
            WelcomeFragment welcomeFragment = new WelcomeFragment(name -> {
                user = new User(name, UUID.randomUUID().toString());
                sendUserToBe(MainActivity.this,user);
                initLoggedInUser();
            });
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, welcomeFragment).commit();
            drawerLayout.setEnabled(false);
            navigationView.setEnabled(false);
            navigationView.setVisibility(View.GONE);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        }else{ //User exists -> Main Page
            if(user.getId() == 0) { //User want not created in DB
                sendUserToBe(this, user);
            }
            initLoggedInUser();
        }

    }

    private void initLoggedInUser() {

        MainFragment mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, mainFragment).commit();

        drawerLayout.setEnabled(true);
        navigationView.setEnabled(true);
        navigationView.setVisibility(View.VISIBLE);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

//        ImageButton menuBtn = findViewById(R.id.menu_btn);
//        menuBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawerLayout.openDrawer(GravityCompat.START);
//
//            }
//        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);
                Toast.makeText(MainActivity.this, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}