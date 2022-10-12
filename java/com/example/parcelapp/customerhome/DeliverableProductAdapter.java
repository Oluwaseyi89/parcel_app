package com.example.parcelapp.customerhome;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.parcelapp.R;
import com.example.parcelapp.utils.JSONApiUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class DeliverableProductAdapter extends ArrayAdapter<DeliverableProduct> {

    Context ctx;
    List<DeliverableProduct> products;
    String ipAddress;
    SharedPreferences myPreference;

    DeliverableProductAdapter (Context context, List<DeliverableProduct> products, String ipAddress, SharedPreferences myPref) {
        super(context, R.layout.delivery_product, products);
        this.ctx = context;
        this.products = products;
        this.ipAddress = ipAddress;
        this.myPreference = myPref;
    }

    static class DeliverableProductHolder {
        TextView venResponse, courResponse, delRowStatus, delRowOrderId, delRowProduct, delRowModel, delRowPrice,
                    delRowQty, delRowAmount;
        ImageView delProImg;
        CheckBox delReceiveThis;
        Context ctx;
        String ipAddress;
        SharedPreferences myPref;
        JSONApiUtility jsonApiUtility;
        List<String> resBody;
        String resBodyStr;

        DeliverableProductHolder (View row, Context context, String ipAddress, SharedPreferences myPref) {
            venResponse = row.findViewById(R.id.venResponse);
            courResponse = row.findViewById(R.id.courResponse);
            delRowStatus = row.findViewById(R.id.delRowStatus);
            delRowOrderId = row.findViewById(R.id.delRowOrderId);
            delRowProduct = row.findViewById(R.id.delRowProduct);
            delRowModel = row.findViewById(R.id.delRowModel);
            delRowPrice = row.findViewById(R.id.delRowPrice);
            delRowQty = row.findViewById(R.id.delRowQty);
            delRowAmount = row.findViewById(R.id.delRowAmount);
            delProImg = row.findViewById(R.id.delProImg);
            delReceiveThis = row.findViewById(R.id.delReceiveThis);
            this.ctx = context;
            this.ipAddress = ipAddress;
            this.myPref = myPref;

        }

        @SuppressLint("SetTextI18n")
        void populateFrom (DeliverableProduct product) {
            String photoUrl = product.getProd_photo().replace("localhost", ipAddress);
            Glide.with(ctx).load(photoUrl).into(delProImg);
            venResponse.setText("Vendor Response: " + (product.getIs_supply_ready() ? "Supplied" : "Pending"));
            courResponse.setText("Courier Response: " + (product.getIs_supply_received() ? "Received" : "Pending"));
            delRowStatus.setText("Delivery Status: " + (product.getIs_delivered() ? "Delivered": "Pending"));
            delRowOrderId.setText("Order Id: " + product.getOrder_id());
            delRowProduct.setText("Product: " + product.getProduct_name());
            delRowModel.setText("Model: " + product.getProd_model());
            delRowPrice.setText("Price: " + "₦ " + product.getProd_price());
            delRowQty.setText("Quantity: " + product.getQuantity());
            delRowAmount.setText("Amount: " + "₦ " + product.getTotal_amount());
            delReceiveThis.setChecked(product.getIs_received());

            delReceiveThis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String updateUrl =  "http://" + ipAddress + ":7000/parcel_dispatch/update_dispatched_product_mobile/" + product.getOrder_id() + "/" + product.getProduct_id() + "/";
                    if(isChecked) {
                        new Thread(() -> {
                            try {
                                jsonApiUtility = new JSONApiUtility(updateUrl, "PUT", "UTF-8");
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (NullPointerException npe) {
                                npe.printStackTrace();
                            }

                            try {
                                jsonApiUtility.addBoolReqBody("is_received", true);
                                jsonApiUtility.addStrReqBody("updated_at", "dummy_date");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (NullPointerException npe) {
                                npe.printStackTrace();
                            }

                            try {
                                resBody = jsonApiUtility.finish();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (NullPointerException npe) {
                                npe.printStackTrace();
                            }

                            if(resBody != null) {
                                for (String line : resBody) {
                                    resBodyStr = line;
                                }
                            }

                            if(resBodyStr != null) {
                                try {
                                    JSONObject resObj = new JSONObject(resBodyStr);
                                    String status = resObj.getString("status");
                                    String data = resObj.getString("data");


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (NullPointerException npe) {
                                    npe.printStackTrace();
                                }
                            }
                        }).start();
                    } else {
                        new Thread(() -> {

                            try {
                                jsonApiUtility = new JSONApiUtility(updateUrl, "PUT", "UTF-8");
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (NullPointerException npe) {
                                npe.printStackTrace();
                            }

                            try {
                                jsonApiUtility.addBoolReqBody("is_received", false);
                                jsonApiUtility.addStrReqBody("updated_at", "dummy_date");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (NullPointerException npe) {
                                npe.printStackTrace();
                            }

                            try {
                                resBody = jsonApiUtility.finish();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (NullPointerException npe) {
                                npe.printStackTrace();
                            }

                            if(resBody != null) {
                                for (String line : resBody) {
                                    resBodyStr = line;
                                }
                            }

                            if(resBodyStr != null) {
                                try {
                                    JSONObject resObj = new JSONObject(resBodyStr);
                                    String status = resObj.getString("status");
                                    String data = resObj.getString("data");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (NullPointerException npe) {
                                    npe.printStackTrace();
                                }
                            }
                        }).start();
                    }// End of else
                }
            });
        }

    }


    public View getView (int position, View convertView, ViewGroup parent) {
        View row = convertView;
        DeliverableProductHolder holder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = inflater.inflate(R.layout.delivery_product, parent, false);
            holder = new DeliverableProductHolder(row, ctx, ipAddress, myPreference);
            row.setTag(holder);
        } else {
            holder = (DeliverableProductHolder) row.getTag();
        }
        holder.populateFrom(products.get(position));
        return (row);
    }
}
