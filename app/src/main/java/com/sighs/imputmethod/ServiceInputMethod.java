package com.sighs.imputmethod;

import android.graphics.Rect;
import android.inputmethodservice.InputMethodService;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.GridView;
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
        // TODO: Load this from file
        // Define the currency of the
        Currency[] notes = {
                new Currency ("FiveHundred", 500.0f, new int[]{R.drawable.fivehund_bill}),
                new Currency ("OneThousand", 1000.0f, new int[]{R.drawable.onethou_bill}),
                new Currency ("TwoThousand", 2000.0f, new int[]{R.drawable.twothou_bill}),
                new Currency ("FiveThousand", 5000.0f, new int[]{R.drawable.fivethou_bill}),
                new Currency ("TenThousand", 10000.0f, new int[]{R.drawable.tenthou_bill})
        };

        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.keyboard_layout, null);
        // TODO Layout top level functionality
        // CashTable Setup
        CashTableGridViewAdapter adapter = new CashTableGridViewAdapter(this, notes);
        GridView table = (GridView) layout.findViewById(R.id.cashTable);
        table.setNumColumns(notes.length);
        table.setAdapter(adapter);
        // Set the Cash Pager
        cashPagerAdapter = new CashPagerAdapter(this,notes);
        cashPagerAdapter.setTableAdapter(adapter);
        cashPagerAdapter.setTable(table);
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
