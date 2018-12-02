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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseError;
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
 *                         Personal Information Page
 */

public class AccountInfo extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final String TAG = "error_msg";

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mNameView;
    private EditText mPhoneView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    // Database related
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        mNameView = (EditText) findViewById(R.id.user_name);
        mPhoneView = (EditText) findViewById(R.id.user_phone);
        mPasswordView = (EditText) findViewById(R.id.user_password);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = firebaseAuth.getCurrentUser();
        userReference = databaseReference.child("users").child(user.getUid());


        if (user != null) {
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }

        // myRef.child(userID).child("which field do you want to update").setValue(ValueVariable);

        // listener is not needed since no need to check login authentication status
        /*
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

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // we are not doing login so ignore this function
                // attemptLogin();
                saveInfo();
            }
        });

        // Button mSwitchPageButton = (Button) findViewById(R.id.);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currentName = (String) dataSnapshot.child("name").getValue();
                String currentPhone = (String) dataSnapshot.child("phone").getValue();
                String currentPassword = (String) dataSnapshot.child("password").getValue();
                // Toast.makeText(getApplicationContext(), "data snapshot get: " + currentName, Toast.LENGTH_SHORT).show();
                if (currentName.length() >= 1) {
                    mNameView.setText(currentName, TextView.BufferType.EDITABLE);
                }
                if (currentPhone.length() >= 1) {
                    mPhoneView.setText(currentPhone, TextView.BufferType.EDITABLE);
                }
                if (currentPassword.length() >= 1) {
                    mPasswordView.setText(currentPassword, TextView.BufferType.EDITABLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });

    }

    private void saveInfo(){
        // save account information to firebase
        // TODO
        String mName = mNameView.getText().toString();
        String mPhone = mPhoneView.getText().toString();
        String mPassword = mPasswordView.getText().toString();
        // String mAddress = mAddressView.getText().toString();

        // DatabaseReference mUser = databaseReference.child(user.getUid());
        // mUser.child("name").setValue(mName);
        if(mName.length() >= 1) {
            userReference.child("name").setValue(mName);
        }
        else{
            Toast.makeText(getApplicationContext(), "All info need to be filled. Please don't leave any field empty", Toast.LENGTH_SHORT).show();
        }
        if(mPhone.length() >= 1) {
            userReference.child("phone").setValue(mPhone);
        }
        else{
            Toast.makeText(getApplicationContext(), "All info need to be filled. Please don't leave any field empty", Toast.LENGTH_SHORT).show();
        }
        if(mPassword.length() >= 6) {
            userReference.child("password").setValue(mPassword);
        }
        else{
            Toast.makeText(getApplicationContext(), "Password length minumum 6 characters", Toast.LENGTH_SHORT).show();
        }

        // toast a message "personal information is updated"
        Toast.makeText(getApplicationContext(), "account information is updated for: " + mName, Toast.LENGTH_SHORT).show();
    }

    public void switchRelativesInfo(View view){
        Intent intent = new Intent(getApplicationContext(), RelativesInfo.class);
        startActivity(intent);
    }

    public void switchMainMenu(View view){
        Intent intent = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(intent);
    }


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
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
                new ArrayAdapter<>(AccountInfo.this,
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

