/**
 * File Name : DisplayConfirmation.java
 * Created by: Humaira Patel
 * Date: 15/03/2016
 *
 */
package edu.sdsu.cs.cs646.assign4;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

/**
 * This class represents the confirmation page displayed on the successful
 * submission of the pothole report.
 *
 */
public class DisplayConfirmation extends MainActivity {

    /**
     * This over-riden method sets up the GUI, fills in the value, and
     * creates the confirmation view.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set up the content
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_confirmation);

        // get the pothole object
        Pothole mPothole = (Pothole) getIntent().getSerializableExtra("pothole");
        final double latitude = mPothole.getLatitude();
        final double longitude = mPothole.getLongitude();
        byte[] byteArray = getIntent().getByteArrayExtra("image");

        // Set the  text values
        TextView user = (TextView) findViewById(R.id.conUserLabel);
        user.setText(USER);

        TextView description = (TextView) findViewById(R.id.conDescLabel);
        description.setText(mPothole.getDescription());

        // Get the address.
        Address address = LocAndConnUtils.getAddress(getApplicationContext(), latitude, longitude);
        TextView location = (TextView) findViewById(R.id.conLocationLabel);
        location.setText(address.getAddressLine(1).toString());

        // Set the image
        ImageView imageView = (ImageView) findViewById(R.id.conImageLabel);
        Bitmap bitmap = null;
        if (byteArray!=null)
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        if(bitmap!=null)
            imageView.setImageBitmap(bitmap);
        else
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.img_not_found));

        // Show the location of the pothole in the map
        MapFragment mapFrag = (MapFragment)getFragmentManager().findFragmentById(R.id.conMapView);
        mapFrag.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LocAndConnUtils.displayMap(googleMap,latitude,longitude);
            }
        });
    }

}
