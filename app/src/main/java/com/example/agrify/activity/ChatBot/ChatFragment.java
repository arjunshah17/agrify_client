package com.example.agrify.activity.ChatBot;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.agrify.R;
import com.example.agrify.databinding.FragmentChatBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    FragmentChatBinding bind;
    public ChatFragment() {
        // Required empty public constructor
    }
static  final String chatbotUrl="https://snatchbot.me/webchat?botID=52622&appID=sKkDZ6o4cIQLE9QvsgIF";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bind = DataBindingUtil.inflate(inflater, R.layout.fragment_chat,
                container, false);
        bind.chatActionbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), chatbotActivity.class));
            }
        });

        return bind.getRoot();
    }

}
