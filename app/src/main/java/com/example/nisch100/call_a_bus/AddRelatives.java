package com.example.nisch100.call_a_bus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    Relative relativeOne = null;
    Relative relativeTwo = null;
    Relative relativeThree = null;

    String rel1Name, rel1Phone, rel2Name, rel2Phone, rel3Name, rel3Phone;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_relatives);

//        email = getIntent().getExtras().getString("email");
//        password = getIntent().getExtras().getString("password");
//        name = getIntent().getExtras().getString("name");
//        number = getIntent().getExtras().getString("number");
//        signup = (Button) findViewById(R.id.buttonSignup);

        TextView why = (TextView) findViewById(R.id.why);
        why.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = "Users may add between one and three relatives. " +
                        "Users may set relatives to be reminded while scheduling a bus. " +
                        "Relatives will receive a text message with a scheduled bus' information if selected " +
                        "by a user when scheduling a bus.";
                final AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(view.getContext(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getApplicationContext());
                }
                builder.setTitle("About Relatives")
                        .setMessage(msg)
                        .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
                return;
            }
        });



        email = getIntent().getExtras().getString("email");
        password = getIntent().getExtras().getString("password");
        name = getIntent().getExtras().getString("name");
        number = getIntent().getExtras().getString("number");
        signup = (Button) findViewById(R.id.buttonSignup);
        firebaseAuth = FirebaseAuth.getInstance();
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rel1Name = ((EditText) findViewById(R.id.relative1Name)).getText().toString().trim();
                rel1Phone = ((EditText) findViewById(R.id.relative1Phone)).getText().toString().trim();

                if(rel1Name.equals("") || rel1Phone.equals("")){
                    final AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(view.getContext(), android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(getApplicationContext());
                    }
                    builder.setTitle("Error")
                            .setMessage("You must fill out information for Relative 1.")
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                } else {
                    if(!setRelatives()){
                        Toast.makeText(view.getContext(), "All fields of a relative must be filled if you are trying to add them.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                progressDialog = new ProgressDialog(AddRelatives.this);
                progressDialog.setMessage("Registering Please Wait...");
                progressDialog.show();
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(AddRelatives.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //checking if success
                                if(task.isSuccessful()){
                                    saveUserInfo(task.getResult().getUser().getUid());
                                    //firebaseAuth.signInWithEmailAndPassword(email, password);
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
                                Registerfirebase obj = new Registerfirebase(name,email,number,password);
                                databaseReference = FirebaseDatabase.getInstance().getReference();
                                //FirebaseUser user = firebaseAuth.getCurrentUser();
                                //databaseReference.child(user.getUid()).setValue(obj);
                                databaseReference.child("users").child(uid).setValue(obj);

                                databaseReference.child("relatives").child(uid).child("rel1").setValue(relativeOne);
                                databaseReference.child("relatives").child(uid).child("rel2").setValue(relativeTwo);
                                databaseReference.child("relatives").child(uid).child("rel3").setValue(relativeThree);

                                Toast.makeText(AddRelatives.this,"RegisterInfo is saved",Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    public boolean setRelatives() {
        relativeOne = new Relative(rel1Name, rel1Phone);

        rel2Name = ((EditText) findViewById(R.id.relative2Name)).getText().toString().trim();
        rel2Phone = ((EditText) findViewById(R.id.relative2Phone)).getText().toString().trim();

        rel3Name = ((EditText) findViewById(R.id.relative3Name)).getText().toString().trim();
        rel3Phone = ((EditText) findViewById(R.id.relative3Phone)).getText().toString().trim();

        if((!rel2Name.equals("") && rel2Phone.equals("")) || (rel2Name.equals("") && !rel2Phone.equals(""))){
            return false;
        } else if (!rel2Name.equals("") && !rel2Phone.equals("")){
            relativeTwo = new Relative(rel2Name, rel2Phone);
        }else if (rel2Name.equals("") && rel2Phone.equals("")) {
            relativeTwo = new Relative("", "");
        }

        if((!rel3Name.equals("") && rel3Phone.equals("")) || (rel3Name.equals("") && !rel3Phone.equals(""))){
            return false;
        } else if (!rel3Name.equals("") && !rel3Phone.equals("")){
            relativeThree = new Relative(rel3Name, rel3Phone);
        } else if(rel3Name.equals("") && rel3Phone.equals("")){
            relativeThree = new Relative("", "");
        }

        return true;
    }
}
