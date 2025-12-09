package com.example.faten;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

  class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "MonSuivi.db";
    private static final int DATABASE_VERSION = 1;

    // Table Users
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD = "user_password";

    // Table Appointments
    private static final String TABLE_APPOINTMENTS = "appointments";
    private static final String COLUMN_APPT_ID = "appt_id";
    private static final String COLUMN_APPT_USER_ID = "user_id";
    private static final String COLUMN_APPT_DOCTOR = "doctor_name";
    private static final String COLUMN_APPT_SPECIALTY = "specialty";
    private static final String COLUMN_APPT_DATE = "date";
    private static final String COLUMN_APPT_TIME = "time";
    private static final String COLUMN_APPT_COLOR = "color";

    // Create Tables SQL
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_NAME + " TEXT NOT NULL, " +
                    COLUMN_USER_EMAIL + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_USER_PASSWORD + " TEXT NOT NULL)";

    private static final String CREATE_TABLE_APPOINTMENTS =
            "CREATE TABLE " + TABLE_APPOINTMENTS + " (" +
                    COLUMN_APPT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_APPT_USER_ID + " INTEGER NOT NULL, " +
                    COLUMN_APPT_DOCTOR + " TEXT NOT NULL, " +
                    COLUMN_APPT_SPECIALTY + " TEXT NOT NULL, " +
                    COLUMN_APPT_DATE + " TEXT NOT NULL, " +
                    COLUMN_APPT_TIME + " TEXT NOT NULL, " +
                    COLUMN_APPT_COLOR + " TEXT DEFAULT 'blue', " +
                    "FOREIGN KEY(" + COLUMN_APPT_USER_ID + ") REFERENCES " +
                    TABLE_USERS + "(" + COLUMN_USER_ID + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_APPOINTMENTS);

        // Insert données de démonstration
        insertDemoData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ===== USER METHODS =====

    // Ajouter un utilisateur
    public long addUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, name);
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PASSWORD, password);

        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    // Vérifier login
    public User checkLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USER_EMAIL + " = ? AND " +
                COLUMN_USER_PASSWORD + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        User user = null;

        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD))
            );
        }

        cursor.close();
        db.close();
        return user;
    }

    // Vérifier si email existe
    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USER_EMAIL + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{email});
        boolean exists = cursor.getCount() > 0;

        cursor.close();
        db.close();
        return exists;
    }

    // ===== APPOINTMENT METHODS =====

    // Ajouter un rendez-vous
    public long addAppointment(int userId, String doctor, String specialty,
                               String date, String time, String color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_APPT_USER_ID, userId);
        values.put(COLUMN_APPT_DOCTOR, doctor);
        values.put(COLUMN_APPT_SPECIALTY, specialty);
        values.put(COLUMN_APPT_DATE, date);
        values.put(COLUMN_APPT_TIME, time);
        values.put(COLUMN_APPT_COLOR, color);

        long id = db.insert(TABLE_APPOINTMENTS, null, values);
        db.close();
        return id;
    }

    // Obtenir tous les rendez-vous d'un utilisateur
    public List<Appointment> getUserAppointments(int userId) {
        List<Appointment> appointments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_APPOINTMENTS +
                " WHERE " + COLUMN_APPT_USER_ID + " = ? " +
                " ORDER BY " + COLUMN_APPT_DATE + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Appointment appointment = new Appointment(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_APPT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPT_DOCTOR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPT_SPECIALTY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPT_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPT_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPT_COLOR))
                );
                appointments.add(appointment);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return appointments;
    }

    // Supprimer un rendez-vous
    public boolean deleteAppointment(int appointmentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_APPOINTMENTS,
                COLUMN_APPT_ID + " = ?",
                new String[]{String.valueOf(appointmentId)});
        db.close();
        return result > 0;
    }

    // Mettre à jour un rendez-vous
    public boolean updateAppointment(int appointmentId, String doctor,
                                     String specialty, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_APPT_DOCTOR, doctor);
        values.put(COLUMN_APPT_SPECIALTY, specialty);
        values.put(COLUMN_APPT_DATE, date);
        values.put(COLUMN_APPT_TIME, time);

        int result = db.update(TABLE_APPOINTMENTS, values,
                COLUMN_APPT_ID + " = ?",
                new String[]{String.valueOf(appointmentId)});
        db.close();
        return result > 0;
    }

    // Insérer des données de démonstration
    private void insertDemoData(SQLiteDatabase db) {
        // Créer un utilisateur de démo
        ContentValues userValues = new ContentValues();
        userValues.put(COLUMN_USER_NAME, "Marie Dubois");
        userValues.put(COLUMN_USER_EMAIL, "marie.dubois@email.com");
        userValues.put(COLUMN_USER_PASSWORD, "123456");
        long userId = db.insert(TABLE_USERS, null, userValues);

        // Ajouter des rendez-vous de démo
        String[] doctors = {"Dr. Martin Dupont", "Dr. Sarah Ben Ali", "Dr. Ahmed Karim"};
        String[] specialties = {"Cardiologue", "Dentiste", "Ophtalmologue"};
        String[] dates = {"2024-01-15", "2024-01-18", "2024-12-08"};
        String[] times = {"14:30", "10:00", "16:45"};
        String[] colors = {"blue", "green", "orange"};

        for (int i = 0; i < doctors.length; i++) {
            ContentValues apptValues = new ContentValues();
            apptValues.put(COLUMN_APPT_USER_ID, userId);
            apptValues.put(COLUMN_APPT_DOCTOR, doctors[i]);
            apptValues.put(COLUMN_APPT_SPECIALTY, specialties[i]);
            apptValues.put(COLUMN_APPT_DATE, dates[i]);
            apptValues.put(COLUMN_APPT_TIME, times[i]);
            apptValues.put(COLUMN_APPT_COLOR, colors[i]);
            db.insert(TABLE_APPOINTMENTS, null, apptValues);
        }
    }
}
