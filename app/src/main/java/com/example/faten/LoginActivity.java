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



import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private CardView logoCardLogin;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvSignup;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    // 🥚 Easter Egg
    private int logoClickCount = 0;
    private Handler clickResetHandler = new Handler(Looper.getMainLooper());
    private Runnable resetClickRunnable = () -> logoClickCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            goToMainActivity();
            return;
        }

        logoCardLogin = findViewById(R.id.logoCardLogin);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);

        // 🥚 EASTER EGG
        logoCardLogin.setOnClickListener(v -> {
            logoClickCount++;
            clickResetHandler.removeCallbacks(resetClickRunnable);
            clickResetHandler.postDelayed(resetClickRunnable, 1000);

            if (logoClickCount == 3) {
                showEasterEggDialog();
                logoClickCount = 0;
            }
        });

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Email requis");
                etEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Mot de passe requis");
                etPassword.requestFocus();
                return;
            }

            User user = dbHelper.checkLogin(email, password);

            if (user != null) {
                sessionManager.createLoginSession(user.getId(), user.getName(), user.getEmail());
                Toast.makeText(this, "Connexion réussie!", Toast.LENGTH_SHORT).show();
                goToMainActivity();
            } else {
                Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
            }
        });

        tvSignup.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
            finish();
        });
    }

    private void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
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
