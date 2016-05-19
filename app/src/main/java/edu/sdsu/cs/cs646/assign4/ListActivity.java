/**
 * File Name : ListActivity.java
 * Created by: Humaira Patel
 * Date: 15/03/2016
 *
 */
package edu.sdsu.cs.cs646.assign4;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

/**
 * This class displays the list of potholes in the form of list view
 * or map view. The user has an option to see the potholes submitted by
 * himself or by all the users.
 *
 */
public class ListActivity extends MainActivity {

    private Button showListView;
    private Button showMapView;
    private Spinner userSpinner;
    private FragmentManager fragmentManager;
    /**
     * This over-riden method sets up the GUI, fills in the value, and
     * creates the list view.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set the content view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Clear the volley cache
        VolleyQueue.instance(getApplicationContext()).getRequestQueue().getCache().clear();

        userSpinner = (Spinner)findViewById(R.id.user);
        showListView = (Button)findViewById(R.id.showListButton);

        // show the list view on button click
        showListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // Setting the ListView fragment
            try{
                fragmentManager = getSupportFragmentManager();
                Fragment listFragment = new ListFragment();
                listFragment.setArguments(getBundle());
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, listFragment)
                        .commit();
            }
            catch(Exception e){
                Log.e("Exception", e.toString());
            }

            }
        });

        // show the map view on button click
        showMapView = (Button)findViewById(R.id.showMapButton);
        showMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // Setting the MapView fragment
            try{
                fragmentManager = getSupportFragmentManager();
                android.support.v4.app.Fragment mapFragment = new MapviewFragment();
                mapFragment.setArguments(getBundle());
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, mapFragment)
                        .commit();
            }
            catch(Exception e){
                Log.e("Exception", e.toString());
            }
            }
        });
    }

    /***
     *  This method returns the bundle to pass to the fragments.
     *
     * @return
     */
    private Bundle getBundle(){
        Bundle bundle = new Bundle();
        bundle.putString("user", userSpinner.getSelectedItem().toString());
        return  bundle;
    }
}
