package com.example.socialmediamonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class launcher_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent main_frame_intent = new Intent(launcher_activity.this, MainActivity.class);
                startActivity(main_frame_intent);

                finish();
            }
        }, 3000);
    }
}