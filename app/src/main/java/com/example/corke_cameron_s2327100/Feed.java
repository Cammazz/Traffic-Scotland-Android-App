package com.example.corke_cameron_s2327100;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
public class Feed {
    public static void saveFeed(Context context, String key, String xml) {
        SharedPreferences preferences = context.getSharedPreferences("TrafficFeeds", Context.MODE_PRIVATE);
        preferences.edit().putString(key, xml).putLong("lastUpdated", System.currentTimeMillis()).apply();
    }

    public static String getFeed(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences("TrafficFeeds", Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }

    public static boolean downloadFeed(Context context, String urlString, String clientId, String clientKey, String cacheKey) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            String credentials = clientId + ":" + clientKey;
            String encodedCredentials = android.util.Base64.encodeToString(credentials.getBytes("UTF-8"), Base64.NO_WRAP);
            connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            StringBuilder result = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }
            String xml = result.toString();
            int xmlStart = xml.indexOf("<?");
            if (xmlStart >= 0) {
                xml = xml.substring(xmlStart);
            }
            saveFeed(context, cacheKey, xml);
            return true;
        } catch (Exception e) {
            android.util.Log.e("Feed", "Download Failed", e);
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}