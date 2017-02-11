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
import com.sighs.imputmethod.CashPager.PagerContainer;
import com.sighs.imputmethod.CashTable.CashTable;
import com.sighs.imputmethod.CashTable.CashTableGridViewAdapter;
import com.sighs.imputmethod.CashTable.OnCashTableUpdate;
import com.sighs.imputmethod.models.Currency;

/**
 * Created by stuart on 1/16/17.
 */

public class ServiceInputMethod extends InputMethodService implements OnCashTableUpdate {
    private CashPagerAdapter cashPagerAdapter;
    private ViewPager pagerView=null;
    private TableLayout cashTable;
    private CashTable cashTableAdapter;
    private TextView cashValueOutput;
    private ImageButton acceptButton;
    private ImageButton cancelButton;
    private ImageButton clearButton;
    private ImageButton leftButton;
    private ImageButton rightButton;
    PagerContainer mContainer;


    @Override
    public View onCreateInputView() {
        // Get the top level layout
        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.keyboard_layout, null);
        // Define the currency
        Currency[] notes = Currency.loadFromJson("tza.json", layout.getContext());
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
        pagerView.setOffscreenPageLimit(cashPagerAdapter.getCount());
        pagerView.setClipChildren(false);


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
