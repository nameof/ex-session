package com.nameof.casandroidclient.utils;

import com.nameof.casandroidclient.response.HandleResult;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpRequest {

    public static String SERVER = "http://192.168.43.160:8080/cas-web";

    public static String APPID = "AiOjE1MjI0NzcyODQeyJzdWIiOiJKb2UiLCJleHsIjQiOiI0In0";

    public static String JWT = "";

    public static HandleResult postHandleResult(String url, String params, Map<String, String> cookies) throws IOException {
        return JsonUtils.toBean(httpPost(url, params, cookies), HandleResult.class);
    }

    public static String httpPost(String url, String params, Map<String, String> cookies) throws IOException {
        URL realurl = null;
        InputStream in = null;
        HttpURLConnection conn = null;
        realurl = new URL(url);
        conn = (HttpURLConnection) realurl.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        String cookieStr = "";
        if (cookies != null) {
            for (Map.Entry<String, String> entry : cookies.entrySet()) {
                cookieStr += (entry.getKey() + "=" + entry.getValue() + ";");
            }
        }
        conn.setRequestProperty("Cookie", cookieStr);
        conn.setRequestProperty("JWT", JWT);
        conn.setRequestProperty("AppId", APPID);
        PrintWriter pw = new PrintWriter(conn.getOutputStream());
        pw.print(params);
        pw.flush();
        pw.close();
        in = conn.getInputStream();
        return convertStreamToString(in);
    }

    public static String convertStreamToString(InputStream is) {
        if (is == null)
            return "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
