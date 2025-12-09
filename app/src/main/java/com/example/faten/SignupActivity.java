package com.example.faten;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.faten.LoginActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SignupActivity extends AppCompatActivity {

    private CardView logoCardSignup;
    private TextInputEditText etFullName, etEmailSignup, etPasswordSignup;
    private MaterialButton btnSignup;
    private TextView tvLogin;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    // 🥚 Easter Egg
    private int logoClickCount = 0;
    private Handler clickResetHandler = new Handler(Looper.getMainLooper());
    private Runnable resetClickRunnable = () -> logoClickCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        logoCardSignup = findViewById(R.id.logoCardSignup);
        etFullName = findViewById(R.id.etFullName);
        etEmailSignup = findViewById(R.id.etEmailSignup);
        etPasswordSignup = findViewById(R.id.etPasswordSignup);
        btnSignup = findViewById(R.id.btnSignup);
        tvLogin = findViewById(R.id.tvLogin);

        // 🥚 EASTER EGG
        logoCardSignup.setOnClickListener(v -> {
            logoClickCount++;
            clickResetHandler.removeCallbacks(resetClickRunnable);
            clickResetHandler.postDelayed(resetClickRunnable, 1000);

            if (logoClickCount == 3) {
                showEasterEggDialog();
                logoClickCount = 0;
            }
        });

        btnSignup.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmailSignup.getText().toString().trim();
            String password = etPasswordSignup.getText().toString().trim();

            if (fullName.isEmpty()) {
                etFullName.setError("Nom requis");
                etFullName.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                etEmailSignup.setError("Email requis");
                etEmailSignup.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                etPasswordSignup.setError("Mot de passe requis");
                etPasswordSignup.requestFocus();
                return;
            }

            if (password.length() < 6) {
                etPasswordSignup.setError("Au moins 6 caractères");
                etPasswordSignup.requestFocus();
                return;
            }

            if (dbHelper.checkEmailExists(email)) {
                Toast.makeText(this, "Cet email existe déjà", Toast.LENGTH_SHORT).show();
                return;
            }

            long userId = dbHelper.addUser(fullName, email, password);

            if (userId > 0) {
                sessionManager.createLoginSession((int) userId, fullName, email);
                Toast.makeText(this, "Compte créé avec succès!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Erreur lors de la création du compte", Toast.LENGTH_SHORT).show();
            }
        });

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void showEasterEggDialog() {
        new AlertDialog.Builder(this)
                .setTitle("🎉 Bravo! Easter Egg trouvé! 🥚")
                .setMessage(
                        "Le secret des développeurs:\n\n" +
                                "☕ On code mieux avec du café\n" +
                                "🍕 Et encore mieux avec une pizza\n" +
                                "🐛 Les bugs? C'est des features!"
                )
                .setPositiveButton("Haha! 😄", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clickResetHandler.removeCallbacks(resetClickRunnable);
    }
}
