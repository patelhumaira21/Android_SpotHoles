/**
 * File Name : ListFragment.java
 * Created by: Humaira Patel
 * Date: 15/03/2016
 *
 */
package edu.sdsu.cs.cs646.assign4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the fragment for list view of potholes.
 * It communicates with the PotholesAdapter using the ItemClickListener.
 *
 */
public class ListFragment extends Fragment implements PotholesAdapter.ItemClickListener {

    private static final int BATCH_SIZE=10;
    private static final String URL = "http://bismarck.sdsu.edu/city/batch?type=street&size="+BATCH_SIZE+"&batch-number=";

    private List<Pothole> mPotholeList;
    private String user;
    private int batch_number = 0;
    private int max_batch_number = 0;
    private Response.Listener<JSONArray> success;
    private Response.ErrorListener failure;
    private boolean isEndOfData = false;
    private ArrayAdapter<Pothole> mAdapter;
    private ListView listView;
    private ImageButton prevButton;
    private ImageButton nextButton;
    private ProgressBar progress;

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
     * This over-riden method sets up the GUI, fills in the value, and
     * inflates the fragment with the list of potholes.
     *
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        // Set the progress bar while the data is fetched from the server.
        progress=(ProgressBar)view.findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);

        listView = (ListView)view.findViewById(R.id.list_view);

        // Set the next button.
        nextButton = (ImageButton)view.findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (!isEndOfData) {
                batch_number++;
                callVolley();
            }
            }
        });

        // Set thr previous button.
        prevButton = (ImageButton) view.findViewById(R.id.previous);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (batch_number > 0) {
                batch_number--;
                max_batch_number = 0;
                callVolley();
            }
            }
        });
        getJsonArrayRequest();
        return view;
    }

    /**
     * This method fetches data from the server.
     */
    public void getJsonArrayRequest() {
        initVolleyListener();
        callVolley();
    }

    /**
     * This method will check for network connectivity before making a request.
     * It will check if data is available in cache.
     */
    public void callVolley(){
        if(LocAndConnUtils.getNetworkInfo(getActivity().getApplicationContext())) {
            String url = URL + batch_number;
            if (!user.equals("All Users"))
                url += "&user=" + user;

            // Check cache
            Cache cache = VolleyQueue.instance(getActivity().getApplicationContext()).getRequestQueue().getCache();
            Cache.Entry entry = cache.get(url);
            if (entry != null) {
                Log.d("Cache","Data Received");
                try {
                    handleResponse(new JSONArray(new String(entry.data, "UTF-8")));
                } catch (Exception e) {
                    Log.e("Exception", e.toString());
                }
            } else {
                progress.setVisibility(View.VISIBLE);
                listView.setVisibility(View.INVISIBLE);

                // Make request to server if data not available in cache.
                JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
                VolleyQueue.instance(getActivity().getApplicationContext()).add(getRequest);
            }
        }
        else {
            callErrorPage("Network Unavailable");
        }
    }

    /**
     * This method initializes volley's success and failure listeners.
     */
    public void initVolleyListener(){
        success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response)  {
                Log.i("Volley List Response", "Success");
                progress.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                handleResponse(response);
            }
        };

        failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley List Error", error.toString());
                progress.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                String error_msg = new String(error.networkResponse.data);
                callErrorPage(error_msg);
            }
        };

    }

    /**
     * This method handles the data received from the server.
     * It makes an array list of pothole objects.
     *
     * @param response
     */
    private void handleResponse(JSONArray response){
        int length = response.length();
        if(length < BATCH_SIZE){
            isEndOfData = true;
            max_batch_number = batch_number;
        }
        mPotholeList = new ArrayList<>();
        for(int i =0;i<length;i++){
            try {
                JSONObject obj = response.getJSONObject(i);
                Pothole p = new Pothole();
                p.setId(obj.get("id").toString());
                p.setLocation(Double.parseDouble(obj.get("latitude").toString()),
                        Double.parseDouble(obj.get("longitude").toString()),
                        getActivity().getApplicationContext());
                p.setType(obj.get("type").toString());
                p.setDescription(obj.get("description").toString());
                p.setCreatedDate(obj.get("created").toString());
                p.setImageType(obj.get("imagetype").toString());
                mPotholeList.add(p);
            } catch (JSONException e) {
                Log.e("Exception", e.toString());
            }
        }
        attachAdapter();
        chkNextBtnVisibility();
        chkPreviousBtnVisibility();
    }

    /**
     * This method attaches the adapter to list view.
     */
    private void attachAdapter(){
        mAdapter = new PotholesAdapter(mPotholeList,getActivity().getApplicationContext(),this);
        listView.setAdapter(mAdapter);
    }

    /**
     * This method checks the visibility of the next button.
     */
    private  void chkNextBtnVisibility(){
        if (max_batch_number !=0)
            nextButton.setVisibility(View.INVISIBLE);
        else{
            nextButton.setVisibility(View.VISIBLE);
            isEndOfData = false;
        }
    }

    /**
     * This method checks the visibility of the previous button.
     */
    private void chkPreviousBtnVisibility(){
        if(batch_number==0)
            prevButton.setVisibility(View.INVISIBLE);
        else
            prevButton.setVisibility(View.VISIBLE);
    }

    /**
     * This method calls the DetailedViewActivity when an item in the list
     * is clicked.
     * @param pothole
     */
    @Override
    public void itemClicked(Pothole pothole) {
        Intent goToDetailedActivity = new Intent(getActivity(),DetailedViewActivity.class);
        goToDetailedActivity.putExtra("pothole", pothole);
        startActivity(goToDetailedActivity);
    }

    /**
     * This method takes the user to error handling activity which can be a result of a
     * network issue or exception.
     *
     * @param error
     */
    public void callErrorPage(String error){
        Log.i("Error Page", "Called");
        Intent goToErrorPage = new Intent(getActivity(), ErrorHandlingActivity.class);
        goToErrorPage.putExtra("error", error);
        startActivity(goToErrorPage);
    }

}
