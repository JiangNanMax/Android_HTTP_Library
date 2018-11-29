package com.example.android_http_library;

import android.app.Activity;

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
                        String getResult = StreamToString(urlConn.getInputStream());
                    }else{

                    }

                } catch (final Exception e){

                }
            }
        }.start();
    }

    public static String StreamToString(InputStream inputStream){
        return "";
    }

    public interface HttpListener{
        public void onHttpSuccess();


    }
}
