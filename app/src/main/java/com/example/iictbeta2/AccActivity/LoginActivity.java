package com.example.iictbeta2.AccActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.iictbeta2.AfterLoginActivity.LoggedinHomeActivity;
import com.example.iictbeta2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class LoginActivity extends AppCompatActivity {

    private Button gobutton, forgetbutton;
    private TextInputLayout emailField, passField;

    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;

    private final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        gobutton = findViewById(R.id.go);
        forgetbutton = findViewById(R.id.forgot);
        emailField = findViewById(R.id.editTextEmail);
        passField = findViewById(R.id.editTextPass);

        mAuth = FirebaseAuth.getInstance();

        forgetbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goforget = new Intent(LoginActivity.this, ForgotPassActivity.class);
                startActivity(goforget);
            }
        });

        gobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setTitle("Logging in");
                progressDialog.setMessage("Please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                String email = emailField.getEditText().getText().toString().trim();
                String pass = passField.getEditText().getText().toString();

                if(!checkInvalidInput(email, pass)){
                    emailField.setError(null);
                    mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                if(mAuth.getCurrentUser().isEmailVerified()){
                                    Intent i = new Intent(LoginActivity.this, LoggedinHomeActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this,
                                            "Please verify your email first!",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                progressDialog.dismiss();
                                if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                    Toast.makeText(LoginActivity.this,
                                            "Wrong email or password", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }
    private boolean checkValidEmail(String email){
        if(email.matches(emailPattern)){
            return true;
        }
        return false;
    }
    private boolean checkInvalidInput(String email, String pass){

        boolean ret = false;

        if(pass.isEmpty()){
            passField.setError("Field can't be empty!");
            ret = true;
        } else {
            passField.setError(null);
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
