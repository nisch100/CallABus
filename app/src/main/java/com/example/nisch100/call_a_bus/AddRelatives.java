package com.example.nisch100.call_a_bus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddRelatives extends AppCompatActivity {

    private String relName;
    private String relRel;
    private String relNumber;
    private Button signup;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private String email;
    private String password;
    private String number;
    private String name;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_relatives);
        email = getIntent().getExtras().getString("email");
        password = getIntent().getExtras().getString("password");
        name = getIntent().getExtras().getString("name");
        number = getIntent().getExtras().getString("number");
        signup = (Button) findViewById(R.id.buttonSignup);
        firebaseAuth = FirebaseAuth.getInstance();
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(AddRelatives.this);
                progressDialog.setMessage("Registering Please Wait...");
                progressDialog.show();
                relName = ((EditText) findViewById(R.id.editTextRelName)).getText().toString().trim();
                relRel = ((EditText) findViewById(R.id.editTextRelRelationship)).getText().toString().trim();
                relNumber = ((EditText) findViewById(R.id.editTextRelNumber)).getText().toString().trim();
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(AddRelatives.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //checking if success
                                if(task.isSuccessful()){
                                    saveUserInfo(task.getResult().getUser().getUid());
                                    firebaseAuth.signInWithEmailAndPassword(email, password);
                                    //firebaseAuth.signOut();
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), MainMenu.class));
                                }else{
                                    //display some message here
                                    Toast.makeText(AddRelatives.this,"Registration Error",Toast.LENGTH_LONG).show();
                                }
                                progressDialog.dismiss();
                            }
                            private void saveUserInfo(String uid){
                                Registerfirebase obj = new Registerfirebase(name,email,number,password, relName, relRel, relNumber);
                                databaseReference = FirebaseDatabase.getInstance().getReference();
                                //FirebaseUser user = firebaseAuth.getCurrentUser();
                                //databaseReference.child(user.getUid()).setValue(obj);
                                databaseReference.child("users").child(uid).setValue(obj);

                                Toast.makeText(AddRelatives.this,"RegisterInfo is saved",Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}
