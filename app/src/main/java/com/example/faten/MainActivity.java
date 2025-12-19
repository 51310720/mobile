package com.example.faten;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Constantes
    private static final int ADD_APPOINTMENT_REQUEST = 1; // Code pour l'ajout de rendez-vous
    private static final String CHANNEL_ID = "appointment_channel"; // Canal notifications
    private static final String TAG = "MainActivity"; // Tag pour les logs

    // Vues
    private TextView tvUserName;           // Affiche le nom de l'utilisateur
    private ImageButton btnNotifications;  // Bouton pour notifications
    private MaterialCardView cardNewAppointment; // Carte pour ajouter un rendez-vous
    private RecyclerView rvAppointments;   // Liste des rendez-vous
    private BottomNavigationView bottomNav; // Navigation en bas

    // Cartes et badges pour catégories de médecins
    private CardView cardDentiste, cardCardiologue, cardMedecinGeneral;
    private TextView tvDentisteCount, tvCardiologueCount, tvMedecinCount;

    // Gestion des données
    private DatabaseHelper dbHelper;      // Accès à la base de données locale
    private SessionManager sessionManager; // Gestion de session utilisateur
    private AppointmentsAdapter adapter;  // Adapter pour RecyclerView
    private List<Appointment> appointments; // Liste des rendez-vous

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Création du canal de notifications
        createNotificationChannel();

        // Initialisation de la base de données et session
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Vérification si l'utilisateur est connecté
        if (!sessionManager.isLoggedIn()) {
            goToWelcome(); // Redirection vers l'écran d'accueil
            return;
        }

        // Initialisation des vues et listeners
        initViews();
        setupListeners();

        // Charger la liste des rendez-vous et mettre à jour les badges
        loadAppointments();
        updateCategoryCounts();

        // Configurer RecyclerView
        rvAppointments.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppointmentsAdapter(this, appointments, this::deleteAppointment);
        rvAppointments.setAdapter(adapter);
    }

    /**
     * Initialisation des vues et badges
     */
    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        btnNotifications = findViewById(R.id.btnNotifications);
        cardNewAppointment = findViewById(R.id.cardNewAppointment);
        rvAppointments = findViewById(R.id.rvAppointments);
        bottomNav = findViewById(R.id.bottomNav);

        cardDentiste = findViewById(R.id.cardDentiste);
        cardCardiologue = findViewById(R.id.cardCardiologue);
        cardMedecinGeneral = findViewById(R.id.cardMedecinGeneral);

        tvDentisteCount = findViewById(R.id.tvDentisteCount);
        tvCardiologueCount = findViewById(R.id.tvCardiologueCount);
        tvMedecinCount = findViewById(R.id.tvMedecinCount);

        // Affiche le nom de l'utilisateur connecté
        tvUserName.setText(sessionManager.getUserName());

        // Debug - Vérifie que les badges existent
        Log.d(TAG, "Badges trouvés:");
        Log.d(TAG, "- tvDentisteCount: " + (tvDentisteCount != null ? "OK ✓" : "NULL ✗"));
        Log.d(TAG, "- tvCardiologueCount: " + (tvCardiologueCount != null ? "OK ✓" : "NULL ✗"));
        Log.d(TAG, "- tvMedecinCount: " + (tvMedecinCount != null ? "OK ✓" : "NULL ✗"));
    }

    /**
     * Configuration des événements (click listeners)
     */
    private void setupListeners() {
        // Notifications - Affiche toast si aucune notification
        btnNotifications.setOnClickListener(v ->
                Toast.makeText(this, "Aucune notification", Toast.LENGTH_SHORT).show()
        );

        // Ajout d'un nouveau rendez-vous
        cardNewAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddAppointmentActivity.class);
            startActivityForResult(intent, ADD_APPOINTMENT_REQUEST);
        });

        // Affichage du nombre de rendez-vous par spécialité
        cardDentiste.setOnClickListener(v -> showCategoryCount("Dentiste"));
        cardCardiologue.setOnClickListener(v -> showCategoryCount("Cardiologue"));
        cardMedecinGeneral.setOnClickListener(v -> showCategoryCount("Médecin général"));

        // Navigation du bas
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_logout) {
                logout(); // Déconnexion
                return true;
            } else if (id == R.id.nav_doctors) {
                Intent intent = new Intent(MainActivity.this, DoctorListActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_Documents) {
                // Ouvre l'activité Documents
                Intent intent = new Intent(MainActivity.this, DocumentsActivity.class);
                startActivity(intent);
                return true;
            }

            return false;
        });
    }

    /**
     * Résultat de l'activité AddAppointmentActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Vérifie si un rendez-vous a été ajouté
        if (requestCode == ADD_APPOINTMENT_REQUEST && resultCode == RESULT_OK && data != null) {
            String doctor = data.getStringExtra("doctor");
            String specialty = data.getStringExtra("specialty");
            String time = data.getStringExtra("time");

            // Debug logs
            Log.d(TAG, "NOUVEAU RENDEZ-VOUS REÇU: " + doctor + " | " + specialty + " à " + time);

            // Notification après 2 secondes
            new Handler().postDelayed(() -> showNotification(doctor, time), 2000);

            Toast.makeText(this, "✅ Rendez-vous ajouté ! Notification dans 2 sec", Toast.LENGTH_LONG).show();

            // Recharger la liste
            loadAppointments();
            if (adapter != null) adapter.updateAppointments(appointments);

            // Mise à jour des badges
            updateCategoryCounts();
        }
    }

    /**
     * Charge la liste des rendez-vous depuis la base de données
     */
    private void loadAppointments() {
        int userId = sessionManager.getUserId();
        appointments = dbHelper.getUserAppointments(userId);

        Log.d(TAG, "CHARGEMENT DES RENDEZ-VOUS: User ID = " + userId + ", Total = " + appointments.size());

        if (!appointments.isEmpty()) {
            for (int i = 0; i < appointments.size(); i++) {
                Appointment app = appointments.get(i);
                Log.d(TAG, (i + 1) + ". " + app.getDoctorName() + " | " + app.getSpecialty());
            }
        } else {
            Log.d(TAG, "Aucun rendez-vous trouvé");
        }
    }

    /**
     * Supprime un rendez-vous par ID
     */
    private void deleteAppointment(int appointmentId) {
        boolean deleted = dbHelper.deleteAppointment(appointmentId);
        if (deleted) {
            Toast.makeText(this, "Rendez-vous supprimé", Toast.LENGTH_SHORT).show();
            loadAppointments();
            adapter.updateAppointments(appointments);
            updateCategoryCounts();
        }
    }

    /**
     * Compte les rendez-vous par spécialité
     */
    private int countBySpecialty(String specialty) {
        int count = 0;
        String searchTerm = specialty.toLowerCase().trim();

        for (Appointment app : appointments) {
            if (app.getSpecialty() != null) {
                String appSpecialtyLower = app.getSpecialty().toLowerCase().trim();
                // Comparaison flexible
                if (appSpecialtyLower.equals(searchTerm) ||
                        appSpecialtyLower.contains(searchTerm) ||
                        searchTerm.contains(appSpecialtyLower)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Affiche le nombre de rendez-vous d'une catégorie
     */
    private void showCategoryCount(String specialty) {
        int count = countBySpecialty(specialty);
        Toast.makeText(this, count + " rendez-vous - " + specialty, Toast.LENGTH_LONG).show();
    }

    /**
     * Met à jour les badges pour chaque catégorie
     */
    private void updateCategoryCounts() {
        int dentisteCount = countBySpecialty("Dentiste");
        int cardioCount = countBySpecialty("Cardiologue");
        int medecinCount = countBySpecialty("Médecin");

        updateBadge(tvDentisteCount, dentisteCount, "Dentiste");
        updateBadge(tvCardiologueCount, cardioCount, "Cardiologue");
        updateBadge(tvMedecinCount, medecinCount, "Médecin");
    }

    /**
     * Met à jour un badge spécifique
     */
    private void updateBadge(TextView badge, int count, String name) {
        if (badge == null) return;

        if (count > 0) {
            badge.setText(String.valueOf(count));
            badge.setVisibility(View.VISIBLE);
        } else {
            badge.setVisibility(View.GONE);
        }
    }

    /**
     * Création du canal de notifications pour API >= 26
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Rendez-vous",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications pour les rendez-vous");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    /**
     * Affiche une notification pour un rendez-vous proche
     */
    private void showNotification(String doctor, String time) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.bell)
                .setContentTitle("🔔 Rendez-vous proche !")
                .setContentText("RDV avec " + doctor + " à " + time)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) manager.notify(1, builder.build());
    }

    /**
     * Déconnexion utilisateur et retour vers WelcomeActivity
     */
    private void logout() {
        sessionManager.logout();
        Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Redirection vers l'écran Welcome si non connecté
     */
    private void goToWelcome() {
        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Recharge les rendez-vous et les badges lorsque l'activité reprend le focus
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadAppointments();
        updateCategoryCounts();

        if (adapter == null) {
            adapter = new AppointmentsAdapter(this, appointments, this::deleteAppointment);
            rvAppointments.setAdapter(adapter);
        } else {
            adapter.updateAppointments(appointments);
        }
    }
}
