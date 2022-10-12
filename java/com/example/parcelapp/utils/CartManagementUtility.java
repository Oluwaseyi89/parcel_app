package com.example.parcelapp.utils;

import android.content.SharedPreferences;

import com.example.parcelapp.customerhome.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartManagementUtility {
    SharedPreferences myPreference;
    SharedPreferences.Editor myEditor;
    private final String CART = "CART";

    public CartManagementUtility(SharedPreferences myPref) {
        this.myPreference = myPref;
    }

    public JSONObject productToJson(Product product) throws JSONException {

        JSONObject jsonProd = new JSONObject();

        jsonProd.put("product_id", product.getProduct_id());
        jsonProd.put("vendor_name", product.getVendor_name());
        jsonProd.put("vend_photo", product.getVend_photo());
        jsonProd.put("prod_cat", product.getProd_cat());
        jsonProd.put("prod_name", product.getProd_name());
        jsonProd.put("prod_model", product.getProd_model());
        jsonProd.put("prod_photo", product.getProd_photo());
        jsonProd.put("prod_desc", product.getProd_desc());
        jsonProd.put("prod_price", product.getProd_price());
        jsonProd.put("prod_disc", product.getProd_disc());
        jsonProd.put("prod_qty", product.getProd_qty());
        jsonProd.put("addedQty", product.getAddedQty());

        return jsonProd;
    }

    public Product jsonToProduct(JSONObject jsonProd) throws JSONException {

        Product product = new Product();

        product.setProduct_id(jsonProd.getInt("product_id"));
        product.setVendor_name(jsonProd.getString("vendor_name"));
        product.setVend_photo(jsonProd.getString("vend_photo"));
        product.setProd_cat(jsonProd.getString("prod_cat"));
        product.setProd_name(jsonProd.getString("prod_name"));
        product.setProd_model(jsonProd.getString("prod_model"));
        product.setProd_photo(jsonProd.getString("prod_photo"));
        product.setProd_desc(jsonProd.getString("prod_desc"));
        product.setProd_price(jsonProd.getInt("prod_price"));
        product.setProd_disc(jsonProd.getInt("prod_disc"));
        product.setProd_qty(jsonProd.getInt("prod_qty"));
        product.setAddedQty(jsonProd.getInt("addedQty"));

        return product;
    }

    public boolean checkCart(Product product) throws JSONException {
        String strCart = myPreference.getString(CART, "n/a");
        JSONObject rawProd;
        Product existProd;
        int count = 0;
        if(strCart.equals("n/a")) {
            return false;
        } else {
            JSONArray cartArr = new JSONArray(strCart);
            for(int i = 0; i < cartArr.length(); i++) {
                rawProd = cartArr.getJSONObject(i);
                existProd = jsonToProduct(rawProd);
                if(existProd.getProduct_id() == product.getProduct_id()) count++;
            }
            return count > 0;
        }
    }

    public void updateCart(Product product) throws JSONException {
        String strCart = myPreference.getString(CART, "n/a");
        JSONObject jsonProd = productToJson(product);
        JSONArray originalArr = new JSONArray(strCart);
        JSONArray newArr = new JSONArray();
        JSONObject rawProd;
        Product existProd;
        for(int i = 0; i < originalArr.length(); i++) {
            rawProd = originalArr.getJSONObject(i);
            existProd = jsonToProduct(rawProd);
            if(existProd.getProduct_id() != product.getProduct_id()) {
                newArr.put(rawProd);
            }
        }
        newArr.put(jsonProd);
        myEditor = myPreference.edit();
        myEditor.putString(CART, newArr.toString());
        myEditor.apply();
    }

    public void addToNewCart(Product product) throws JSONException {
        JSONObject rawProd = productToJson(product);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(rawProd);

        myEditor = myPreference.edit();
        myEditor.putString(CART, jsonArray.toString());
        myEditor.apply();
    }

    public void addToCart(Product product) throws JSONException {

        String strCart = myPreference.getString(CART, "n/a");


        if(checkCart(product)) {
            updateCart(product);
        } else if(strCart.equals("n/a")) {
            addToNewCart(product);
        } else {
            JSONObject jsonProd = productToJson(product);
            JSONArray originalArr = new JSONArray(strCart);
            JSONArray newArr = new JSONArray();
            JSONObject rawProd;

            for(int i = 0; i < originalArr.length(); i++) {
                rawProd = originalArr.getJSONObject(i);
                newArr.put(rawProd);
            }
            newArr.put(jsonProd);
            myEditor = myPreference.edit();
            myEditor.putString(CART, newArr.toString());
            myEditor.apply();
        }
    }

    public void removeFromCart(Product product) throws JSONException {
        String strCart = myPreference.getString(CART, "n/a");
        JSONArray originalArr = new JSONArray(strCart);
        JSONArray newArr = new JSONArray();
        JSONObject rawProd;
        Product existProd;
        for(int i = 0; i < originalArr.length(); i++) {
            rawProd = originalArr.getJSONObject(i);
            existProd = jsonToProduct(rawProd);
            if(existProd.getProduct_id() != product.getProduct_id()) {
                newArr.put(rawProd);
            }
        }
        myEditor = myPreference.edit();
        myEditor.putString(CART, newArr.toString());
        myEditor.apply();
    }

    public List<Product> getAllFromCart() throws JSONException {

        String strCart = myPreference.getString(CART, "n/a");
        List<Product> prods = new ArrayList<>();

        if(!strCart.equals("n/a")) {
            List<Product> products = new ArrayList<>();
            JSONArray originalArr = new JSONArray(strCart);
            JSONObject rawProd;
            Product eachProd;

            for(int i = 0; i < originalArr.length(); i++) {
                rawProd = originalArr.getJSONObject(i);
                eachProd = jsonToProduct(rawProd);
                products.add(eachProd);
            }
            return products;
        } else return prods;
    }

    public Product getProduct(Product product) throws JSONException {
        String strCart = myPreference.getString(CART, "n/a");
        Product fetchedProd = new Product();
        int count = 0;
        if(strCart.equals("n/a")) {
            return null;
        } else {
            JSONArray originalArr = new JSONArray(strCart);

            JSONObject rawProd;
            Product foundProd;
            for(int i = 0; i < originalArr.length(); i++) {
                rawProd = originalArr.getJSONObject(i);
                foundProd = jsonToProduct(rawProd);
                if(foundProd.getProduct_id() == product.getProduct_id()) {
                    fetchedProd = foundProd;
                    count++;
                }
            }
            if(count > 0) return fetchedProd;
            else return null;
        }
    }

    public int getTotalItems() throws JSONException {
        int totItems = 0;

        List<Product> products = getAllFromCart();

        for(Product product : products) {
            totItems += product.getAddedQty();
        }

        return totItems;
    }

    public int getTotalAmount() throws JSONException {
        int totAmount = 0;

        List<Product> products = getAllFromCart();

        for(Product product : products) {

            int added_qty = product.getAddedQty();
            int prod_price = product.getProd_price();
            int amount = added_qty * prod_price;

            totAmount += amount;
        }

        return totAmount;
    }
}
