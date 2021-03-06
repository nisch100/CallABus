package com.example.nisch100.call_a_bus;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.content.Intent;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


public class SchedulingActivity extends AppCompatActivity {

    EditText date, time;
    Spinner pickUp, dropOff;
    DatePickerDialog datePickerDialog;
    Button rtTimeButton, cont, mCancel;
    int cYear, cMonth, cDay;
    String AM_PM;
    int cHour, cMin, rtHour, rtMin;
    String pickupLocation, dropoffLocation;

    RadioGroup rtGroup;
    RadioButton rtY, rtN;
    Boolean roundtrip;
    int busID;
    Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduling);

        dateTimePickers();
        roundTrip();


        // Pick-up location text field
        pickUp = (Spinner) findViewById(R.id.pickup);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.places, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pickUp.setAdapter(adapter);

        /*pickUp.setOnItemClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pickUp.getText().toString().equals("Full address of pick-up location")){
                    pickUp.getText().clear();
                }
            }
        });*/

        // Drop-off location text field
        dropOff = (Spinner) findViewById(R.id.dropoff);
        dropOff.setAdapter(adapter);
        /*dropOff.setOnItemClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dropOff.getText().toString().equals("Full address of drop-off location")){
                    dropOff.getText().clear();
                }
            }
        });*/


        // Cancel button
        mCancel = (Button) findViewById(R.id.cancel);
        mCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                promptCancel(view);
            }
        });


        // Continue button
        cont = (Button) findViewById(R.id.continue2);
        cont.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSchedReminders(view);
            }
        });


        if (getIntent().hasExtra("bus")) {
            bus = (Bus) getIntent().getExtras().getParcelable("bus");
            populateFields();
        } else {
            bus = null;
        }

        if (getIntent().hasExtra("busID")) {
            busID = getIntent().getExtras().getInt("busID");
        } else {
            busID = -1;
        }
    }

    public void dateTimePickers() {
        /********************************
         * Date Picker
         */
        date = (EditText) findViewById(R.id.date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                datePickerDialog = new DatePickerDialog(SchedulingActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                date.setText((monthOfYear + 1) + "/"  + dayOfMonth + "/" + year);
                                cYear = year;
                                cMonth = monthOfYear+1;
                                cDay = dayOfMonth;


                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        /********************************
         * Time Picker
         */
        time = (EditText) findViewById(R.id.time);
        // perform click event on edit text
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SchedulingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        cHour = selectedHour;
                        cMin = selectedMinute;
                        if (selectedHour == 0) {
                            selectedHour += 12;
                            AM_PM = "AM";
                        } else if (selectedHour == 12) {
                            AM_PM = "PM";
                        } else if (selectedHour > 12) {
                            selectedHour -= 12;
                            AM_PM = "PM";
                        } else {
                            AM_PM = "AM";
                        }

                        if(selectedMinute < 10){
                            time.setText(selectedHour + ":0" + selectedMinute + AM_PM);
                        } else {
                            time.setText(selectedHour + ":" + selectedMinute + AM_PM);
                        }
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
    }

    public void roundTrip() {
        rtGroup = (RadioGroup) findViewById(R.id.radioRT);
        rtY = (RadioButton) findViewById(R.id.yesRT);
        rtN = (RadioButton) findViewById(R.id.noRT);
        rtTimeButton = (Button) findViewById(R.id.rtTime);


        rtGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(rtN.isChecked() == true) {
                    // disable button
                    rtTimeButton.setEnabled(false);
                }
                if(rtY.isChecked()) {
                    // enable button
                    rtTimeButton.setEnabled(true);
                }
            }
        });

        rtTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog rtTimePicker;
                rtTimePicker = new TimePickerDialog(SchedulingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        rtHour = selectedHour;
                        rtMin = selectedMinute;
                        if (selectedHour == 0) {
                            selectedHour += 12;
                            AM_PM = "AM";
                        } else if (selectedHour == 12) {
                            AM_PM = "PM";
                        } else if (selectedHour > 12) {
                            selectedHour -= 12;
                            AM_PM = "PM";
                        } else {
                            AM_PM = "AM";
                        }

                        if(selectedMinute < 10){
                            rtTimeButton.setText(selectedHour + ":0" + selectedMinute + AM_PM);
                        } else {
                            rtTimeButton.setText(selectedHour + ":" + selectedMinute + AM_PM);
                        }
                    }
                }, hour, minute, false);
                rtTimePicker.setTitle("Select Return Trip Time");
                rtTimePicker.show();
            }
        });
    }

    public void goToSchedReminders(View v){
        pickupLocation = pickUp.getSelectedItem().toString().trim();
        dropoffLocation = dropOff.getSelectedItem().toString().trim();

        //check all fields filled out
        if(!errorCheck()){
            errorAllFields(v);
            return;
        }

        //check locations aren't the same
        if(pickupLocation.equals(dropoffLocation)){
            errorLocations(v);
            return;
        }

        // error check for RT trip time
        if(rtY.isChecked()){
            if((cHour > rtHour || (cHour == rtHour && cMin >= rtMin)) && bus == null) {
                errorTime(v);
                return;
            } else {
                Intent intent = new Intent(SchedulingActivity.this, SchedulingReminders.class);
                intent.putExtra("yearExtra", cYear);
                intent.putExtra("dayExtra", cDay);
                intent.putExtra("monthExtra", cMonth);
                intent.putExtra("hourExtra", cHour);
                intent.putExtra("minExtra", cMin);
                intent.putExtra("rtHourExtra", rtHour);
                intent.putExtra("rtMinExtra", rtMin);
                intent.putExtra("pickDest", pickupLocation);
                intent.putExtra("dropDest", dropoffLocation);
                intent.putExtra("roundtrip?", true);
                intent.putExtra("bus", bus);
                intent.putExtra("busID", busID);

                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(SchedulingActivity.this, SchedulingReminders.class);
            intent.putExtra("yearExtra", cYear);
            intent.putExtra("dayExtra", cDay);
            intent.putExtra("monthExtra", cMonth);
            intent.putExtra("hourExtra", cHour);
            intent.putExtra("minExtra", cMin);
            intent.putExtra("rtHourExtra", -1);
            intent.putExtra("rtMinExtra", -1);
            intent.putExtra("pickDest", pickupLocation);
            intent.putExtra("dropDest", dropoffLocation);
            intent.putExtra("roundtrip?", false);
            intent.putExtra("bus", bus);
            intent.putExtra("busID", busID);

            startActivity(intent);
        }

    }

    public boolean errorCheck(){
        if(!rtY.isChecked() && !rtN.isChecked()){
            return false;
        }

        if(date.getText().toString().equals("Select Date...")){
            return false;
        }

        if(time.getText().toString().equals("Select Time...")){
            return false;
        }

        if(pickUp.getSelectedItem().toString().equals("") || pickUp.getSelectedItem().toString().equals("Full address of pick-up location")){
            return false;
        }

        if(dropOff.getSelectedItem().toString().equals("") || dropOff.getSelectedItem().toString().equals("Full address of drop-off location")){
            return false;
        }

        return true;
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
                        //go back to menu page
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

    public void errorAllFields(View v) {
        String err = "Please fill out all fields.";
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

    public void errorTime(View v){
        // display error and don't move to next page
        String err = "Round trip pick up time must be after original pick up time.";

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

    public void errorLocations(View v){
        // display error and don't move to next page
        String err = "Pick-up location and Drop-off location cannot be the same.";

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

    private void populateFields() {
        int rtHr, rtMin;
        ArrayList<String> locations;

        locations = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.places)));

        cYear = bus.year;
        cMonth = bus.month;
        cDay = bus.day;
        AM_PM = bus.initialAmPm;
        cHour = bus.initialHour;
        cMin = bus.initialMinute;
        rtHour = bus.roundTripHour;
        rtMin = bus.roundTripMin;
        pickupLocation = bus.pickUpLocation;
        dropoffLocation = bus.dropOffLocation;

        date.setText((bus.month) + "/"  + bus.day + "/" + bus.year);

        if(bus.initialMinute < 10){
            time.setText(bus.initialHour + ":0" + bus.initialMinute + bus.initialAmPm);
        } else {
            time.setText(bus.initialHour + ":" + bus.initialMinute + bus.initialAmPm);
        }

        pickUp.setSelection(locations.indexOf(bus.pickUpLocation));
        dropOff.setSelection(locations.indexOf(bus.dropOffLocation));

        if(bus.roundTrip) {
            // enable button
            rtY.setChecked(true);
            rtHr = bus.roundTripHour;
            rtMin = bus.roundTripMin;

            if(bus.roundTripMin < 10){
                rtTimeButton.setText(rtHr + ":0" + rtMin + bus.rtAmPm);
            } else {
                rtTimeButton.setText(rtHr + ":" + rtMin + bus.rtAmPm);
            }
        } else {
            rtN.setChecked(true);
        }
    }


}
