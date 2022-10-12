package com.example.parcelapp.customerhome;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.parcelapp.R;
import com.example.parcelapp.utils.JSONGetApiUtility;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomerHomeFragment extends Fragment {


    ListView productListView;
    Context ctx;
    FragmentActivity listener;
    List<Product> products = new ArrayList<>();
    SharedPreferences sharedPref;
    String ipAddress;
    ProductAdapter productAdapter;
    String BASEURL, routeUrl, fetchProdUrl;
    JSONGetApiUtility jsonGetApiUtility;
    List<String> responseBody;
    String responseString;
    String status;
    JSONArray data;
    JSONObject resObj;
    JSONObject rawProd;
    String vendor_name, vend_photo, prod_cat, prod_name, prod_photo, prod_desc, prod_model;
    int product_id, prod_price, prod_disc, prod_qty;

    public CustomerHomeFragment(Context context, SharedPreferences sharedPreferences) {
        this.ctx = context;
        this.sharedPref = sharedPreferences;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity) {
            this.listener = (FragmentActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.customer_home_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ipAddress= sharedPref.getString("ipAddress", "n/a");
        BASEURL = "http://" + ipAddress + ":7000/";
        routeUrl = "parcel_product/get_prod/";
        fetchProdUrl = BASEURL + routeUrl;

        productAdapter = new ProductAdapter(ctx, products, ipAddress, sharedPref);
        productListView = view.findViewById(R.id.productListView);


        new Thread(() -> {
            if(fetchProdUrl != null) {
                try {
                    jsonGetApiUtility = new JSONGetApiUtility(fetchProdUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }

                try {
                    responseBody = jsonGetApiUtility.finish();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }

                if (responseBody != null) {
                    for(String line : responseBody) {
                        responseString = line;
                    }
                }

                if (responseString != null) {
                    try {
                        resObj = new JSONObject(responseString);

                        status = resObj.getString("status");

                        data = resObj.getJSONArray("data");

                    } catch (JSONException jse) {
                        jse.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }


                    if(status.equals("success")) {

                        for (int i = 0; i < data.length(); i++) {
                            try {
                                rawProd = data.getJSONObject(i);
                                product_id = rawProd.getInt("id");
                                vendor_name = rawProd.getString("vendor_name");
                                vend_photo = rawProd.getString("vend_photo");
                                prod_cat = rawProd.getString("prod_cat");
                                prod_name = rawProd.getString("prod_name");
                                prod_photo = rawProd.getString("prod_photo");
                                prod_price = rawProd.getInt("prod_price");
                                prod_desc = rawProd.getString("prod_desc");
                                prod_disc = rawProd.getInt("prod_disc");
                                prod_qty = rawProd.getInt("prod_qty");
                                prod_model = rawProd.getString("prod_model");
                                productAdapter.add(new Product(product_id, vendor_name, vend_photo, prod_cat, prod_name,
                                            prod_model, prod_photo, prod_desc, prod_price, prod_disc, prod_qty));

                            } catch (JSONException jse) {
                                jse.printStackTrace();
                            } catch (NullPointerException npe) {
                                npe.printStackTrace();
                            }
                        }

                    }
                }//
            }
        }).start();
        try {
            productListView.setAdapter(productAdapter);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

}
