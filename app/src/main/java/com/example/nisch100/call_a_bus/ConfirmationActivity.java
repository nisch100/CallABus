package com.example.nisch100.call_a_bus;

import android.content.Intent;
import android.opengl.Visibility;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
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

    FirebaseAuth myAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    Registerfirebase userObj;
    String uid;
    int numBuses;

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


        databaseReference.child("users").child(uid).child("numBuses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numBuses = dataSnapshot.getValue(Integer.class);
                databaseReference.child("users").child(uid).child("numBuses").setValue(numBuses+1);

                // initiateUserObj(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addToDatabase(busToSchedule);


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


    public void addToDatabase(Bus bus) {
        //Toast.makeText(getApplicationContext(), numBuses, Toast.LENGTH_LONG);


        databaseReference.child("buses").child(uid).child("bus" + numBuses).setValue(bus);

        //updates user in database
//        userObj.setNumBuses(userObj.getNumBuses() + 1);
//        databaseReference.child("users").child(uid).setValue(userObj);
    }

    public void initiateUserObj(DataSnapshot snapshot){
        for(DataSnapshot ds : snapshot.getChildren()){
            if(ds.getValue().equals("users")){
                for(DataSnapshot snap : ds.getChildren()){
                    if(snap.getValue().equals(uid)){
                        userObj = new Registerfirebase();
                        userObj.setName(snap.child(uid).getValue(Registerfirebase.class).getName());
                        userObj.setEmail(snap.child(uid).getValue(Registerfirebase.class).getEmail());
                        userObj.setPassword(snap.child(uid).getValue(Registerfirebase.class).getPassword());
                        userObj.setPhone(snap.child(uid).getValue(Registerfirebase.class).getPhone());
                        userObj.setNumBuses(snap.child(uid).getValue(Registerfirebase.class).getNumBuses());
                    }
                }
            }
        }
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
