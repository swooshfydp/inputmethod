package com.sighs.imputmethod;

import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by stuart on 1/16/17.
 */

public class ServiceInputMethod extends InputMethodService {

    @Override
    public View onCreateInputView() {
        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.keyboard_layout, null);
        // TODO Layout top level functionality
        return layout;
    }

    // Outputs values to the current text field
    private void outputResults(String result) {
        InputConnection ic = getCurrentInputConnection();
        ic.deleteSurroundingText(1000000, 1000000);
        ic.commitText(String.valueOf(result), 0);
    }
}
