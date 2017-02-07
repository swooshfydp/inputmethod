package com.sighs.imputmethod.models;

/**
 * Created by stuart on 1/24/17.
 */

public class Currency {
    private float value;
    private int[] images;
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
}
