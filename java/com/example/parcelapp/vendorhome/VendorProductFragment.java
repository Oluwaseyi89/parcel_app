package com.example.parcelapp.vendorhome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.example.parcelapp.R;
import com.example.parcelapp.customerhome.Product;
import com.example.parcelapp.utils.ImageLoaderHelper;
import com.example.parcelapp.utils.JSONGetApiUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VendorProductFragment extends Fragment {

    Context ctx;
    SharedPreferences sharedPref;
    FragmentActivity listener;
    UploadProduct productHelper;
    ImageLoaderHelper imageLoaderHelper = new ImageLoaderHelper();

    ActivityResultLauncher<Intent> imageActivityResultLauncher;

    EditText venProdName, venProdModel, venProdPrice, venProdDiscount, venProdQuantity, venProdDescription;
    TextView venProdCat, prodImgPath;
    ImageView prodImg;
    Button uploadProduct;
    String photoPath, prod_cat, prod_name, prod_model, prod_price, prod_qty, prod_disc, prod_desc;
    File prod_photo;
    String vendorData;
    JSONObject vendorObject;

    ListView venProdListView;
    List<Product> vendorProducts = new ArrayList<>();
    VendorProductAdapter vendorProductAdapter;
    String ipAddress;

    JSONObject rawProd, resObj;
    JSONArray data;

    JSONGetApiUtility jsonGetApiUtility;

    int prod_id, prodPrice, prodDisc, prodQty;
    String responseString;
    List<String> responseBody;
    String vend_name, vend_photo, prodCat, prodName, prodPhoto, prodDesc, prodModel;
    String fetchProdUrl;
    String vendorEmail;
    ManageProduct manageProduct;


    public VendorProductFragment(Context context, SharedPreferences sharedPreferences) {
        this.ctx = context;
        this.sharedPref = sharedPreferences;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity) {
            this.listener = (FragmentActivity) context;
        }

        try {
            productHelper = (UploadProduct) getActivity();
        } catch (ClassCastException cce) {
            throw new ClassCastException("Error uploading the product, please try again");
        }

        try {
            manageProduct = (ManageProduct) getActivity();
        } catch (ClassCastException cce) {
            throw new ClassCastException("Error managing the product, please try again");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vendor_product_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        Uri imgUri = data.getData();
                        photoPath = imageLoaderHelper.getRealPathFromURI(imgUri, ctx);
                        Glide.with(ctx).load(imgUri).into(prodImg);
                    }
                });

        vendorData = sharedPref.getString("vendorData", "n/a");

        try {
            vendorObject = new JSONObject(vendorData);
            prod_cat = vendorObject.getString("bus_category");
            vendorEmail = vendorObject.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }


        prodImgPath = view.findViewById(R.id.prodImgPath);
        prodImg = view.findViewById(R.id.prodImg);
        venProdName = view.findViewById(R.id.venProdName);
        venProdModel = view.findViewById(R.id.venProdModel);
        venProdPrice = view.findViewById(R.id.venProdPrice);
        venProdDiscount = view.findViewById(R.id.venProdDiscount);
        venProdQuantity = view.findViewById(R.id.venProdQuantity);
        venProdDescription = view.findViewById(R.id.venProdDescription);
        venProdCat = view.findViewById(R.id.venProdCat);
        uploadProduct = view.findViewById(R.id.uploadProduct);
        venProdListView = view.findViewById(R.id.venProdListView);

        ipAddress = sharedPref.getString("ipAddress", "n/a");

        vendorProductAdapter = new VendorProductAdapter(ctx, vendorProducts, ipAddress, sharedPref, manageProduct);
        getVendorProducts();
        venProdListView.setAdapter(vendorProductAdapter);

        venProdCat.setText(prod_cat);


        prodImgPath.setOnClickListener(handleSelectPhoto);

        uploadProduct.setOnClickListener(v -> {

            vendorData = sharedPref.getString("vendorData", "n/a");

            try {
                vendorObject = new JSONObject(vendorData);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }


            prod_name = venProdName.getText().toString(); prod_model = venProdModel.getText().toString();
            prod_price = venProdPrice.getText().toString(); prod_disc = venProdDiscount.getText().toString();
            prod_qty = venProdQuantity.getText().toString(); prod_desc = venProdDescription.getText().toString();

            prod_photo = photoPath != null ? new File(photoPath) : null;

            String[] inputs = {prod_name, prod_model, prod_price, prod_disc, prod_qty, prod_desc};

            int errorCount = checkBlankInput(inputs);

            if (errorCount == 0 && prod_photo != null) {
                productHelper.uploadProduct(prod_name, prod_model, Integer.parseInt(prod_price), Integer.parseInt(prod_disc),
                        Integer.parseInt(prod_qty), prod_desc, vendorObject, prod_photo);
            } else {
                if(prod_photo == null) {
                    Toast.makeText(ctx, "Upload product photo", Toast.LENGTH_LONG).show();
                } else if(errorCount > 0) {
                    String message = errorCount + " input fields are left blank";
                    Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    private final View.OnClickListener handleSelectPhoto = v -> {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        imageActivityResultLauncher.launch(intent);
    };

    private int checkBlankInput(String[] inputs) {
        int count = 0;
        for(String input : inputs) {
            if(input.equals("")) count++;
        }
        return  count;
    }

    private void getVendorProducts() {

        fetchProdUrl = "http://" + ipAddress + ":7000/parcel_product/get_dist_ven_product/" + vendorEmail + "/";

        new Thread(() -> {
            if(fetchProdUrl != null) {
                try {
                    jsonGetApiUtility = new JSONGetApiUtility(fetchProdUrl);
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

                if (responseString != null) {
                    try {
                        resObj = new JSONObject(responseString);
                        data = resObj.getJSONArray("data");

                    } catch (JSONException jse) {
                        jse.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                        for (int i = 0; i < data.length(); i++) {
                            try {
                                rawProd = data.getJSONObject(i);
                                prod_id = rawProd.getInt("id");
                                vend_name = rawProd.getString("vendor_name");
                                vend_photo = rawProd.getString("vend_photo");
                                prodCat = rawProd.getString("prod_cat");
                                prodName = rawProd.getString("prod_name");
                                prodPhoto = rawProd.getString("prod_photo");
                                prodPrice = rawProd.getInt("prod_price");
                                prodDesc = rawProd.getString("prod_desc");
                                prodDisc = rawProd.getInt("prod_disc");
                                prodQty = rawProd.getInt("prod_qty");
                                prodModel = rawProd.getString("prod_model");
                                vendorProductAdapter.add(new Product(prod_id, vend_name, vend_photo, prodCat, prodName,
                                        prodModel, prodPhoto, prodDesc, prodPrice, prodDisc, prodQty));

                            } catch (JSONException jse) {
                                jse.printStackTrace();
                            } catch (NullPointerException npe) {
                                npe.printStackTrace();
                            }
                        }


                }//
            }
        }).start();

    }
}
