package com.example.parcelapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.parcelapp.customerhome.Product;
import com.example.parcelapp.utils.JSONApiUtility;
import com.example.parcelapp.utils.JSONDeleteApiUtility;
import com.example.parcelapp.vendorhome.ManageProduct;
import com.example.parcelapp.vendorhome.UploadProduct;
import com.example.parcelapp.utils.MultipartUtility;
import com.example.parcelapp.vendorhome.VendorDealFragment;
import com.example.parcelapp.vendorhome.VendorNotificationFragment;
import com.example.parcelapp.vendorhome.VendorProductFragment;
import com.example.parcelapp.vendorhome.VendorResolutionFragment;
import com.example.parcelapp.vendorhome.VendorTabAdapter;
import com.example.parcelapp.vendorhome.VendorTransactionFragment;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class VendorActivity extends AppCompatActivity implements UploadProduct, ManageProduct {

    private final String PREF_FILE = BuildConfig.APPLICATION_ID.replace(".", "_");


    TabLayout vendorTabLayout;
    ViewPager2 vendorViewPager;

    public static String POSITION = "POSITION";

    VendorTabAdapter vendorTabAdapter;
    SharedPreferences myPreference;
    SharedPreferences.Editor myEditor;
    MultipartUtility multipartUtility;
    JSONApiUtility prodUpdateUtility;
    JSONDeleteApiUtility delProdUtility;
    String prodUploadUrl, ipAddress, updateProdUrl, delProdUrl;
    String vendor_name, vendor_phone, vendor_email, vend_photo, prod_cat;

    List<String> resBody, prodUpdateResBody, delProdResBody;
    JSONObject resObj, prodUpdateResObj, delProdResObj;
    String resBodyStr, prodUpdateResStr, delProdResStr, delProdResStatus, delProdResData, prodUpdateResStatus, prodUpdateResData, resStatus, resData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_vendor);

        myPreference = this.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);

        vendorTabLayout = findViewById(R.id.vendorTabLayout);
        vendorViewPager = findViewById(R.id.vendorViewPager);

        vendorTabAdapter = new VendorTabAdapter(getSupportFragmentManager(), getLifecycle());

        vendorTabAdapter.addFragment(new VendorProductFragment(VendorActivity.this, myPreference));
        vendorTabAdapter.addFragment(new VendorDealFragment(VendorActivity.this, myPreference));
        vendorTabAdapter.addFragment(new VendorTransactionFragment(VendorActivity.this, myPreference));
        vendorTabAdapter.addFragment(new VendorResolutionFragment(VendorActivity.this, myPreference));
        vendorTabAdapter.addFragment(new VendorNotificationFragment(VendorActivity.this, myPreference));

        vendorViewPager.setAdapter(vendorTabAdapter);

        vendorTabLayout.addTab(vendorTabLayout.newTab().setText("Products"));
        vendorTabLayout.addTab(vendorTabLayout.newTab().setText("Deals"));
        vendorTabLayout.addTab(vendorTabLayout.newTab().setText("Transactions"));
        vendorTabLayout.addTab(vendorTabLayout.newTab().setText("Resolutions"));
        vendorTabLayout.addTab(vendorTabLayout.newTab().setText("Notifications"));

        vendorTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vendorViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        vendorViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                vendorTabLayout.selectTab(vendorTabLayout.getTabAt(position));
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, vendorTabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        vendorViewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void uploadProduct(@NonNull String productName, @NonNull String productModel, int productPrice, int productDiscount, int productQuantity, @NonNull String productDescription, @NonNull JSONObject vendorObj, @NonNull File prod_photo) {

        ipAddress= myPreference.getString("ipAddress", "n/a");
        prodUploadUrl = "http://" + ipAddress + ":7000/parcel_product/product_upload_mobile/";

        try {
            vendor_name = vendorObj.getString("last_name") + " " + vendorObj.getString("first_name");
            vendor_phone = vendorObj.getString("phone_no");
            vendor_email = vendorObj.getString("email");
            vend_photo = vendorObj.getString("vend_photo");
            prod_cat = vendorObj.getString("bus_category");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        if (ipAddress != null && prodUploadUrl != null) {
            new Thread(() -> {

                try {
                    multipartUtility = new MultipartUtility(prodUploadUrl,"UTF-8");
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(VendorActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show());
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }

                try {
                    multipartUtility.addFormFields("vendor_name", vendor_name);
                    multipartUtility.addFormFields("vendor_phone", vendor_phone);
                    multipartUtility.addFormFields("vendor_email", vendor_email);
                    multipartUtility.addFormFields("vend_photo", vend_photo);
                    multipartUtility.addFormFields("prod_cat", prod_cat);
                    multipartUtility.addFormFields("prod_name", productName);
                    multipartUtility.addFormFields("prod_model", productModel);
                    multipartUtility.addFileParts("prod_photo", prod_photo);
                    multipartUtility.addFormFields("prod_price", String.valueOf(productPrice));
                    multipartUtility.addFormFields("prod_qty", String.valueOf(productQuantity));
                    multipartUtility.addFormFields("prod_disc", String.valueOf(productDiscount));
                    multipartUtility.addFormFields("prod_desc", productDescription);
                    multipartUtility.addFormFields("img_base", prod_photo.getName());
                    multipartUtility.addFormFields("upload_date", "dummy_date");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }

                try {
                    resBody = multipartUtility.finish();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }

                if(resBody != null) {
                    for(String line : resBody) {
                        resBodyStr = line;
                    }
                }

                if(resBodyStr != null) {
                    try {
                        resObj = new JSONObject(resBodyStr);
                        resStatus = resObj.getString("status");
                        resData = resObj.getString("data");

                        if(resStatus.equals("success")) {
                            runOnUiThread(() -> {
                                if(!isFinishing()) {
                                    new AlertDialog.Builder(VendorActivity.this)
                                            .setCancelable(true)
                                            .setTitle("Success!")
                                            .setMessage(resData)
                                            .setPositiveButton("Ok", null)
                                            .setNegativeButton("Cancel", null)
                                            .create()
                                            .show();
                                }
                            });
                        } else if(resStatus.equals("error")) {
                            runOnUiThread(() -> {
                                if(!isFinishing()) {
                                    new AlertDialog.Builder(VendorActivity.this)
                                            .setCancelable(true)
                                            .setTitle("Error!")
                                            .setMessage(resData)
                                            .setPositiveButton("Ok", null)
                                            .setNegativeButton("Cancel", null)
                                            .create()
                                            .show();
                                }
                            });
                        } else {
                            runOnUiThread(() -> {
                                if(!isFinishing()) {
                                    new AlertDialog.Builder(VendorActivity.this)
                                            .setCancelable(true)
                                            .setTitle("Error!")
                                            .setMessage("An error occurred...")
                                            .setPositiveButton("Ok", null)
                                            .setNegativeButton("Cancel", null)
                                            .create()
                                            .show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }
                }


            }).start();
        } else {
            runOnUiThread(() -> {
                if(!isFinishing()) {
                    new AlertDialog.Builder(VendorActivity.this)
                            .setCancelable(true)
                            .setTitle("Error!")
                            .setMessage("An error occurred...")
                            .setPositiveButton("Ok", null)
                            .setNegativeButton("Cancel", null)
                            .create()
                            .show();
                }
            });
        }
    }

    @Override
    public void updateProduct(String price, String discount, String quantity, int product_id) {

        ipAddress= myPreference.getString("ipAddress", "n/a");
        updateProdUrl = "http://" + ipAddress + ":7000/parcel_product/update_product_mobile/" + product_id + "/";

        new Thread(() -> {

            try {
                prodUpdateUtility = new JSONApiUtility(updateProdUrl, "POST", "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(VendorActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show());
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

            try{
                prodUpdateUtility.addStrReqBody("prod_price", price);
                prodUpdateUtility.addStrReqBody("prod_qty", quantity);
                prodUpdateUtility.addStrReqBody("prod_disc", discount);
                prodUpdateUtility.addStrReqBody("updated_at", "dummy_date");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

            try {
                prodUpdateResBody = prodUpdateUtility.finish();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

            if(prodUpdateResBody != null) {
                for(String line : prodUpdateResBody) {
                    prodUpdateResStr = line;
                }
            }

            if(prodUpdateResStr != null) {
                try {
                    prodUpdateResObj = new JSONObject(prodUpdateResStr);
                    prodUpdateResStatus = prodUpdateResObj.getString("status");
                    prodUpdateResData = prodUpdateResObj.getString("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }

                if(prodUpdateResStatus.equals("success")) {
                    runOnUiThread(() -> {
                        if(!isFinishing()) {
                            new AlertDialog.Builder(VendorActivity.this)
                                    .setCancelable(true)
                                    .setTitle("Success!")
                                    .setMessage(prodUpdateResData)
                                    .setPositiveButton("Ok", null)
                                    .setNegativeButton("Cancel", null)
                                    .create()
                                    .show();
                        }
                    });
                } else if(prodUpdateResStatus.equals("error")) {
                    runOnUiThread(() -> {
                        if(!isFinishing()) {
                            new AlertDialog.Builder(VendorActivity.this)
                                    .setCancelable(true)
                                    .setTitle("Error!")
                                    .setMessage(prodUpdateResData)
                                    .setPositiveButton("Ok", null)
                                    .setNegativeButton("Cancel", null)
                                    .create()
                                    .show();
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        if(!isFinishing()) {
                            new AlertDialog.Builder(VendorActivity.this)
                                    .setCancelable(true)
                                    .setTitle("Error!")
                                    .setMessage("An error occurred...")
                                    .setPositiveButton("Ok", null)
                                    .setNegativeButton("Cancel", null)
                                    .create()
                                    .show();
                        }
                    });
                }

            }
        }).start();
    }

    @Override
    public void deleteProduct(Product product) {

        new AlertDialog.Builder(VendorActivity.this)
                .setCancelable(true)
                .setTitle("Delete Product!")
                .setMessage("Are you sure, you want to delete " + product.getProd_name() + "?")
                .setPositiveButton("Yes", (dialog, which) -> handleDeleteProduct(product))
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .create()
                .show();
    }

    private void handleDeleteProduct(Product product) {

        ipAddress= myPreference.getString("ipAddress", "n/a");
        delProdUrl = "http://" + ipAddress + ":7000/parcel_product/del_product_mobile/" + product.getProduct_id() + "/";

        new Thread(() -> {
            try {
                delProdUtility = new JSONDeleteApiUtility(delProdUrl);
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(VendorActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show());
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

            try {
                delProdResBody = delProdUtility.finish();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

            if(delProdResBody != null) {
                for(String line : delProdResBody) {
                    delProdResStr = line;
                }
            }

            if(delProdResStr != null) {
                try {
                    delProdResObj = new JSONObject(delProdResStr);
                    delProdResStatus = delProdResObj.getString("status");
                    delProdResData = delProdResObj.getString("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }

                if(delProdResStatus.equals("success")) {
                    runOnUiThread(() -> Toast.makeText(VendorActivity.this, delProdResData, Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(VendorActivity.this, "Error deleting product", Toast.LENGTH_SHORT).show());
                }
            }

        }).start();

    }

}
