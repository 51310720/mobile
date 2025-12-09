package com.example.faten;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class WelcomeActivity extends AppCompatActivity {
    // Déclaration du bouton "Commencer"
    private MaterialButton btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Charge l'interface depuis welcome_activity.xml
        setContentView(R.layout.welcome_activity);

        // Récupération du bouton dans l'interface
        btnStart = findViewById(R.id.btnStart);

        // Action lors du clic sur le bouton
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Création d'un Intent pour passer à l'écran LoginActivity
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);

                // Lancer l'activité d'authentification
                startActivity(intent);

                // Fermer l’écran actuel pour éviter de revenir dessus
                finish();
            }
        });
    }
}
