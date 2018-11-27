package com.example.nisch100.call_a_bus;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class SchedulingReminders extends AppCompatActivity {

    Button finishButton, canButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedulingreminders);

        finishButton = (Button) findViewById(R.id.finishSched);
        canButton = (Button) findViewById(R.id.cancelButton);

        canButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptCancel(view);
            }
        });


    }

    public void set(View v) {

    }

    public void promptCancel(View v) {
        final AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(v.getContext(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getApplicationContext());
        }
        builder.setTitle("Alert")
                .setMessage("Are you sure you want to cancel? If you cancel, all changes will be lost.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
