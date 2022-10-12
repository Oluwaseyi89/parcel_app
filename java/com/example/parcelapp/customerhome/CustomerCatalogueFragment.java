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

public class CustomerCatalogueFragment extends Fragment {

    Context ctx;
    TextView cusCat;
    String text = "Click Catalogue";
    SharedPreferences sharedPref;

    public CustomerCatalogueFragment(Context context, SharedPreferences sharedPreferences) {
        this.ctx = context;
        this.sharedPref = sharedPreferences;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.customer_catalogue_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        cusCat = view.findViewById(R.id.cusCat);
        cusCat.setText(text);

        cusCat.setOnClickListener(v -> Toast.makeText(ctx, "This is Catalogue Fragment", Toast.LENGTH_LONG).show());
    }
}
