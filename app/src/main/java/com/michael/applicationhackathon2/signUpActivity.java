package com.michael.applicationhackathon2;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signUpActivity extends AppCompatActivity {
    EditText email ;
    EditText password;
    EditText username;
    EditText enrollno;
    EditText mobile;
    TextView sup;
    FirebaseAuth auth;
    FirebaseUser useree;
    DatabaseReference mDatabase;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        email =  findViewById(R.id.mail);
        password =  findViewById(R.id.passwrd);
        username =  findViewById(R.id.usrusr);
        enrollno =  findViewById(R.id.enrollno);
        mobile = findViewById(R.id.mobphone);
        sup = findViewById(R.id.supe);
        sup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                register();
            }
        });


    }


    public void register(){
        if(email.getText().toString()!=null && password.getText().toString()!=null &&
                username.getText().toString()!=null &&
                enrollno.getText().toString()!=null && mobile.getText().toString()!=null  ){
             auth = FirebaseAuth.getInstance();

            auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                useree = auth.getCurrentUser(); emailverification();
                                Intent t = new Intent(signUpActivity.this, loginActivity.class);
                                startActivity(t);
                                finish();

                            } else {
                                Toast.makeText(signUpActivity.this, task.getException().toString(),
                                        Toast.LENGTH_SHORT).show();
                                // If sign in fails, display a message to the user.
                                Toast.makeText(signUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });



        }
    }

    void emailverification(){
        if(useree!=null){
            useree.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(signUpActivity.this,"Email Sent",Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(signUpActivity.this,"Email NOT Sent",Toast.LENGTH_SHORT).show();

                            }                // Re-enable button

                        }
                    });
            mDatabase = FirebaseDatabase.getInstance().getReference();
            Toast.makeText(signUpActivity.this,mDatabase.toString(),Toast.LENGTH_LONG).show();

            user = new User(auth.getCurrentUser().getUid().toString(),username.getText().toString(),
                    email.getText().toString(),password.getText().toString(),enrollno.getText().toString(),
                    mobile.getText().toString());
            Handler handler = new Handler();
            mDatabase.child("Login_Details").child(auth.getCurrentUser().getUid()).setValue(user);


        }else{
            Toast.makeText(signUpActivity.this,"User is null",Toast.LENGTH_SHORT).show();
        }

    }
}
