package com.sighs.imputmethod;

import android.support.v4.view.PagerAdapter;
import android.view.View;

/**
 * Created by stuart on 1/16/17.
 */
/*
    The scrolling pager that is used to show the draggable cash options
*/
public class CashPagerAdapter extends PagerAdapter {
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }
}
