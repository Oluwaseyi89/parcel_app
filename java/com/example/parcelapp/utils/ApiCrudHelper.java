package com.example.parcelapp.utils;

import android.content.Context;
import android.util.Log;

import org.chromium.net.CronetEngine;
import org.chromium.net.CronetException;
import org.chromium.net.UploadDataProvider;
import org.chromium.net.UploadDataProviders;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.security.auth.login.LoginException;

public class ApiCrudHelper {

    CronetEngine.Builder myBuilder;
    CronetEngine cronetEngine;
    Executor executor;
    UrlRequest.Builder requestBuilder;
    UrlRequest request;
    Map<String, List<String>> responseHeaders;
    String responseBody = "";
    String responseBodyString;


    private static byte[] convertStringToBytes (String payload) {
        byte[] bytes;
        ByteBuffer byteBuffer = ByteBuffer.wrap(payload.getBytes());
        if(byteBuffer.hasArray()) {
            bytes = byteBuffer.array();
        } else {
            bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
        }
        return bytes;
    }

    private static UploadDataProvider generateUploadDataProvider (String payload) {
        byte[] bytes = convertStringToBytes(payload);
        return UploadDataProviders.create(bytes);
    }

    public String get(String url, String[][]headers, Context context) {
        myBuilder = new CronetEngine.Builder(context);
        cronetEngine = myBuilder.build();
        executor = Executors.newSingleThreadExecutor();
        requestBuilder = cronetEngine.newUrlRequestBuilder(url, new APIUrlRequestCallback(), executor)
                .setHttpMethod("GET");

        for (String[] header : headers) {
            requestBuilder.addHeader(header[0], header[1]);
        }

        request = requestBuilder.build();
        request.start();
        return responseBody;
    }

    public String get(String url, Context context) {
        myBuilder = new CronetEngine.Builder(context);
        cronetEngine = myBuilder.build();
        executor = Executors.newSingleThreadExecutor();
        requestBuilder = cronetEngine.newUrlRequestBuilder(url, new APIUrlRequestCallback(), executor)
                .setHttpMethod("GET");

        request = requestBuilder.build();
        request.start();
        return responseBody;
    }

    public String post(String url, String payload, String[][] headers, Context context) {
        myBuilder = new CronetEngine.Builder(context);
        cronetEngine = myBuilder.build();
        executor = Executors.newSingleThreadExecutor();
        requestBuilder = cronetEngine.newUrlRequestBuilder(url, new APIUrlRequestCallback(), executor)
                .setHttpMethod("POST")
                .setUploadDataProvider(generateUploadDataProvider(payload), executor);

        for (String[] header : headers) {
            requestBuilder.addHeader(header[0], header[1]);
        }

        request = requestBuilder.build();
        request.start();
        return responseBody;
    }

    public String put(String url, String payload, String[][] headers, Context context) {
        myBuilder = new CronetEngine.Builder(context);
        cronetEngine = myBuilder.build();
        executor = Executors.newSingleThreadExecutor();
        requestBuilder = cronetEngine.newUrlRequestBuilder(url, new APIUrlRequestCallback(), executor)
                .setHttpMethod("PUT")
                .setUploadDataProvider(generateUploadDataProvider(payload), executor);

        for (String[] header : headers) {
            requestBuilder.addHeader(header[0], header[1]);
        }

        request = requestBuilder.build();
        request.start();
        return responseBody;
    }

    public String delete(String url, String payload, String[][] headers, Context context) {
        myBuilder = new CronetEngine.Builder(context);
        cronetEngine = myBuilder.build();
        executor = Executors.newSingleThreadExecutor();
        requestBuilder = cronetEngine.newUrlRequestBuilder(url, new APIUrlRequestCallback(), executor)
                .setHttpMethod("DELETE")
                .setUploadDataProvider(generateUploadDataProvider(payload), executor);

        for (String[] header : headers) {
            requestBuilder.addHeader(header[0], header[1]);
        }

        request = requestBuilder.build();
        request.start();
        return responseBody;
    }



    class APIUrlRequestCallback extends UrlRequest.Callback {

        private static final String TAG = "APIUrlRequestCallback";

        @Override
        public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) {
            Log.i(TAG, "onRedirectReceived method called");
            if((info.getHttpStatusCode() > 300) && (info.getHttpStatusCode() < 400)) {
                request.followRedirect();
            } else {
                request.cancel();
            }
        }

        @Override
        public void onResponseStarted(UrlRequest request, UrlResponseInfo info) {
            Log.i(TAG, "onResponseStarted method called");
            int httpStatusCode = info.getHttpStatusCode();
            if(httpStatusCode == 200) {
                request.read(ByteBuffer.allocateDirect(102400));
            } else if(httpStatusCode == 503) {
                request.read(ByteBuffer.allocateDirect(102400));
            }
            responseHeaders = info.getAllHeaders();
        }

        @Override
        public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) {
            Log.i(TAG, "onReadCompleted method called");
            byte[] bytes;
            if(byteBuffer.hasArray()) {
                bytes = byteBuffer.array();
            } else {
                bytes = new byte[byteBuffer.remaining()];
                byteBuffer.get(bytes);
            }
            responseBodyString = new String(bytes);
            responseBodyString = responseBodyString.trim().replaceAll("(\r\n|\n\r|\r|\n|\r0|\n0)", "");
            if(responseBodyString.endsWith("0")) {
                responseBodyString = responseBodyString.substring(0, responseBodyString.length()-1);
            }
            responseBody = responseBodyString;
        }

        @Override
        public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
            Log.i(TAG, "onSucceeded method called");
        }

        @Override
        public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
            String inform = "";
            try {
                inform = "CronetExceptionError: Failed with Status-Code - " + info.getHttpStatusCode() +
                        ". Caused by: " + error.getLocalizedMessage() + "(" + info.getHttpStatusText() + ")";
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }
            JSONObject results = new JSONObject();
            try {
                try {
                    results.put("headers", info.getAllHeaders());
                    results.put("body", inform);
                    results.put("statusCode", info.getHttpStatusCode());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

            responseBody = results.toString();
            Log.i(TAG, results.toString());
        }
    }
}
