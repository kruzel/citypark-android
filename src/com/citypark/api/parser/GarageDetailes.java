package com.citypark.api.parser;

import com.citypark.constants.GarageAvailability;

//TODO unify with dto.GarageDetailes
public class GarageDetailes{	
	private int id;	//<ParkingId>int</ParkingId>
	private String name; //<Name>string</Name>
	private String city; //<City>string</City>
	private String streetName; //<StreetName>string</StreetName>
	private int houseNumber; //<HouseNumber>int</HouseNumber>
	private double longitude; //<Longitude>string</Longitude>
	private double latitude; //<Latitude>string</Latitude>
	//<Numberofparks>string</Numberofparks>
	//<Tel>string</Tel>
	//<Fax>string</Fax>
	//<Email>string</Email>
	//<Onlymail>string</Onlymail>
	//<Comment>string</Comment>
	//<Payment>string</Payment>
	private String imageURL;//<Image>string</Image>
	//<Image2>string</Image2>
	//<StartDate>string</StartDate>
	//<EndDate>string</EndDate>
	//<Parkingtype>string</Parkingtype>
	//<Contactname>string</Contactname>
	//<Hourly>string</Hourly>
	//<Daily>string</Daily>
	//<Weekly>string</Weekly>
	//<Monthly>string</Monthly>
	//<Yearly>string</Yearly>
	//<Price>string</Price>
	//<Pricefortime>string</Pricefortime>
	//<Cartype>string</Cartype>
	//<Manuytype>string</Manuytype>
	//<Payments>string</Payments>
	//<Cc>string</Cc>
	//<Cash>string</Cash>
	//<Cheak>string</Cheak>
	//<Paypal>string</Paypal>
	//<Lelohagbalatheshbon>string</Lelohagbalatheshbon>
	//<Majsom>string</Majsom>
	//<Tatkarkait>string</Tatkarkait>
	private Boolean garage = false; //<Jenion>string</Jenion>
	private Boolean criple = false; //<Criple>string</Criple>
	private Boolean noLimit = false; //<Nolimit>string</Nolimit>
	//<Henion>string</Henion>
	private Boolean withLock = false; //<Withlock>string</Withlock>
	private Boolean underground = false; //<Underground>string</Underground>
	private Boolean roof = false; //<Roof>string</Roof>
	//<Vip>string</Vip>
	private Boolean toshav = false; //<Toshav>string</Toshav>
	//<Coupon>string</Coupon>
	private String couponText = "N/A";//<Coupon_text>string</Coupon_text>
	private GarageAvailability availability; //<Current_Pnuyot>string</Current_Pnuyot>
	//<SiteID>string</SiteID>
	//<Heniontype>string</Heniontype>
	private double allDayPrice; //<AllDayPrice>string</AllDayPrice>
	private double extraQuarterPrice; //<ExtraQuarterPrice>string</ExtraQuarterPrice>
	private double firstHourPrice; //<FirstHourPrice>string</FirstHourPrice>
	
	public GarageDetailes() {
		super();
	}

	public GarageDetailes(GarageDetailes g) {
		super();
		this.id = g.id;
		this.name = g.name;
		this.city = g.city;
		this.streetName = g.streetName;
		this.houseNumber = g.houseNumber;
		this.longitude = g.longitude;
		this.latitude = g.latitude;
		this.garage = g.garage;
		this.criple = g.criple;
		this.noLimit = g.noLimit;
		this.withLock = g.withLock;
		this.underground = g .underground;
		this.roof = g.roof;
		this.toshav = g.toshav;
		this.couponText = g.couponText;
		this.availability = g.availability;
		this.allDayPrice = g.allDayPrice;
		this.extraQuarterPrice = g.extraQuarterPrice;
		this.firstHourPrice = g.firstHourPrice;
		this.imageURL = g.imageURL;
	}

	
	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public Boolean getGarage() {
		return garage;
	}

	public void setGarage(Boolean garage) {
		this.garage = garage;
	}

	public Boolean getCriple() {
		return criple;
	}

	public void setCriple(Boolean criple) {
		this.criple = criple;
	}

	public Boolean getNoLimit() {
		return noLimit;
	}

	public void setNoLimit(Boolean noLimit) {
		this.noLimit = noLimit;
	}

	public Boolean getWithLock() {
		return withLock;
	}

	public void setWithLock(Boolean withLock) {
		this.withLock = withLock;
	}

	public Boolean getUnderground() {
		return underground;
	}

	public void setUnderground(Boolean underground) {
		this.underground = underground;
	}

	public Boolean getRoof() {
		return roof;
	}

	public void setRoof(Boolean roof) {
		this.roof = roof;
	}

	public Boolean getToshav() {
		return toshav;
	}

	public void setToshav(Boolean toshav) {
		this.toshav = toshav;
	}

	public String getCouponText() {
		return couponText;
	}

	public void setCouponText(String couponText) {
		this.couponText = couponText;
	}

	public GarageAvailability getAvailability() {
		return availability;
	}

	public void setAvailability(GarageAvailability availability) {
		this.availability = availability;
	}

	public double getAllDayPrice() {
		return allDayPrice;
	}

	public void setAllDayPrice(double allDayPrice) {
		this.allDayPrice = allDayPrice;
	}

	public double getExtraQuarterPrice() {
		return extraQuarterPrice;
	}

	public void setExtraQuarterPrice(double extraQuarterPrice) {
		this.extraQuarterPrice = extraQuarterPrice;
	}

	public double getFirstHourPrice() {
		return firstHourPrice;
	}

	public void setFirstHourPrice(double firstHourPrice) {
		this.firstHourPrice = firstHourPrice;
	}		
}