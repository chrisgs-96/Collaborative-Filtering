package com.example.lux.mapsapp;

//A class that keeps all the data about a com.example.lux.mapsapp.POI
public class POI implements java.io.Serializable{
	private int id;
	private String name;
	private double latitude;
	private double longitude;
	private String category;
	private String code;
	private String photo;
	
	//Constructor
	public POI(int id, String name, double latitude, double longitude, String category, String code, String photo) {
		this.id = id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.category = category;
		this.code = code;
		this.photo = photo;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public void setLongitude(double longtitude) {
		this.longitude = longtitude;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
	public int getID() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public String getCategory() {
		return category;
	}

	public String getCode() {
		return code;
	}

	public String getPhoto() {
		return photo;
	}
	
	public void print() {
		System.out.println("ID: "+id);
		System.out.println("Name: "+name);
		System.out.println("Longitude: "+longitude);
		System.out.println("Latitude: "+latitude);
		System.out.println("Category: "+category);
		System.out.println("Code: "+code);
		System.out.println("Photo: "+photo);
	}
}