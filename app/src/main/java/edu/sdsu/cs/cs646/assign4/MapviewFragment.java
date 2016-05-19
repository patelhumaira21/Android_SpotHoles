/**
 * File Name : MapViewFragment.java
 * Created by: Humaira Patel
 * Date: 15/03/2016
 *
 */
package edu.sdsu.cs.cs646.assign4;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * This class represents the fragment for map view of potholes.
 *
 */
public class MapviewFragment extends android.support.v4.app.Fragment  {

    private static final String URL = "http://bismarck.sdsu.edu/city/fromLocation?type=street";
    private String user;
    private SupportMapFragment mapFragment;
    private HashMap<Marker,Pothole> mPotholesHashMap = new HashMap<>();
    private Location currLocation;
    private double start_latitude=0.0;
    private double end_latitude=0.0;
    private double start_longitude=0.0;
    private double end_longitude=0.0;

    /**
     * This over-riden method retrieves the user name that was selected.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getArguments().getString("user");
    }

    /**
     * This over-riden method sets up the GUI and
     * inflates the fragment with the map.
     *
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        getJsonArrayRequest(user);
        return view;
    }

    /**
     * This method fetches data from the server.
     */
    public void getJsonArrayRequest(String user) {
        Context context = getActivity().getApplicationContext();
        if(LocAndConnUtils.getNetworkInfo(context)) {
            // Get current location
            currLocation = LocAndConnUtils.getCurrLocation(context);
            if (currLocation!=null) {
                final LatLng myLatLng = new LatLng(currLocation.getLatitude(), currLocation.getLongitude());

                Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
                    public void onResponse(final JSONArray response) {
                        Log.i("Volley Map Response","Success");
                        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                // set the camera position and marker listener.
                                CameraPosition cameraPosition = new CameraPosition.Builder().
                                        target(myLatLng).
                                        tilt(60).
                                        zoom(15).
                                        bearing(0).
                                        build();
                                googleMap.getUiSettings().setZoomControlsEnabled(true);
                                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                googleMap.setBuildingsEnabled(true);
                                googleMap.setMyLocationEnabled(true);
                                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {
                                        Pothole pothole = mPotholesHashMap.get(marker);
                                        showDetailedView(pothole);
                                        return false;
                                    }
                                });
                                handleResponse(googleMap, response);
                            }
                        });
                    }
                };
                // error handler for the request
                Response.ErrorListener failure = new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Map Error", new String(error.networkResponse.data));
                    }
                };

                // create the request
                setStartEndLatLng();
                String url = URL+"&start-latitude="+start_latitude+"&end-latitude="+end_latitude+
                        "&start-longitude="+start_longitude+"&end-longitude="+end_longitude;

                if (!user.equals("All Users"))
                    url += "&user=" + user;
                JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
                VolleyQueue.instance(getActivity().getApplicationContext()).add(getRequest);
            }
            else {
                callErrorPage("Unable to receive current location \n Set your current Location");
            }
        }
        else {
            callErrorPage("Network Unavailable");
        }
    }

    /**
     * This method calls the DetailedViewActivity when a marker on the
     * map is clicked.
     * @param pothole
     */
    public void showDetailedView(Pothole pothole) {
        Intent goToDetailedActivity = new Intent(getActivity(),DetailedViewActivity.class);
        goToDetailedActivity.putExtra("pothole", pothole);
        startActivity(goToDetailedActivity);
    }

    /**
     * This method handles the data received from server.
     * It adds a marker for each pothole object and creates
     * a hashmap.
     *
     * @param googleMap
     * @param response
     */
    public void handleResponse(GoogleMap googleMap,JSONArray response){
        int length = response.length();

        for (int i = 0; i < length; i++) {
            try {
                JSONObject obj = response.getJSONObject(i);
                double latitude = Double.parseDouble(obj.get("latitude").toString());
                double longitude = Double.parseDouble(obj.get("longitude").toString());

                Pothole p = new Pothole();
                p.setId(obj.get("id").toString());
                p.setLocation(latitude, longitude, getActivity().getApplicationContext());
                p.setType(obj.get("type").toString());
                p.setDescription(obj.get("description").toString());
                p.setCreatedDate(obj.get("created").toString());
                p.setImageType(obj.get("imagetype").toString());
                LatLng latLng = new LatLng(latitude, longitude);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .snippet(p.getDescription())
                        .title(p.getCreatedDate());
                Marker marker = googleMap.addMarker(markerOptions);
                mPotholesHashMap.put(marker, p);

            } catch (Exception e) {
                Log.e("Exception", e.toString());
            }
        }
    }

    /**
     * This method takes the user to error handling activity which
     * can be a result of a network issue or exception.
     *
     * @param error
     */
    public void callErrorPage(String error){
        Log.e("Go to Error Page", error);
        Intent goToErrorPage = new Intent(getActivity(), ErrorHandlingActivity.class);
        goToErrorPage.putExtra("error", error);
        startActivity(goToErrorPage);
    }

    /**
     * This method sets the start and end values of latitude and
     * longitude to get potholes within a specified region.
     */
    private void setStartEndLatLng(){
        double delta = 1.0;
        double currLat = currLocation.getLatitude();
        double currLon = currLocation.getLongitude();

        start_latitude=currLat-delta;
        end_latitude=currLat+delta;

        start_longitude=currLon-delta;
        end_longitude=currLon+delta;
    }
}



