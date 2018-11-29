package com.example.nisch100.call_a_bus;

import android.content.Intent;
import android.opengl.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
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

    FirebaseDatabase database;
    DatabaseReference databaseReference;

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

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        addToDatabase(busToSchedule);
        printInfo();
/*
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHome(view);
            }
        });
*/

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

        String pickUpLocation = extras.getString("pickDest");
        String dropOffLocation = extras.getString("dropDest");

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

        ArrayList<Integer> reminderTimes = extras.getIntegerArrayList("reminders");
        ArrayList<String> relativeReminders = extras.getStringArrayList("relatives");

        Bus scheduledBus = new Bus(month, day, year,
                                initialHour, initialMinute, initialAmPm,
                                pickUpLocation, dropOffLocation,
                                roundTrip, roundTripHour, roundTripMin, rtAmPm,
                                reminderTimes, relativeReminders);
        return scheduledBus;
    }


    private void addToDatabase(Bus bus) {
        Map<String, Object> busScheduler = new HashMap<String, Object>();
        busScheduler.put("Scheduled Buses", bus);

        databaseReference.child("users");
    }

    public void goToHome(View view){
        //Intent goHome =
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
