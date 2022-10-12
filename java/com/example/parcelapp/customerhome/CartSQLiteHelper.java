package com.example.parcelapp.customerhome;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class CartSQLiteHelper extends SQLiteOpenHelper {

    /**
     * Table Name Below:
     */

    public static final String tableName = "CUSTOMER_CART";


    /**
     * Table Columns:
     */

    public static final String _ID = "_id";
    public static final String PRODUCT_ID = "product_id";
    public static final String VENDOR_NAME = "vendor_name";
    public static final String VEND_PHOTO = "vend_photo";
    public static final String PROD_CAT = "prod_cat";
    public static final String PROD_NAME = "prod_name";
    public static final String PROD_MODEL = "prod_model";
    public static final String PROD_PHOTO = "prod_photo";
    public static final String PROD_DESC = "prod_desc";
    public static final String PROD_PRICE = "prod_price";
    public static final String PROD_DISC = "prod_disc";
    public static final String PROD_QTY = "prod_qty";
    public static final String ADDED_QTY = "addedQty";


    /**
     * Table Creating Variable
     */

    private static final String CREATE_TABLE = "create table " + tableName + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PRODUCT_ID + " INTEGER, "
            + VENDOR_NAME + " TEXT, "
            + VEND_PHOTO + " TEXT, "
            + PROD_CAT + " TEXT, "
            + PROD_NAME + " TEXT, "
            + PROD_MODEL + " TEXT, "
            + PROD_PHOTO + " TEXT, "
            + PROD_DESC + " TEXT, "
            + PROD_PRICE + " INTEGER, "
            + PROD_DISC + " INTEGER, "
            + PROD_QTY + " INTEGER, "
            + ADDED_QTY + " INTEGER"
            + ");";

    /**
     * Constructor superclass parameters:
     */

    static final String dbName = "PARCEL_CART.DB";
    static final int dbVersion = 1;

    /**
     * Constructor of the Class:
     */

    public CartSQLiteHelper(@Nullable Context context) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName + ";");
            onCreate(db);
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}
