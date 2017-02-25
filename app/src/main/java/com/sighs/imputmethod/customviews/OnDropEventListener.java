package com.sighs.imputmethod.customviews;

import android.view.View;

import com.sighs.imputmethod.models.Coordinates;

/**
 * Created by stuart on 1/22/17.
 */

public interface OnDropEventListener {
    public void onItemDrop(View view, Coordinates c);
}

