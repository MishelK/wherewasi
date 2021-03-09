package com.kdkvit.wherewasi.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.kdkvit.wherewasi.R;
import com.kdkvit.wherewasi.utils.SharedPreferencesUtils;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import br.com.simplepass.loadingbutton.customViews.ProgressButton;
import models.User;


public class WelcomeFragment extends Fragment {


    private View rootView;

    public interface WelcomeCallback{
        void onStartClick(String name);
    }

    WelcomeCallback listener;

    public WelcomeFragment(WelcomeCallback listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_welcome, container, false);
        final TextInputEditText nicknameTI = rootView.findViewById(R.id.nickname);
        CircularProgressButton startBtn = (CircularProgressButton) rootView.findViewById(R.id.btn_start);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBtn.startAnimation();
                if(listener!=null){
                    listener.onStartClick(nicknameTI.getText().toString());
                }
            }
        });
        return rootView;
    }
}