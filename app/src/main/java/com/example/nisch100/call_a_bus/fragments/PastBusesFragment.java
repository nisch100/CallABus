package com.example.nisch100.call_a_bus.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.nisch100.call_a_bus.Bus;
import com.example.nisch100.call_a_bus.CustomArrayAdapter;
import com.example.nisch100.call_a_bus.ListItem;
import com.example.nisch100.call_a_bus.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.*;

/**
 * A simple {@link ListFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PastBusesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PastBusesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PastBusesFragment extends ListFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    List<ListItem> items;
    ArrayList<Bus> buses;
    PastBusesFragment fragment;

    DatabaseReference rootRef;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PastBusesFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PastBusesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PastBusesFragment newInstance(String param1, String param2) {
        PastBusesFragment fragment = new PastBusesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.list, container, false);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        items = new ArrayList<>();
        buses = new ArrayList<>();
        fragment = this;

        new LongOperation().execute("");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class LongOperation extends AsyncTask<String, Void, String> {
        ArrayList<String> statusListTextOnly;
        @Override
        protected String doInBackground(String... params) {
            rootRef = FirebaseDatabase.getInstance().getReference();
            firebaseAuth = FirebaseAuth.getInstance();
            user = firebaseAuth.getCurrentUser();

            rootRef.child("buses").child(user.getUid()).
                    addListenerForSingleValueEvent(new ValueEventListener() {
                        int month, day, year, initialHour, initialMinute;
                        String initialAmPm, pickUpLocation, dropOffLocation;

                        boolean roundTrip;
                        int roundTripHour, roundTripMin;
                        String rtAmPm;

                        ArrayList<Integer> reminderTimes = new ArrayList<>();
                        ArrayList<String> relativeReminders = new ArrayList<>();

                        Date busDate, currDate = new Date();

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                month = snap.child("month").getValue(Integer.class);
                                day = snap.child("day").getValue(Integer.class);
                                year = snap.child("year").getValue(Integer.class);
                                initialHour = snap.child("initialHour").getValue(Integer.class);
                                initialMinute = snap.child("initialMinute").getValue(Integer.class);
                                initialAmPm = snap.child("initialAmPm").getValue(String.class);
                                pickUpLocation = snap.child("pickUpLocation").getValue(String.class);
                                dropOffLocation = snap.child("dropOffLocation").getValue(String.class);
                                roundTrip = snap.child("roundTrip").getValue(Boolean.class);
                                roundTripHour = snap.child("roundTripHour").getValue(Integer.class);
                                roundTripMin = snap.child("roundTripMin").getValue(Integer.class);
                                rtAmPm = snap.child("rtAmPm").getValue(String.class);
                                reminderTimes = (ArrayList<Integer>) snap.child("reminderTimes").getValue();
                                relativeReminders = (ArrayList<String>) snap.child("relativeReminders").getValue();
                                busDate = new Date(year - 1900, month - 1, day,
                                        initialAmPm.equals("AM") ? initialHour : initialHour + 12, initialMinute);


                                if (busDate.before(currDate)) {
                                    Bus newBus = new Bus(month, day, year, initialHour, initialMinute,
                                            initialAmPm, pickUpLocation, dropOffLocation, roundTrip,
                                            roundTripHour, roundTripMin, rtAmPm, reminderTimes,
                                            relativeReminders);

                                    buses.add(newBus);
                                    items.add(new ListItem(busDate, dropOffLocation));
                                }

                                fragment.setListAdapter(new CustomArrayAdapter(getActivity(), R.layout.list_item, items, buses, 1));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            fragment.setListAdapter(new CustomArrayAdapter(getActivity(), R.layout.list_item, items, buses, 0));
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}

