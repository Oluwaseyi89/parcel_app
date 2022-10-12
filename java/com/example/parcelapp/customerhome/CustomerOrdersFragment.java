package com.example.parcelapp.customerhome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.parcelapp.R;

public class CustomerOrdersFragment extends Fragment {

    Context ctx;
    TextView cusOrder;
    String text = "Click Orders";
    SharedPreferences sharedPref;

    public CustomerOrdersFragment(Context context, SharedPreferences sharedPreferences) {
        this.ctx = context;
        this.sharedPref = sharedPreferences;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.customer_orders_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        cusOrder = view.findViewById(R.id.cusOrder);
        cusOrder.setText(text);

        cusOrder.setOnClickListener(v -> Toast.makeText(ctx, "This is Orders Fragment", Toast.LENGTH_LONG).show());
    }
}
