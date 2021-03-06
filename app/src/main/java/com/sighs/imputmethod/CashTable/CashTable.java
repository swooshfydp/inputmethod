package com.sighs.imputmethod.CashTable;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.sighs.imputmethod.Overlay.TouchAnalytics;
import com.sighs.imputmethod.R;
import com.sighs.imputmethod.customviews.LockableHorizontalScrollView;
import com.sighs.imputmethod.models.Currency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;

/**
 * Created by stuart on 2/6/17.
 */

public class CashTable implements View.OnTouchListener, Runnable {
    private final static int MINDISTANCE = 10;
    private final static int HOLDWAIT = 500;
    private final static String LOGKEY = "SWOOSH_INPUT";
    private final Handler handler = new Handler();
    private int lastTouch = MotionEvent.ACTION_UP;
    private HashMap<String, ColumnItem> cashValues = new HashMap<>();
    private OnCashTableUpdate updateListener = null;
    private TableLayout table;
    private ImageView dragImage;
    private View trashIcon;
    private LockableHorizontalScrollView scrollView;
    private float deltaX, deltaY;
    private float WIDTHSCALER;
    private String selectedKey;

    public CashTable(FrameLayout layout, Currency[] currencies, int screenWidth) {
        // Get Screen width

        WIDTHSCALER = screenWidth / 480;
        this.table = (TableLayout) layout.findViewById(R.id.cashTable);
        this.dragImage = (ImageView) layout.findViewById(R.id.dragImage);
        this.scrollView = (LockableHorizontalScrollView) layout.findViewById(R.id.horizontalScroll);
        this.trashIcon = layout.findViewById(R.id.btnClear);
        this.trashIcon.setPadding(10, 10, 10, 10);
        this.trashIcon.setMinimumWidth((int) (75 * WIDTHSCALER));
        this.trashIcon.setMinimumHeight((int) (75 * WIDTHSCALER));
        this.trashIcon.setBackgroundColor(0x00000000);
        this.trashIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearTable();
            }
        });
        TableRow tableRow = new TableRow(table.getContext());
        for(Currency currency: currencies) {
            ColumnItem item = new CashTable.ColumnItem(table.getContext(), currency);
            ListView list = new ListView(this.table.getContext());
            list.setPadding(10, 10, 10, 10);
            // (int)(150 * WIDTHSCALER);
            list.setAdapter(item.getAdaptor());
            list.setTag(currency.getId());
            list.setOnTouchListener(this);
            tableRow.addView(list);
            item.setView(list);
            cashValues.put(currency.getId(), item);
        }
        this.table.addView(tableRow);
    }

    public void scaleLists() {
        for(ColumnItem item : cashValues.values()) {
            item.getView().setMinimumWidth((int)(150 * WIDTHSCALER));
            item.getView().getLayoutParams().width = (int)(150 * WIDTHSCALER);
        }
    }

    public TableLayout getTable() {
        return this.table;
    }

    public String getCurrencyCounts() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%d", (int) updateTotals()));
        builder.append("|");
        for(String k : cashValues.keySet()) {
            builder.append(k);
            builder.append(":");
            builder.append(cashValues.get(k).count);
            builder.append(";");
        }
        // builder.append("\n");
        return builder.toString();
    }

    public void focusOnView(int id){
        this.scrollView.post(new ScrollProcess(this.scrollView, id));
    }

    private class ScrollProcess implements Runnable {
        private int id;
        private LockableHorizontalScrollView view;
        ScrollProcess(LockableHorizontalScrollView view, int id) {
            this.view = view;
            this.id = id;
        }

        @Override
        public void run() {
            this.view.scrollTo((int) (this.id * 150 * WIDTHSCALER), 0);
        }
    }


    public void loadCurrencyCount(String record, String lastValue) {
        if (record.equals("")) {
            clearTable();
            return;
        }
        if(!record.split("\\|")[0].equals(lastValue)) {
            clearTable();
            return;
        }
        String counts = record.split("\\|")[1];
        String[] records = counts.split(";");
        for(String rec : records) {
            cashValues.get(rec.split(":")[0]).setCount(Integer.parseInt(rec.split(":")[1]));
            cashValues.get(rec.split(":")[0]).UpdateStack();
        }
        float total = updateTotals();
        updateListener.OnTotalUpdate(String.format("%d", (int) total));
    }

    public void updateCashGrid(String key, int val) {
        if (cashValues.containsKey(key)) {
            ColumnItem value = cashValues.get(key);
            value.UpdateStacks(val);
            cashValues.put(key, value);
            value.getAdaptor().notifyDataSetChanged();
            float total = updateTotals();
            Log.d(LOGKEY, "Current Total: " + total);
            if(updateListener != null) updateListener.OnTotalUpdate(String.format("%d", (int) total));
        }
    }

    public float updateTotals() {
        float total = 0;
        for (CashTable.ColumnItem item: cashValues.values()) {
            total += item.getTotal();
        }
        return total;
    }

    public void clearTable() {
        for(CashTable.ColumnItem item: cashValues.values()) {
            item.clear();
        }
        float total = updateTotals();
        updateListener.OnTotalUpdate(String.format("%d", (int) total));
    }

    public OnCashTableUpdate getUpdateListener() {
        return updateListener;
    }

    public void setUpdateListener(OnCashTableUpdate updateListener) {
        this.updateListener = updateListener;
    }

    @Override
    public void run() {
        this.scrollView.setScrollingEnabled(false);
        this.dragImage.setX(deltaX - Math.max(150, this.dragImage.getWidth())/2);
        this.dragImage.setY(deltaY - Math.max(105, this.dragImage.getHeight())/2);
        this.cashValues.get(this.selectedKey).getView().setBackgroundColor(R.color.red);
        this.trashIcon.setBackgroundColor(R.color.red);
        this.dragImage.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        String key = (String) view.getTag();
        switch (motionEvent.getAction()) {
            case (MotionEvent.ACTION_DOWN):
                this.lastTouch = MotionEvent.ACTION_DOWN;
                this.selectedKey = key;
                this.dragImage.setImageResource(cashValues.get(key).currency.getBaseImage());
                this.handler.postDelayed(this, HOLDWAIT);
                this.deltaX = motionEvent.getRawX();
                this.deltaY = motionEvent.getY();
                this.dragImage.setX(deltaX - Math.max(150, this.dragImage.getWidth())/2);
                this.dragImage.setY(deltaY - Math.max(105, this.dragImage.getHeight())/2);
                break;
            case (MotionEvent.ACTION_MOVE):
                lastTouch = MotionEvent.ACTION_MOVE;
                float newX = motionEvent.getRawX();
                if(Math.abs(this.deltaX - newX) > MINDISTANCE && this.dragImage.getVisibility() == View.GONE) {
                    this.handler.removeCallbacks(this);
                    this.scrollView.setScrollingEnabled(true);
                    this.dragImage.setVisibility(View.GONE);
                    this.cashValues.get(this.selectedKey).getView().setBackgroundColor(0x00000000);
                    this.trashIcon.setBackgroundColor(0x00000000);
                    return true;
                }
                this.deltaX = newX;
                this.deltaY = motionEvent.getY();
                this.dragImage.setX(deltaX - Math.max(150, this.dragImage.getWidth())/2);
                this.dragImage.setY(deltaY - Math.max(105, this.dragImage.getHeight())/2);
                break;
            case (MotionEvent.ACTION_UP):
            case (MotionEvent.ACTION_CANCEL):
                this.handler.removeCallbacks(this);
                this.scrollView.setScrollingEnabled(true);
                this.dragImage.setVisibility(View.GONE);
                this.cashValues.get(this.selectedKey).getView().setBackgroundColor(0x00000000);
                this.trashIcon.setBackgroundColor(0x00000000);
                if (isViewOverlapping(this.dragImage, this.trashIcon)) {
                    updateCashGrid(key, -1);
                }
                lastTouch = MotionEvent.ACTION_UP;
                return true;
        }
        TouchAnalytics.WriteEvent(this.table.getContext(), motionEvent);
        return false;
    }

    // Determine if two vies are overlapping
    // Specifically made for for the trash button
    private boolean isViewOverlapping(View firstView, View secondView) {
        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];

        firstView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        firstView.getLocationOnScreen(firstPosition);
        secondView.getLocationOnScreen(secondPosition);

        int r = firstView.getWidth() + firstPosition[0];
        int l = secondPosition[0];
        int u = firstView.getHeight() + firstPosition[1];
        int d = secondPosition[1];
        return (r >= l && (r != 0 && l != 0)) && (u >= d && (u != 0 && d != 0));
    }

    class ColumnItem {
        private CashTable.TableListAdaptor adaptor;
        private Currency currency;
        private int StackCount = 5;
        private int count = 0;
        private ListView view;

        public ColumnItem(Context context, Currency currency) {
            this.currency = currency;
            this.adaptor = new CashTable.TableListAdaptor(context);
        }

        public int getCount() {return this.count;}
        public void setCount(int c) {this.count = c;}

        public float getTotal() {
            return this.currency.getValue() * this.count;
        }

        public void UpdateStacks(int amount) {
            this.count += amount;
            ArrayList list = new ArrayList<Integer>();
            int stack = this.count;
            if (this.count <= this.StackCount - 1) {
                while (stack > 0) {
                    list.add(this.currency.getBaseImage());
                    stack--;
                }
            } else {
                while (stack > 0) {
                    int rem = this.StackCount;
                    if (stack < this.StackCount - 1) rem = stack % this.StackCount;
                    list.add(this.currency.getImageSrc(rem));
                    stack -= this.StackCount;
                }
            }
            this.adaptor.items = list;
            this.adaptor.notifyDataSetChanged();
        }

        public void UpdateStack() {
            ArrayList list = new ArrayList<Integer>();
            int stack = this.count;
            if (this.count <= this.StackCount - 1) {
                while (stack > 0) {
                    list.add(this.currency.getBaseImage());
                    stack--;
                }
            } else {
                while (stack > 0) {
                    int rem = this.StackCount;
                    if (stack < this.StackCount - 1) rem = stack % this.StackCount;
                    list.add(this.currency.getImageSrc(rem));
                    stack -= this.StackCount;
                }
            }
            this.adaptor.items = list;
            this.adaptor.notifyDataSetChanged();
        }

        public CashTable.TableListAdaptor getAdaptor() {
            return adaptor;
        }

        public void clear() {
            ArrayList list = new ArrayList<Integer>();
            this.adaptor.items = list;
            this.adaptor.notifyDataSetChanged();
            this.count = 0;
        }

        public ListView getView() {
            return view;
        }

        public void setView(ListView view) {
            this.view = view;
        }
    }

    class TableListAdaptor extends BaseAdapter {
        private ArrayList<Integer> items = new ArrayList<Integer>();
        private Context mContext;

        public TableListAdaptor(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            // DragNDropImageView v = new DragNDropImageView(mContext, items.get(i), items.get(i), viewGroup);
            // v.setDimensions(40, 40);
            ImageView v = new ImageView(viewGroup.getContext());
            v.setImageResource(items.get(i));
            v.setMaxWidth((int) (150 * WIDTHSCALER));
            v.setMinimumWidth(1);
            v.setAdjustViewBounds(true);
            v.setPadding(2, 2, 2, 2);
            v.setOnTouchListener(null);
            v.setFocusableInTouchMode(false);
            return v;
        }

        public void addItem(int value) {
            items.add(value);
        }
    }
}
