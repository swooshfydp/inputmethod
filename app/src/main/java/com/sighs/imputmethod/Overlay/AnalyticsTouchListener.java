package com.sighs.imputmethod.Overlay;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import static android.content.Context.MODE_APPEND;

/**
 * Created by stuart on 2/7/17.
 */

public class AnalyticsTouchListener implements View.OnTouchListener {
    private final static String LOGKEY = "SWOOSH_INPUT";
    private final Context mContext;
    private FileOutputStream fOut;

    public AnalyticsTouchListener(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        StringBuilder logLine = new StringBuilder();
        logLine.append(DateFormat.format("yyyyy-mm-dd hh:mm:ss", System.currentTimeMillis()));
        logLine.append("- x: ");
        logLine.append(motionEvent.getX());
        logLine.append(" , y: ");
        logLine.append(motionEvent.getY());
        logLine.append(" => ");
        logLine.append(motionEvent.getAction());
        Log.d(LOGKEY ,logLine.toString());
        try {
            fOut = mContext.openFileOutput("touchLog.txt", Context.MODE_APPEND);
            logLine.append("\n");
            fOut.write(logLine.toString().getBytes());
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
