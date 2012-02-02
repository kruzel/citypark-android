/**
 * 
 */
package com.citypark.utility.route;

import com.google.android.maps.GeoPoint;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class PGeoPoint extends GeoPoint implements Parcelable {

	/**
	 * @param aLocation
	 */
	public PGeoPoint(Location aLocation) {
		super((int)aLocation.getLatitude(),(int)aLocation.getLongitude());
	}

	/**
	 * @param aGeopoint
	 */
	public PGeoPoint(GeoPoint aGeopoint) {
		super(aGeopoint.getLatitudeE6(), aGeopoint.getLatitudeE6());
	}

	/**
	 * @param aLatitudeE6
	 * @param aLongitudeE6
	 */
	public PGeoPoint(int aLatitudeE6, int aLongitudeE6) {
		super(aLatitudeE6, aLongitudeE6);
	}

	/**
	 * @param aLatitude
	 * @param aLongitude
	 */
	public PGeoPoint(double aLatitude, double aLongitude) {
		super((int)aLatitude, (int)aLongitude);
		// TODO Auto-generated constructor stub
	}

	
	
	// ===========================================================
    // Parcelable
    // ===========================================================
    private PGeoPoint(final Parcel in) {
    	super(in.readInt(), in.readInt());
    }

    @Override
    public int describeContents() {
            return 0;
    }

    @Override
    public void writeToParcel(final Parcel out, final int flags) {
            out.writeInt(getLatitudeE6());
            out.writeInt(getLongitudeE6());
    }

    public static final Parcelable.Creator<PGeoPoint> CREATOR = new Parcelable.Creator<PGeoPoint>() {
            @Override
            public PGeoPoint createFromParcel(final Parcel in) {
                    return new PGeoPoint(in);
            }

            @Override
            public PGeoPoint[] newArray(final int size) {
                    return new PGeoPoint[size];
            }
    };

}
