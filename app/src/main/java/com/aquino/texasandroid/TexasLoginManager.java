package com.aquino.texasandroid;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.aquino.texasandroid.activities.LoginActivity;
import com.aquino.texasandroid.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class TexasLoginManager {

    private static final String mClientId="acme";
    private static final String mClientSecret ="secret";
    private static final String mClientCredentials = (mClientId +":" + mClientSecret);
    public static final String cred64 = Base64.encodeToString(mClientCredentials.getBytes(),Base64.DEFAULT);

    private String host, path;


    private TexasPreferences preferences;
    private TexasRequestManager texasRequestManager;


    private static TexasLoginManager texasLoginManager;

    public static TexasLoginManager getInstance(Context packageContext) {
        if(texasLoginManager == null)
            texasLoginManager = new TexasLoginManager(packageContext);
        return texasLoginManager;
    }

    private TexasLoginManager(Context packageContext){
        preferences = TexasPreferences.getInstance(packageContext);
        List<String> list = TexasAssetManager.getInstance()
                .getProperty("server.host","server.token-path");
        host = list.get(0);
        path = list.get(1);
        texasRequestManager = TexasRequestManager.getSetupInstance();
    }



    public boolean validToken() {
        String token;
        try {
            token = preferences.loadToken();
            User user = texasRequestManager.getUser();
            //TODO fix exception message after debug
            if(user == null)
                throw new Exception("User null/token possible not valid");
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String createValidToken(String username, String password) throws IOException {

        Log.i(this.getClass().getName(),"Got username and password. Making request");
            URL url;
            try {
                url = new URL(new Uri.Builder()
                        .scheme("http")
                        .encodedAuthority(host)
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
        try {
            return preferences.loadToken();

        } catch (NullPointerException e) {
            Log.i(this.getClass().getName(),"NO TOKEN");
            e.printStackTrace();
            return null;
        }
    }

}
