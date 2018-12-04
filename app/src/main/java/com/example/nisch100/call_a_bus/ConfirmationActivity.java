package com.example.nisch100.call_a_bus;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.opengl.Visibility;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ConfirmationActivity extends AppCompatActivity {

    private TextView pageHeading;
    private TextView date;
    private TextView time;
    private TextView pickUp;
    private TextView dropOff;
    private TextView timeRemin;
    private TextView relRemin;
    private TextView rtTime;

    Button doneButton;

    Bus busToSchedule;

    FirebaseAuth myAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    String uid;

    int numBuses;
    String rel1;
    String rel2;
    String rel3;
    Calendar cal = Calendar.getInstance();
    HashMap<String, String> phoneNums;

    int busID;
    Bus bus;

    private AlarmManager mAlarmManager;
    private PendingIntent mNotificationReceiverPendingIntent;
    private Intent mNotificationReceiverIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        pageHeading = (TextView) findViewById(R.id.textView2);
        date = (TextView) findViewById(R.id.textView3);
        time = (TextView) findViewById(R.id.textView4);
        pickUp = (TextView) findViewById(R.id.textView5);
        dropOff = (TextView) findViewById(R.id.textView6);
        rtTime = (TextView) findViewById(R.id.rtDisplay);
        timeRemin = (TextView) findViewById(R.id.textView7);
        relRemin = (TextView) findViewById(R.id.textView8);
        doneButton = (Button) findViewById(R.id.done);

        busToSchedule = initializeBus();
        printInfo();

        myAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        uid = myAuth.getCurrentUser().getUid();

        if (getIntent().hasExtra("busID")) {
            busID = getIntent().getExtras().getInt("busID");
        } else {
            busID = -1;
        }

        databaseReference.child("users").child(uid).child("numBuses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int num = dataSnapshot.getValue(Integer.class);

                databaseReference.child("users").child(uid).child("numBuses").removeEventListener(this);
                databaseReference.child("users").child(uid).child("numBuses").setValue(num+1);

                if (busID != -1) {
                    Log.d("TAG", "adding to DB with " + busID);
                    addToDatabase(busToSchedule, busID);
                } else {
                    Log.d("TAG", "adding to DB with " + num);
                    addToDatabase(busToSchedule, num);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHome(view);
            }
        });
    }


    private Bus initializeBus(){
        Intent received = getIntent();
        Bundle extras = received.getExtras();

        int month = extras.getInt("monthExtra");
        int day = extras.getInt("dayExtra");
        int year = extras.getInt("yearExtra");

        int initialHour = extras.getInt("hourExtra");
        int initialMinute = extras.getInt("minExtra");
        String initialAmPm;
        if (initialHour == 0) {
            initialHour += 12;
            initialAmPm = "AM";
        } else if (initialHour == 12) {
            initialAmPm = "PM";
        } else if (initialHour > 12) {
            initialHour -= 12;
            initialAmPm = "PM";
        } else {
            initialAmPm = "AM";
        }

        final String pickUpLocation = extras.getString("pickDest");
        final String dropOffLocation = extras.getString("dropDest");

        boolean roundTrip = extras.getBoolean("roundtrip?");
        int roundTripHour= extras.getInt("rtHourExtra");
        int roundTripMin = extras.getInt("rtMinExtra");
        String rtAmPm;
        if (roundTripHour == 0) {
            roundTripHour += 12;
            rtAmPm = "AM";
        } else if (roundTripHour == 12) {
            rtAmPm = "PM";
        } else if (roundTripHour > 12) {
            roundTripHour -= 12;
            rtAmPm = "PM";
        } else {
            rtAmPm = "AM";
        }

        final ArrayList<Integer> reminderTimes = extras.getIntegerArrayList("reminders");
        final ArrayList<String> relativeReminders = extras.getStringArrayList("relatives");

        Bus scheduledBus = new Bus(month, day, year,
                initialHour, initialMinute, initialAmPm,
                pickUpLocation, dropOffLocation,
                roundTrip, roundTripHour, roundTripMin, rtAmPm,
                reminderTimes, relativeReminders);



        if (initialAmPm.equals("PM") && initialHour != 12) {
            initialHour += 12;
        }
        else if (initialAmPm.equals("AM") && initialHour == 12) {
            initialHour -= 12;
        }

        phoneNums = new HashMap<String, String>();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        myAuth = FirebaseAuth.getInstance();
        uid = myAuth.getCurrentUser().getUid();
        cal.set(year, month - 1, day, initialHour, initialMinute);
        databaseReference.child("relatives").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String rel1Name = (String) dataSnapshot.child("rel1").child("name").getValue();
                String rel2Name = (String) dataSnapshot.child("rel2").child("name").getValue();
                String rel3Name = (String) dataSnapshot.child("rel3").child("name").getValue();

                for (int j = 0; j < relativeReminders.size(); j++) {
                    if (relativeReminders.get(j).equals(rel1Name)) {
                        String temp = String.valueOf(dataSnapshot.child("rel1").child("phone").getValue());
                        Log.i("if called", "here");
                        phoneNums.put("rel1", "+1" + temp);
                    }
                    else if (relativeReminders.get(j).equals(rel2Name)) {
                        String temp = String.valueOf(dataSnapshot.child("rel2").child("phone").getValue());
                        phoneNums.put("rel2", "+1" + temp);
                        ConfirmationActivity.this.rel2 = "+1" + temp;
                    }
                    else if(relativeReminders.get(j).equals(rel3Name)){
                        String temp = String.valueOf(dataSnapshot.child("rel3").child("phone").getValue());
                        phoneNums.put("rel3", "+1" + temp);
                        ConfirmationActivity.this.rel3 = "+1" + temp;
                    }
                }
                for (int i = 0; i < reminderTimes.size(); i++) {

                    mNotificationReceiverIntent = new Intent(ConfirmationActivity.this,
                            AlarmNotificationReceiver.class);
                    mNotificationReceiverIntent.putExtra("pickup", pickUpLocation);
                    mNotificationReceiverIntent.putExtra("dropoff", dropOffLocation);
                    mNotificationReceiverIntent.putExtra("time", reminderTimes.get(i).toString());

                    if (phoneNums.get("rel1") != null) {
                        mNotificationReceiverIntent.putExtra(getString(R.string.rel1), phoneNums.get("rel1"));
                    }
                    if (phoneNums.get("rel2") != null) {
                        mNotificationReceiverIntent.putExtra(getString(R.string.rel2), phoneNums.get("rel2"));
                    }
                    if (phoneNums.get("rel3") != null) {
                        mNotificationReceiverIntent.putExtra(getString(R.string.rel3), phoneNums.get("rel3"));
                    }



                    mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                            ConfirmationActivity.this, i, mNotificationReceiverIntent, 0);
                    mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.SEND_SMS)
                                == PackageManager.PERMISSION_GRANTED) {
                            mAlarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() - (60000*reminderTimes.get(i)), mNotificationReceiverPendingIntent);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return scheduledBus;
    }


    public void addToDatabase(Bus bus, int numBuses) {
        String busNumber = "bus" + numBuses;
        databaseReference.child("buses").child(uid).child(busNumber).setValue(bus);
    }

    public void goToHome(View view){
        startActivity(new Intent(view.getContext(), MainMenu.class));
    }

    public void printInfo(){

        int month = busToSchedule.getMonth();
        int day = busToSchedule.getDay();
        int year = busToSchedule.getYear();
        String dateHTML = "<b>Date: </b>" + month + "/" + day + "/" + year;
        date.setText(Html.fromHtml(dateHTML));

        int hour = busToSchedule.getInitialHour();
        int min = busToSchedule.getInitialMinute();
        String am_pm = busToSchedule.initialAmPm;

        String timeHTML = "<b>Time: </b>";

        if (min < 10) {
            timeHTML = timeHTML + hour + ":0" + min + am_pm;
            time.setText(Html.fromHtml(timeHTML));
        } else {
            timeHTML = timeHTML + hour + ":" + min + am_pm;
            time.setText(Html.fromHtml(timeHTML));
        }

        String pick = busToSchedule.getPickUpLocation();
        String drop = busToSchedule.getDropOffLocation();

        String pickHTML = "<b>Pick-up Location: </b>" + pick;
        String dropHTML = "<b>Drop-off Location: </b>" + drop;

        pickUp.setText(Html.fromHtml(pickHTML));
        dropOff.setText(Html.fromHtml(dropHTML));

        int rtHour = busToSchedule.getRoundTripHour();
        int rtMin = busToSchedule.getRoundTripMin();
        boolean rt = busToSchedule.isRoundTrip();
        am_pm = busToSchedule.getRtAmPm();

        //checks for a round trip
        if(rt){
            rtTime.setVisibility(View.VISIBLE);
            String rtTimeHTML;
            if (rtMin < 10) {
                rtTimeHTML = "<b>Round Trip Pick-up Time: </b>" + rtHour + ":0" + rtMin;
                rtTime.setText(Html.fromHtml(rtTimeHTML));
            } else {
                rtTimeHTML = "<b>Round Trip Pick-up Time: </b>" + rtHour + ":" + rtMin + am_pm;
                rtTime.setText(Html.fromHtml(rtTimeHTML));
            }
        } else {
            rtTime.setVisibility(View.GONE);
        }


        ArrayList<Integer> reminders = busToSchedule.getReminderTimes();
        String remsHTML = "<b>Reminders: </b>";
        for (int i=0; i < reminders.size(); i++) {
            if(reminders.get(i) == 0){
                remsHTML = remsHTML + "At bus' arrival";
            } else {
                remsHTML = remsHTML +  reminders.get(i) + " minutes before bus' arrival";
            }

            if(i != reminders.size()-1){
                remsHTML = remsHTML + ", ";
            }
        }
        timeRemin.setText(Html.fromHtml(remsHTML));
        timeRemin.setGravity(Gravity.CENTER_HORIZONTAL);

        ArrayList<String> relatives = busToSchedule.getRelativeReminders();
        String relsHTML = "<b>Relative reminders: </b>";
        for (int i=0; i < relatives.size(); i++) {
            relsHTML = relsHTML + relatives.get(i);

            if(i != relatives.size() - 1) {
                relsHTML = relsHTML + ", ";
            }
        }
        relRemin.setText(Html.fromHtml(relsHTML));

    }
}
