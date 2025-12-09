package com.example.faten;

public class Appointment {
    private int id;
    private String doctorName;
    private String specialty;
    private String date;
    private String time;
    private String color;

    public Appointment(int id, String doctorName, String specialty, String date, String time, String color) {
        this.id = id;
        this.doctorName = doctorName;
        this.specialty = specialty;
        this.date = date;
        this.time = time;
        this.color = color;
    }

    public int getId() { return id; }
    public String getDoctorName() { return doctorName; }
    public String getSpecialty() { return specialty; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getColor() { return color; }

    public void setId(int id) { this.id = id; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setColor(String color) { this.color = color; }}

