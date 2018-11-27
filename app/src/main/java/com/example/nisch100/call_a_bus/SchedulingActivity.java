package com.example.nisch100.call_a_bus;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;


public class SchedulingActivity extends AppCompatActivity {

    EditText date, time, pickUp, dropOff;
    DatePickerDialog datePickerDialog;
    Button rtTimeButton, cont, mCancel;
    int cYear, cMonth, cDay;
    String AM_PM;
    int cHour, cMin, rtHour, rtMin;

    RadioGroup rtGroup;
    RadioButton rtY, rtN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduling);

        cont = (Button) findViewById(R.id.continue2);
        cont.setOnClickListener(this);


        /********************************
         * Pick up location
         */
        pickUp = (EditText) findViewById(R.id.pickup);
        pickUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                pickUp.getText().clear();
            }
        });

        /********************************
         * Drop off location
         */
        dropOff = (EditText) findViewById(R.id.dropoff);
        dropOff.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dropOff.getText().clear();
            }
        });


        /********************************
         * Continue button
         */
        cont = (Button) findViewById(R.id.continue2);
        cont.setOnClickListener(this, new OnClickListener() {
            @Override
            public void onClick(View view) {
                // error check for RT trip time
                Toast.makeText(getApplicationContext(), "Hi", Toast.LENGTH_SHORT);
                /*if(rtY.isChecked()){
                    Toast.makeText(getApplicationContext(), "Hi", Toast.LENGTH_SHORT);
                    if(cHour > rtHour || (cHour == rtHour && cMin > rtMin)) {
                        // display error and don't move to next page
                        String err = "Round trip pick up time must be after original pick up time.";

                        AlertDialog.Builder builder = new AlertDialog.Builder(SchedulingActivity.this);
                        builder.setTitle("Error")
                                .setMessage(err)
                                .setCancelable(true)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
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


                        startActivity(intent);
                    }
                }

                Intent intent = new Intent(SchedulingActivity.this, SchedulingReminders.class);
                intent.putExtra("yearExtra", cYear);
                intent.putExtra("dayExtra", cDay);
                intent.putExtra("monthExtra", cMonth);
                intent.putExtra("hourExtra", cHour);
                intent.putExtra("minExtra", cMin);

                startActivity(intent);*/

            }
        });

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
                        time.setText(selectedHour + ":" + selectedMinute + AM_PM);
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
                        rtTimeButton.setText("Return trip at " + selectedHour + ":" + selectedMinute + AM_PM);
                    }
                }, hour, minute, false);
                rtTimePicker.setTitle("Select Return Trip Time");
                rtTimePicker.show();
            }
        });
    }

}
