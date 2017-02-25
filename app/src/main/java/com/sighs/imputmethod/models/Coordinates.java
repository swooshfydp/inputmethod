package com.sighs.imputmethod.models;

/**
 * Created by stuart on 1/23/17.
 */

public class Coordinates {
    private int x, y, w, h;

    public Coordinates(int _x, int _y, int _w, int _h) {
        this.x = _x;
        this.y = _y;
        this.w = _w;
        this.h = _h;
    }


    public boolean contains(Coordinates c) {
        return this.x < c.getX() && this.x + this.w > c.getX() + c.getW() &&
                this.y < c.getY() && this.y + this.h > c.getY() + c.getH();
    }

    public boolean collides(Coordinates c) {
        return this.x >= c.getX() && this.x + this.w <= c.getX() + c.getW() &&
                this.y >= c.getY() && this.y + this.h <= c.getY() + c.getH();
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }
}
