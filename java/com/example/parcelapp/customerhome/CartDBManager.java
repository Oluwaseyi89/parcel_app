package com.example.parcelapp.customerhome;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;


public class CartDBManager {

    private final SQLiteOpenHelper sqLiteOpenHelper;
    Context ctx;
    private SQLiteDatabase database;
    @SuppressLint("StaticFieldLeak")
    private static CartDBManager instance;

    public CartDBManager(Context context) {
        ctx = context;
        this.sqLiteOpenHelper = new CartSQLiteHelper(context);
    }

    public static CartDBManager getInstance(Context context) {
        if(instance == null) {
            instance = new CartDBManager(context);
        }
        return instance;
    }

    public void open() throws SQLException {
        this.database = sqLiteOpenHelper.getWritableDatabase();
    }

    public void close() {
        if(database != null) {
            this.database.close();
        }
    }


    public void insertProduct(Product product) {

        ContentValues contentValue = new ContentValues();
        contentValue.put(CartSQLiteHelper.PRODUCT_ID, product.getProduct_id());
        contentValue.put(CartSQLiteHelper.VENDOR_NAME, product.getVendor_name());
        contentValue.put(CartSQLiteHelper.VEND_PHOTO, product.getVend_photo());
        contentValue.put(CartSQLiteHelper.PROD_CAT, product.getProd_cat());
        contentValue.put(CartSQLiteHelper.PROD_NAME, product.getProd_name());
        contentValue.put(CartSQLiteHelper.PROD_MODEL, product.getProd_model());
        contentValue.put(CartSQLiteHelper.PROD_PHOTO, product.getProd_photo());
        contentValue.put(CartSQLiteHelper.PROD_DESC, product.getProd_desc());
        contentValue.put(CartSQLiteHelper.PROD_PRICE, product.getProd_price());
        contentValue.put(CartSQLiteHelper.PROD_DISC, product.getProd_disc());
        contentValue.put(CartSQLiteHelper.PROD_QTY, product.getProd_qty());
        contentValue.put(CartSQLiteHelper.ADDED_QTY, product.getAddedQty());

        try {
            database.insert(CartSQLiteHelper.tableName, null, contentValue);
        } catch (SQLException sqe) {
            sqe.printStackTrace();
            this.close();
            Toast.makeText(ctx, "Error inserting", Toast.LENGTH_SHORT).show();
        }

        this.close();
    }

    public ArrayList<Product> fetchProducts() {
        ArrayList<Product> products = new ArrayList<>();
        String[] columns = new String[] {CartSQLiteHelper._ID, CartSQLiteHelper.PRODUCT_ID, CartSQLiteHelper.VENDOR_NAME,
        CartSQLiteHelper.VEND_PHOTO, CartSQLiteHelper.PROD_CAT, CartSQLiteHelper.PROD_NAME, CartSQLiteHelper.PROD_MODEL,
        CartSQLiteHelper.PROD_PHOTO, CartSQLiteHelper.PROD_DESC, CartSQLiteHelper.PROD_PRICE, CartSQLiteHelper.PROD_DISC,
        CartSQLiteHelper.PROD_QTY, CartSQLiteHelper.ADDED_QTY};

        Cursor cursor = database.query(CartSQLiteHelper.tableName, columns, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Product product = readProduct(cursor);
                products.add(product);
                cursor.moveToNext();
            }
            cursor.close();
            return products;
        } else return null;
    }


    public void updateProduct(Product product) {

        ContentValues contentValue = new ContentValues();
        contentValue.put(CartSQLiteHelper.PRODUCT_ID, product.getProduct_id());
        contentValue.put(CartSQLiteHelper.VENDOR_NAME, product.getVendor_name());
        contentValue.put(CartSQLiteHelper.VEND_PHOTO, product.getVend_photo());
        contentValue.put(CartSQLiteHelper.PROD_CAT, product.getProd_cat());
        contentValue.put(CartSQLiteHelper.PROD_NAME, product.getProd_name());
        contentValue.put(CartSQLiteHelper.PROD_MODEL, product.getProd_model());
        contentValue.put(CartSQLiteHelper.PROD_PHOTO, product.getProd_photo());
        contentValue.put(CartSQLiteHelper.PROD_DESC, product.getProd_desc());
        contentValue.put(CartSQLiteHelper.PROD_PRICE, product.getProd_price());
        contentValue.put(CartSQLiteHelper.PROD_DISC, product.getProd_disc());
        contentValue.put(CartSQLiteHelper.PROD_QTY, product.getProd_qty());
        contentValue.put(CartSQLiteHelper.ADDED_QTY, product.getAddedQty());

        if(this.getProduct(product) != null) {
            String sql = "SELECT * FROM " + CartSQLiteHelper.tableName + " WHERE " + CartSQLiteHelper.PRODUCT_ID + " = \"" + product.getProduct_id() + "\";";

            try {
                Cursor c = database.rawQuery(sql, null);
                c.moveToFirst();
                database.update(CartSQLiteHelper.tableName, contentValue, CartSQLiteHelper.PRODUCT_ID + " = ?", new String[]{String.valueOf(product.getProduct_id())});
                c.close();
            } catch (SQLException sqe) {
                sqe.printStackTrace();
                this.close();
                Toast.makeText(ctx, "Error updating", Toast.LENGTH_SHORT).show();
            }

            this.close();
        } else {
            this.open();
            this.insertProduct(product);
        }
    }


    public void deleteProduct(Product product) {

        String sql = "SELECT * FROM " + CartSQLiteHelper.tableName + " WHERE " + CartSQLiteHelper.PRODUCT_ID + " = \"" + product.getProduct_id() + "\";";

        try {
            Cursor c = database.rawQuery(sql, null);
            c.moveToFirst();
            database.delete(CartSQLiteHelper.tableName,CartSQLiteHelper.PRODUCT_ID + " = ?", new String[]{String.valueOf(product.getProduct_id())});
            c.close();
        } catch (SQLException sqe) {
            sqe.printStackTrace();
            Toast.makeText(ctx, "Error deleting", Toast.LENGTH_SHORT).show();
            this.close();
        }

        this.close();
    }

    public Product getProduct(Product product) {

        String sql = "SELECT * FROM " + CartSQLiteHelper.tableName + " WHERE " + CartSQLiteHelper.PRODUCT_ID + " = \"" + product.getProduct_id() + "\";";
        
        Cursor cursor = database.rawQuery(sql, null);

        if(cursor.moveToFirst()) {
            cursor.moveToFirst();
            Product newProduct = null;
            while (!cursor.isAfterLast()) {
                newProduct = readProduct(cursor);
            }
            cursor.close();
            return newProduct;
        } else return null;
    }

    @SuppressLint("Range")
    public static Product readProduct(Cursor cursor) {
        Product product = new Product();

        if(cursor != null) {
            int product_id_index = cursor.getColumnIndex(CartSQLiteHelper.PRODUCT_ID);
            int vendor_name_index = cursor.getColumnIndex(CartSQLiteHelper.VENDOR_NAME);
            int vend_photo_index = cursor.getColumnIndex(CartSQLiteHelper.VEND_PHOTO);
            int prod_cat_index = cursor.getColumnIndex(CartSQLiteHelper.PROD_CAT);
            int prod_name_index = cursor.getColumnIndex(CartSQLiteHelper.PROD_NAME);
            int prod_model_index = cursor.getColumnIndex(CartSQLiteHelper.PROD_MODEL);
            int prod_photo_index = cursor.getColumnIndex(CartSQLiteHelper.PROD_PHOTO);
            int prod_desc_index = cursor.getColumnIndex(CartSQLiteHelper.PROD_DESC);
            int prod_price_index = cursor.getColumnIndex(CartSQLiteHelper.PROD_PRICE);
            int prod_disc_index = cursor.getColumnIndex(CartSQLiteHelper.PROD_DISC);
            int prod_qty_index = cursor.getColumnIndex(CartSQLiteHelper.PROD_QTY);
            int added_qty_index = cursor.getColumnIndex(CartSQLiteHelper.ADDED_QTY);

            product.setProduct_id(cursor.getInt(product_id_index));
            product.setVendor_name(cursor.getString(vendor_name_index));
            product.setVend_photo(cursor.getString(vend_photo_index));
            product.setProd_cat(cursor.getString(prod_cat_index));
            product.setProd_name(cursor.getString(prod_name_index));
            product.setProd_model(cursor.getString(prod_model_index));
            product.setProd_photo(cursor.getString(prod_photo_index));
            product.setProd_desc(cursor.getString(prod_desc_index));
            product.setProd_price(cursor.getInt(prod_price_index));
            product.setProd_disc(cursor.getInt(prod_disc_index));
            product.setProd_qty(cursor.getInt(prod_qty_index));
            product.setAddedQty(cursor.getInt(added_qty_index));

            return product;
        } else return null;
    }
}
