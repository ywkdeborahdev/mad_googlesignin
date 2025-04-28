package com.example.googlesignin;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword;
    private Button btnUpdate, btnEnableBiometric, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnEnableBiometric = findViewById(R.id.btnEnableBiometric);
        btnLogin = findViewById(R.id.btnLogin);

        btnUpdate.setOnClickListener(v -> updateUserProfile());
        btnEnableBiometric.setOnClickListener(v -> enableBiometricAuthentication());
        btnLogin.setOnClickListener(v -> navigateToLogin());
    }

    private void updateUserProfile() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            System.out.println("user");
            if (!email.isEmpty()) {
                user.updateEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Email updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (!password.isEmpty()) {
                user.updatePassword(password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void enableBiometricAuthentication() {
        getSharedPreferences("AuthPrefs", MODE_PRIVATE)
                .edit()
                .putBoolean("biometricEnabled", true)
                .apply();
        Toast.makeText(ProfileActivity.this, "Biometric authentication enabled", Toast.LENGTH_SHORT).show();
        System.out.println(getSharedPreferences("AuthPrefs", MODE_PRIVATE)
                .getBoolean("biometricEnabled", false));
    }

    private void navigateToLogin() {
        Log.d(TAG, "Navigating to Login");
        startActivity(new Intent(this, LoginActivity.class));
        System.out.println(getSharedPreferences("AuthPrefs", MODE_PRIVATE)
                .getBoolean("biometricEnabled", false));
        finish();
    }

}
