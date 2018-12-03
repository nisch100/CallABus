package com.example.nisch100.call_a_bus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * (Ran)
 *                             Relatives Information Page
 */

public class RelativesInfo extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final String TAG = "error_msg";

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mNameView;
    private EditText mPhoneView;
    private EditText mName2View;
    private EditText mPhone2View;
    private EditText mName3View;
    private EditText mPhone3View;
    private View mProgressView;
    private View mLoginFormView;
    // Database related
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference userReference;
    private DatabaseReference relativeReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatives_info);
        // Set up the login form.
        // mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        // populateAutoComplete();

        /*
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        */

        mNameView = (EditText) findViewById(R.id.relative_name);
        mPhoneView = (EditText) findViewById(R.id.relative_phone);

        mName2View = (EditText) findViewById(R.id.relative2_name);
        mPhone2View = (EditText) findViewById(R.id.relative2_phone);

        mName3View = (EditText) findViewById(R.id.relative3_name);
        mPhone3View = (EditText) findViewById(R.id.relative3_phone);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = firebaseAuth.getCurrentUser();
        userReference = databaseReference.child("users").child(user.getUid());
        relativeReference = databaseReference.child("relatives").child(user.getUid());

        if (user != null) {
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // attemptLogin();
                saveInfo();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        relativeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currentName1 = (String) dataSnapshot.child("rel1").child("name").getValue();
                String currentPhone1 = (String) dataSnapshot.child("rel1").child("phone").getValue();
                String currentName2 = (String) dataSnapshot.child("rel2").child("name").getValue();
                String currentPhone2 = (String) dataSnapshot.child("rel2").child("phone").getValue();
                String currentName3 = (String) dataSnapshot.child("rel3").child("name").getValue();
                String currentPhone3 = (String) dataSnapshot.child("rel3").child("phone").getValue();
                // String currentRelationship = (String) dataSnapshot.child("relRel").getValue();
                // Toast.makeText(getApplicationContext(), "data snapshot get: " + currentName, Toast.LENGTH_SHORT).show();

                if(currentName1.length() >= 1) {
                    mNameView.setText(currentName1, TextView.BufferType.EDITABLE);
                }
                if (currentPhone1.length() >= 1) {
                    mPhoneView.setText(currentPhone1, TextView.BufferType.EDITABLE);
                }
                if(currentName2.length() >= 1) {
                    mName2View.setText(currentName2, TextView.BufferType.EDITABLE);
                }
                if (currentPhone2.length() >= 1) {
                    mPhone2View.setText(currentPhone2, TextView.BufferType.EDITABLE);
                }
                if(currentName3.length() >= 1) {
                    mName3View.setText(currentName3, TextView.BufferType.EDITABLE);
                }
                if (currentPhone3.length() >= 1) {
                    mPhone3View.setText(currentPhone3, TextView.BufferType.EDITABLE);
                }
                /*
                if(currentRelationship.length() >= 1){
                    mRelationshipView.setText(currentRelationship, TextView.BufferType.EDITABLE);
                }*/

            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }

    // Switch to Main Menu (upon button click)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_general, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_menu_button) {
            // make intent to swithc to main menu
            Intent intent = new Intent(getApplicationContext(), MainMenu.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveInfo(){
        // save account information to firebase
        // TODO
        String mName = mNameView.getText().toString();
        String mPhone = mPhoneView.getText().toString();
        String mName2 = mName2View.getText().toString();
        String mPhone2 = mPhone2View.getText().toString();
        String mName3 = mName3View.getText().toString();
        String mPhone3 = mPhone3View.getText().toString();
        // String mRelationship = mRelationshipView.getText().toString();

        // relative 1 update
        if(mName.length() >= 1 && mPhone.length() >= 1) {
            relativeReference.child("rel1").child("name").setValue(mName);
            relativeReference.child("rel1").child("phone").setValue(mPhone);
        }
        else{
            Toast.makeText(getApplicationContext(), "Info for primary relative need to be filled", Toast.LENGTH_SHORT).show();
        }

        // relative 2 update
        if(mName2.length() >= 1 && mPhone2.length() >= 1) {
            relativeReference.child("rel2").child("name").setValue(mName2);
            relativeReference.child("rel2").child("phone").setValue(mPhone2);
        }
        else if(mName2.length() == 0 && mPhone2.length() == 0){

        }
        else{
            Toast.makeText(getApplicationContext(), "Please fill in complete info for relative 2", Toast.LENGTH_SHORT).show();
        }

        // relative 3 update
        if(mName3.length() >= 1 && mPhone3.length() >= 1) {
            relativeReference.child("rel3").child("name").setValue(mName3);
            relativeReference.child("rel3").child("phone").setValue(mPhone3);
        }
        else if(mName3.length() == 0 && mPhone3.length() == 0){

        }
        else{
            Toast.makeText(getApplicationContext(), "Please fill in complete info for relative 3", Toast.LENGTH_SHORT).show();
        }

        /*
        if(mRelationship.length() >= 1) {
            databaseReference.child("users").child(user.getUid()).child("relRel").setValue(mRelationship);
        }
        else{
            Toast.makeText(getApplicationContext(), "All info need to be filled. Please don't leave any field empty", Toast.LENGTH_SHORT).show();
        }*/

        // toast a message "personal information is updated"
        Toast.makeText(getApplicationContext(), "relative information is updated for: " + mName, Toast.LENGTH_SHORT).show();
    }

    public void switchAccountInfo(View view){
        Intent intent = new Intent(getApplicationContext(), AccountInfo.class);
        startActivity(intent);
        // Utilities.switchScreen(getApplicationContext(), "AccountInfo");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }


    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RelativesInfo.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }



    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

}

