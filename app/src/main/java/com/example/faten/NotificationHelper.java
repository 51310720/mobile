package com.example.faten;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotificationHelper {

    private static final String TAG = "NotificationHelper";
    private static final String CHANNEL_ID = "appointment_reminders";
    private static final String CHANNEL_NAME = "Rappels de rendez-vous";

    private Context context;
    private NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications pour vos rendez-vous médicaux");
            channel.enableVibration(true);
            channel.enableLights(true);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void scheduleAppointmentNotification(Appointment appointment) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date appointmentDate = sdf.parse(appointment.getDate() + " " + appointment.getTime());
            if (appointmentDate == null) return;

            Calendar appointmentCal = Calendar.getInstance();
            appointmentCal.setTime(appointmentDate);

            // Notification 1 jour avant
            Calendar oneDayBefore = (Calendar) appointmentCal.clone();
            oneDayBefore.add(Calendar.DAY_OF_MONTH, -1);
            scheduleNotification(appointment, oneDayBefore.getTimeInMillis(),
                    "Rappel: Rendez-vous demain",
                    "N'oubliez pas votre rendez-vous avec " + appointment.getDoctorName() +
                            " (" + appointment.getSpecialty() + ") à " + appointment.getTime());

            // Notification 1 heure avant
            Calendar oneHourBefore = (Calendar) appointmentCal.clone();
            oneHourBefore.add(Calendar.HOUR_OF_DAY, -1);
            scheduleNotification(appointment, oneHourBefore.getTimeInMillis(),
                    "Rendez-vous dans 1 heure",
                    "Rendez-vous avec " + appointment.getDoctorName() +
                            " à " + appointment.getTime());

        } catch (ParseException e) {
            Log.e(TAG, "Erreur lors du parsing de la date: " + e.getMessage());
        }
    }

    private void scheduleNotification(Appointment appointment, long triggerTime,
                                      String title, String message) {
        if (triggerTime < System.currentTimeMillis()) return;

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("appointment_id", appointment.getId());
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("doctor", appointment.getDoctorName());
        intent.putExtra("specialty", appointment.getSpecialty());

        int requestCode = appointment.getId() * 100 + (int) (triggerTime % 100);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        }
    }

    public void cancelAppointmentNotifications(int appointmentId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for (int i = 0; i < 2; i++) {
            Intent intent = new Intent(context, NotificationReceiver.class);
            int requestCode = appointmentId * 100 + i;

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
        }
    }

    @SuppressLint("NotificationPermission")
    public void showImmediateNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
