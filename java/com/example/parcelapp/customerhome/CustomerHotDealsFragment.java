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

public class CustomerHotDealsFragment extends Fragment {

    Context ctx;
    TextView cusHot;
    String text = "Click Hotdeals";
    SharedPreferences sharedPref;

    public CustomerHotDealsFragment(Context context, SharedPreferences sharedPreferences) {
        this.ctx = context;
        this.sharedPref = sharedPreferences;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.customer_hotdeals_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        cusHot = view.findViewById(R.id.cusHot);
        cusHot.setText(text);

        cusHot.setOnClickListener(v -> Toast.makeText(ctx, "This is Hotdeals Fragment", Toast.LENGTH_LONG).show());
    }


}
