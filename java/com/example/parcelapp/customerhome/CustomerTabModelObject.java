package com.example.parcelapp.customerhome;

import com.example.parcelapp.R;

public enum CustomerTabModelObject {
    HOME(R.string.customer_home, R.layout.customer_home_fragment),
    CATALOGUE(R.string.customer_catalogue, R.layout.customer_catalogue_fragment),
    HOTDEALS(R.string.customer_hot_deals, R.layout.customer_hotdeals_fragment),
    CART(R.string.customer_cart, R.layout.customer_cart_fragment),
    ORDERS(R.string.customer_orders, R.layout.customer_orders_fragment),
    DELIVERIES(R.string.customer_deliveries, R.layout.customer_deliveries_fragment);
    private int titleResId;
    private int layoutResId;

    CustomerTabModelObject(int titResId, int layResId) {
        titleResId = titResId;
        layoutResId = layResId;
    }

    public int getTitleResId() {
        return titleResId;
    }

    public int getLayoutResId() {
        return layoutResId;
    }
}
