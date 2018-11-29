package com.example.nisch100.call_a_bus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //view objects
    private TextView textViewUserEmail;
    private Button buttonLogout;

    private DatabaseReference databaseReference;

    private EditText editTextName, editTextmail;
    private Button buttonsave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();

        //if the user is not logged in
        //that means current user will return null
        if(firebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, Login.class));
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //initializing views
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        editTextName  = (EditText) findViewById(R.id.editTextName);
        editTextmail = (EditText) findViewById(R.id.editTextEmail);
        buttonsave = (Button) findViewById(R.id.buttonAdd);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);

        //displaying logged in user name
        textViewUserEmail.setText("Welcome "+user.getDisplayName());

        //adding listener to button
        buttonLogout.setOnClickListener(this);
        buttonsave.setOnClickListener(this);
    }

    private void saveUserInfo(){
        String name = editTextName.getText().toString().trim();
        String email = editTextmail.getText().toString().trim();
        String id = databaseReference.push().getKey();
        UserInfo obj = new UserInfo(name,email);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        //databaseReference.child(user.getUid()).setValue(obj);
        databaseReference.child(id).setValue(obj);

        Toast.makeText(this,"Info is saved",Toast.LENGTH_LONG).show();
    }

    public void savingTest(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
    }

    @Override
    public void onClick(View view) {
        //if logout is pressed
        if(view == buttonLogout){
            //logging out the user
            firebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this, Login.class));
        }
        if(view == buttonsave){
            saveUserInfo();
        }
    }
}