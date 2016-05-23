package toronto.amazinglocations.com.discovertoronto;

import android.graphics.Bitmap;

public class PointOfInterest {
    private Bitmap mImage;
    private String mName;
    private double mLatitude;
    private double mLongitude;
    private String mURL;

    public PointOfInterest(Bitmap image, String name, double latitude, double longitude, String url) {
        mImage = image;
        mName = name;
        mLatitude = latitude;
        mLongitude = longitude;
        mURL = url;
    }

    public Bitmap getImage() {
        return mImage;
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