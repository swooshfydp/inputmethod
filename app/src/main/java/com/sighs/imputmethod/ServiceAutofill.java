package com.sighs.imputmethod;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by stuart on 1/16/17.
 */

public class ServiceAutofill extends AccessibilityService {
    private final static String LOGKEY = "SWOOSH_INPUT";
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(LOGKEY, "Accessibility Event Happened");
        if(event.getEventType() == AccessibilityEvent.TYPE_VIEW_FOCUSED) {
            AccessibilityNodeInfo source = event.getSource();
            Intent message = new Intent("swoosh_autofill");
            message.putExtra("sourceNode", source);
            Log.d(LOGKEY, "Send Message");
            LocalBroadcastManager.getInstance(this).sendBroadcast(message);
        }
    }

    @Override
    public void onInterrupt() {

    }
}
