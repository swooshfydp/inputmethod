package com.sighs.imputmethod;

import android.content.Context;
import android.graphics.Rect;
import android.inputmethodservice.InputMethodService;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.sighs.imputmethod.CashPager.CashPagerAdapter;

import org.w3c.dom.Text;

/**
 * Created by stuart on 1/16/17.
 */

public class ServiceInputMethod extends InputMethodService implements OnCashTableUpdate{
    private CashPagerAdapter cashPagerAdapter;
    private ViewPager pagerView;
    private GridView cashTable;
    private CashTableGridViewAdapter cashTableAdapter;
    private TextView cashValueOutput;
    private ImageButton acceptButton;
    private ImageButton cancelButton;

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
        cashTableAdapter = new CashTableGridViewAdapter(this, notes);
        cashTableAdapter.setOnCashTableUpdate(this);
        cashTable = (GridView) layout.findViewById(R.id.cashTable);
        cashTable.setNumColumns(notes.length);
        cashTable.setAdapter(cashTableAdapter);
        // Set the Cash Pager
        cashPagerAdapter = new CashPagerAdapter(this,notes);
        cashPagerAdapter.setTableAdapter(cashTableAdapter);
        cashPagerAdapter.setTable(cashTable);
        pagerView = (ViewPager) layout.findViewById(R.id.pagerView);
        pagerView.setAdapter(cashPagerAdapter);
        // Set the Text Output
        cashValueOutput = (TextView) layout.findViewById(R.id.txtValueOutput);
        // Set the Buttons
        acceptButton = (ImageButton) layout.findViewById(R.id.btnAccept);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Just Close the Keyboard
                close();
            }
        });
        cancelButton = (ImageButton) layout.findViewById(R.id.btnCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close the keyboard and undo the last change
                outputResults("0");
                close();
            }
        });
        return layout;
    }

    // Outputs values to the current text field
    private void outputResults(String result) {
        InputConnection ic = getCurrentInputConnection();
        ic.deleteSurroundingText(1000000, 1000000);
        ic.commitText(String.valueOf(result), 0);
    }

    private void close() {
        this.requestHideSelf(0);
    }

    @Override
    public void OnTotalUpdate(String value) {
        // On Table update, update value on the app and the Input
        outputResults(value);
        cashValueOutput.setText(value);
    }
}
