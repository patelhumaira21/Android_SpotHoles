/**
 * File Name : LocAndConnUtils.java
 * Created by: Humaira Patel
 * Date: 15/03/2016
 *
 */
package edu.sdsu.cs.cs646.assign4;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;
import java.util.Locale;

/**
 * This class represents the utility method for the location.
 *
 */
public class LocAndConnUtils {

    /***
     * This method returns the current location.
     *
     * @param mGoogleApiClient
     * @return
     */
    public static Location getLocation(GoogleApiClient mGoogleApiClient) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    /**
     * This method returns the address from the lotitude and longitude.
     *
     * @param context
     * @param latitude
     * @param longitude
     * @return
     */
    public static Address getAddress(Context context,double latitude, double longitude){
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0)
                return addresses.get(0);
        }
        catch(Exception e) {
            return null;
        }
        return null;
    }

    /**
     * This method pin-points the given latitude and longitude in the given map.
     *
     * @param googleMap
     * @param latitude
     * @param longitude
     */
    public static void displayMap(GoogleMap googleMap,double latitude, double longitude){
        LatLng latLng = new LatLng(latitude,longitude);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(latLng).
                tilt(60).
                zoom(17).
                bearing(0).
                build();
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.addMarker(markerOptions);
        googleMap.setBuildingsEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.getUiSettings().setTiltGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    /**
     * This method gives the network information
     *
     * @param context
     * @return
     */
    public static boolean getNetworkInfo(Context context){
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    /**
     * This method returns the current location.
     *
     * @param context
     * @return
     */
    public static Location getCurrLocation(Context context) {
        Location loc = null;
        LocationManager service = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = service.getBestProvider(criteria,true);
        loc = service.getLastKnownLocation(provider);
        return loc;
    }

}
