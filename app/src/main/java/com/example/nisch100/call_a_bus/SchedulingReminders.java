package com.example.nisch100.call_a_bus;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class SchedulingReminders extends AppCompatActivity {

    Button finishButton, canButton;
    RadioGroup reminderGroup;
    RadioButton defaultReminder, customReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedulingreminders);

        reminderGroup = (RadioGroup) findViewById(R.id.radioReminder);
        defaultReminder = (RadioButton) findViewById(R.id.reminderDefault);
        customReminder = (RadioButton) findViewById(R.id.reminderCustom);

        finishButton = (Button) findViewById(R.id.finishSched);
        canButton = (Button) findViewById(R.id.cancelButton);

        canButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptCancel(view);
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmation(view);
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

    public void confirmation(View v){
        Intent received = getIntent();

        if(/* radio buttons not checked */) {


        } else {
            Intent intent = new Intent(SchedulingReminders.this, ConfirmationActivity.class);
            //intent.putExtra("yearExtra", cYear);

            startActivity(intent);
        }


    }
}
