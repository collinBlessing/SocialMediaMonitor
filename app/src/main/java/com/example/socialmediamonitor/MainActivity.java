package com.example.socialmediamonitor;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    // list of date
    String[] months_worked_on = {"Oct 2023", "Nov 2023"};
    AutoCompleteTextView autoCompleteTextView;

    ArrayAdapter<String> adapter_items;

    //connect button
    Button connectButton;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the connect button after setContentView
        connectButton = findViewById(R.id.connect_button);

        //auto complete text view
        autoCompleteTextView = findViewById(R.id.auto_complete_text);
        adapter_items = new ArrayAdapter<>(this, R.layout.list_item, months_worked_on);
        autoCompleteTextView.setAdapter(adapter_items);

            //drawer layout
        drawerLayout = findViewById(R.id.drawer_layout);


        //set
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(1).toString();
                Toast.makeText(MainActivity.this, "Item " + item, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
