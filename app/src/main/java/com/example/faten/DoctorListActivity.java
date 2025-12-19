package com.example;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doctorapp.adapters.DoctorAdapter;
import com.example.doctorapp.models.Doctor;
import com.example.faten.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DoctorListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDoctors;
    private Spinner spinnerSpeciality, spinnerRegion;
    private TextView tvCount;
    private DoctorAdapter adapter;
    private List<Doctor> doctors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        initViews();
        loadDoctors();
        setupSpinners();
        setupRecyclerView();
    }

    private void initViews() {
        recyclerViewDoctors = findViewById(R.id.recyclerViewDoctors);
        spinnerSpeciality = findViewById(R.id.spinnerSpeciality);
        spinnerRegion = findViewById(R.id.spinnerRegion);
        tvCount = findViewById(R.id.tvCount);
    }

    private void loadDoctors() {
        doctors = new ArrayList<>();

        // Cardiologues
        doctors.add(new Doctor(1, "Dr. Amira Ben saida", "Cardiologue", "Tunis",
                "15 Avenue Habib Bourguiba, Tunis", "+216 71 123 456",
                4.8, "15 ans d'expérience", "👩‍⚕️"));
        doctors.add(new Doctor(2, "Dr. Karim ismail", "Cardiologue", "Sousse",
                "Boulevard 14 Janvier, Sousse", "+216 73 456 789",
                4.6, "20 ans d'expérience", "👨‍⚕️"));
        doctors.add(new Doctor(3, "Dr. Sana Ben Ouada", "Cardiologue", "Mahdia",
                "Avenue Farhat Hached, Mahdia", "+216 73 789 012",
                4.7, "14 ans d'expérience", "👩‍⚕️"));
        doctors.add(new Doctor(4, "Dr. Meriem Khiari", "Cardiologue", "Sfax",
                "Avenue Majida Boulila, Sfax", "+216 74 234 567",
                4.9, "22 ans d'expérience", "👨‍⚕️"));

        // Dentistes
        doctors.add(new Doctor(5, "Dr. Mohamed kamoun", "Dentiste", "Tunis",
                "Avenue de la Liberté, Centre-ville", "+216 71 234 567",
                4.9, "10 ans d'expérience", "👨‍⚕️"));
        doctors.add(new Doctor(6, "Dr. Naimaa Hamdi", "Dentiste", "Sfax",
                "Rue de la République, Sfax", "+216 74 567 890",
                4.8, "8 ans d'expérience", "👩‍⚕️"));
        doctors.add(new Doctor(7, "Dr. Rachida Jdoula", "Dentiste", "Mahdia",
                "Rue de la Corniche, Mahdia", "+216 73 890 123",
                4.6, "9 ans d'expérience", "👨‍⚕️"));
        doctors.add(new Doctor(8, "Dr. Samia Bouzidi", "Dentiste", "Sousse",
                "Rue Hédi Chaker, Sousse", "+216 73 345 678",
                4.7, "13 ans d'expérience", "👩‍⚕️"));

        // Médecins généraux
        doctors.add(new Doctor(9, "Dr. Leila ben Gharbi", "Médecin général", "Sfax",
                "Rue Habib Thameur, Sfax", "+216 74 345 678",
                4.7, "12 ans d'expérience", "👩‍⚕️"));
        doctors.add(new Doctor(10, "Dr. Ahmed Khelifi", "Médecin général", "Tunis",
                "Rue de Marseille, La Marsa", "+216 71 678 901",
                4.5, "18 ans d'expérience", "👨‍⚕️"));
        doctors.add(new Doctor(11, "Dr. Fatma ben meriem", "Médecin général", "Mahdia",
                "Centre ville, Mahdia", "+216 73 901 234",
                4.8, "11 ans d'expérience", "👩‍⚕️"));
        doctors.add(new Doctor(12, "Dr. Mehdi Samil", "Médecin général", "Sousse",
                "Avenue Léopold Sédar Senghor, Sousse", "+216 73 567 890",
                4.6, "15 ans d'expérience", "👨‍⚕️"));

        // Pédiatres
        doctors.add(new Doctor(13, "Dr. dorra Touati", "Pédiatre", "Tunis",
                "Avenue Mohamed V, Tunis", "+216 71 345 678",
                4.9, "17 ans d'expérience", "👩‍⚕️"));
        doctors.add(new Doctor(14, "Dr. Slah Eddine Ben kamla", "Pédiatre", "Sfax",
                "Rue Commandant Béjaoui, Sfax", "+216 74 456 789",
                4.7, "19 ans d'expérience", "👨‍⚕️"));
        doctors.add(new Doctor(15, "Dr. Rim Chaabane", "Pédiatre", "Sousse",
                "Boulevard Yahia Ibn Omar, Sousse", "+216 73 234 567",
                4.8, "14 ans d'expérience", "👩‍⚕️"));
        doctors.add(new Doctor(16, "Dr. h   Hana Harrabi", "Pédiatre", "Mahdia",
                "Rue Ibn Khaldoun, Mahdia", "+216 73 678 901",
                4.6, "12 ans d'expérience", "👨‍⚕️"));

        // Dermatologues
        doctors.add(new Doctor(17, "Dr. Loujain Kraiem", "Dermatologue", "Tunis",
                "Rue de Rome, Tunis", "+216 71 456 789",
                4.8, "16 ans d'expérience", "👩‍⚕️"));
        doctors.add(new Doctor(18, "Dr. Hedi ben Jemaa", "Dermatologue", "Sfax",
                "Avenue Ali Belhaouane, Sfax", "+216 74 678 901",
                4.7, "13 ans d'expérience", "👨‍⚕️"));
        doctors.add(new Doctor(19, "Dr. Najet  abdlOueslati", "Dermatologue", "Sousse",
                "Avenue Tahar Sfar, Sousse", "+216 73 789 012",
                4.9, "18 ans d'expérience", "👩‍⚕️"));
        doctors.add(new Doctor(20, "Dr. Maher Ayari", "Dermatologue", "Mahdia",
                "Avenue Habib Bourguiba, Mahdia", "+216 73 123 456",
                4.5, "10 ans d'expérience", "👨‍⚕️"));

        // Gynécologues
        doctors.add(new Doctor(21, "Dr. Samia Ghedira", "Gynécologue", "Tunis",
                "Avenue Taieb Mhiri, Tunis", "+216 71 567 890",
                4.9, "21 ans d'expérience", "👩‍⚕️"));
        doctors.add(new Doctor(22, "Dr. sinda Belhadj", "Gynécologue", "Sfax",
                "Rue Habib Maazoun, Sfax", "+216 74 789 012",
                4.8, "15 ans d'expérience", "👩‍⚕️"));
        doctors.add(new Doctor(23, "Dr. isra Karoui", "Gynécologue", "Sousse",
                "Rue Abdelhamid El Kadhi, Sousse", "+216 73 890 123",
                4.7, "17 ans d'expérience", "👩‍⚕️"));
        doctors.add(new Doctor(24, "Dr. Rahma Mansouri", "Gynécologue", "Mahdia",
                "Rue de la Liberté, Mahdia", "+216 73 234 567",
                4.6, "14 ans d'expérience", "👩‍⚕️"));

        // Ophtalmologues
        doctors.add(new Doctor(25, "Dr. Hamdi Mejri", "Ophtalmologue", "Tunis",
                "Avenue de Paris, Tunis", "+216 71 678 901",
                4.8, "19 ans d'expérience", "👨‍⚕️"));
        doctors.add(new Doctor(26, "Dr. Wassim Dridi", "Ophtalmologue", "Sfax",
                "Avenue Hedi Chaker, Sfax", "+216 74 890 123",
                4.7, "16 ans d'expérience", "👩‍⚕️"));
        doctors.add(new Doctor(27, "Dr. Nouefil Mkhinini", "Ophtalmologue", "Sousse",
                "Rue du 2 Mars 1934, Sousse", "+216 73 901 234",
                4.9, "22 ans d'expérience", "👨‍⚕️"));
        doctors.add(new Doctor(28, "Dr. Aida Ayari", "Ophtalmologue", "Mahdia",
                "Boulevard 7 Novembre, Mahdia", "+216 73 345 678",
                4.6, "13 ans d'expérience", "👩‍⚕️"));
    }

    private void setupSpinners() {
        // Spinner Spécialité
        List<String> specialities = Arrays.asList(
                "Toutes les spécialités",
                "Cardiologue",
                "Dentiste",
                "Dermatologue",
                "Gynécologue",
                "Médecin général",
                "Ophtalmologue",
                "Pédiatre"
        );
        ArrayAdapter<String> specialityAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, specialities);
        specialityAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinnerSpeciality.setAdapter(specialityAdapter);

        // Spinner Région
        List<String> regions = Arrays.asList(
                "Toutes les régions",
                "Tunis",
                "Sfax",
                "Sousse",
                "Mahdia"
        );
        ArrayAdapter<String> regionAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, regions);
        regionAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinnerRegion.setAdapter(regionAdapter);

        // Listeners
        spinnerSpeciality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterDoctors();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterDoctors();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupRecyclerView() {
        adapter = new DoctorAdapter(doctors);
        recyclerViewDoctors.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDoctors.setAdapter(adapter);
        updateCount();
    }

    private void filterDoctors() {
        String speciality = spinnerSpeciality.getSelectedItem().toString();
        String region = spinnerRegion.getSelectedItem().toString();
        adapter.filter(speciality, region);
        updateCount();
    }

    private void updateCount() {
        int count = adapter.getFilteredCount();
        tvCount.setText(count + " docteur(s) trouvé(s)");
    }
}