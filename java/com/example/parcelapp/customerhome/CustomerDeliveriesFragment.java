package com.example.parcelapp.customerhome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.parcelapp.CustomerActivity;
import com.example.parcelapp.R;
import com.example.parcelapp.utils.JSONApiUtility;
import com.example.parcelapp.utils.JSONGetApiUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomerDeliveriesFragment extends Fragment {

    Context ctx;
    SharedPreferences sharedPref;
    String ipAddress, BASEURL, routeUrl, fetchDeliverableUrl;
    ListView deliveryListView;
    List<Deliverable> deliverables = new ArrayList<>();
    DeliverableAdapter deliverableAdapter;
    JSONGetApiUtility jsonGetApiUtility;
    JSONApiUtility jsonApiOrderUpdate, jsonApiDispatchUpdate;
    List<String> orderUpdateRes, dispatchUpdateRes;
    List<String> responseBody;
    String responseString, orderUpdateResStr, dispatchUpdateResStr;
    ExecutorService executor;
    JSONArray rawJsonDeliverables;
    List<Deliverable> rawDeliverables;
    String first_name, last_name, email, phone_no, country, state, street;
    int customer_id;
    JSONObject customerJsonData;

    public CustomerDeliveriesFragment(Context context, SharedPreferences sharedPreferences) {
        this.ctx = context;
        this.sharedPref = sharedPreferences;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.customer_deliveries_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        executor = Executors.newCachedThreadPool();

        String customerData = sharedPref.getString("customerData", "n/a");
        try {
            customerJsonData = new JSONObject(customerData);
            customer_id = customerJsonData.getInt("id");
            first_name = customerJsonData.getString("first_name");
            last_name = customerJsonData.getString("last_name");
            email = customerJsonData.getString("email");
            phone_no = customerJsonData.getString("phone_no");
            country = customerJsonData.getString("country");
            state = customerJsonData.getString("state");
            street = customerJsonData.getString("street");
        } catch (JSONException jse) {
            jse.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        ipAddress= sharedPref.getString("ipAddress", "n/a");
        BASEURL = "http://" + ipAddress + ":7000/";
        routeUrl = "parcel_dispatch/get_dispatch_from_db/";
        fetchDeliverableUrl = BASEURL + routeUrl;

        deliveryListView = view.findViewById(R.id.deliveryListView);

        new Thread(() -> {
            try {
                jsonGetApiUtility = new JSONGetApiUtility(fetchDeliverableUrl);
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

            if(responseString != null) {
                try {
                    JSONObject rawDeals = new JSONObject(responseString);
                    rawJsonDeliverables = rawDeals.getJSONArray("deals");
                    rawDeliverables = convertAllDeliverables(rawJsonDeliverables);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
            }
        }).start();

        try {
            for(Deliverable deliverable : rawDeliverables) {
                int count = 0;
                String cusEmail = deliverable.getEmail();
                if(cusEmail.equals(email)) {
                    for(DeliverableProduct product : deliverable.getProducts()) {
                        if(!product.getIs_received()) {
                            count++;
                        }
                    }
                    if(count > 0) {
                        deliverables.add(deliverable);
                    } else {
                        updateOrder(deliverable.getOrder_id());
                        updateDispatch(deliverable.getOrder_id());
                    }
                }
            }
            executor.shutdown();
            deliverableAdapter = new DeliverableAdapter(ctx, deliverables, ipAddress, sharedPref);
            deliveryListView.setAdapter(deliverableAdapter);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

    }

    public Deliverable jsonToDeliverable(JSONObject deliverable) throws JSONException {
        Deliverable convertedDeliverable = new Deliverable();
        

        convertedDeliverable.setOrder_id(deliverable.getInt("order_id"));
        convertedDeliverable.setCustomer_id(deliverable.getInt("customer_id"));
        convertedDeliverable.setCustomer_name(deliverable.getString("customer_name"));
        convertedDeliverable.setTotal_items(deliverable.getInt("total_items"));
        convertedDeliverable.setTotal_price(deliverable.getInt("total_price"));
        convertedDeliverable.setIs_customer(deliverable.getBoolean("is_customer"));
        convertedDeliverable.setZip_code(deliverable.getString("zip_code"));
        convertedDeliverable.setShipping_method(deliverable.getString("shipping_method"));
        convertedDeliverable.setIs_delivered(deliverable.getBoolean("is_delivered"));
        convertedDeliverable.setIs_received(deliverable.getBoolean("is_received"));
        convertedDeliverable.setHandled_dispatch(deliverable.getBoolean("handled_dispatch"));
        convertedDeliverable.setCourier_id(deliverable.getInt("courier_id"));
        convertedDeliverable.setCourier_email(deliverable.getString("courier_email"));
        convertedDeliverable.setCourier_phone(deliverable.getString("courier_phone"));
        convertedDeliverable.setCourier_name(deliverable.getString("courier_name"));
        convertedDeliverable.setAddress(deliverable.getString("address"));
        convertedDeliverable.setPhone_no(deliverable.getString("phone_no"));
        convertedDeliverable.setEmail(deliverable.getString("email"));
        convertedDeliverable.setCreated_at(deliverable.getString("created_at"));
        convertedDeliverable.setUpdated_at(deliverable.getString("updated_at"));
        convertedDeliverable.setProducts(jsonArrToDeliverableProducts(deliverable.getJSONArray("products")));


        return convertedDeliverable;
    }

    public DeliverableProduct jsonToDeliverableProduct(JSONObject product) throws JSONException {
        DeliverableProduct convertedProduct = new DeliverableProduct();

        convertedProduct.setOrder_id(product.getInt("order_id"));
        convertedProduct.setProduct_id(product.getInt("product_id"));
        convertedProduct.setProduct_name(product.getString("product_name"));
        convertedProduct.setQuantity(product.getInt("quantity"));
        convertedProduct.setIs_supply_ready(product.getBoolean("is_supply_ready"));
        convertedProduct.setIs_supply_received(product.getBoolean("is_supply_received"));
        convertedProduct.setIs_delivered(product.getBoolean("is_delivered"));
        convertedProduct.setIs_received(product.getBoolean("is_received"));
        convertedProduct.setProd_price(product.getInt("prod_price"));
        convertedProduct.setProd_model(product.getString("prod_model"));
        convertedProduct.setVendor_name(product.getString("vendor_name"));
        convertedProduct.setVendor_phone(product.getString("vendor_phone"));
        convertedProduct.setVendor_email(product.getString("vendor_email"));
        convertedProduct.setProd_photo(product.getString("prod_photo"));
        convertedProduct.setTotal_amount(product.getInt("total_amount"));
        convertedProduct.setVendor_address(product.getString("vendor_address"));
        convertedProduct.setCreated_at(product.getString("created_at"));
        convertedProduct.setUpdated_at(product.getString("updated_at"));

        return convertedProduct;
    }

    public List<DeliverableProduct> jsonArrToDeliverableProducts(JSONArray products) throws JSONException {
        List<DeliverableProduct> convertedProducts = new ArrayList<>();

        for(int i = 0; i < products.length(); i++) {
            DeliverableProduct newProd = jsonToDeliverableProduct(products.getJSONObject(i));
            convertedProducts.add(newProd);
        }

        return convertedProducts;
    }

    public List<Deliverable> convertAllDeliverables(JSONArray deliverables) throws JSONException {
        List<Deliverable> convertedDeliverables = new ArrayList<>();

        for(int i = 0; i < deliverables.length(); i++) {
            Deliverable newDeliverable = jsonToDeliverable(deliverables.getJSONObject(i));
            convertedDeliverables.add(newDeliverable);
        }

        return convertedDeliverables;
    }

    private void updateOrder (int order_id) {

        String updateUrl = "http://" + ipAddress + ":7000/parcel_order/update_order_dispatched_mobile/" + order_id + "/";

        Runnable orderRunnable = () -> {
            try {
                jsonApiOrderUpdate = new JSONApiUtility(updateUrl, "PUT", "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

            try {
                jsonApiOrderUpdate.addBoolReqBody("is_dispatched", true);
                jsonApiOrderUpdate.addStrReqBody("updated_at", "dummy_date");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

            try {
                orderUpdateRes = jsonApiOrderUpdate.finish();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

            if(orderUpdateRes != null) {
                for(String line : orderUpdateRes) {
                    orderUpdateResStr = line;
                }
            }
        };

        executor.submit(orderRunnable);
    }

    private void updateDispatch (int order_id) {

        String updateUrl = "http://" + ipAddress + ":7000/parcel_dispatch/update_dispatch_mobile/" + order_id + "/";

        Runnable dispatchRunnable = () -> {
            try {
                jsonApiDispatchUpdate = new JSONApiUtility(updateUrl, "PUT", "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

            try {
                jsonApiDispatchUpdate.addBoolReqBody("is_delivered", true);
                jsonApiDispatchUpdate.addBoolReqBody("is_received", true);
                jsonApiDispatchUpdate.addStrReqBody("updated_at", "dummy_date");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

            try {
                dispatchUpdateRes = jsonApiDispatchUpdate.finish();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

            if(dispatchUpdateRes != null) {
                for(String line : dispatchUpdateRes) {
                    dispatchUpdateResStr = line;
                }
            }
        };

        executor.submit(dispatchRunnable);
    }
}
