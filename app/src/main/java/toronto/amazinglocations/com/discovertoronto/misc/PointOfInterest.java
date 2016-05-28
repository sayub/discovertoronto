/*
* @author  Saad Muhammad Ayub
* Copyright 2016, Saad Muhammad Ayub, All rights reserved.
*/

package toronto.amazinglocations.com.discovertoronto.misc;

public class PointOfInterest {
    private int mImageResourceId;
    private String mName;
    private double mLatitude;
    private double mLongitude;
    private String mURL;

    public PointOfInterest(int imageResourceId, String name, double latitude, double longitude, String url) {
        mImageResourceId = imageResourceId;
        mName = name;
        mLatitude = latitude;
        mLongitude = longitude;
        mURL = url;
    }

    public int getImageResourceId() {
        return  mImageResourceId;
    }

    public String getName() {
        return mName;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public String getURL() {
        return mURL;
    }
}