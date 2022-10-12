package com.example.parcelapp.customerhome;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.parcelapp.R;
import com.example.parcelapp.utils.CartManagementUtility;
import com.example.parcelapp.utils.JSONApiUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomerCartFragment extends Fragment {

    Context ctx;
    SharedPreferences sharedPref;
    SharedPreferences.Editor myEditor;
    FragmentActivity listener;
    List<Product> products = new ArrayList<>();
    CartItemAdapter cartItemAdapter;
    String ipAddress;
    ListView cartListView;
    Spinner shippingMethod;
    String shipping_method;
    String totItemText, totAmountText;
    TextView totItems, totAmount;
    EditText zipcode;
    Button checkOut;

    CartCheckout cartCheckOut;


    String zip_code;
    CartManagementUtility cartUtils;


    public CustomerCartFragment(Context context, SharedPreferences sharedPreferences) {
        this.ctx = context;
        this.sharedPref = sharedPreferences;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity) {
            this.listener = (FragmentActivity) context;
        }

        try {
            cartCheckOut = (com.example.parcelapp.customerhome.CartCheckout) getActivity();
        } catch (ClassCastException cce) {
            throw new ClassCastException("Error Checking-out, please try again");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.customer_cart_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        ipAddress= sharedPref.getString("ipAddress", "n/a");

        cartUtils = new CartManagementUtility(sharedPref);


        cartItemAdapter = new CartItemAdapter(ctx, products, ipAddress, sharedPref);
        cartListView = view.findViewById(R.id.cartListView);

        totItems = view.findViewById(R.id.totItems);
        totAmount = view.findViewById(R.id.totAmount);
        zipcode = view.findViewById(R.id.zipcode);
        checkOut = view.findViewById(R.id.checkOut);

        zip_code = zipcode.getText().toString();

        try {
            totItemText = "Total Items: " + cartUtils.getTotalItems();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        totItems.setText(totItemText);

        try {
            totAmountText = "Total Amount: " + "₦ " + cartUtils.getTotalAmount();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        totAmount.setText(totAmountText);

        shippingMethod = view.findViewById(R.id.shippingMethod);

        ArrayAdapter<CharSequence> shippingMetAdpt = ArrayAdapter.createFromResource(ctx,
                R.array.shipping_methods, android.R.layout.simple_spinner_item);

        shippingMetAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        shippingMethod.setAdapter(shippingMetAdpt);
        shippingMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(ctx, item, Toast.LENGTH_LONG).show();
                shipping_method = item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        checkOut.setOnClickListener(v -> {
            zip_code = zipcode.getText().toString();
            shipping_method = shippingMethod.getSelectedItem().toString();
            if (!shipping_method.equals("Shipping_Method") && !zip_code.equals("")) {
                cartCheckOut.checkOut(shipping_method, zip_code);
            } else {
                Toast.makeText(ctx, "Choose shipping method and add zip code", Toast.LENGTH_LONG).show();
            }
        });


        try {
            products = cartUtils.getAllFromCart();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        cartItemAdapter.clear();
        cartItemAdapter.addAll(products);
        cartListView.setAdapter(cartItemAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }
}
