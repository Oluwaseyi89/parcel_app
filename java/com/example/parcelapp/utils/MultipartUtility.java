package com.example.parcelapp.utils;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import java.util.ArrayList;
import java.util.List;

public class MultipartUtility {

    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private final HttpURLConnection httpConn;
    private final String charset;
    private final OutputStream outPutStream;

    public MultipartUtility(String requestUrl, String charset) throws IOException {
        this.charset = charset;
        boundary = "===" + System.currentTimeMillis() + "===";
        URL url = new URL(requestUrl);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setConnectTimeout(10000);
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        httpConn.connect();
        outPutStream = httpConn.getOutputStream();
    }


    public void addFormFields(String name, String value) throws IOException {
        String myBuilder = "--" + boundary + LINE_FEED +
                "Content-Disposition: form-data; name=\"" + name + "\"" + LINE_FEED +
                "Content-Type: text/plain; charset=" + charset + LINE_FEED +
                LINE_FEED +
                value + LINE_FEED;
        outPutStream.write(myBuilder.getBytes(charset));
        outPutStream.flush();
    }


    public void addFormBooleanFields(String name, boolean value) throws IOException {
        String myBuilder = "--" + boundary + LINE_FEED +
                "Content-Disposition: form-data; name=\"" + name + "\"" + LINE_FEED +
                "Content-Type: text/plain; charset=" + charset + LINE_FEED +
                LINE_FEED +
                value + LINE_FEED;
        outPutStream.write(myBuilder.getBytes(charset));
        outPutStream.flush();
    }

    public void addFileParts(String fieldName, File uploadFile) throws IOException {
        StringBuilder myBuilder = new StringBuilder();
        String fileName = uploadFile.getName();
        myBuilder.append("--").append(boundary).append(LINE_FEED);
        myBuilder.append("Content-Disposition: form-data; name=\"").append(fieldName).append("\"; ").append("filename=\"").append(fileName).append("\"").append(LINE_FEED);
        myBuilder.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
        myBuilder.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        myBuilder.append(LINE_FEED);
        outPutStream.write(myBuilder.toString().getBytes(charset));
        outPutStream.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead;

        while((bytesRead = inputStream.read(buffer)) != -1) {
            outPutStream.write(buffer, 0, bytesRead);
        }
        outPutStream.flush();
        inputStream.close();
        outPutStream.write(LINE_FEED.getBytes(charset));
        outPutStream.flush();
    }

    public void addHeader(String name, String value) {
        httpConn.setRequestProperty(name, value);
    }

    public void addHeaders(String[][] reqHeaders) {
        for (String[] header : reqHeaders) {
            httpConn.setRequestProperty(header[0], header[1]);
        }
    }

    public List<String> finish() throws IOException {
        List<String> response = new ArrayList<>();
        StringBuilder myBuilder = new StringBuilder();
        outPutStream.write(LINE_FEED.getBytes(charset));
        outPutStream.flush();
        myBuilder.append("--").append(boundary).append("--").append(LINE_FEED);
        outPutStream.write(myBuilder.toString().getBytes(charset));
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
