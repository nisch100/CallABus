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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
 *                         Account Information Page
 */

public class AccountInfo extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final String TAG = "error_msg";

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mNameView;
    private EditText mPhoneView;
    private EditText mCurrentPasswordView;
    private EditText mResetPasswordView;
    private EditText mConfirmPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    String mUserEmail;

    // Database related
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        // Load views
        mNameView = (EditText) findViewById(R.id.user_name);
        mPhoneView = (EditText) findViewById(R.id.user_phone);
        mCurrentPasswordView = (EditText) findViewById(R.id.user_current_password);
        mResetPasswordView = (EditText) findViewById(R.id.user_reset_password);
        mConfirmPasswordView = (EditText) findViewById(R.id.user_confirm_password);

        // Set button : save form
        Button mSaveInfoButton = (Button) findViewById(R.id.save_personal_info_button);
        mSaveInfoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // we are not doing login so ignore this function
                // attemptLogin();
                saveInfo();
            }
        });

        // Set button: reset password
        Button mResetPasswordButton = (Button) findViewById(R.id.reset_password_button);
        mSaveInfoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // we are not doing login so ignore this function
                // attemptLogin();
                resetPassword();
            }
        });

        // Get database an user reference
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = firebaseAuth.getCurrentUser();
        userReference = databaseReference.child("users").child(user.getUid());

        // Check if user is signed in
        if (user != null) {
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }

        // listens on current user's data change
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currentName = (String) dataSnapshot.child("name").getValue();
                String currentPhone = (String) dataSnapshot.child("phone").getValue();
                mUserEmail = (String) dataSnapshot.child("email").getValue();
                // Toast.makeText(getApplicationContext(), "data snapshot get: " + currentName, Toast.LENGTH_SHORT).show();
                if (currentName.length() >= 1) {
                    mNameView.setText(currentName, TextView.BufferType.EDITABLE);
                }
                if (currentPhone.length() >= 1) {
                    mPhoneView.setText(currentPhone, TextView.BufferType.EDITABLE);
                }
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

    private void resetPassword(){
        String mCurrentPassword = mCurrentPasswordView.getText().toString();
        String mResetPassword = mResetPasswordView.getText().toString();
        String mConfirmPassword = mConfirmPasswordView.getText().toString();

        // Reset Password for user
        if(mResetPassword.length() >= 6) {

            if(mCurrentPassword.length() == 0){
                Toast.makeText(getApplicationContext(), "Current password is required to reset password", Toast.LENGTH_SHORT).show();
                return;
            }

            if(mResetPassword.equals(mConfirmPassword)){

                // Call method to hash the password
                mResetPassword = HashPassword.hashing(mResetPassword);
                if(mResetPassword == null){
                    Toast.makeText(getApplicationContext(), "Password Hashing Failed", Toast.LENGTH_SHORT).show();
                    return;
                }

                mCurrentPassword = HashPassword.hashing(mCurrentPassword);
                if(mCurrentPassword == null){
                    Toast.makeText(getApplicationContext(), "Password Hashing Failed", Toast.LENGTH_SHORT).show();
                    return;
                }

                AuthCredential credential = EmailAuthProvider
                        .getCredential(mUserEmail, mCurrentPassword);

                final String newPassword = mResetPassword;

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Password Updated", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Password Update Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Log.d(TAG, "Error auth failed");
                                }
                            }
                        });
            }
            else{
                Toast.makeText(getApplicationContext(), "Passwords not matching", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else if(mResetPassword.length() == 0){
            // do nothing since users are not required to reset their password;
        }
        else{
            Toast.makeText(getApplicationContext(), "Password length minumum 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    // Method: save form to Firebase
    private void saveInfo(){
        // Store input text as strings
        String mName = mNameView.getText().toString();
        String mPhone = mPhoneView.getText().toString();

        // Check if inputs are valid. If so, save them to Firebase.
        if(mName.length() >= 1 && mPhone.length() >= 1) {
            userReference.child("name").setValue(mName);
            userReference.child("phone").setValue(mPhone);
        }
        else{
            Toast.makeText(getApplicationContext(), "Both name and phone need to be filled. Please don't leave any field empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Toast a message to confirm data update
        Toast.makeText(getApplicationContext(), "account information is updated", Toast.LENGTH_SHORT).show();
    }

    // Method: switch to Relatives Info Page
    public void switchRelativesInfo(View view){
        Intent intent = new Intent(getApplicationContext(), RelativesInfo.class);
        startActivity(intent);
        // Utilities.switchScreen(getApplicationContext(), "RelativesInfo");
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

