package com.aquino.texasandroid;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.aquino.texasandroid.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class TexasRequestManager {

    private ObjectMapper objectMapper;
    private String token, host;

    private static TexasRequestManager texasRequestManager;

    public static TexasRequestManager getInstance(String token, Context packageContext) {
        if (texasRequestManager == null)
            texasRequestManager = new TexasRequestManager();
        texasRequestManager.setToken(token);
        texasRequestManager.setHost(packageContext.getResources().getString(R.string.service_host));

        return texasRequestManager;
    }

    private TexasRequestManager() {
        objectMapper = new ObjectMapper();
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public User getUser() throws IOException {
        try {
            //TODO add path on server to return user from principal
            return objectMapper.readValue(getResponse("me"), User.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public String getResponse(String path) throws IOException {
        try {
            URL url = new URL(new Uri.Builder()
                    .scheme("http")
                    .authority(this.host)
                    .appendPath(path)
                    .build().toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Basic " + TexasLoginManager.cred64);
            con.setRequestProperty("Authorization", "Bearer " + this.token);
            con.connect();
            if (!(con.getResponseCode() == 200))
                return null;
            Log.i(this.getClass().getName(), "Reading response");
            return readStream(con.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private String readStream(InputStream inputStream) throws IOException {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            try {
                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    sb.append(line);
                }
                return sb.toString();
            } finally {
                br.close();
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

}
