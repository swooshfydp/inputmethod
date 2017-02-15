package com.sighs.imputmethod.models;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.sighs.imputmethod.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * Created by stuart on 1/24/17.
 */

public class Currency {
    private float value;
    private int[] images;
    private String[] imageRes;
    private int baseImage;
    private String id;
    private int count;

    public Currency(String _id, float _val, int[] _img) {
        this.id = _id;
        this.value = _val;
        this.images = _img;
        if(this.images.length > 0) this.baseImage = this.images[0];
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public int getImageSrc(int stackCount) {
        if (images.length > 0) return images[stackCount-1];
        return 0;
    }

    public void setImages(int[] images) {
        this.images = images;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getBaseImage() {
        return baseImage;
    }

    public void setBaseImage(int baseImage) {
        this.baseImage = baseImage;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void resToInts() {
        if(this.imageRes != null) {
            try {
                this.images = new int[this.imageRes.length];
                Class res = R.drawable.class;
                for(int i = 0; i < this.images.length; i++) {
                    Field field = res.getField(this.imageRes[i]);
                    this.images[i] = field.getInt(null);
                }
                if(this.images.length > 0) this.baseImage = this.images[0];
            } catch (Exception e) {

            }
        }
    }

    public static Currency[] loadFromJson(String path, Context context)  {
        Currency[] result = null;
        try {
            String json = loadJSONFromAsset(context, path);
            Gson gson = new Gson();
            result = gson.fromJson(json, Currency[].class);
            for (int i = 0; i < result.length; i++) {
                result[i].resToInts();
            }
        } catch (Exception e) {

        }
        return result;
    }

    private static String loadJSONFromAsset(Context context, String path) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
