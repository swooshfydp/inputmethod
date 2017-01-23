package com.sighs.imputmethod;

import android.inputmethodservice.InputMethodService;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;

import com.sighs.imputmethod.CashPager.CashPagerAdapter;

/**
 * Created by stuart on 1/16/17.
 */

public class ServiceInputMethod extends InputMethodService {
    private CashPagerAdapter cashPagerAdapter;
    private ViewPager pagerView;
    @Override
    public View onCreateInputView() {
        int[] notes = {
                R.drawable.fivehund_bill,
                R.drawable.onethou_bill,
                R.drawable.twothou_bill,
                R.drawable.fivethou_bill,
                R.drawable.tenthou_bill
        };

        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.keyboard_layout, null);
        // TODO Layout top level functionality
        cashPagerAdapter = new CashPagerAdapter(this,notes);
        pagerView = (ViewPager) layout.findViewById(R.id.pagerView);
        pagerView.setAdapter(cashPagerAdapter);
        return layout;
    }

    // Outputs values to the current text field
    private void outputResults(String result) {
        InputConnection ic = getCurrentInputConnection();
        ic.deleteSurroundingText(1000000, 1000000);
        ic.commitText(String.valueOf(result), 0);
    }
}
