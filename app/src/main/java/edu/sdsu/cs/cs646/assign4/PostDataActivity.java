/**
 * File Name : PostDataActivity.java
 * Created by: Humaira Patel
 * Date: 15/03/2016
 *
 */
package edu.sdsu.cs.cs646.assign4;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;

/**
 * This class allows the user to report a pothole. The user can
 * add a description and upload a photo while posting a report
 *
 */
public class PostDataActivity extends MainActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int GALLERY_REQ_CODE=1;
    private static final int REQUEST_TAKE_PHOTO=2;
    private static final String FILENAME = "tmpImage.jpg";
    private static final String M_TYPE = "street";
    private static final String URL = "http://bismarck.sdsu.edu/city/report";

    private GoogleApiClient mGoogleApiClient;
    private EditText descriptionText;
    private ImageView mImageView;
    private File mPhotoFile;
    private String mImageType="none";
    private String mEncodedImageData;
    private Location mLastLocation;
    private Bitmap bitmap;

    /**
     * This over-riden method sets up the GUI, and
     * creates a page to report a pothole.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the content view
        setContentView(R.layout.activity_post_data);

        // set the edit text for description.
        descriptionText = (EditText)findViewById(R.id.description);
        descriptionText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView editText, int actionId, KeyEvent event) {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handled = true;
            }
            if (handled) {
                hideSoftKeyboard(descriptionText);
            }
            return true;
            }
        });

        mImageView = (ImageView) findViewById(R.id.imageView);

        // Check for location service and start it
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
            Log.i("Location Service", "Started");
        }
    }

    /**
     * This method returns the image in the form of file.
     *
     * @return
     */
    private File imageFile() {
        File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if ( externalFilesDir == null )
            return null;
        return new File(externalFilesDir, FILENAME);
    }

    /**
     * This method takes the picture using the camera.
     *
     * @param button
     */
    public void dispatchTakePictureIntent(View button) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            mPhotoFile = imageFile();
            if (mPhotoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * This method displays the image.
     *
     */
    private void displayImage(){
        mImageView.setImageBitmap(bitmap);
        mEncodedImageData = ImageUtils.getEncoded64Img(bitmap);
        mImageType="jpg";
    }

    /**
     * This method opens the phones gallery so that the user can select an image.
     *
     * @param button
     */
    public void goToGallery(View button) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQ_CODE);
    }

    /**
     * This method updates the image view with the photo taken or selected.
     *
     * @param photoFile
     */
    private void updatePhotoView(File photoFile) {
        if (photoFile == null || !photoFile.exists()) {
            mImageView.setImageDrawable(null);
        }
        else {
            bitmap = ImageUtils.getScaledBitmap(
                    photoFile.getPath());
            displayImage();
        }
    }

    /**
     * This method gets the result from the gallery or the camera activity.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQ_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
                goToErrorPage(e.toString());
            }
            displayImage();
        }

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            updatePhotoView(mPhotoFile);
        }
    }

    /**
     *
     * Over-riding the onstart of the activity.
     */
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    /**
     *
     * Over-riding the onStop of the activity.
     */
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Over-riding the connection failed
     *
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("Error", "Location services connection failed with message " + connectionResult.getErrorMessage());
        goToErrorPage(connectionResult.getErrorMessage());
    }

    /***
     * Over-riding the onConnected method
     *
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.i("Connection", "Connected");
    }

    /***
     * Over-riding the onConnectionSuspended method
     *
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Connection", "Suspended");
    }

    /**
     * This method hides the Soft Keyboard.
     * @param view
     */
    public void hideSoftKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     *
     *
     * @param view
     */
    public void postJsonObjectRequest(View view) {
        //get location only when posting data.
        mLastLocation = LocAndConnUtils.getLocation(mGoogleApiClient);
        // prepare the json
        if (LocAndConnUtils.getNetworkInfo(getApplicationContext()) && mLastLocation!=null) {
            JSONObject data = new JSONObject();
            try {
                data.put("type", M_TYPE);
                data.put("latitude", mLastLocation.getLatitude());
                data.put("longitude", mLastLocation.getLongitude());
                data.put("user", USER);
                data.put("imagetype", mImageType);
                data.put("description", descriptionText.getText());
                data.put("image", mEncodedImageData);
            } catch (JSONException error) {
                Log.e("Exception", error.toString());
                goToErrorPage("Exception caught creating json");
            }

            // success listener
            Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("Post Data","Success");
                    displayConfirmation(response.toString());
                }
            };

            // error listener
            Response.ErrorListener failure = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String error_msg = new String(error.networkResponse.data);
                    Log.i("Post Data Error",error_msg);
                    goToErrorPage(error_msg);
                }
            };

            // create the request
            JsonObjectRequest postRequest = new JsonObjectRequest(URL, data, success, failure);
            VolleyQueue.instance(this).add(postRequest);
        }
        else {
            goToErrorPage("Network or Location Service Unavailable");
        }
    }

    /**
     * This method displays the confirmation by taking the user to
     * the confirmation activity.
     *
     * @param id
     */
    public void displayConfirmation(String id){
        Pothole p = new Pothole();
        p.setId(id);
        p.setLocation(mLastLocation.getLatitude(),
                mLastLocation.getLongitude(),
                getApplicationContext());
        p.setType(M_TYPE);
        p.setDescription(descriptionText.getText().toString());
        p.setImageType(mImageType);

        Intent goToConfirmation = new Intent(this,DisplayConfirmation.class);
        goToConfirmation.putExtra("pothole", p);
        if (bitmap !=null)
            goToConfirmation.putExtra("image",ImageUtils.compressImage(bitmap));
        startActivity(goToConfirmation);
    }

}
