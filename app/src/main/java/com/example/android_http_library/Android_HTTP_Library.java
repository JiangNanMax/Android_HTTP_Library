package com.example.android_http_library;

import android.app.Activity;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by 江南 on 2018/11/28.
 */

public class Android_HTTP_Library {

    public static void http_request_GET(final Activity activity, final String address, final HashMap<String, String> paramsMap, final HttpListener httpListener){
        new Thread() {
            public void run(){
                try {
                    StringBuilder params = new StringBuilder();
                    int  tag = 0;
                    for (String key : paramsMap.keySet()){
                        if (tag > 0){
                            params.append("&");
                        }
                        params.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                        tag++;
                    }
                    String requestURL = address + "?" + params.toString();

                    URL url = new URL(requestURL);
                    final HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.setRequestMethod("GET");
                    urlConn.setRequestProperty("Content-Type", "application/json");
                    urlConn.addRequestProperty("Connection", "Keep-Alive");
                    urlConn.setConnectTimeout(8 * 1000);
                    urlConn.setReadTimeout(8 * 1000);
                    urlConn.setUseCaches(true);
                    urlConn.connect();

                    final int responseCode = urlConn.getResponseCode();
                    if (responseCode == 200){
                        final String result  = StreamToString(urlConn.getInputStream());
                        if(result != null) {
                            if(httpListener != null) {

                            }
                            Log.e("http_request_GET", "Get Request Success, result ---> " + result);
                        }else {
                            if(httpListener != null) {

                            }
                            Log.e("http_request_GET", "Get Request get null data");
                        }
                    }else{
                        if(httpListener != null) {

                        }
                        Log.e("http_request_GET", "Get Request Fail");
                    }
                    urlConn.disconnect();
                } catch (final Exception e){

                }
            }
        }.start();
    }

    public static String StreamToString(InputStream inputStream){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            inputStream.close();
            byte[] byteArr = baos.toByteArray();
            return new String(byteArr);
        } catch (Exception e) {
            Log.e("StreamToString", e.toString());
            return null;
        }
    }

    public interface HttpListener{
        public void onHttpSuccess(String data);
        public void onHttpFail(String failInfo);
        public void onHttpNull();
    }
}
