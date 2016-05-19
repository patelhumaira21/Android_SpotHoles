/**
 * File Name : PotholesAdapter.java
 * Created by: Humaira Patel
 * Date: 02/4/2016
 *
 */
package edu.sdsu.cs.cs646.assign4;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;


/***
 * This class represents the adapter for the list view of the potholes.
 *
 */
public class PotholesAdapter extends ArrayAdapter<Pothole> {

    private List<Pothole> mPotholeList;
    private ItemClickListener itemClickListener;
    private Context context;

    /***
     * Constructor for the adapter.
     *
     * @param potholeList
     * @param context
     * @param fragment
     */
    public PotholesAdapter(List<Pothole> potholeList,Context context,ListFragment fragment) {
        super(context,-1, potholeList);
        this.context = context;
        this.mPotholeList = potholeList;
        this.itemClickListener = (ItemClickListener)fragment;
    }

    /**
     * Interface for the fragment to implement the item click event
     * on the pothole.
     *
     */
    public interface ItemClickListener {
        void itemClicked(Pothole pothole);
    }


    /**
     *
     * This gets the view for a single item in the list.
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Pothole pothole = mPotholeList.get(position);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if( convertView == null ){
            convertView = inflater.inflate(R.layout.pothole_list_row, parent, false);
        }

        TextView description = (TextView) convertView.findViewById(R.id.description);
        TextView createdDate = (TextView) convertView.findViewById(R.id.createdDate);
        TextView location = (TextView) convertView.findViewById(R.id.location);

        String desc = pothole.getDescription().replaceAll("\\n"," ");
        description.setText(desc);
        createdDate.setText(pothole.getCreatedDate().toString());

        Address address = LocAndConnUtils.getAddress(context, pothole.getLatitude(), pothole.getLongitude());
        String addressString = (address != null)
                ? address.getAddressLine(1)
                : "Invalid latitude/longitude";
        location.setText(addressString);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(pothole);
            }
        });
        return convertView;
    }

}
