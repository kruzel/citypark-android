package com.citypark.view.overlay;


public class StreetSegment{
	double start_latitude;
	double start_longitude;
	double end_latitude;
	double end_longitude;
	Integer id;
	double search_time;
	
	public StreetSegment() {
		// TODO Auto-generated constructor stub
	}public StreetSegment(StreetSegment s) {
		start_latitude = s.start_latitude;
		start_longitude = s.start_longitude;
		end_latitude = s.end_latitude;
		end_longitude = s.end_longitude;
		id = s.id;
		search_time = s.search_time;
	}
	
	public double getSearch_time() {
		return search_time;
	}
	public void setSearch_time(double search_time) {
		this.search_time = search_time;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public double getStart_latitude() {
		return start_latitude;
	}
	public void setStart_latitude(double start_latitude) {
		this.start_latitude = start_latitude;
	}
	public double getStart_longitude() {
		return start_longitude;
	}
	public void setStart_longitude(double start_longitude) {
		this.start_longitude = start_longitude;
	}
	public double getEnd_latitude() {
		return end_latitude;
	}
	public void setEnd_latitude(double end_latitude) {
		this.end_latitude = end_latitude;
	}
	public double getEnd_longitude() {
		return end_longitude;
	}
	public void setEnd_longitude(double end_longitude) {
		this.end_longitude = end_longitude;
	}

}