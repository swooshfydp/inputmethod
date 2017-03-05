package com.sighs.imputmethod;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.inputmethodservice.InputMethodService;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sighs.imputmethod.CashPager.CashPagerAdapter;
import com.sighs.imputmethod.CashTable.CashTable;
import com.sighs.imputmethod.CashTable.OnCashTableUpdate;
import com.sighs.imputmethod.Overlay.TouchAnalytics;
import com.sighs.imputmethod.models.Currency;

/**
 * Created by stuart on 1/16/17.
 */

public class ServiceInputMethod extends InputMethodService implements OnCashTableUpdate {
    private ViewPager pagerView=null;
    private CashTable cashTableAdapter;
    private TextView cashValueOutput;
    private InputConnection lastInputConntection = null;
    private int fieldId = -1;
    private SharedPreferences settings;


    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        lastInputConntection = getCurrentInputConnection();
        fieldId = getCurrentInputEditorInfo().fieldId;
        Log.d("SWOSH-InputConnect", String.valueOf(fieldId));
        // Update Cash table if we have history of this input
        lastInputConntection.performContextMenuAction(android.R.id.selectAll);
        String lastValue = (String) lastInputConntection.getSelectedText(0);
        cashTableAdapter.scaleLists();
        cashTableAdapter.loadCurrencyCount(settings.getString(String.valueOf(fieldId), ""), lastValue);
        TouchAnalytics.WriteMessage(this, "KeyboardOpen", "True");
        super.onStartInputView(info, restarting);
    }

    @Override
    public View onCreateInputView() {
        Log.d("SWOSH-IMS-CreateView", "Creating Input");
        // Get Preferences for the InputMethod
        settings = getSharedPreferences(TouchAnalytics.SETTINGS, MODE_PRIVATE);
        // Get the top level layout
        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.keyboard_layout,
                null);
        // Define the currency
        // TODO: Add mechanism to change this string
        Currency[] notes = Currency.loadFromJson("tza.json", layout.getContext());

        // Get Screen Width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);

        // CashTable Setup
        final FrameLayout cashTableFrame = (FrameLayout) layout.findViewById(R.id.cashTableFrame);
        cashTableAdapter = new CashTable(cashTableFrame, notes, displayMetrics.widthPixels);
        cashTableAdapter.setUpdateListener(this);
        // Set the Cash Pager
        CashPagerAdapter cashPagerAdapter = new CashPagerAdapter(this, notes);
        cashPagerAdapter.setTableAdapter(cashTableAdapter);
        cashPagerAdapter.setTable(cashTableAdapter.getTable());
        pagerView = (ViewPager) layout.findViewById(R.id.pagerView);
        pagerView.setAdapter(cashPagerAdapter);
        pagerView.setOffscreenPageLimit(cashPagerAdapter.getCount());
        pagerView.setClipChildren(false);

        // Set the Text Output
        cashValueOutput = (TextView) layout.findViewById(R.id.txtValueOutput);
        // Set the Buttons
        ImageButton acceptButton = (ImageButton) layout.findViewById(R.id.btnAccept);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log that the keyboard is closed
                TouchAnalytics.WriteMessage(pagerView.getContext(), "KeyboardOpen", "False");
                // Save the current config if it isn't 0 and close the keyboard
                settings.edit().putString(String.valueOf(fieldId), cashTableAdapter.getCurrencyCounts()).apply();
                // Log the users data
                TouchAnalytics.WriteMessage(pagerView.getContext(), "CashTableState", cashTableAdapter.getCurrencyCounts());
                TouchAnalytics.WriteMessage(pagerView.getContext(), "ScreenHeight",  ""+view.getRootView().getWidth());
                TouchAnalytics.WriteMessage(pagerView.getContext(), "ScreenWidth", ""+view.getRootView().getHeight());
                close();
            }
        });
        ImageButton cancelButton = (ImageButton) layout.findViewById(R.id.btnCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log that the keyboard is closed
                TouchAnalytics.WriteMessage(pagerView.getContext(), "KeyboardOpen", "False");
                // Close the keyboard and undo the last change
                outputResults("0");
                // Clear the table history of the field
                settings.edit().remove(String.valueOf(fieldId)).apply();
                cashTableAdapter.clearTable();
                TouchAnalytics.WriteMessage(pagerView.getContext(), "ScreenWidth",  ""+view.getRootView().getWidth());
                TouchAnalytics.WriteMessage(pagerView.getContext(), "ScreenHeight", ""+view.getRootView().getHeight());
                close();
            }
        });
        ImageButton leftButton = (ImageButton) layout.findViewById(R.id.btnLeftArrow);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int curr = pagerView.getCurrentItem();
                pagerView.setCurrentItem(curr - 1, true);
            }
        });
        ImageButton rightButton = (ImageButton) layout.findViewById(R.id.btnRightArrow);
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
