package com.aquino.texasandroid;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TexasAssetManager {

    private static TexasAssetManager sTexasAssetManager;
    private AssetManager assetManager;
    private Properties properties;

    private TexasAssetManager(Context packageContext) {
        assetManager = packageContext.getAssets();
        properties = new Properties();
    }

    public static TexasAssetManager makeInstance(Context packageContext) {
        if(sTexasAssetManager == null)
            sTexasAssetManager = new TexasAssetManager(packageContext);
        return sTexasAssetManager;
    }

    public static TexasAssetManager getInstance() {
        return sTexasAssetManager;
    }

    public List<String> getProperty(String ... name) {
        ArrayList<String> list = new ArrayList<>();
        try {
            InputStream in = assetManager.open("config.properties");
            try {
                properties.load(in);
                for (String s : name) {
                    list.add(properties.getProperty(s));
                }
                return list;
            } finally {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
