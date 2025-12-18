package com.example.faten;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddAppointmentActivity extends AppCompatActivity {

    private static final String TAG = "AddAppointment";

    private EditText etDoctor, etDate, etTime;
    private Spinner spinnerSpecialty;
    private Button btnConfirm, btnCancel;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_form);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        calendar = Calendar.getInstance();

        initViews();
        setupSpinner();
        setupListeners();
    }

    private void initViews() {
        etDoctor = findViewById(R.id.etDoctor);
        spinnerSpecialty = findViewById(R.id.spinnerSpecialty);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void setupSpinner() {
        // Liste des spécialités
        String[] specialties = {
                "Sélectionner une spécialité",
                "Dentiste",
                "Cardiologue",
                "Médecin général",
                "Pédiatre",
                "Dermatologue",
                "Ophtalmologue",
                "ORL",
                "Gynécologue",
                "Psychiatre",
                "Autre"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                specialties
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpecialty.setAdapter(adapter);
    }

    private void setupListeners() {
        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());
        btnConfirm.setOnClickListener(v -> saveAppointment());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    updateDateField();
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void showTimePicker() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    calendar.set(Calendar.MINUTE, selectedMinute);
                    updateTimeField();
                },
                hour, minute, true
        );

        timePickerDialog.show();
    }

    private void updateDateField() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        etDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void updateTimeField() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        etTime.setText(timeFormat.format(calendar.getTime()));
    }

    private void saveAppointment() {
        String doctor = etDoctor.getText().toString().trim();
        String specialty = spinnerSpecialty.getSelectedItem().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();

        // Validation
        if (doctor.isEmpty()) {
            etDoctor.setError("Nom du docteur requis");
            etDoctor.requestFocus();
            return;
        }

        if (specialty.equals("Sélectionner une spécialité")) {
            Toast.makeText(this, "Veuillez sélectionner une spécialité", Toast.LENGTH_SHORT).show();
            return;
        }

        if (date.isEmpty()) {
            Toast.makeText(this, "Veuillez sélectionner une date", Toast.LENGTH_SHORT).show();
            etDate.requestFocus();
            return;
        }

        if (time.isEmpty()) {
            Toast.makeText(this, "Veuillez sélectionner une heure", Toast.LENGTH_SHORT).show();
            etTime.requestFocus();
            return;
        }

        // Déterminer la couleur selon la spécialité
        String color = getColorForSpecialty(specialty);

        Log.d(TAG, "========== SAUVEGARDE RENDEZ-VOUS ==========");
        Log.d(TAG, "Docteur: " + doctor);
        Log.d(TAG, "Spécialité: '" + specialty + "'");
        Log.d(TAG, "Date: " + date);
        Log.d(TAG, "Heure: " + time);
        Log.d(TAG, "Couleur: " + color);

        // Sauvegarder dans la base de données
        int userId = sessionManager.getUserId();
        long appointmentId = dbHelper.addAppointment(userId, doctor, specialty, date, time, color);

        if (appointmentId > 0) {
            Log.d(TAG, "✓✓✓ RENDEZ-VOUS AJOUTÉ AVEC SUCCÈS - ID: " + appointmentId);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("id", (int) appointmentId);
            resultIntent.putExtra("doctor", doctor);
            resultIntent.putExtra("specialty", specialty);
            resultIntent.putExtra("date", date);
            resultIntent.putExtra("time", time);
            resultIntent.putExtra("color", color);

            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, "Erreur lors de l'ajout du rendez-vous", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "❌ Erreur: appointmentId <= 0");
        }
    }

    private String getColorForSpecialty(String specialty) {
        if (specialty == null) return "#607D8B";

        String s = specialty.toLowerCase().trim();

        if (s.contains("dentiste")) {
            return "#4A90E2"; // Bleu
        } else if (s.contains("cardio")) {
            return "#E91E63"; // Rose
        } else if (s.contains("médecin") || s.contains("general") || s.contains("général")) {
            return "#9C27B0"; // Violet
        } else if (s.contains("pédiatre")) {
            return "#FF9800"; // Orange
        } else if (s.contains("dermato")) {
            return "#00BCD4"; // Cyan
        } else if (s.contains("ophtalmo")) {
            return "#8BC34A"; // Vert clair
        } else if (s.contains("orl")) {
            return "#FFC107"; // Jaune
        } else if (s.contains("gynéco")) {
            return "#E91E63"; // Rose
        } else if (s.contains("psychiatre")) {
            return "#673AB7"; // Violet foncé
        } else {
            return "#607D8B"; // Gris bleu
        }
    }
}