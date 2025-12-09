package com.example.faten;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Random;

public class AddAppointmentActivity extends AppCompatActivity {

    private EditText etDoctor, etSpecialty, etDate, etTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_form);

        etDoctor = findViewById(R.id.etDoctor);
        etSpecialty = findViewById(R.id.etSpecialty);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        Button btnCancel = findViewById(R.id.btnCancel);
        Button btnConfirm = findViewById(R.id.btnConfirm);

        // Date Picker
        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    AddAppointmentActivity.this,
                    (view, year1, month1, day1) -> etDate.setText(day1 + "/" + (month1 + 1) + "/" + year1),
                    year, month, day
            );
            dialog.show();
        });

        // Time Picker
        etTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(
                    AddAppointmentActivity.this,
                    (view, hourOfDay, minute1) -> etTime.setText(String.format("%02d:%02d", hourOfDay, minute1)),
                    hour, minute,
                    true
            );
            dialog.show();
        });

        // Cancel
        btnCancel.setOnClickListener(v -> finish());

        // Confirm
        btnConfirm.setOnClickListener(v -> {
            String doctor = etDoctor.getText().toString().trim();
            String specialty = etSpecialty.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String time = etTime.getText().toString().trim();

            if (doctor.isEmpty() || specialty.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            // Couleur aléatoire
            String[] colors = {"blue", "green", "orange", "purple", "pink"};
            String color = colors[new Random().nextInt(colors.length)];

            // ✅ Utiliser le vrai userId
            SessionManager sessionManager = new SessionManager(this);
            int userId = sessionManager.getUserId();

            DatabaseHelper db = new DatabaseHelper(this);
            long id = db.addAppointment(userId, doctor, specialty, date, time, color);

            if (id > 0) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("id", (int) id);
                resultIntent.putExtra("doctor", doctor);
                resultIntent.putExtra("specialty", specialty);
                resultIntent.putExtra("date", date);
                resultIntent.putExtra("time", time);
                resultIntent.putExtra("color", color);
                setResult(RESULT_OK, resultIntent);

                finish();
            } else {
                Toast.makeText(this, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
