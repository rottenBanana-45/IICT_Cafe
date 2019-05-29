package com.example.iictbeta2.AfterLoginActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.iictbeta2.AccActivity.HomeActivity;
import com.example.iictbeta2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoggedinHomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button logoutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loggedin_home);

        mAuth = FirebaseAuth.getInstance();

        logoutButton = findViewById(R.id.logout);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent gohome = new Intent(LoggedinHomeActivity.this, HomeActivity.class);
                startActivity(gohome);
            }
        });
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null || !mAuth.getCurrentUser().isEmailVerified()){
            Intent home = new Intent(LoggedinHomeActivity.this, HomeActivity.class);
            startActivity(home);
            finish();
        }
    }
}
