package com.example.nisch100.call_a_bus;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class SchedulingReminders extends AppCompatActivity {

    Button finishButton, canButton;
    CheckBox r10, rArrival, rOther, rel1, rel2, rel3;
    EditText amount;
    int reminderTime;
    String uid;
    int busID;
    Bus bus;

    ArrayList<String> mRelatives = new ArrayList<String>();
    ArrayList<Integer> mReminders = new ArrayList<Integer>();

    FirebaseDatabase database;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedulingreminders);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.child("relatives").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String rName1, rName2, rName3;

                rName1 = (String) dataSnapshot.child("rel1").child("name").getValue();
                rName2 = (String) dataSnapshot.child("rel2").child("name").getValue();
                rName3 = (String) dataSnapshot.child("rel3").child("name").getValue();

                displayRelatives(rName1, rName2, rName3);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

        if (getIntent().hasExtra("bus")) {
            bus = (Bus) getIntent().getExtras().getParcelable("bus");
        } else {
            bus = null;
        }

        if (getIntent().hasExtra("busID")) {
            busID = getIntent().getExtras().getInt("busID");
        } else {
            busID = -1;
        }
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

    public void displayRelatives(String one, String two, String three){
        rel1.setText(one.trim());

        //check if these relatives have been added/set up by user
        if(!two.equals("")){
            rel2.setText(two.trim());
        } else {
            rel2.setVisibility(View.INVISIBLE);
        }

        if(!three.equals("")){
            rel3.setText(three.trim());
        } else {
            rel3.setVisibility(View.INVISIBLE);
        }
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
        confirmationIntent.putExtra("bus", bus);
        confirmationIntent.putExtra("busID", busID);

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
