package com.example.iictbeta2.AccActivity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.iictbeta2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    private TextInputLayout nameField, emailField, passField, confirmPassField;
    private Button signupButton, loginButton;

    //Firebase Authentication
    private FirebaseAuth mAuth;

    //Firebase Database
    private DatabaseReference rootReference;

    //Valid Email Pattern
    private final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameField = findViewById(R.id.editTextName);
        emailField = findViewById(R.id.editTextEmail);
        passField = findViewById(R.id.editTextPass);
        confirmPassField = findViewById(R.id.editTextConfirmPass);
        signupButton = findViewById(R.id.go);
        loginButton = findViewById(R.id.login);

        mAuth = FirebaseAuth.getInstance();

        signupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final String name = nameField.getEditText().getText().toString().trim();
                final String email = emailField.getEditText().getText().toString().trim();
                String pass = passField.getEditText().getText().toString();
                String confirmPass = confirmPassField.getEditText().getText().toString();

                if(!checkInvalidInput(name, email, pass, confirmPass)){

                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener( new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {

                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                            String uid = mAuth.getCurrentUser().getUid();

                                            rootReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                                            HashMap<String, String> userdata = new HashMap<>();
                                            userdata.put("Name", name);
                                            userdata.put("Email", email);

                                            rootReference.setValue(userdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        FirebaseAuth.getInstance().signOut();
                                                        Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                                                        Toast.makeText(SignupActivity.this,
                                                                "Registration completed. Please verify your email.",
                                                                Toast.LENGTH_LONG).show();
                                                        startActivity(i);
                                                    }
                                                }
                                            });

                                        } else {
                                            Toast.makeText(SignupActivity.this,
                                                    "Registration unsuccessful. Please try again!",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(SignupActivity.this, "This Email is already registered",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent goToLogin = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(goToLogin);
            }
        });
    }

    private boolean checkValidEmail(String email){

        if(email.matches(emailPattern)){
            return true;
        }
        return false;
    }

    private boolean checkInvalidInput(String name, String email, String pass, String confirmPass){

        boolean ret = false;

        if(name.isEmpty()){
            nameField.setError("Field can't be empty!");
            ret = true;
        } else {
            nameField.setError(null);
        }

        if(pass.length() < 6){
            if(pass.isEmpty()){
                passField.setError("Field can't be empty!");
            } else {
                passField.setError("Password length must be at least 6");
            }
            ret = true;
        } else {
            passField.setError(null);
        }

        if(!pass.equals(confirmPass) || confirmPass.isEmpty()){
            if(confirmPass.isEmpty()){
                confirmPassField.setError("Field can't be empty!");
            } else {
                passField.setError("Passwords didn't match!");
                confirmPassField.setError("Passwords didn't match!");
            }
            ret = true;
        } else {
            confirmPassField.setError(null);
        }

        if(!checkValidEmail(email) || email.isEmpty()){
            if(email.isEmpty()){
                emailField.setError("Field can't be empty!");
            } else {
                emailField.setError("Invalid Email");
            }
            ret = true;
        } else {
            emailField.setError(null);
        }

        return ret;
    }

}