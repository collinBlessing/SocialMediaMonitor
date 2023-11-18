package com.example.socialmediamonitor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AccountsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        // WhatsApp Account
        LinearLayout whatsappAccount = findViewById(R.id.whatsapp_account);
        whatsappAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openApp("com.whatsapp");
            }
        });

        // Instagram Account
        LinearLayout instagramAccount = findViewById(R.id.instagram_account);
        instagramAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openApp("com.instagram.android");
            }
        });

        // Facebook Account
        LinearLayout facebookAccount = findViewById(R.id.facebook_account);
        facebookAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openApp("com.facebook.katana");
            }
        });

        // Add more accounts click listeners as needed
    }

    private void openApp(String packageName) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            Toast.makeText(this, "App not installed", Toast.LENGTH_SHORT).show();
        }
    }
}
