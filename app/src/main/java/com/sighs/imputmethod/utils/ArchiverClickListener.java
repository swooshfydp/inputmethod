package com.sighs.imputmethod.utils;

import android.content.Context;
import android.view.View;

import com.sighs.imputmethod.Overlay.TouchAnalytics;

import static com.sighs.imputmethod.Overlay.TouchAnalytics.GetParticipant;

/**
 * Created by stuart on 2/24/17.
 */

public class ArchiverClickListener implements View.OnClickListener {
    private Context context;
    private String task;

    public ArchiverClickListener(Context context, String task) {
        this.context = context;
        this.task = task;
    }

    @Override
    public void onClick(View view) {
        String file = GetParticipant(context);
        String source = file;
        file  = file + "-" + this.task;
        TouchAnalytics.ArchiveLog(this.context, source, file);
        TouchAnalytics.ClearLog(this.context);
    }
}
