package com.sighs.imputmethod;

import android.content.Context;
import android.graphics.PixelFormat;
import android.inputmethodservice.InputMethodService;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.sighs.imputmethod.CashPager.CashPagerAdapter;
import com.sighs.imputmethod.CashPager.PagerContainer;
import com.sighs.imputmethod.CashTable.CashTable;
import com.sighs.imputmethod.CashTable.OnCashTableUpdate;
import com.sighs.imputmethod.Overlay.AnalyticsTouchListener;
import com.sighs.imputmethod.models.Currency;

/**
 * Created by stuart on 1/16/17.
 */

public class ServiceInputMethod extends InputMethodService implements OnCashTableUpdate {
    private CashPagerAdapter cashPagerAdapter;
    private ViewPager pagerView=null;
    private CashTable cashTableAdapter;
    private TextView cashValueOutput;
    private ImageButton acceptButton;
    private ImageButton cancelButton;
    private ImageButton clearButton;
    private ImageButton leftButton;
    private ImageButton rightButton;
    private FrameLayout cashTableFrame;
    PagerContainer mContainer;
    private WindowManager mWindowManager;
    private InputConnection lastInputConntection = null;


    @Override
    public View onCreateInputView() {
        Log.d("SWOSH-InputConnect",
                String.valueOf(getCurrentInputConnection().equals(lastInputConntection)));
        lastInputConntection = getCurrentInputConnection();
        Log.d("SWOSH-InputConnect", lastInputConntection.toString());
        // Get the top level layout
        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.keyboard_layout,
                null);
        // Define the currency
        Currency[] notes = Currency.loadFromJson("tza.json", layout.getContext());
        // Tracking user touch points of the entire inputmethod
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                1, 1,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.BOTTOM;
        mWindowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        LinearLayout dummyView = new LinearLayout(this);
        dummyView.setOnTouchListener(new AnalyticsTouchListener(this));
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(dummyView, params);
        // TODO Layout top level functionality

        // CashTable Setup
        cashTableFrame = (FrameLayout) layout.findViewById(R.id.cashTableFrame);
        cashTableAdapter = new CashTable(cashTableFrame, notes);
        cashTableAdapter.setUpdateListener(this);
        // Set the Cash Pager
        cashPagerAdapter = new CashPagerAdapter(this,notes);
        cashPagerAdapter.setTableAdapter(cashTableAdapter);
        cashPagerAdapter.setTable(cashTableAdapter.getTable());
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
