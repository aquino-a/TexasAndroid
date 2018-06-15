package com.aquino.texasandroid;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.aquino.texasandroid.model.GameInfo;
import com.aquino.texasandroid.model.GameList;
import com.aquino.texasandroid.model.GameState;
import com.aquino.texasandroid.model.Move;
import com.aquino.texasandroid.model.NewUser;
import com.aquino.texasandroid.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

public class TexasRequestManager {

    private ObjectMapper objectMapper;
    private String token, host;

    private static TexasRequestManager texasRequestManager;

    public static TexasRequestManager getInstance(String token) {
        if (texasRequestManager == null)
            texasRequestManager = new TexasRequestManager();
        texasRequestManager.setToken(token);
        return texasRequestManager;
    }

    public static TexasRequestManager getSetupInstance() {
        return texasRequestManager;
    }

    private TexasRequestManager() {
        objectMapper = new ObjectMapper();
        List<String> list = TexasAssetManager.getInstance().getProperty("server.host");
        host = list.get(0);
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

    public User getUser() throws IOException, NullPointerException {
        try {
            return objectMapper.readValue(getResponse("/games/me", "GET",null), User.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public GameList getGameList() throws IOException, JSONException {
        GameList result = new GameList();
        List<GameInfo> list = new ArrayList<>();
        JSONObject json = new JSONObject(getResponse("/games","GET",null));
        result.setSize(json.getInt("size"));
        json = json.getJSONObject("list");
        Iterator<String> it = json.keys();
        while(it.hasNext()) {
            list.add(objectMapper.readValue(json.getString(it.next()), GameInfo.class));
        }
        result.setList(list);

        return result;
    }

    public long createNewGame() throws IOException {
        return Long.parseLong(getResponse("/games","POST",null));
    }

    public GameState sendMove(Move move, long gameId) throws IOException {
        String path = String.format("/games/%d/move",gameId);
        return objectMapper.readValue(getResponse(path,"POST", move),GameState.class);
    }
    public GameState pingServer(long gameId) throws IOException {
        String path = String.format("/games/%d",gameId);
        return objectMapper.readValue(getResponse(path,"GET",null),GameState.class);
    }

    public void room(String action, long gameId) throws IOException {
        String path = String.format("/games/%d/%s",gameId,action);
        getResponse(path,"POST",null);
    }

    public void registerNewUser(NewUser newUser) throws IOException {
        String json = objectMapper.writeValueAsString(newUser);
        addNewUser("/users/new","POST", newUser);
    }

    public GameState fold(long gameId) throws IOException {
        return sendMove(new Move("FOLD", 0), gameId);
    }


    public String getResponse(String path, String requestMethod, Object json) throws IOException {
        RunnableFuture<String> rf = new FutureTask<String>(()->{
            try {
                URL url = new URL(new Uri.Builder()
                        .scheme("http")
                        .encodedAuthority(this.host)
                        .encodedPath(path)
                        .build().toString());
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod(requestMethod);
//                con.setRequestProperty("Authorization", "Basic " + TexasLoginManager.cred64);
                con.setRequestProperty("Authorization", "Bearer " + this.token);


                //con.connect();
                if (!(con.getResponseCode() == 200)){
                    //TODO close
                    throw new IOException("Not 200");
                }
                if(json != null)
                    objectMapper.writeValue(con.getOutputStream(),json);
                Log.i(this.getClass().getName(), "Reading response at: " + path);
                return readStream(con.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        });

        Thread getReponseThread = new Thread(rf);
        getReponseThread.start();
        try {
            getReponseThread.join();
            return rf.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addNewUser(String path,String requestMethod, Object json) {

        RunnableFuture<String> rf = new FutureTask<String>(()->{
            try {
                URL url = new URL(new Uri.Builder()
                        .scheme("http")
                        .encodedAuthority(this.host)
                        .encodedPath(path)
                        .build().toString());
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod(requestMethod);

                //TODO manually write json to output stream, spring isn't getting right format
                if(json != null) {
                    con.setDoOutput(true);
                    con.setRequestProperty( "Content-Type", "application/json" );
                    objectMapper.writeValue(con.getOutputStream(),json);
                    //writeJson(json,os);
                }

                //con.connect();
                if (!(con.getResponseCode() == 200)){
                    //TODO close
                    throw new IOException("Not 200");
                }
                Log.i(this.getClass().getName(), "Reading response at: " + path);
                return readStream(con.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        });

        Thread getReponseThread = new Thread(rf);
        getReponseThread.start();
        try {
            getReponseThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void addJson(OutputStream outputStream, String json) throws IOException {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);
            try {
                writer.write(json);
            } finally {
                writer.close();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void writeJson(OutputStream os, String json) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os))) {
            bw.write(json);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
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
