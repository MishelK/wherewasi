package com.kdkvit.wherewasi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.kdkvit.wherewasi.fragments.MainFragment;
import com.kdkvit.wherewasi.fragments.WelcomeFragment;
import com.kdkvit.wherewasi.utils.SharedPreferencesUtils;

import java.util.UUID;

import models.User;

import static actions.actions.sendUserToBe;

public class MainActivity extends AppCompatActivity {

    public static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Init user from local storage
        user = SharedPreferencesUtils.getUser(this);
        if(user==null){ //User not exists -> welcome page
            WelcomeFragment welcomeFragment = new WelcomeFragment(name -> {
                user = new User(name, UUID.randomUUID().toString());
                sendUserToBe(MainActivity.this,user);
                MainFragment mainFragment = new MainFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, mainFragment).commit();
            });
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, welcomeFragment).commit();
        }else{ //User exists -> Main Page
            if(user.getId() == 0) { //User want not created in DB
                sendUserToBe(this, user);
            }
            MainFragment mainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, mainFragment).commit();
        }
    }
}