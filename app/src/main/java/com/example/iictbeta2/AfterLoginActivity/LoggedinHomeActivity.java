package com.example.iictbeta2.AfterLoginActivity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iictbeta2.AccActivity.HomeActivity;
import com.example.iictbeta2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoggedinHomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView userName, balanceView;
    private Button logoutButton, changeNameButton;
    private DatabaseReference databaseReference;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loggedin_home);

        logoutButton = findViewById(R.id.logout);
        changeNameButton = findViewById(R.id.changeDisplayName);
        userName = findViewById(R.id.userNameView);
        balanceView = findViewById(R.id.balanceView);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser= mAuth.getCurrentUser();

        if(currentUser != null){
            uid = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String viewName = dataSnapshot.child("display_name").getValue().toString();
                    viewName = "Name: " + viewName;
                    Integer balance = dataSnapshot.child("balance").getValue(Integer.class);
                    String balanceShow = "Current Balance: " + balance.toString() + " TAKA";
                    userName.setText(viewName);
                    balanceView.setText(balanceShow);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        



        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent gohome = new Intent(LoggedinHomeActivity.this, HomeActivity.class);
                startActivity(gohome);
                finish();
            }
        });
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null || !currentUser.isEmailVerified()){
            Intent home = new Intent(LoggedinHomeActivity.this, HomeActivity.class);
            startActivity(home);
            finish();
        }
    }
}
