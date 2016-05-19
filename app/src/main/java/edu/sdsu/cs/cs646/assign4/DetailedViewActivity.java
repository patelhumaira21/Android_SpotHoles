/**
 * File Name : DetailedViewActivity.java
 * Created by: Humaira Patel
 * Date: 15/03/2016
 */
package edu.sdsu.cs.cs646.assign4;

import android.graphics.Bitmap;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.MapFragment;

/**
 *
 * This class represents the detailedView of a particular pothole. It displays all
 * the information about a particular pothole.
 *
 */
public class DetailedViewActivity extends MainActivity implements OnMapReadyCallback{

    private static final String URL="http://bismarck.sdsu.edu/city/image?id=";
    private Pothole mPothole;
    private ImageView mImageView;
    private TextView mDescription;
    private TextView mDate;
    private TextView mLocation;
    private Address mAddress;

    /**
     * This over-riden method sets up the GUI, fills in the value, and
     * creates the detailed view.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set up the content
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);

        // Get the widget
        mPothole = (Pothole) getIntent().getSerializableExtra("pothole");
        mImageView = (ImageView)findViewById(R.id.imageLabel);
        mDate = (TextView)findViewById(R.id.dateLabel);
        mDescription = (TextView)findViewById(R.id.descLabel);
        mLocation = (TextView)findViewById(R.id.locationLabel);
        mAddress = LocAndConnUtils.getAddress(getApplicationContext(), mPothole.getLatitude(), mPothole.getLongitude());

        // setting up the map fragment
        MapFragment mMapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapview);
        mMapFragment.getMapAsync(this);
        updateUI();
    }

    /***
     * This method creates a volley request for the image and
     * gets the image from the given url.
     *
     * @param url
     */
    public void getImageRequest(String url){
        // Success listener for image
        Response.Listener<Bitmap> success = new Response.Listener<Bitmap>() {
            public void onResponse(Bitmap image) {
                Log.i("Image found","Success");
                //checking for the image
                if(image != null) {
                    mImageView.setImageBitmap(image);
                }
                else{
                    mImageView.setImageDrawable(getResources().getDrawable(R.drawable.img_not_found));
                }
            }
        };
        // Error listener for image
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e("Image request", error.toString());
                mImageView.setImageDrawable(getResources().getDrawable(R.drawable.img_not_found));
            }
        };

        // create the image request to get the image.
        ImageRequest imageRequest = new ImageRequest(url,
                success, 0, 0, ImageView.ScaleType.CENTER_INSIDE, null, failure);
        VolleyQueue.instance(this).add(imageRequest);
    }

    /**
     * This method updates the UI with the details about the potholes.
     *
     */
    public void updateUI(){
        // Check if image was uploaded
        if(!mPothole.getImageType().equals("none"))
            getImageRequest( URL+ mPothole.getId());
        else
            mImageView.setImageDrawable(getResources().getDrawable(R.drawable.img_not_found));
        // Set the values
        mDescription.setText(mPothole.getDescription());
        mDate.setText(mPothole.getCreatedDate());
        String addressString = (mAddress != null)
                ? mAddress.getAddressLine(0) + "\n" + mAddress.getAddressLine(1) + "\n" + mAddress.getAddressLine(2)
                : "Invalid Address from Server";
        Log.i("AddressString", "received");
        mLocation.setText(addressString);
    }

    /**
     * This method marks the location of the pothole in the map.
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LocAndConnUtils.displayMap(googleMap, mPothole.getLatitude(), mPothole.getLongitude());
    }

    /**
     * This function is the onclick for the back button. It finishes the
     * acitivity
     *
     * @param button
     */
    public void onBackPressed(View button){
        finish();
    }

}

