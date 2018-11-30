package com.example.nisch100.call_a_bus;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class SchedulingReminders extends AppCompatActivity {

    Button finishButton, canButton;
    CheckBox r10, rArrival, rOther, rel1, rel2, rel3;
    EditText amount;
    int reminderTime;

    ArrayList<String> mRelatives = new ArrayList<String>();
    ArrayList<Integer> mReminders = new ArrayList<Integer>();

    FirebaseDatabase database;
    DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedulingreminders);

        r10 = (CheckBox) findViewById(R.id.min10);
        maintainCheckboxes(r10, "rem");

        rArrival = (CheckBox) findViewById(R.id.atArrival);
        maintainCheckboxes(rArrival, "rem");

        rOther = (CheckBox) findViewById(R.id.other);

        rel1 = (CheckBox) findViewById(R.id.relative1);
        maintainCheckboxes(rel1, "rel");

        rel2 = (CheckBox) findViewById(R.id.relative2);
        maintainCheckboxes(rel2, "rel");

        rel3 = (CheckBox) findViewById(R.id.relative3);
        maintainCheckboxes(rel3, "rel");

        amount = (EditText) findViewById(R.id.minOther);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

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
                        startActivity(new Intent(getApplicationContext(), MainMenu.class));
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
        String otherTime = amount.getText().toString();

        if(rOther.isChecked() && otherTime.equals("")){
            String err = "You must specify an amount time for \"Other\".";

            final AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(v.getContext(), android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(getApplicationContext());
            }
            builder.setTitle("Error")
                    .setMessage(err)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        } else if(!r10.isChecked() && !rArrival.isChecked() && !rOther.isChecked()) {
            String err = "You must choose at least one reminder time.";

            final AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(v.getContext(), android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(getApplicationContext());
            }
            builder.setTitle("Error")
                    .setMessage(err)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        } else if(!rel1.isChecked() && !rel2.isChecked() && !rel3.isChecked()){
            String err = "You must choose at least one relative to be notified.";

            final AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(v.getContext(), android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(getApplicationContext());
            }
            builder.setTitle("Error")
                    .setMessage(err)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        if(!otherTime.equals("")){
            reminderTime = Integer.parseInt(otherTime);
        }

        //check to make sure "Other" checkbox is accounted for (either removed or added)
        for(int i = 0; i < mReminders.size(); i++){
            if(mReminders.get(i) != 10 && mReminders.get(i) != 0){
                if(!rOther.isChecked()){
                    mReminders.remove(i);
                }
            }
        }
        if(rOther.isChecked()){
            mReminders.add(reminderTime);
        }


        Intent received = getIntent();
        Bundle extras = received.getExtras();

        Intent confirmationIntent = new Intent(SchedulingReminders.this, ConfirmationActivity.class);
        confirmationIntent.putExtras(extras);

        confirmationIntent.putIntegerArrayListExtra("reminders", mReminders);
        confirmationIntent.putStringArrayListExtra("relatives", mRelatives);

        startActivity(confirmationIntent);

    }

    public void maintainCheckboxes(CheckBox cb, String key){
        if(key.equals("rel")){
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(((CheckBox)view).isChecked()){
                        mRelatives.add(((CheckBox)view).getText().toString());
                    } else {
                        mRelatives.remove(((CheckBox)view).getText().toString());
                    }
                }
            });

        } else {
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((CheckBox) view).getId() == R.id.min10) {
                        if (((CheckBox) view).isChecked()) {
                            mReminders.add(10);
                        } else {
                            int index = mReminders.lastIndexOf(10);
                            mReminders.remove(index);
                        }
                    } else if (((CheckBox) view).getId() == R.id.atArrival) {
                        if (((CheckBox) view).isChecked()) {
                            mReminders.add(0);
                        } else {
                            int index = mReminders.lastIndexOf(0);
                            mReminders.remove(index);
                        }
                    }

                }
            });
        }

    }
}
