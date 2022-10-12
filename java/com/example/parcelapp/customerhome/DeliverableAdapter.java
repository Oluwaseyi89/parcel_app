package com.example.parcelapp.customerhome;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.parcelapp.R;

import java.util.List;

public class DeliverableAdapter extends ArrayAdapter<Deliverable> {

    Context ctx;
    List<Deliverable> deliverables;
    String ipAddress;
    SharedPreferences myPreference;

    DeliverableAdapter (Context context, List<Deliverable> deliverables, String ipAddress, SharedPreferences myPref) {
        super(context, R.layout.delivery_row, deliverables);
        this.ctx = context;
        this.deliverables = deliverables;
        this.ipAddress = ipAddress;
        this.myPreference = myPref;
    }

    static class DeliverableHolder {
        protected TextView orderId, courier, phone, totDelItems, totDelAmount, delStatus;
        protected ListView deliveryRowList;
        DeliverableProductAdapter deliverableProductAdapter;
        Context ctx;
        String ipAddress;
        SharedPreferences myPref;

        DeliverableHolder (View row, Context context, String ipAddress, SharedPreferences myPref) {
            orderId = row.findViewById(R.id.orderId);
            courier = row.findViewById(R.id.courier);
            phone = row.findViewById(R.id.phone);
            totDelItems = row.findViewById(R.id.totDelItems);
            totDelAmount = row.findViewById(R.id.totDelAmount);
            delStatus = row.findViewById(R.id.delStatus);
            deliveryRowList = row.findViewById(R.id.deliveryRowList);
            this.ctx = context;
            this.ipAddress = ipAddress;
            this.myPref = myPref;
        }

        @SuppressLint("SetTextI18n")
        void populateFrom (Deliverable deliverable) {
            orderId.setText("Order Id: " + deliverable.getOrder_id());
            courier.setText("Courier: " + deliverable.getCourier_name());
            phone.setText("Phone: " + deliverable.getCourier_phone());
            totDelItems.setText("Total Items: " + deliverable.getTotal_items());
            totDelAmount.setText("Total Amount: " + "₦ " + deliverable.getTotal_price());
            delStatus.setText("Status: " + (deliverable.getIs_delivered() ? "Delivered" : "Pending"));

            deliverableProductAdapter = new DeliverableProductAdapter(ctx, deliverable.getProducts(), ipAddress, myPref);

            deliveryRowList.setAdapter(deliverableProductAdapter);
        }
        
    }


    public View getView (int position, View convertView, ViewGroup parent) {
        View row = convertView;
        DeliverableHolder holder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = inflater.inflate(R.layout.delivery_row, parent, false);
            holder = new DeliverableHolder(row, ctx, ipAddress, myPreference);
            row.setTag(holder);
        } else {
            holder = (DeliverableHolder) row.getTag();
        }
        holder.populateFrom(deliverables.get(position));
        return (row);
    }
}
