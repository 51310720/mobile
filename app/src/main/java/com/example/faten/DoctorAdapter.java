package com.example.faten;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private List<Doctor> doctors;
    private List<Doctor> doctorsFiltered;
    private OnDoctorClickListener listener;

    public interface OnDoctorClickListener {
        void onDoctorClick(Doctor doctor);
    }

    public DoctorAdapter(List<Doctor> doctors, OnDoctorClickListener listener) {
        this.doctors = doctors;
        this.doctorsFiltered = new ArrayList<>(doctors);
        this.listener = listener;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorsFiltered.get(position);
        holder.bind(doctor, listener);
    }

    @Override
    public int getItemCount() {
        return doctorsFiltered.size();
    }

    public void filter(String speciality, String region) {
        doctorsFiltered.clear();

        for (Doctor doctor : doctors) {
            boolean specialityMatch = speciality.equals("Toutes les spécialités") ||
                    doctor.getSpeciality().equals(speciality);
            boolean regionMatch = region.equals("Toutes les régions") ||
                    doctor.getRegion().equals(region);

            if (specialityMatch && regionMatch) {
                doctorsFiltered.add(doctor);
            }
        }
        notifyDataSetChanged();
    }

    public int getFilteredCount() {
        return doctorsFiltered.size();
    }

    static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorImage, tvDoctorName, tvSpeciality, tvAddress;
        TextView tvPhone, tvExperience, tvRating;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorImage = itemView.findViewById(R.id.tvDoctorImage);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvSpeciality = itemView.findViewById(R.id.tvSpeciality);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvExperience = itemView.findViewById(R.id.tvExperience);
            tvRating = itemView.findViewById(R.id.tvRating);
        }

        public void bind(Doctor doctor, OnDoctorClickListener listener) {
            tvDoctorImage.setText(doctor.getImage());
            tvDoctorName.setText(doctor.getName());
            tvSpeciality.setText(doctor.getSpeciality());
            tvAddress.setText(doctor.getAddress());
            tvPhone.setText(doctor.getPhone());
            tvExperience.setText(doctor.getExperience());
            tvRating.setText(String.valueOf(doctor.getRating()));

            // Click sur tout l'item
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDoctorClick(doctor);
                }
            });
        }
    }
}