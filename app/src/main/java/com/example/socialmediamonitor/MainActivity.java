package com.example.socialmediamonitor;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // Import Toolbar
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "SocialMediaMonitorChannel";
    private static final int NOTIFICATION_PERMISSION_CODE = 1;

    String[] months_worked_on = {"Oct 2023", "Nov 2023"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapter_items;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    TextView connectTextView;
    TextView timerDisplay;
    ProgressBar remainingTimeProgressBar;

    boolean isTimerRunning = false;
    long totalTimeInMillis = 2 * 60 * 60 * 1000; // 2 hours
    long timeLeftInMillis = 0;

    Handler handler;
    Runnable timerRunnable;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        autoCompleteTextView = findViewById(R.id.auto_complete_text);
        adapter_items = new ArrayAdapter<>(this, R.layout.list_item, months_worked_on);
        autoCompleteTextView.setAdapter(adapter_items);

        // Change the type of connectButton to TextView
        connectTextView = findViewById(R.id.connect_textview);
        timerDisplay = findViewById(R.id.timer_display);
        remainingTimeProgressBar = findViewById(R.id.remaining_time_progressBar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Add Toolbar and set it as the ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the drawer toggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Set up the timer
        handler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                updateTimer();
                handler.postDelayed(this, 1000);
            }
        };

        // Set up notifications channel
        createNotificationChannel();

        // Start the timer when the app is launched
        startTimer();

        // Set up listeners
        connectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleTimer();
            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(MainActivity.this, "Item " + item, Toast.LENGTH_SHORT).show();
            }
        });

        // Request notification permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, NOTIFICATION_PERMISSION_CODE);
        }
    }

    private void toggleTimer() {
        if (isTimerRunning) {
            pauseTimer();
        } else {
            startTimer();
        }
    }

    private void startTimer() {
        isTimerRunning = true;
        connectTextView.setText("Running");
//        connectTextView.setBackgroundResource(R.drawable.green_button_background);
        handler.postDelayed(timerRunnable, 0);
    }

    private void pauseTimer() {
        isTimerRunning = false;
        connectTextView.setText("Not Running");
//        connectTextView.setBackgroundResource(R.drawable.red_button_background);
        handler.removeCallbacks(timerRunnable);
    }

    private boolean isAppRunning(String packageName) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = activityManager.getRunningAppProcesses();

        if (runningProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void updateTimer() {
        timeLeftInMillis += 1000;

        // Check if specific apps are running
        boolean isWhatsAppRunning = isAppRunning("com.whatsapp");
        boolean isFacebookRunning = isAppRunning("com.facebook.katana");
        boolean isInstagramRunning = isAppRunning("com.instagram.android");

        // Check if any of the specified apps is not running
        if (!(isWhatsAppRunning || isFacebookRunning || isInstagramRunning)) {
            pauseTimer();
            connectTextView.setText("Not Running");
            return;
        }

        // Update UI
        updateUI();

        // Send notifications
        if (timeLeftInMillis == totalTimeInMillis / 2) {
            sendNotification("Half of the time has passed. Take a break soon.");

        } else if (timeLeftInMillis == 5 * 60 * 1000) {
            sendNotification("You have 5 minutes left. Wrap up and take a break.");
        }

        // Update the connectTextView text based on the timer state
        if (isTimerRunning) {
            connectTextView.setText("Running");
        } else {
            connectTextView.setText("Not Running");
        }

        // Check if the total time has elapsed
        if (timeLeftInMillis >= totalTimeInMillis) {
            timeLeftInMillis = totalTimeInMillis;
            pauseTimer();
            sendNotification("Time's up! Take a break.");
        }
    }

    private void updateUI() {
        long hours = timeLeftInMillis / 3600000;
        long minutes = (timeLeftInMillis % 3600000) / 60000;
        long seconds = (timeLeftInMillis % 60000) / 1000;

        String timeLeftFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timerDisplay.setText(timeLeftFormatted);

        int progress = (int) ((timeLeftInMillis * 100) / totalTimeInMillis);
        remainingTimeProgressBar.setProgress(progress);
    }

    private void sendNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Social Media Monitor")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, continue with your code
                startTimer(); // or any other actions that require the permission
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
                Toast.makeText(this, "Permission denied. App may not work as expected.", Toast.LENGTH_SHORT).show();
                // You might want to handle this case more gracefully, perhaps by disabling certain features.
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Social Media Monitor";
            String description = "Channel for Social Media Monitor notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(timerRunnable);
    }
    public void openMenu(View view) {
        // Add your logic to open the menu here
        // For example, you can show a popup menu or navigate to another activity
        Toast.makeText(this, "Menu Clicked", Toast.LENGTH_SHORT).show();
    }

}

