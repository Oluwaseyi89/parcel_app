package com.example.parcelapp;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import android.view.Window;
import android.widget.Toast;


import com.example.parcelapp.customerhome.CartCheckout;
import com.example.parcelapp.customerhome.CustomerCartFragment;
import com.example.parcelapp.customerhome.CustomerCatalogueFragment;
import com.example.parcelapp.customerhome.CustomerDeliveriesFragment;
import com.example.parcelapp.customerhome.CustomerHomeFragment;
import com.example.parcelapp.customerhome.CustomerHotDealsFragment;
import com.example.parcelapp.customerhome.CustomerOrdersFragment;
import com.example.parcelapp.customerhome.CustomerResolutionFragment;
import com.example.parcelapp.customerhome.CustomerTabAdapter;

import com.example.parcelapp.customerhome.Product;
import com.example.parcelapp.utils.CartManagementUtility;
import com.example.parcelapp.utils.JSONApiUtility;
import com.google.android.material.tabs.TabLayout;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class CustomerActivity extends AppCompatActivity implements CartCheckout {

    private final String PREF_FILE = BuildConfig.APPLICATION_ID.replace(".", "_");

    TabLayout customerTabLayout;
    ViewPager2 customerViewPager;

    public static String POSITION = "POSITION";

    CustomerTabAdapter customerTabAdapter;
    SharedPreferences myPreference;
    SharedPreferences.Editor myEditor;

    int customer_id, resDataInt;
    int order_id = 0;
    String first_name, last_name, email, phone_no, country, state, street;
    String saveOrderUrl, saveItemUrl, payUrl, customer_name, updateOrderUrl, updateItemUrl, updatePayUrl;
    JSONObject customerJsonData;
    CartManagementUtility cartUtils;
    JSONApiUtility jsonSaveOrder, jsonUpdateOrder, jsonSaveItem, jsonUpdateItem, jsonSavePayment, jsonUpdatePayment;

    List<String> resBodyArr, resOrdBodyArr, resPayBodyArr;
    String responseString, ipAddress;
    String responseOrdString, responsePayString;

    List<Product> cartItems;

    JSONObject jsonResSaveOrder, jsonResUpdateOrder, jsonResSaveItem, jsonResUpdateItem,
            jsonResSavePayment, jsonResUpdatePayment;

    String resDataStr, resStatus;
    String resOrdStatus, resPayStatus;
    String resOrdDataStr, resPayDataStr;

    boolean isOrderError;
    int updatePayCount = 0;
    int payCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_customer);


        myPreference = this.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);

        customerTabLayout = findViewById(R.id.customerTabLayout);
        customerViewPager = findViewById(R.id.customerViewPager);

        customerTabAdapter = new CustomerTabAdapter(getSupportFragmentManager(), getLifecycle());

        cartUtils = new CartManagementUtility(myPreference);

        customerTabAdapter.addFragment(new CustomerHomeFragment(CustomerActivity.this, myPreference));
        customerTabAdapter.addFragment(new CustomerCatalogueFragment(CustomerActivity.this, myPreference));
        customerTabAdapter.addFragment(new CustomerHotDealsFragment(CustomerActivity.this, myPreference));
        customerTabAdapter.addFragment(new CustomerCartFragment(CustomerActivity.this, myPreference));
        customerTabAdapter.addFragment(new CustomerOrdersFragment(CustomerActivity.this, myPreference));
        customerTabAdapter.addFragment(new CustomerDeliveriesFragment(CustomerActivity.this, myPreference));
        customerTabAdapter.addFragment(new CustomerResolutionFragment(CustomerActivity.this, myPreference));

        customerViewPager.setAdapter(customerTabAdapter);


        customerTabLayout.addTab(customerTabLayout.newTab().setText("Home"));
        customerTabLayout.addTab(customerTabLayout.newTab().setText("Catalogue"));
        customerTabLayout.addTab(customerTabLayout.newTab().setText("HotDeals"));
        customerTabLayout.addTab(customerTabLayout.newTab().setText("Cart"));
        customerTabLayout.addTab(customerTabLayout.newTab().setText("Orders"));
        customerTabLayout.addTab(customerTabLayout.newTab().setText("Deliveries"));
        customerTabLayout.addTab(customerTabLayout.newTab().setText("Resolutions"));

        customerTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                customerViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        customerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                customerTabLayout.selectTab(customerTabLayout.getTabAt(position));
            }
        });
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, customerTabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        customerViewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
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
    public void checkOut(@NonNull String shipping_method, @NonNull String zip_code) {

        if(payCount > 0) payCount = 0;
        if(updatePayCount > 0) updatePayCount = 0;
        responseString = ""; responseOrdString = ""; responsePayString = "";
        resStatus = ""; resOrdStatus = ""; resPayStatus = "";

                String customerData = myPreference.getString("customerData", "n/a");
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


                ipAddress= myPreference.getString("ipAddress", "n/a");

                customer_name = last_name + " " + first_name;

                    Toast.makeText(CustomerActivity.this, "Processing your request...", Toast.LENGTH_LONG).show();

                    saveOrderUrl = "http://" + ipAddress + ":7000/parcel_order/order_save/" + last_name + "%20" + first_name + "/";


                    ExecutorService executor = Executors.newCachedThreadPool();
                    Runnable runnable = () -> {

                        try {
                            jsonSaveOrder = new JSONApiUtility(saveOrderUrl, "POST", "UTF-8");
                        } catch (IOException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> Toast.makeText(CustomerActivity.this, "Error connecting with server...", Toast.LENGTH_LONG).show());
                        } catch (NullPointerException npe) {
                            npe.printStackTrace();
                            runOnUiThread(() -> {
                                if(!isFinishing()) {
                                    new AlertDialog.Builder(CustomerActivity.this)
                                            .setCancelable(true)
                                            .setTitle("Error!")
                                            .setMessage(npe.getMessage())
                                            .setPositiveButton("Ok", null)
                                            .setNegativeButton("Cancel", null)
                                            .create()
                                            .show();
                                }
                            });
                        }

                        try {
                            jsonSaveOrder.addIntReqBody("customer_id", customer_id);
                            jsonSaveOrder.addStrReqBody("customer_name", last_name + " " + first_name);
                            jsonSaveOrder.addIntReqBody("total_items", cartUtils.getTotalItems());
                            jsonSaveOrder.addIntReqBody("total_price", cartUtils.getTotalAmount());
                            jsonSaveOrder.addStrReqBody("shipping_method", shipping_method);
                            jsonSaveOrder.addStrReqBody("zip_code", zip_code);
                            jsonSaveOrder.addBoolReqBody("is_customer", true);
                            jsonSaveOrder.addBoolReqBody("is_completed", false);
                            jsonSaveOrder.addStrReqBody("created_at", "dummy_date");
                            jsonSaveOrder.addStrReqBody("updated_at", "dummy_date");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException npe) {
                            npe.printStackTrace();
                        }

                        try {
                            resOrdBodyArr = jsonSaveOrder.finish();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (NullPointerException npe) {
                            npe.printStackTrace();
                        }

                        if (resOrdBodyArr != null) {
                            for(String line : resOrdBodyArr) {
                                responseOrdString = line;
                            }
                        }

                        if(responseOrdString != null) {
                            try {
                                jsonResSaveOrder = new JSONObject(responseOrdString);
                                resOrdStatus = jsonResSaveOrder.getString("status");
                                resDataInt = jsonResSaveOrder.getInt("data");
                                resOrdDataStr = jsonResSaveOrder.getString(("data"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (NullPointerException npe) {
                                npe.printStackTrace();
                            }

                            switch (resOrdStatus) {
                                case "success":
                                    order_id = resDataInt;
                                    myEditor = myPreference.edit();
                                    myEditor.putInt("curOrder", order_id);
                                    myEditor.apply();
                                    break;
                                case "error":
                                    isOrderError = true;
                                    break;
                                case "invalid":
                                    runOnUiThread(() -> {
                                        if (!isFinishing()) {
                                            new AlertDialog.Builder(CustomerActivity.this)
                                                    .setCancelable(true)
                                                    .setTitle("Error!")
                                                    .setMessage(resOrdDataStr)
                                                    .setPositiveButton("Ok", null)
                                                    .setNegativeButton("Cancel", null)
                                                    .create()
                                                    .show();
                                        }
                                    });
                                    break;
                                default:
                                    runOnUiThread(() -> {
                                        if (!isFinishing()) {
                                            new AlertDialog.Builder(CustomerActivity.this)
                                                    .setCancelable(true)
                                                    .setTitle("Error!")
                                                    .setMessage("An error occurred...")
                                                    .setPositiveButton("Ok", null)
                                                    .setNegativeButton("Cancel", null)
                                                    .create()
                                                    .show();
                                        }
                                    });
                                    break;
                            }
                        }

                    };

        Runnable runnablePayment = () -> {
            if(order_id > 0 && !isOrderError) {
                while (payCount == 0) {
                    payCount++;
                    payUrl = "http://" + ipAddress + ":7000/parcel_order/payment_save/" + order_id + "/";
                    try {
                        jsonSavePayment = new JSONApiUtility(payUrl, "POST", "UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(CustomerActivity.this, "Error connecting with server...", Toast.LENGTH_LONG).show());
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    try {
                        jsonSavePayment.addIntReqBody("order_id", order_id);
                        jsonSavePayment.addIntReqBody("customer_id", customer_id);
                        jsonSavePayment.addStrReqBody("customer_name", last_name + " " + first_name);
                        jsonSavePayment.addBoolReqBody("is_customer", true);
                        jsonSavePayment.addIntReqBody("amount", cartUtils.getTotalAmount());
                        jsonSavePayment.addStrReqBody("created_at", "dummy_date");
                        jsonSavePayment.addStrReqBody("updated_at", "dummy_date");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    try {
                        resPayBodyArr = jsonSavePayment.finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    if (resPayBodyArr != null) {
                        for(String line : resPayBodyArr) {
                            responsePayString = line;
                        }
                    }

                    if(responsePayString != null) {
                        try {
                            jsonResSavePayment = new JSONObject(responsePayString);
                            resPayStatus = jsonResSavePayment.getString("status");
                            resPayDataStr = jsonResSavePayment.getString("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException npe) {
                            npe.printStackTrace();
                        }

                        if(resPayStatus.equals("success")) {
                            Toast.makeText(CustomerActivity.this, resPayDataStr, Toast.LENGTH_LONG).show();
                        }
                    }//End of payment
                    if(payCount > 0) break;
                }
            }//End of if condition
        };

        Future<Integer> result = executor.submit(runnable, order_id);

        while(!result.isDone() && !isOrderError) {
            executor.submit(runnablePayment);
        }


                            try {
                                cartItems = cartUtils.getAllFromCart();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (NullPointerException npe) {
                                npe.printStackTrace();
                            }

                            while (result.isDone() && order_id > 0 && !isOrderError) {
                                int count = 0;
                                for(Product item : cartItems) {
                                    Runnable saveEach = () -> {

                                            saveItemUrl = "http://" + ipAddress + ":7000/parcel_order/order_item_save_mobile/" + order_id + "/" + item.getProduct_id() + "/";
                                            try {
                                                jsonSaveItem = new JSONApiUtility(saveItemUrl, "PUT", "UTF-8");
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                runOnUiThread(() -> Toast.makeText(CustomerActivity.this, "Error connecting with server...", Toast.LENGTH_LONG).show());
                                            } catch (NullPointerException npe) {
                                                npe.printStackTrace();
                                            }

                                            try {
                                                jsonSaveItem.addIntReqBody("order_id", order_id);
                                                jsonSaveItem.addIntReqBody("product_id", item.getProduct_id());
                                                jsonSaveItem.addStrReqBody("product_name", item.getProd_name());
                                                jsonSaveItem.addIntReqBody("quantity", item.getAddedQty());
                                                jsonSaveItem.addBoolReqBody("is_customer", true);
                                                jsonSaveItem.addBoolReqBody("is_completed", false);
                                                jsonSaveItem.addStrReqBody("created_at", "dummy_date");
                                                jsonSaveItem.addStrReqBody("updated_at", "dummy_date");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            } catch (NullPointerException npe) {
                                                npe.printStackTrace();
                                            }

                                            try {
                                                resBodyArr = jsonSaveItem.finish();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            } catch (NullPointerException npe) {
                                                npe.printStackTrace();
                                            }

                                            if (resBodyArr != null) {
                                                for(String line : resBodyArr) {
                                                    responseString = line;
                                                }
                                            }

                                            if(responseString != null) {
                                                try {
                                                    jsonResSaveItem = new JSONObject(responseString);
                                                    resStatus = jsonResSaveItem.getString("status");
                                                    resDataStr = jsonResSaveItem.getString("data");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                } catch (NullPointerException npe) {
                                                    npe.printStackTrace();
                                                }

                                                switch (resStatus) {
                                                    case "success":
                                                    case "error":
                                                    case "invalid":
                                                        runOnUiThread(() -> Toast.makeText(CustomerActivity.this, resDataStr, Toast.LENGTH_LONG).show());
                                                        break;
                                                    default:
                                                        runOnUiThread(() -> Toast.makeText(CustomerActivity.this, "An error occurred...", Toast.LENGTH_LONG).show());
                                                        break;
                                                }
                                            }
                                    };

                                        executor.submit(saveEach);

                                } //End of saving items for Loop;
                                count++;
                                Intent intent = new Intent(CustomerActivity.this, PaymentActivity.class);
                                intent.putExtra("customerEmail", email);
                                intent.putExtra("shipping_method", shipping_method);
                                intent.putExtra("order_id", order_id);
                                try {
                                    intent.putExtra("chargeAmount", cartUtils.getTotalAmount());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (NullPointerException npe) {
                                    npe.printStackTrace();
                                }
                                startActivity(intent);
                                if (count > 0) break;
                            }


                   Runnable updateOrder = () -> {
                       if(isOrderError) {
                           updateOrderUrl = "http://" + ipAddress + ":7000/parcel_order/order_update_mobile/" + last_name + "%20" + first_name + "/";

                           try {
                               jsonUpdateOrder = new JSONApiUtility(updateOrderUrl, "PUT", "UTF-8");
                           } catch (IOException e) {
                               e.printStackTrace();
                               runOnUiThread(() -> Toast.makeText(CustomerActivity.this, "Error connecting with server...", Toast.LENGTH_LONG).show());
                           } catch (NullPointerException npe) {
                               npe.printStackTrace();
                               runOnUiThread(() -> {
                                   if(!isFinishing()) {
                                       new AlertDialog.Builder(CustomerActivity.this)
                                               .setCancelable(true)
                                               .setTitle("Error!")
                                               .setMessage(npe.getMessage())
                                               .setPositiveButton("Ok", null)
                                               .setNegativeButton("Cancel", null)
                                               .create()
                                               .show();
                                   }
                               });
                           }

                           try {
                               jsonUpdateOrder.addIntReqBody("customer_id", customer_id);
                               jsonUpdateOrder.addStrReqBody("customer_name", last_name + " " + first_name);
                               jsonUpdateOrder.addIntReqBody("total_items", cartUtils.getTotalItems());
                               jsonUpdateOrder.addIntReqBody("total_price", cartUtils.getTotalAmount());
                               jsonUpdateOrder.addStrReqBody("shipping_method", shipping_method);
                               jsonUpdateOrder.addStrReqBody("zip_code", zip_code);
                               jsonUpdateOrder.addBoolReqBody("is_customer", true);
                               jsonUpdateOrder.addBoolReqBody("is_completed", false);
                               jsonUpdateOrder.addStrReqBody("updated_at", "dummy_date");
                           } catch (JSONException e) {
                               e.printStackTrace();
                           } catch (NullPointerException npe) {
                               npe.printStackTrace();
                           }

                           try {
                               resOrdBodyArr = jsonUpdateOrder.finish();
                           } catch (IOException e) {
                               e.printStackTrace();
                           } catch (NullPointerException npe) {
                               npe.printStackTrace();
                           }

                           if (resOrdBodyArr != null) {
                               for(String line : resOrdBodyArr) {
                                   responseOrdString = line;
                               }
                           }

                           if(responseOrdString != null) {
                               try {
                                   jsonResUpdateOrder = new JSONObject(responseOrdString);
                                   resOrdStatus = jsonResUpdateOrder.getString("status");
                                   resDataInt = jsonResUpdateOrder.getInt("data");
                                   resOrdDataStr = jsonResUpdateOrder.getString(("data"));
                               } catch (JSONException e) {
                                   e.printStackTrace();
                               } catch (NullPointerException npe) {
                                   npe.printStackTrace();
                               }

                               switch (resOrdStatus) {
                                   case "success":
                                       order_id = resDataInt;
                                       myEditor = myPreference.edit();
                                       myEditor.putInt("curOrder", order_id);
                                       myEditor.apply();
                                       break;
                                   case "error":
                                   case "invalid":
                                       runOnUiThread(() -> {
                                           if (!isFinishing()) {
                                               new AlertDialog.Builder(CustomerActivity.this)
                                                       .setCancelable(true)
                                                       .setTitle("Error!")
                                                       .setMessage(resOrdDataStr)
                                                       .setPositiveButton("Ok", null)
                                                       .setNegativeButton("Cancel", null)
                                                       .create()
                                                       .show();
                                           }
                                       });
                                       break;
                                   default:
                                       runOnUiThread(() -> {
                                           if (!isFinishing()) {
                                               new AlertDialog.Builder(CustomerActivity.this)
                                                       .setCancelable(true)
                                                       .setTitle("Error!")
                                                       .setMessage("An error occurred...")
                                                       .setPositiveButton("Ok", null)
                                                       .setNegativeButton("Cancel", null)
                                                       .create()
                                                       .show();
                                           }
                                       });
                                       break;
                               }


                           }
                       }
                   };


        Runnable updatePayment = () -> {
            if(order_id > 0 && isOrderError) {
                while(updatePayCount == 0) {
                    updatePayCount++;
                    updatePayUrl = "http://" + ipAddress + ":7000/parcel_order/payment_update_mobile/" + order_id + "/";
                    try {
                        jsonUpdatePayment = new JSONApiUtility(updatePayUrl, "PUT", "UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(CustomerActivity.this, "Error connecting with server...", Toast.LENGTH_LONG).show());
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    try {
                        jsonUpdatePayment.addIntReqBody("order_id", order_id);
                        jsonUpdatePayment.addIntReqBody("customer_id", customer_id);
                        jsonUpdatePayment.addStrReqBody("customer_name", last_name + " " + first_name);
                        jsonUpdatePayment.addBoolReqBody("is_customer", true);
                        jsonUpdatePayment.addIntReqBody("amount", cartUtils.getTotalAmount());
                        jsonUpdatePayment.addStrReqBody("updated_at", "dummy_date");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    try {
                        resPayBodyArr = jsonUpdatePayment.finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    if (resPayBodyArr != null) {
                        for(String line : resPayBodyArr) {
                            responsePayString = line;
                        }
                    }

                    if(responsePayString != null) {
                        try {
                            jsonResUpdatePayment = new JSONObject(responsePayString);
                            resPayStatus = jsonResUpdatePayment.getString("status");
                            resPayDataStr = jsonResUpdatePayment.getString("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException npe) {
                            npe.printStackTrace();
                        }

                        if(resPayStatus.equals("success")) {
                           Toast.makeText(CustomerActivity.this, resPayDataStr, Toast.LENGTH_LONG).show();
                        } else if(resPayStatus.equals("error")) {
                            runOnUiThread(() -> {
                                if(!isFinishing()) {
                                    new AlertDialog.Builder(CustomerActivity.this)
                                            .setCancelable(true)
                                            .setTitle("Error!")
                                            .setMessage(resPayDataStr)
                                            .setPositiveButton("Ok", null)
                                            .setNegativeButton("Cancel", null)
                                            .create()
                                            .show();
                                }
                            });
                        } else {
                            runOnUiThread(() -> {
                                if(!isFinishing()) {
                                    new AlertDialog.Builder(CustomerActivity.this)
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
                    }//End of payment
                    if(updatePayCount > 0) break;
                }
            }//End of if condition
        };

        while (isOrderError) {
            int count = 0;
            executor.submit(updateOrder);
            count++;
            if(count > 0) break;
        }

        Future<Integer> updateResult = executor.submit(updateOrder, order_id);

        while(!updateResult.isDone() && isOrderError) {
            executor.submit(updatePayment);
        }

        while (updateResult.isDone() && order_id > 0 && isOrderError) {
            int count = 0;
            for(Product item : cartItems) {
                Runnable saveEach = () -> {

                    updateItemUrl = "http://" + ipAddress + ":7000/parcel_order/order_item_update_mobile/" + order_id + "/" + item.getProduct_id() + "/";
                    try {
                        jsonUpdateItem = new JSONApiUtility(updateItemUrl, "PUT", "UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(CustomerActivity.this, "Error connecting with server...", Toast.LENGTH_LONG).show());
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    try {
                        jsonUpdateItem.addIntReqBody("order_id", order_id);
                        jsonUpdateItem.addIntReqBody("product_id", item.getProduct_id());
                        jsonUpdateItem.addStrReqBody("product_name", item.getProd_name());
                        jsonUpdateItem.addIntReqBody("quantity", item.getAddedQty());
                        jsonUpdateItem.addBoolReqBody("is_customer", true);
                        jsonUpdateItem.addBoolReqBody("is_completed", false);
                        jsonUpdateItem.addStrReqBody("updated_at", "dummy_date");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    try {
                        resBodyArr = jsonUpdateItem.finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    if (resBodyArr != null) {
                        for(String line : resBodyArr) {
                            responseString = line;
                        }
                    }

                    if(responseString != null) {
                        try {
                            jsonResUpdateItem = new JSONObject(responseString);
                            resStatus = jsonResUpdateItem.getString("status");
                            resDataStr = jsonResUpdateItem.getString("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException npe) {
                            npe.printStackTrace();
                        }

                        switch (resStatus) {
                            case "success":
                            case "expired":
                            case "invalid":
                            case "non-exist":
                                runOnUiThread(() -> Toast.makeText(CustomerActivity.this, resDataStr, Toast.LENGTH_LONG).show());
                                break;
                            default:
                                runOnUiThread(() -> Toast.makeText(CustomerActivity.this, "An error occurred...", Toast.LENGTH_LONG).show());
                                break;
                        }
                    }
                };
                executor.submit(saveEach);
            } //End of saving items for Loop;
            count++;
            Intent intent = new Intent(CustomerActivity.this, PaymentActivity.class);
            intent.putExtra("customerEmail", email);
            intent.putExtra("shipping_method", shipping_method);
            intent.putExtra("order_id", order_id);
            try {
                intent.putExtra("chargeAmount", cartUtils.getTotalAmount());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }
            startActivity(intent);
            if (count > 0) break;
        }
            executor.shutdown();
        }
    }