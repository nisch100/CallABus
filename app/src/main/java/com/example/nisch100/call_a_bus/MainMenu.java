package com.example.nisch100.call_a_bus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainMenu extends AppCompatActivity {

    private Button schedule, myBuses, account, logoutButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        schedule = findViewById(R.id.schedule_button);
        myBuses = findViewById(R.id.viewBuses);
        account = findViewById(R.id.info_button);
        logoutButton = findViewById(R.id.logout_button);

        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), SchedulingActivity.class));
            }
        });

        myBuses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), YourBusesActivity.class));
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), RelativesInfo.class));
            }
        });

    }

}