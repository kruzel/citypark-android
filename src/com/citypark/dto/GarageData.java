package com.citypark.dto;

//TODO can we unify with garageDetails?
public class GarageData {
	private int parkingId;    
    private String name;
    private String city;
    private String streetName;
    private int houseNumber;
	
	private double latitude;
	private double longitude;
	/*Pricing*/
	private int firstHourPrice;
	private int extraQuarterPrice;
	private int allDayPrice;
	
	/*Filter properties*/
    private String spacesForDisabled;//criple
    private String nolimit;
    private String henion;
    private String withlock;//or security guard
    private String underground;
    private String roof;
    private String vip;
    private String resident;//toshav
    
    /*Coupon*/
    private String coupon;
    private String coupon_text;
    /*Status*/
    private int freeSpot;
    
	private String image1;
	private String image2;
	
	public GarageData() {
		
	}
	
	

	public GarageData(int parkingId, String name, String city,
			String streetName, int houseNumber, double latitude,
			double longitude, int firstHourPrice, int extraQuarterPrice,
			int allDayPrice, String spacesForDisabled, String nolimit,
			String henion, String withlock, String underground, String roof,
			String vip, String resident, String coupon, String coupon_text,
			int freeSpot, String image1, String image2) {
		super();
		this.parkingId = parkingId;
		this.name = name;
		this.city = city;
		this.streetName = streetName;
		this.houseNumber = houseNumber;
		this.latitude = latitude;
		this.longitude = longitude;
		this.firstHourPrice = firstHourPrice;
		this.extraQuarterPrice = extraQuarterPrice;
		this.allDayPrice = allDayPrice;
		this.spacesForDisabled = spacesForDisabled;
		this.nolimit = nolimit;
		this.henion = henion;
		this.withlock = withlock;
		this.underground = underground;
		this.roof = roof;
		this.vip = vip;
		this.resident = resident;
		this.coupon = coupon;
		this.coupon_text = coupon_text;
		this.freeSpot = freeSpot;
		this.image1 = image1;
		this.image2 = image2;
	}



	public int getParkingId() {
		return parkingId;
	}

	public void setParkingId(int parkingId) {
		this.parkingId = parkingId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public int getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(int houseNumber) {
		this.houseNumber = houseNumber;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getFirstHourPrice() {
		return firstHourPrice;
	}

	public void setFirstHourPrice(int firstHourPrice) {
		this.firstHourPrice = firstHourPrice;
	}

	public int getExtraQuarterPrice() {
		return extraQuarterPrice;
	}

	public void setExtraQuarterPrice(int extraQuarterPrice) {
		this.extraQuarterPrice = extraQuarterPrice;
	}

	public int getAllDayPrice() {
		return allDayPrice;
	}

	public void setAllDayPrice(int allDayPrice) {
		this.allDayPrice = allDayPrice;
	}

	public String getSpacesForDisabled() {
		return spacesForDisabled;
	}

	public void setSpacesForDisabled(String spacesForDisabled) {
		this.spacesForDisabled = spacesForDisabled;
	}

	public String getNolimit() {
		return nolimit;
	}

	public void setNolimit(String nolimit) {
		this.nolimit = nolimit;
	}

	public String getHenion() {
		return henion;
	}

	public void setHenion(String henion) {
		this.henion = henion;
	}

	public String getWithlock() {
		return withlock;
	}

	public void setWithlock(String withlock) {
		this.withlock = withlock;
	}

	public String getUnderground() {
		return underground;
	}

	public void setUnderground(String underground) {
		this.underground = underground;
	}

	public String getRoof() {
		return roof;
	}

	public void setRoof(String roof) {
		this.roof = roof;
	}

	public String getVip() {
		return vip;
	}

	public void setVip(String vip) {
		this.vip = vip;
	}

	public String getResident() {
		return resident;
	}

	public void setResident(String resident) {
		this.resident = resident;
	}

	public String getCoupon() {
		return coupon;
	}

	public void setCoupon(String coupon) {
		this.coupon = coupon;
	}

	public String getCoupon_text() {
		return coupon_text;
	}

	public void setCoupon_text(String coupon_text) {
		this.coupon_text = coupon_text;
	}

	public int getFreeSpot() {
		return freeSpot;
	}

	public void setFreeSpot(int freeSpot) {
		this.freeSpot = freeSpot;
	}

	public String getImage1() {
		return image1;
	}

	public void setImage1(String image1) {
		this.image1 = image1;
	}

	public String getImage2() {
		return image2;
	}

	public void setImage2(String image2) {
		this.image2 = image2;
	}
	
	
	
}
