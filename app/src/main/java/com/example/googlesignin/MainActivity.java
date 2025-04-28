package com.example.googlesignin;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button btnLogout, btnProfile;
    private static final long SESSION_TIMEOUT = 15 * 60 * 1000; // 15 minutes
    private long lastActiveTime;

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
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Logout Button Clicked", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            clearSessionStatus(); // Clear the session status
            navigateToLogin();
        });

        btnProfile = findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(v -> navigateToProfile());
    }
    private void navigateToLogin() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
        finish();
    }


    private void navigateToProfile() {
        Log.d(TAG, "Navigating to Signup");
        startActivity(new Intent(this, ProfileActivity.class));
        finish();
    }
    private void clearSessionStatus() {
        getSharedPreferences("AuthPrefs", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }

    private void checkBiometricLogin() {
        if (!isBiometricEnabled()) {
            return; // Do not prompt for biometric authentication if it is not enabled
        }
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                BiometricPrompt biometricPrompt = new BiometricPrompt(this,
                        ContextCompat.getMainExecutor(this),
                        new BiometricPrompt.AuthenticationCallback() {
                            @Override
                            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                                super.onAuthenticationSucceeded(result);
                                Toast.makeText(MainActivity.this, "Biometric login successful", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAuthenticationFailed() {
                                super.onAuthenticationFailed();
                                Toast.makeText(MainActivity.this, "Biometric authentication failed", Toast.LENGTH_SHORT).show();
                                navigateToLogin();
                            }
                        });

                BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Biometric Login")
                        .setSubtitle("Log in using your biometric credential")
                        .setNegativeButtonText("Cancel")
                        .build();

                biometricPrompt.authenticate(promptInfo);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "No biometric hardware available", Toast.LENGTH_SHORT).show();
                navigateToLogin();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Biometric hardware unavailable", Toast.LENGTH_SHORT).show();
                navigateToLogin();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "No biometric credentials enrolled", Toast.LENGTH_SHORT).show();
                navigateToLogin();
                break;
        }
    }

    private boolean isBiometricEnabled() {
        return getSharedPreferences("AuthPrefs", MODE_PRIVATE)
                .getBoolean("biometricEnabled", false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (System.currentTimeMillis() - lastActiveTime > SESSION_TIMEOUT && isBiometricEnabled()) {
            checkBiometricLogin();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        lastActiveTime = System.currentTimeMillis();
    }


}