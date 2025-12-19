package com.example.faten;

public class Doctor {
    private int id;
    private String name;
    private String speciality;
    private String region;
    private String address;
    private String phone;
    private double rating;
    private String experience;
    private String image;

    public Doctor(int id, String name, String speciality, String region,
                  String address, String phone, double rating,
                  String experience, String image) {
        this.id = id;
        this.name = name;
        this.speciality = speciality;
        this.region = region;
        this.address = address;
        this.phone = phone;
        this.rating = rating;
        this.experience = experience;
        this.image = image;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getSpeciality() { return speciality; }
    public String getRegion() { return region; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public double getRating() { return rating; }
    public String getExperience() { return experience; }
    public String getImage() { return image; }
}