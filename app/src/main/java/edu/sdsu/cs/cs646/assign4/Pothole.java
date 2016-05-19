/**
 * File Name : Pothole.java
 * Created by: Humaira Patel
 * Date: 15/03/2016
 *
 */
package edu.sdsu.cs.cs646.assign4;

import android.content.Context;
import android.graphics.Bitmap;
import java.io.Serializable;

/**
 * This method represents the pothole object. This is an encapsulation of all the data
 * for the pothole object and contains getters and setters for each data field.
 */
public class Pothole implements Serializable{

    private String mId;
    private double mLatitude;
    private double mLongitude;
    private String mType;
    private String mDescription;
    private String mCreatedDate;
    private String mImageType;
    private Bitmap mImage;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getCreatedDate() {
        return mCreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        mCreatedDate = createdDate;
    }

    public String getImageType() {
        return mImageType;
    }

    public void setImageType(String imageType) {
        mImageType = imageType;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public void setImage(Bitmap image) {
        mImage = image;
    }

    public void setLocation(double latitude, double longitude,Context context) {
        mLatitude = latitude;
        mLongitude = longitude;
    }
}
