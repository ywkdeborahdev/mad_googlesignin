package com.example.googlesignin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Ensure you have a valid layout file for the home screen

        mAuth = FirebaseAuth.getInstance();

        // Check if user is logged in
        boolean isLoggedIn = getSharedPreferences("AuthPrefs", MODE_PRIVATE).getBoolean("isLoggedIn", false);
        if (!isLoggedIn) {
            navigateToLogin();
        }

        // Set up the logout button
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Logout Button Clicked", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            clearSessionStatus(); // Clear the session status
            navigateToLogin();
        });
    }
    private void navigateToLogin() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
        finish();
    }
    private void clearSessionStatus() {
        getSharedPreferences("AuthPrefs", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }
}