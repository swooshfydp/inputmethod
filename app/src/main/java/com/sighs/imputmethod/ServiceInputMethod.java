package com.sighs.imputmethod;

import android.inputmethodservice.InputMethodService;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.sighs.imputmethod.CashPager.CashPagerAdapter;
import com.sighs.imputmethod.CashTable.CashTable;
import com.sighs.imputmethod.CashTable.CashTableGridViewAdapter;
import com.sighs.imputmethod.CashTable.OnCashTableUpdate;
import com.sighs.imputmethod.models.Currency;

/**
 * Created by stuart on 1/16/17.
 */

public class ServiceInputMethod extends InputMethodService implements OnCashTableUpdate {
    private CashPagerAdapter cashPagerAdapter;
    private ViewPager pagerView;
    private TableLayout cashTable;
    private CashTable cashTableAdapter;
    private TextView cashValueOutput;
    private ImageButton acceptButton;
    private ImageButton cancelButton;
    private ImageButton clearButton;
    private ImageButton leftButton;
    private ImageButton rightButton;

    @Override
    public View onCreateInputView() {
        // TODO: Load this from file
        // Define the currency of the
        // Add bills resources to the new int[]
        Currency[] notes = {
                new Currency ("FiveHundred", 500.0f, new int[]{R.drawable.bill_500, R.drawable.bill_500x2, R.drawable.bill_500x3, R.drawable.bill_500x4, R.drawable.bill_500x5}),
                new Currency ("OneThousand", 1000.0f, new int[]{R.drawable.bill_1000, R.drawable.bill_1000x2, R.drawable.bill_1000x3, R.drawable.bill_1000x4, R.drawable.bill_1000x5}),
                new Currency ("TwoThousand", 2000.0f, new int[]{R.drawable.bill_2000, R.drawable.bill_2000x2, R.drawable.bill_2000x3, R.drawable.bill_2000x4, R.drawable.bill_2000x5}),
                new Currency ("FiveThousand", 5000.0f, new int[]{R.drawable.bill_5000, R.drawable.bill_5000x2, R.drawable.bill_5000x3, R.drawable.bill_5000x4, R.drawable.bill_5000x5}),
                new Currency ("TenThousand", 10000.0f, new int[]{R.drawable.bill_10000, R.drawable.bill_10000x2, R.drawable.bill_10000x3, R.drawable.bill_10000x4, R.drawable.bill_10000x5})
        };

        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.keyboard_layout, null);
        // TODO Layout top level functionality
        // CashTable Setup
        cashTable = (TableLayout) layout.findViewById(R.id.cashTable);
        cashTableAdapter = new CashTable(cashTable, notes);
        cashTableAdapter.setUpdateListener(this);
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
                outputResults("0.00");
                close();
            }
        });
        clearButton = (ImageButton) layout.findViewById(R.id.btnClear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cashTableAdapter.clearTable();
            }
        });
        leftButton = (ImageButton) layout.findViewById(R.id.btnLeftArrow);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int curr = pagerView.getCurrentItem();
                pagerView.setCurrentItem(curr - 1, true);
            }
        });
        rightButton = (ImageButton) layout.findViewById(R.id.btnRightArrow);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int curr = pagerView.getCurrentItem();
                pagerView.setCurrentItem(curr + 1, true);
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
