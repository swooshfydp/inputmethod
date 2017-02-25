package com.sighs.imputmethod.Overlay;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.FileOutputStream;

/**
 * Created by stuart on 2/7/17.
 */

public class AnalyticsTouchListener implements View.OnTouchListener {
    private final Context mContext;

    public AnalyticsTouchListener(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        String file = mContext.getSharedPreferences("cashinput", Context.MODE_APPEND)
                .getString(TouchAnalytics.PARTICIPANTKEY, "annon");
        file += ".txt";
        TouchAnalytics.WriteEvent(mContext, motionEvent);
        return true;
    }
}
