package com.michael.applicationhackathon2;

import android.content.Intent;
import android.app.Activity;

import org.json.JSONObject;
import org.json.JSONException;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class loginActivity extends AppCompatActivity {
    FirebaseAuth auth;
    String email = null;
    String password = null;
    private Intent slotpickingIntent ;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Call the function callInstamojo to start payment here
        TextView login = findViewById(R.id.logiin);
        TextView signup = findViewById(R.id.sup);
        final EditText username = findViewById(R.id.usrusr);
        final EditText passwrd = findViewById(R.id.passwrd);
        signup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent t = new Intent(loginActivity.this, signUpActivity.class);
                startActivity(t);
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {                email = username.getText().toString();
                password = passwrd.getText().toString();

                auth= FirebaseAuth.getInstance();


                auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(loginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // If sign in fails, display a messag
                                    if (!task.isSuccessful()) {
                                        // there was an error

                                    } else {
                                        if(auth.getCurrentUser().isEmailVerified()){
                                            slotpickingIntent  = new Intent(loginActivity.this, slotPickingActivity.class);

                                            startActivity(slotpickingIntent);

                                        }else{
                                            Toast.makeText(loginActivity.this,"Email not verified",Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }
                            });

            }
        });

    }
}
