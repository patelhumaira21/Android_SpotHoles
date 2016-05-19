/**
 * File Name : MainActivity.java
 * Created by: Humaira Patel
 * Date: 15/03/2016
 *
 */
package edu.sdsu.cs.cs646.assign4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

/**
 * This class represents the main activity which is the entry point
 * for the app. The user can report a pothole or view the list of potholes
 * from this point.
 *
 */
public class MainActivity extends ActionBarActivity {

    public static final String USER="SDSU_033";

    /**
     * This over-riden method sets up the GUI, and
     * creates the home page view.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * This method takes the user to post data activity where the user can report
     * a pothole
     *
     * @param button
     */
    public void goToPostActivity(View button){
        goToActivity(PostDataActivity.class);
    }

    /**
     * This method takes the user to list activity where the user can view the list
     * potholes in a map view or list view.
     *
     * @param button
     */
    public void goToListActivity(View button){
        goToActivity(ListActivity.class);
    }


    /**
     * This method takes the user to the provided activity passed as a parameter.
     *
     * @param activityClass
     */
    public void goToActivity(Class activityClass){
        Log.i("Going to Activity", activityClass.toString());
        if (LocAndConnUtils.getNetworkInfo(getApplicationContext())) {
            Intent goToPost = new Intent(this, activityClass);
            startActivity(goToPost);
        }
        else{
            goToErrorPage("Network Unavailable");
        }
    }

    /**
     * This method takes the user to error handling activity which can be a result of a
     * network issue or exception.
     *
     * @param error
     */
    public void goToErrorPage(String error){
        Log.i("Go to error page", error);
        Intent goToErrPg = new Intent(this,ErrorHandlingActivity.class);
        goToErrPg.putExtra("error",error);
        startActivity(goToErrPg);
    }

    /**
     * This method takes the user to main activity. This method is used
     * by the child class of this activity.
     *
     * @param button
     */
    public void goToHome(View button){
        Intent goTohome = new Intent(this, MainActivity.class);
        navigateUpTo(goTohome);
    }
}
