package com.kdkvit.wherewasi.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.kdkvit.wherewasi.R;
import com.kdkvit.wherewasi.services.SoniTalkService;
import com.kdkvit.wherewasi.utils.SharedPreferencesUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import Managers.ServerRequestManager;

public class CommunicationFragment extends Fragment {

    private boolean listening = false;
    private View rootView;
    private BroadcastReceiver soniTalkReceiver;
    private MaterialButton listenBtn;


    public CommunicationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_communication, container, false);

        MaterialButton button = rootView.findViewById(R.id.send_message);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SoniTalkService.isBusy()) {
                    Intent intent = new Intent(rootView.getContext(), SoniTalkService.class);
                    intent.putExtra("command", "start_playing");
                    rootView.getContext().startService(intent);
                }
            }
        });
        listenBtn = rootView.findViewById(R.id.listen);
        listenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listening = !listening;
                if (listening) {
                    listenBtn.setText("Stop");
                } else {
                    if (!SoniTalkService.isBusy()) {
                        listenBtn.setText("Listen");
                    }
                }
                Intent intent = new Intent(rootView.getContext(), SoniTalkService.class);
                intent.putExtra("command", listening ? "start_listening" : "stop_listening");
                //view.getContext().startService(intent);
                rootView.getContext().startService(intent);
            }

        });
        initReceiver();

        return rootView;
    }

    private void initReceiver() {

        IntentFilter soniFilter = new IntentFilter(SoniTalkService.SONITALK_RECEIVER);
        soniTalkReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String command = intent.getStringExtra("command");
                if (command.equals("stop_listening")) {
                    listening = !listening;
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            listenBtn.setText("Listen");
                        }
                    });
                }
            }
        };
        LocalBroadcastManager.getInstance(rootView.getContext()).registerReceiver(soniTalkReceiver, soniFilter);
    }


    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(rootView.getContext()).unregisterReceiver(soniTalkReceiver);
        super.onDestroyView();
    }
}