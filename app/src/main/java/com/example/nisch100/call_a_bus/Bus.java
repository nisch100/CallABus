package com.example.nisch100.call_a_bus;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Bus implements Parcelable {

    int month;
    int day;
    int year;

    int initialHour;
    int initialMinute;
    String initialAmPm;

    String pickUpLocation;
    String dropOffLocation;

    boolean roundTrip;
    int roundTripHour;
    int roundTripMin;
    String rtAmPm;

    ArrayList<Integer> reminderTimes;
    ArrayList<String> relativeReminders;

    public Bus(int month, int day, int year, int initialHour, int initialMinute, String initialAmPm, String pickUpLocation, String dropOffLocation, boolean roundTrip, int roundTripHour, int roundTripMin, String rtAmPm, ArrayList<Integer> reminderTimes, ArrayList<String> relativeReminders) {
        this.month = month;
        this.day = day;
        this.year = year;
        this.initialHour = initialHour;
        this.initialMinute = initialMinute;
        this.initialAmPm = initialAmPm;
        this.pickUpLocation = pickUpLocation;
        this.dropOffLocation = dropOffLocation;
        this.roundTrip = roundTrip;
        this.roundTripHour = roundTripHour;
        this.roundTripMin = roundTripMin;
        this.rtAmPm = rtAmPm;
        this.reminderTimes = reminderTimes;
        this.relativeReminders = relativeReminders;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getYear() {
        return year;
    }

    public int getInitialHour() {
        return initialHour;
    }

    public int getInitialMinute() {
        return initialMinute;
    }

    public String getInitialAmPm() {
        return initialAmPm;
    }

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public String getDropOffLocation() {
        return dropOffLocation;
    }

    public boolean isRoundTrip() {
        return roundTrip;
    }

    public int getRoundTripHour() {
        return roundTripHour;
    }

    public int getRoundTripMin() {
        return roundTripMin;
    }

    public String getRtAmPm() {
        return rtAmPm;
    }

    public ArrayList<Integer> getReminderTimes() {
        return reminderTimes;
    }

    public ArrayList<String> getRelativeReminders() {
        return relativeReminders;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setInitialHour(int initialHour) {
        this.initialHour = initialHour;
    }

    public void setInitialMinute(int initialMinute) {
        this.initialMinute = initialMinute;
    }

    public void setInitialAmPm(String initialAmPm) {
        this.initialAmPm = initialAmPm;
    }

    public void setPickUpLocation(String pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public void setDropOffLocation(String dropOffLocation) {
        this.dropOffLocation = dropOffLocation;
    }

    public void setRoundTrip(boolean roundTrip) {
        this.roundTrip = roundTrip;
    }

    public void setRoundTripHour(int roundTripHour) {
        this.roundTripHour = roundTripHour;
    }

    public void setRoundTripMin(int roundTripMin) {
        this.roundTripMin = roundTripMin;
    }

    public void setRtAmPm(String rtAmPm) {
        this.rtAmPm = rtAmPm;
    }

    public void setReminderTimes(ArrayList<Integer> reminderTimes) {
        this.reminderTimes = reminderTimes;
    }

    public void setRelativeReminders(ArrayList<String> relativeReminders) {
        this.relativeReminders = relativeReminders;
    }

    protected Bus(Parcel in) {
        month = in.readInt();
        day = in.readInt();
        year = in.readInt();
        initialHour = in.readInt();
        initialMinute = in.readInt();
        initialAmPm = in.readString();
        pickUpLocation = in.readString();
        dropOffLocation = in.readString();
        roundTrip = in.readByte() != 0x00;
        roundTripHour = in.readInt();
        roundTripMin = in.readInt();
        rtAmPm = in.readString();
        if (in.readByte() == 0x01) {
            reminderTimes = new ArrayList<Integer>();
            in.readList(reminderTimes, Integer.class.getClassLoader());
        } else {
            reminderTimes = null;
        }
        if (in.readByte() == 0x01) {
            relativeReminders = new ArrayList<String>();
            in.readList(relativeReminders, String.class.getClassLoader());
        } else {
            relativeReminders = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(month);
        dest.writeInt(day);
        dest.writeInt(year);
        dest.writeInt(initialHour);
        dest.writeInt(initialMinute);
        dest.writeString(initialAmPm);
        dest.writeString(pickUpLocation);
        dest.writeString(dropOffLocation);
        dest.writeByte((byte) (roundTrip ? 0x01 : 0x00));
        dest.writeInt(roundTripHour);
        dest.writeInt(roundTripMin);
        dest.writeString(rtAmPm);
        if (reminderTimes == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(reminderTimes);
        }
        if (relativeReminders == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(relativeReminders);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Bus> CREATOR = new Parcelable.Creator<Bus>() {
        @Override
        public Bus createFromParcel(Parcel in) {
            return new Bus(in);
        }

        @Override
        public Bus[] newArray(int size) {
            return new Bus[size];
        }
    };
}