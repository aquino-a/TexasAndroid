package com.aquino.texasandroid;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.aquino.texasandroid.activities.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TexasLoginManager {

    private static final String mClientId="acme";
    private static final String mClientSecret ="secret";
    private static final String mClientCredentials = (mClientId +":" + mClientSecret);
    public static final String cred64 = Base64.encodeToString(mClientCredentials.getBytes(),Base64.DEFAULT);


    private TexasPreferences preferences;


    private static TexasLoginManager texasLoginManager;

    public static TexasLoginManager getInstance(Context packageContext) {
        if(texasLoginManager == null)
            texasLoginManager = new TexasLoginManager(packageContext);
        return texasLoginManager;
    }

    private TexasLoginManager(Context packageContext){
        preferences = TexasPreferences.getInstance(packageContext);
    }



    public void startLoginPage(Context packageContext) {
        Intent intent = new Intent(packageContext, LoginActivity.class);
        packageContext.startActivity(intent);
    }




    public boolean validToken() {
        String token;
        try {
            token = preferences.loadToken();
            //TODO check token on server
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    public String createValidToken(String username, String password) throws IOException {



        String path = "token";
        String host =  "10.0.2.2:8080";
        Log.i(this.getClass().getName(),"Got username and password. Making request");
            URL url;
            try {
                url = new URL(new Uri.Builder()
                        .scheme("http")
                        .authority(host)
                        .appendPath(path)
                        .appendQueryParameter("grant_type","password")
                        .appendQueryParameter("username", username)
                        .appendQueryParameter("password",password)
                        .appendQueryParameter("scope","read")
                        .build().toString());
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Authorization", "Basic " + cred64);
                con.connect();
                if(!(con.getResponseCode() == 200))
                    return null;
                Log.i(this.getClass().getName(),"Reading response");
                return findToken(con.getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw e;

            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }

    }

    private String findToken(InputStream inputStream) {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            try {
                for(String line = br.readLine(); line != null; line = br.readLine()) {
                    sb.append(line);
                }
                return parseToken(sb.toString());
            }  finally {
                br.close();
                inputStream.close();
            }
        }  catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String parseToken(String response) throws JSONException {
        JSONObject json = new JSONObject(response);
        String result = json.getString("access_token");
        preferences.saveToken(result);
        Log.i(this.getClass().getName(),"added token");
        return result;
    }

    public TexasPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(TexasPreferences preferences) {
        this.preferences = preferences;
    }

    public String getToken() {
        return preferences.loadToken();
    }
}
