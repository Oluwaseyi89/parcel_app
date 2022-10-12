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

public class CartItemAdapter extends ArrayAdapter<Product> {

    Context ctx;
    List<Product> products;
    String ipAddress;
    SharedPreferences myPreference;

    CartItemAdapter(Context context, List<Product> prods, String ipAddress, SharedPreferences myPref) {
        super(context, R.layout.cart_item_row, prods);
        this.ctx = context;
        this.products = prods;
        this.ipAddress = ipAddress;
        this.myPreference = myPref;
    }

    static class CartItemHolder {
        protected ImageView cart_prod_photo;
        protected TextView cart_prod_desc;
        protected TextView cart_prod_name;
        protected TextView cart_prod_model;
        protected TextView cart_prod_rating;
        protected TextView cart_prod_price;
        protected TextView cart_prod_qty;
        protected TextView cart_addedQty;
        protected Button cart_removeFromCart;
        protected Button cart_incrementQty;
        protected Button cart_decrementQty;
        View row;
        Context ctx;
        String ipAddress;
        SharedPreferences myPreference;

        CartItemHolder (View row, Context context, String ipAddress, SharedPreferences myPref) {
            cart_prod_photo = row.findViewById(R.id.cart_prod_photo);
            cart_prod_desc = row.findViewById(R.id.cart_prod_desc);
            cart_prod_name = row.findViewById(R.id.cart_prod_name);
            cart_prod_model = row.findViewById(R.id.cart_prod_model);
            cart_prod_rating = row.findViewById(R.id.cart_prod_rating);
            cart_prod_price = row.findViewById(R.id.cart_prod_price);
            cart_prod_qty = row.findViewById(R.id.cart_prod_qty);
            cart_addedQty = row.findViewById(R.id.cart_addedQty);
            cart_removeFromCart = row.findViewById(R.id.cart_removeFromCart);
            cart_incrementQty = row.findViewById(R.id.cart_incrementQty);
            cart_decrementQty = row.findViewById(R.id.cart_decrementQty);
            this.row = row;
            this.ctx = context;
            this.ipAddress = ipAddress;
            this.myPreference = myPref;
        }

        @SuppressLint("SetTextI18n")
        void populateFrom (Product product) {
            String photoUrl = product.getProd_photo().replace("localhost", ipAddress);
            Glide.with(ctx).load(photoUrl).into(cart_prod_photo);
            cart_prod_desc.setText(product.getProd_desc());
            cart_prod_name.setText(product.getProd_name());
            cart_prod_model.setText(product.getProd_model());
            cart_prod_rating.setText("n/a");
            cart_prod_price.setText("₦ " + product.getProd_price());
            cart_prod_qty.setText("Stock: " + product.getProd_qty());
            cart_addedQty.setText(String.valueOf(product.getAddedQty()));

            CartManagementUtility cartUtils = new CartManagementUtility(myPreference);


            cart_removeFromCart.setOnClickListener(v -> {
                try {
                    if(cartUtils.getProduct(product) != null) {
                        try {
                            cartUtils.removeFromCart(product);
                            product.setAddedQty(0);
                            cart_addedQty.setText("0");
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


            cart_incrementQty.setOnClickListener(v -> {
                int currQty = product.getAddedQty();
                if(currQty >= 0) {
                    currQty++;
                    cart_addedQty.setText(String.valueOf(currQty));
                    product.setAddedQty(currQty);
                    try {
                        cartUtils.addToCart(product);
                        Toast.makeText(ctx, "Cart updated", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            cart_decrementQty.setOnClickListener(v -> {
                int currQty = product.getAddedQty();
                if(currQty > 0) {
                    currQty--;
                    cart_addedQty.setText(String.valueOf(currQty));
                    product.setAddedQty(currQty);
                    try {
                        cartUtils.addToCart(product);
                        Toast.makeText(ctx, "Cart updated", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(ctx, "Cannot decrement Zero Value", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public View getView (int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CartItemHolder holder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = inflater.inflate(R.layout.cart_item_row, parent, false);
            holder = new CartItemHolder(row, ctx, ipAddress, myPreference);
            row.setTag(holder);
        } else {
            holder = (CartItemHolder) row.getTag();
        }
        holder.populateFrom(products.get(position));
        return (row);
    }
}
