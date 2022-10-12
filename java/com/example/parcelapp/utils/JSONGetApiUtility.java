package com.example.parcelapp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class JSONGetApiUtility {

    private final HttpURLConnection httpConn;

    public JSONGetApiUtility(String requestUrl) throws IOException {
        URL url = new URL(requestUrl);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setConnectTimeout(10000);
        httpConn.setUseCaches(false);
        httpConn.setDoInput(true);
        httpConn.setRequestMethod("GET");
        httpConn.setRequestProperty("Content-Type", "application/json");
        httpConn.connect();
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
