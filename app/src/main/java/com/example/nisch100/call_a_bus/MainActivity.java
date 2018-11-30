package com.example.nisch100.call_a_bus;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //defining view objects
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignup;
    private EditText editTextName;
    private EditText editConfirmPassword;
    private EditText editPhone;

    private TextView textViewSignin;
    private DatabaseReference databaseReference;

    String email;
    String password;
    String confirmPassword;
    String name;
    String number;
    String id;

    ProgressDialog progressDialog;


    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //if getCurrentUser does not returns null
        if(firebaseAuth.getCurrentUser() != null){
            //that means user is already logged in
            //so close this activity
            finish();

            //and open profile activity
            startActivity(new Intent(getApplicationContext(), Profile.class));
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();


        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewSignin = (TextView) findViewById(R.id.textViewSignin);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editConfirmPassword = (EditText) findViewById(R.id.editConfirmPassword);
        editPhone = (EditText) findViewById(R.id.editPhone);


        buttonSignup = (Button) findViewById(R.id.buttonSignup);

        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        buttonSignup.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
    }

    private void registerUser(){

        //getting email and password from edit texts
        email = editTextEmail.getText().toString().trim();
        password  = editTextPassword.getText().toString().trim();
        name = editTextName.getText().toString().trim();
        number = editPhone.getText().toString().trim();
        confirmPassword = editConfirmPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please enter your Name",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(number)){
            Toast.makeText(this,"Please enter your Number",Toast.LENGTH_LONG).show();
            return;
        }

        if(number.length() != 10){
            Toast.makeText(this,"Invalid Number",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        if(password.compareTo(confirmPassword) !=0){
            Toast.makeText(this,"Passwords don't match",Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            saveUserInfo(task.getResult().getUser().getUid());
                            firebaseAuth.signInWithEmailAndPassword(email, password);
                            firebaseAuth.signOut();
                            finish();
                            startActivity(new Intent(getApplicationContext(), Profile.class));
                        }else{
                            //display some message here
                            Toast.makeText(MainActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    private void saveUserInfo(String uid){
        Registerfirebase obj = new Registerfirebase(name,email,number,password);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        //databaseReference.child(user.getUid()).setValue(obj);
        databaseReference.child("users").child(uid).setValue(obj);

        Toast.makeText(this,"RegisterInfo is saved",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {

        if(view == buttonSignup){
            registerUser();
        }

        if(view == textViewSignin){
            //open login activity when user taps on the already registered textview
            startActivity(new Intent(this, Login.class));
        }

        /*
        Intent intent = new Intent(getApplicationContext(), AccountInfo.class);
        startActivity(intent);
        */

    }
}