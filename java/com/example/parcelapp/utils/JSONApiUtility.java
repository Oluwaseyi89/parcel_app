package com.example.parcelapp.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;

public class JSONApiUtility {

    private final HttpURLConnection httpConn;
    private final OutputStream outPutStream;
    private final String charset;
    JSONObject reqBody = new JSONObject();
    String strReqBody;

    public JSONApiUtility(String requestUrl, String method, String charset) throws IOException {
        this.charset = charset;
        URL url = new URL(requestUrl);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setConnectTimeout(10000);
        httpConn.setReadTimeout(10000);
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        if(method.equals("PATCH")) {
            httpConn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            httpConn.setRequestMethod("POST");
        } else {
            httpConn.setRequestMethod(method);
        }
        httpConn.setRequestProperty("Content-Type", "application/json");
        httpConn.connect();
        outPutStream = httpConn.getOutputStream();
    }

    public void addArrStrReqBody(String[][] keyValPairs) throws JSONException {
        for (String[] pair : keyValPairs) {
            reqBody.put(pair[0], pair[1]);
        }
    }

    public void addStrReqBody(String name, String value) throws JSONException {
        reqBody.put(name, value);
    }

    public void addIntReqBody(String name, int value) throws JSONException {
        reqBody.put(name, value);
    }

    public void addBoolReqBody(String name, boolean value) throws JSONException {
        reqBody.put(name, value);
    }


    public void addHeaders(String[][] reqHeaders) {
        for (String[] header : reqHeaders) {
            httpConn.setRequestProperty(header[0], header[1]);
        }
    }

    public void addHeader(String name, String value) {
        httpConn.setRequestProperty(name, value);
    }

    public List<String> finish() throws IOException {
        List<String> response = new ArrayList<>();
        strReqBody = reqBody.toString();
        outPutStream.write(strReqBody.getBytes(charset));
        outPutStream.flush();
        outPutStream.close();

        int status = httpConn.getResponseCode();

        if(status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream()));
            String line;
            while((line = reader.readLine()) != null) {
                response.add(line);
            }
            reader.close();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }
        return response;
    }
}
