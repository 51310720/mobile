package com.example.faten;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.ViewHolder> {

    private List<Appointment> appointments;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(int appointmentId);
    }

    public AppointmentsAdapter(Context context, List<Appointment> appointments, OnItemClickListener listener) {
        this.context = context;
        this.appointments = appointments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);

        holder.tvDoctorName.setText(appointment.getDoctorName());
        holder.tvSpecialty.setText(appointment.getSpecialty());
        holder.tvDate.setText(appointment.getDate());
        holder.tvTime.setText(appointment.getTime());

        // couleurs
        int colorInt = getColorForAppointment(appointment.getColor());
        holder.colorIndicator.setBackgroundColor(colorInt);

        int bgColor = getBackgroundColorForAppointment(appointment.getColor());
        holder.avatarCard.setCardBackgroundColor(bgColor);

        holder.btnMenu.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Options")
                    .setItems(new String[]{"Supprimer"}, (dialog, which) -> {
                        if (which == 0 && listener != null) {
                            listener.onDeleteClick(appointment.getId());
                        }
                    })
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public void updateAppointments(List<Appointment> newAppointments) {
        this.appointments = newAppointments;
        notifyDataSetChanged();
    }

    private int getColorForAppointment(String color) {
        switch (color) {
            case "blue": return Color.parseColor("#3B82F6");
            case "green": return Color.parseColor("#10B981");
            case "orange": return Color.parseColor("#F97316");
            case "purple": return Color.parseColor("#9333EA");
            case "pink": return Color.parseColor("#DB2777");
            default: return Color.parseColor("#3B82F6");
        }
    }

    private int getBackgroundColorForAppointment(String color) {
        switch (color) {
            case "blue": return Color.parseColor("#DBEAFE");
            case "green": return Color.parseColor("#D1FAE5");
            case "orange": return Color.parseColor("#FFEDD5");
            case "purple": return Color.parseColor("#F3E8FF");
            case "pink": return Color.parseColor("#FCE7F3");
            default: return Color.parseColor("#DBEAFE");
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View colorIndicator;
        CardView avatarCard;
        TextView tvDoctorName, tvSpecialty, tvDate, tvTime;
        ImageButton btnMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            colorIndicator = itemView.findViewById(R.id.colorIndicator);
            avatarCard = itemView.findViewById(R.id.itemid);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnMenu = itemView.findViewById(R.id.btnMenu);
        }
    }
}
