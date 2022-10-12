package com.example.parcelapp.customerhome;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.parcelapp.R;
import com.example.parcelapp.utils.CartManagementUtility;

import org.json.JSONException;

import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {

    Context ctx;
    List<Product> products;
    String ipAddress;
    SharedPreferences myPreference;

    ProductAdapter (Context context, List<Product> prods, String ipAddress, SharedPreferences myPref) {
        super(context, R.layout.product_row, prods);
        this.ctx = context;
        this.products = prods;
        this.ipAddress = ipAddress;
        this.myPreference = myPref;
    }

    static class ProdHolder {
        protected ImageView prod_photo;
        protected TextView prod_desc;
        protected TextView prod_name;
        protected TextView prod_model;
        protected TextView prod_rating;
        protected TextView prod_price;
        protected TextView prod_qty;
        protected TextView addedQty;
        protected Button buyNow;
        protected Button addToCart;
        protected Button removeFromCart;
        protected Button incrementQty;
        protected Button decrementQty;
        View row;
        Context ctx;
        String ipAddress;
        SharedPreferences myPreference;

        ProdHolder (View row, Context context, String ipAddress, SharedPreferences myPref) {
            prod_photo = row.findViewById(R.id.prod_photo);
            prod_desc = row.findViewById(R.id.prod_desc);
            prod_name = row.findViewById(R.id.prod_name);
            prod_model = row.findViewById(R.id.prod_model);
            prod_rating = row.findViewById(R.id.prod_rating);
            prod_price = row.findViewById(R.id.prod_price);
            prod_qty = row.findViewById(R.id.prod_qty);
            addedQty = row.findViewById(R.id.addedQty);
            buyNow = row.findViewById(R.id.buyNow);
            addToCart = row.findViewById(R.id.addToCart);
            removeFromCart = row.findViewById(R.id.removeFromCart);
            incrementQty = row.findViewById(R.id.incrementQty);
            decrementQty = row.findViewById(R.id.decrementQty);
            this.row = row;
            this.ctx = context;
            this.ipAddress = ipAddress;
            this.myPreference = myPref;
        }

        @SuppressLint("SetTextI18n")
        void populateFrom (Product product) {
            String photoUrl = product.getProd_photo().replace("localhost", ipAddress);
            Glide.with(ctx).load(photoUrl).into(prod_photo);
            prod_desc.setText(product.getProd_desc());
            prod_name.setText(product.getProd_name());
            prod_model.setText(product.getProd_model());
            prod_rating.setText("n/a");
            prod_price.setText("₦ " + product.getProd_price());
            prod_qty.setText("Stock: " + product.getProd_qty());

            CartManagementUtility cartUtils = new CartManagementUtility(myPreference);

            try {
                Product fetchedProd = cartUtils.getProduct(product);
                if(fetchedProd != null) {
                    addedQty.setText(String.valueOf(fetchedProd.getAddedQty()));
                    product.setAddedQty(fetchedProd.getAddedQty());
                } else addedQty.setText("0");
            } catch (JSONException jse) {
                jse.printStackTrace();
            }


            buyNow.setOnClickListener(v -> {
                String message = "Product with Id of " + product.getProduct_id() + " is bought now";
                Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
            });

            addToCart.setOnClickListener(v -> {
                if(product.getAddedQty() == 0) {
                    Toast.makeText(ctx, "Cannot add Zero quantity to Cart", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        cartUtils.addToCart(product);
                        Toast.makeText(ctx, product.getAddedQty() + " " + product.getProd_name() + " have been added to Cart", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        Toast.makeText(ctx, "Error adding product to Cart", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });

            removeFromCart.setOnClickListener(v -> {
                try {
                    if(cartUtils.getProduct(product) != null) {
                        try {
                            cartUtils.removeFromCart(product);
                            product.setAddedQty(0);
                            addedQty.setText("0");
                            Toast.makeText(ctx, "Product removed from Cart", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            Toast.makeText(ctx, "Error removing product from Cart", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    } else Toast.makeText(ctx, "Product was never in cart originally", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });


            incrementQty.setOnClickListener(v -> {
                int currQty = product.getAddedQty();
                if(currQty >= 0) {
                    currQty++;
                    addedQty.setText(String.valueOf(currQty));
                    product.setAddedQty(currQty);
                }
            });

            decrementQty.setOnClickListener(v -> {
                int currQty = product.getAddedQty();
                if(currQty > 0) {
                    currQty--;
                    addedQty.setText(String.valueOf(currQty));
                    product.setAddedQty(currQty);
                } else {
                    Toast.makeText(ctx, "Cannot decrement Zero Value", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public View getView (int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ProdHolder holder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = inflater.inflate(R.layout.product_row, parent, false);
            holder = new ProdHolder(row, ctx, ipAddress, myPreference);
            row.setTag(holder);
        } else {
            holder = (ProdHolder) row.getTag();
        }
        holder.populateFrom(products.get(position));
        return (row);
    }
}

