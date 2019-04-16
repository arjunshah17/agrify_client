package com.example.agrify.activity.ChatBot;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.agrify.R;
import com.example.agrify.databinding.ActivityChatbotBinding;

public class chatbotActivity extends AppCompatActivity {
    static  final String chatbotUrl="https://snatchbot.me/webchat?botID=52622&appID=sKkDZ6o4cIQLE9QvsgIF";
    ActivityChatbotBinding bind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       bind=DataBindingUtil.setContentView(this, R.layout.activity_chatbot);
        bind.chatbot.getSettings().setJavaScriptEnabled(true);
        bind.chatbot.loadUrl(chatbotUrl);
    }
}
