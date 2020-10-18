package com.example.qasim1793.remindme.Backend;

public class LocationReminder {
    private int id;
    private String title;
    private String description;
    private double lat;
    private double lng;
    private String userId;
    private String status;


    public LocationReminder() {
    }

    public LocationReminder(int id, String title, String description, double lat, double lng,String userId,String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.lat = lat;
        this.lng = lng;
        this.userId = userId;
        this.status=status;
    }

    public LocationReminder(String title, String description, double lat, double lng,String userId,String status) {
        this.title = title;
        this.description = description;
        this.lat = lat;
        this.lng = lng;
        this.userId = userId;
        this.status=status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}