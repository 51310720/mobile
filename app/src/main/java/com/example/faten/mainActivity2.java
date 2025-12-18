package com.example.faten;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    private static final int ADD_APPOINTMENT_REQUEST = 1;
    private static final int NOTIF_PERMISSION_REQUEST = 101;
    private static final String TAG = "MainActivity2";

    private TextView tvUserName;
    private ImageButton btnNotifications;
    private TextView tvNotificationBadge;
    private MaterialCardView cardNewAppointment;
    private RecyclerView rvAppointments;
    private BottomNavigationView bottomNav;

    // Cartes catégories
    private MaterialCardView cardDentiste, cardCardiologue, cardMedecinGeneral;

    // Badges
    private TextView tvCountDentiste, tvCountCardiologue, tvCountMedecinGeneral;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private AppointmentsAdapter adapter;
    private final List<Appointment> appointments = new ArrayList<>();

    // ===== Notification =====
    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainn2);

        // Initialisation
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        notificationHelper = new NotificationHelper(this);

        // Demande permission notifications (Android 13+)
        checkNotificationPermission();

        if (!sessionManager.isLoggedIn()) {
            goToLogin();
            return;
        }

        initViews();
        setupListeners();
        setupRecyclerView();

        loadAppointments();
        updateAllCounters();
    }

    // ================= PERMISSIONS =================
    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIF_PERMISSION_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIF_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission notifications accordée", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission notifications refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ================= INIT =================
    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        btnNotifications = findViewById(R.id.btnNotifications);
        tvNotificationBadge = findViewById(R.id.tvNotificationBadge);
        cardNewAppointment = findViewById(R.id.cardNewAppointment);
        rvAppointments = findViewById(R.id.rvAppointments);
        bottomNav = findViewById(R.id.bottomNav);

        cardDentiste = findViewById(R.id.cardDentiste);
        cardCardiologue = findViewById(R.id.cardCardiologue);
        cardMedecinGeneral = findViewById(R.id.cardMedecinGeneral);

        tvCountDentiste = findViewById(R.id.tvCountDentiste);
        tvCountCardiologue = findViewById(R.id.tvCountCardiologue);
        tvCountMedecinGeneral = findViewById(R.id.tvCountMedecinGeneral);

        tvUserName.setText(sessionManager.getUserName());
    }

    // ================= LISTENERS =================
    private void setupListeners() {
        btnNotifications.setOnClickListener(v -> {
            Toast.makeText(this,
                    "Vous avez " + appointments.size() + " rendez-vous",
                    Toast.LENGTH_SHORT).show();
        });

        cardNewAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddAppointmentActivity.class);
            startActivityForResult(intent, ADD_APPOINTMENT_REQUEST);
        });

        cardDentiste.setOnClickListener(v -> Toast.makeText(this, "Dentiste", Toast.LENGTH_SHORT).show());
        cardCardiologue.setOnClickListener(v -> Toast.makeText(this, "Cardiologue", Toast.LENGTH_SHORT).show());
        cardMedecinGeneral.setOnClickListener(v -> Toast.makeText(this, "Médecin général", Toast.LENGTH_SHORT).show());
    }

    // ================= RECYCLER =================
    private void setupRecyclerView() {
        rvAppointments.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppointmentsAdapter(this, appointments, this::deleteAppointment);
        rvAppointments.setAdapter(adapter);
    }

    // ================= RESULT =================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_APPOINTMENT_REQUEST && resultCode == RESULT_OK) {
            loadAppointments();
            updateAllCounters();

            if (!appointments.isEmpty()) {
                Appointment last = appointments.get(appointments.size() - 1);

                // Notification immédiate pour test
                notificationHelper.showImmediateNotification(
                        "Rendez-vous ajouté",
                        "Avec " + last.getDoctorName() + " à " + last.getTime()
                );

                // Planifier notification
                notificationHelper.scheduleAppointmentNotification(last);
            }

            Toast.makeText(this, "Rendez-vous ajouté et notifications planifiées", Toast.LENGTH_SHORT).show();
        }
    }

    // ================= DATA =================
    private void loadAppointments() {
        int userId = sessionManager.getUserId();
        appointments.clear();
        appointments.addAll(dbHelper.getUserAppointments(userId));
        adapter.updateAppointments(appointments);
    }

    private void deleteAppointment(int appointmentId) {
        if (dbHelper.deleteAppointment(appointmentId)) {
            loadAppointments();
            updateAllCounters();

            notificationHelper.cancelAppointmentNotifications(appointmentId);

            Toast.makeText(this, "Rendez-vous supprimé et notifications annulées", Toast.LENGTH_SHORT).show();
        }
    }

    // ================= BADGES =================
    private void updateAllCounters() {
        updateCategoryCounters();
        updateNotificationBadge();
    }

    private void updateCategoryCounters() {
        int dentiste = 0, cardio = 0, general = 0;
        for (Appointment a : appointments) {
            if (a.getSpecialty() == null) continue;
            String s = normalizeString(a.getSpecialty());
            if (s.contains("dentiste")) dentiste++;
            else if (s.contains("cardiologue")) cardio++;
            else if (s.contains("medecin") || s.contains("general")) general++;
        }
        showBadge(tvCountDentiste, dentiste);
        showBadge(tvCountCardiologue, cardio);
        showBadge(tvCountMedecinGeneral, general);
    }

    private void showBadge(TextView badge, int count) {
        if (count > 0) {
            badge.setText(String.valueOf(count));
            badge.setVisibility(View.VISIBLE);
        } else {
            badge.setVisibility(View.GONE);
        }
    }

    private void updateNotificationBadge() {
        int total = appointments.size();
        if (total > 0) {
            tvNotificationBadge.setText(String.valueOf(total));
            tvNotificationBadge.setVisibility(View.VISIBLE);
        } else {
            tvNotificationBadge.setVisibility(View.GONE);
        }
    }

    // ================= UTIL =================
    private String normalizeString(String str) {
        if (str == null) return "";
        str = str.toLowerCase().trim();
        String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
        return normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAppointments();
        updateAllCounters();
    }
}
