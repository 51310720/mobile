package com.example.faten;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.faten.AddAppointmentActivity;
import com.example.faten.Appointment;
import com.example.faten.AppointmentsAdapter;
import com.example.faten.DatabaseHelper;
import com.example.faten.LoginActivity;
import com.example.faten.R;
import com.example.faten.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int ADD_APPOINTMENT_REQUEST = 1; // ✅ doit être ici, dans la classe

    private TextView tvUserName;
    private ImageButton btnNotifications;
    private MaterialCardView cardNewAppointment;
    private RecyclerView rvAppointments;
    private BottomNavigationView bottomNav;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private AppointmentsAdapter adapter;
    private List<Appointment> appointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Vérifier si connecté
        if (!sessionManager.isLoggedIn()) {
            goToLogin();
            return;
        }

        // Initialisation des vues
        tvUserName = findViewById(R.id.tvUserName);
        btnNotifications = findViewById(R.id.btnNotifications);
        cardNewAppointment = findViewById(R.id.cardNewAppointment);
        rvAppointments = findViewById(R.id.rvAppointments);
        bottomNav = findViewById(R.id.bottomNav);

        // Afficher le nom de l'utilisateur
        tvUserName.setText(sessionManager.getUserName());

        // Bouton notifications
        btnNotifications.setOnClickListener(v ->
                Toast.makeText(this, "Aucune notification", Toast.LENGTH_SHORT).show()
        );

        // Bouton nouveau rendez-vous
        cardNewAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddAppointmentActivity.class);
            startActivityForResult(intent, ADD_APPOINTMENT_REQUEST);
        });

        // Charger les rendez-vous depuis la base de données
        loadAppointments();

        // Setup RecyclerView
        rvAppointments.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppointmentsAdapter(this, appointments, new AppointmentsAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int appointmentId) {
                deleteAppointment(appointmentId);
            }
        });
        rvAppointments.setAdapter(adapter);
    }

    // Méthode pour récupérer le résultat de AddAppointmentActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_APPOINTMENT_REQUEST && resultCode == RESULT_OK && data != null) {
            // Créer un nouvel objet Appointment à partir des infos renvoyées
            Appointment newAppointment = new Appointment(
                    data.getIntExtra("id", 0),
                    data.getStringExtra("doctor"),
                    data.getStringExtra("specialty"),
                    data.getStringExtra("date"),
                    data.getStringExtra("time"),
                    data.getStringExtra("color")
            );

            // Ajouter en haut de la liste
            appointments.add(0, newAppointment);
            adapter.updateAppointments(appointments);
        }
    }

    private void loadAppointments() {
        int userId = sessionManager.getUserId();
        appointments = dbHelper.getUserAppointments(userId);
    }

    private void deleteAppointment(int appointmentId) {
        boolean deleted = dbHelper.deleteAppointment(appointmentId);
        if (deleted) {
            Toast.makeText(this, "Rendez-vous supprimé", Toast.LENGTH_SHORT).show();
            loadAppointments();
            adapter.updateAppointments(appointments);
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // 🔥🔥 IMPORTANT : recharge la liste quand on revient dans l'app
    @Override
    protected void onResume() {
        super.onResume();
        loadAppointments();

        if (adapter == null) {
            adapter = new AppointmentsAdapter(this, appointments, this::deleteAppointment);
            rvAppointments.setAdapter(adapter);
        } else {
            adapter.updateAppointments(appointments);
        }
    }


}
