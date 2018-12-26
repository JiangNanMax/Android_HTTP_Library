package com.example.android_http_library;

import android.app.Activity;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
                                activity.runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            httpListener.onHttpSuccess(result);
                                        }
                                    }
                                );
                            }
                            Log.e("http_request_GET", "Get Request Success, result ---> " + result);
                        }else {
                            if(httpListener != null) {
                                activity.runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            httpListener.onHttpNull();
                                        }
                                    }
                                );
                            }
                            Log.e("http_request_GET", "Get Request get null data");
                        }
                    }else{
                        if(httpListener != null) {
                            activity.runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        httpListener.onHttpFail("Fail! responseCode: " + responseCode);
                                    }
                                }
                            );
                        }
                        Log.e("http_request_GET", "Get Request Fail");
                    }
                    urlConn.disconnect();
                } catch (final Exception e){
                    if (httpListener != null) {
                        activity.runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    httpListener.onHttpFail("Fail! Connect Exception: " + e);
                                }
                            }
                        );
                    }
                    Log.e("http_request_GET", e.toString());
                }
            }
        }.start();
    }

    public static void http_request_POST(final Activity activity, final String address, final HashMap<String, String> paramsMap, final HttpListener httpListener) {
        new Thread() {
            public void run() {
                try {
                    StringBuilder tempParams = new StringBuilder();
                    int tag = 0;
                    for (String key : paramsMap.keySet()) {
                        if (tag > 0) {
                            tempParams.append("&");
                        }
                        tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                    }
                    String params = tempParams.toString();
                    // 请求的参数转换为byte数组
                    byte[] postData = params.getBytes();
                    // 新建一个URL对象
                    URL url = new URL(address);
                    // 打开一个HttpURLConnection连接
                    HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                    // 设置连接超时时间
                    urlConn.setConnectTimeout(5 * 1000);
                    //设置从主机读取数据超时
                    urlConn.setReadTimeout(5 * 1000);
                    // Post请求必须设置允许输出 默认false
                    urlConn.setDoOutput(true);
                    //设置请求允许输入 默认是true
                    urlConn.setDoInput(true);
                    // Post请求不能使用缓存
                    urlConn.setUseCaches(false);
                    // 设置为Post请求
                    urlConn.setRequestMethod("POST");
                    //设置本次连接是否自动处理重定向
                    urlConn.setInstanceFollowRedirects(true);
                    // 配置请求Content-Type
                    urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    // 开始连接
                    urlConn.connect();
                    // 发送请求参数
                    DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
                    dos.write(postData);
                    dos.flush();
                    dos.close();
                    // 向Listener返回请求成败
                    final int responseCode = urlConn.getResponseCode();
                } catch (final Exception e) {
                    if (httpListener != null) {
                        activity.runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        httpListener.onHttpFail("Fail!Connect Exception:" + e);
                                    }
                                }
                        );
                    }
                    Log.e("requestPOST", e.toString());
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
