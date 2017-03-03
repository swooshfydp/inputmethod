package com.sighs.imputmethod;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.sighs.imputmethod.Overlay.AnalyticsTouchListener;
import com.sighs.imputmethod.Overlay.TouchAnalytics;
import com.sighs.imputmethod.utils.ArchiverClickListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    final Context context = this;
    private Button btnTask1;
    private Button btnTask2;
    private Button btnTask3;
    private EditText txtTask1;
    private EditText txtTask2;
    private EditText txtTask3;
    private EditText participantId;
    private Button btnSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Tracking user touch points of the entire inputmethod
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                1, 1,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.BOTTOM;
        LinearLayout dummyView = new LinearLayout(this);
        dummyView.setOnTouchListener(new AnalyticsTouchListener(this));
        this.getWindowManager().addView(dummyView, params);

        // Get Text Fields
        txtTask1 = (EditText) findViewById(R.id.test1);
        txtTask2 = (EditText) findViewById(R.id.test2);
        txtTask3 = (EditText) findViewById(R.id.test3);
        participantId = (EditText) findViewById(R.id.participant_id);

        // Add Button Behaviour
        Button btnReset = (Button) findViewById(R.id.reset);
        btnTask1 = (Button) findViewById(R.id.save_results_task_1);
        btnTask2 = (Button) findViewById(R.id.save_results_task_2);
        btnTask3 = (Button) findViewById(R.id.save_results_task_3);
        btnSave = (Button) findViewById(R.id.save_participant_id);

        btnTask1.setOnClickListener(new ArchiverClickListener(this, "Task1"));
        btnTask2.setOnClickListener(new ArchiverClickListener(this, "Task2"));
        btnTask3.setOnClickListener(new ArchiverClickListener(this, "Task3"));
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TouchAnalytics.ClearLog(getApplicationContext());
                TouchAnalytics.SetParticipant(getApplicationContext(),
                        String.valueOf(participantId.getText()));
                toggleTest(true);
            }
        });
        toggleTest(false);
        btnReset.setOnClickListener(this);
    }

    private void ClearFields() {
        txtTask1.setText("");
        txtTask2.setText("");
        txtTask3.setText("");
    }

    private void toggleTest(boolean enable) {
        participantId.setEnabled(!enable);
        btnSave.setEnabled(!enable);
        txtTask1.setEnabled(enable);
        btnTask1.setEnabled(enable);
        txtTask2.setEnabled(enable);
        btnTask2.setEnabled(enable);
        txtTask3.setEnabled(enable);
        btnTask3.setEnabled(enable);
    }

    @Override
    public void onClick(View view) {
        ClearFields();
        toggleTest(false);
        TouchAnalytics.ClearLog(this);
    }
}
