package com.sighs.imputmethod.Overlay;

import android.text.format.DateFormat;

import com.google.gson.Gson;

/**
 * Created by stuart on 3/1/17.
 */

public class LogWriter {
    private static final Gson gson = new Gson();


    public static String WriteTouchLog(float _x, float _y, float _s) {
        return gson.toJson(new TouchPoint(_x, _y, _s), TouchPoint.class);
    }

    public static String WriteStateLog(String value) {
        return gson.toJson(new StateChange(value), StateChange.class);
    }

    public static String WriteStateLog(String type, String value) {
        return gson.toJson(new StateChange(type, value), StateChange.class);
    }


    private static class TouchPoint {
        private long time;
        private String type = "TouchPoint";
        private float x, y, s;

        public TouchPoint(float _x, float _y, float _s) {
            this.time = System.currentTimeMillis();
            this.x = _x;
            this.y = _y;
            this.s = _s;
        }
    }

    private static class StateChange {
        private transient int[] multis = new int[]{60 * 60 * 100, 60 * 100, 100, 1};
        private long time;
        private String type = "StateChange";
        private String value;

        public StateChange(String _v) {
            this.time = System.currentTimeMillis();
            this.value = _v;
        }

        public StateChange(String _t, String _v) {
            this.time = System.currentTimeMillis();
            this.type = _t;
            this.value = _v;
        }
    }
}