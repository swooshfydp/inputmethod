package com.sighs.imputmethod.CashTable;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.sighs.imputmethod.R;
import com.sighs.imputmethod.customviews.LockableHorizontalScrollView;
import com.sighs.imputmethod.models.Currency;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by stuart on 2/6/17.
 */

public class CashTable implements View.OnTouchListener, Runnable {
    private final static int MINDISTANCE = 10;
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
    private String selectedKey;

    public CashTable(FrameLayout layout, Currency[] currencies) {
        this.table = (TableLayout) layout.findViewById(R.id.cashTable);
        this.dragImage = (ImageView) layout.findViewById(R.id.dragImage);
        this.scrollView = (LockableHorizontalScrollView) layout.findViewById(R.id.horizontalScroll);
        this.trashIcon = layout.findViewById(R.id.btnClear);
        this.trashIcon.setPadding(10, 10, 10, 10);
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
            list.setAdapter(item.getAdaptor());
            list.setTag(currency.getId());
            list.setOnTouchListener(this);
            tableRow.addView(list);
            item.setView(list);
            cashValues.put(currency.getId(), item);
        }
        this.table.addView(tableRow);
    }

    public TableLayout getTable() {
        return this.table;
    }

    public void updateCashGrid(String key, int val) {
        if (cashValues.containsKey(key)) {
            ColumnItem value = cashValues.get(key);
            value.UpdateStacks(val);
            cashValues.put(key, value);
            value.getAdaptor().notifyDataSetChanged();
            float total = updateTotals();
            Log.d(LOGKEY, "Current Total: " + total);
            if(updateListener != null) updateListener.OnTotalUpdate(String.format("%.2f", total));
        }
    }

    public int updateTotals() {
        int total = 0;
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
        updateListener.OnTotalUpdate(String.format("%.2f", total));
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
        this.cashValues.get(this.selectedKey).getView().setBackgroundColor(R.color.blue);
        this.trashIcon.setBackgroundColor(R.color.blue);
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
                this.handler.postDelayed(this, 1000);
                this.deltaX = motionEvent.getRawX();
                this.deltaY = motionEvent.getY();
                this.dragImage.setX(deltaX - Math.max(150, this.dragImage.getWidth())/2);
                this.dragImage.setY(deltaY - Math.max(105, this.dragImage.getHeight())/2);
                break;
            case (MotionEvent.ACTION_MOVE):
            case (MotionEvent.ACTION_CANCEL):
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
        Log.d(LOGKEY, "Raw (" + motionEvent.getRawX() + ", " + motionEvent.getRawY() + ") - Normal (" +
                motionEvent.getX() + ", " + motionEvent.getY() + ") - Size (" +
                this.dragImage.getWidth() + ", " + this.dragImage.getHeight() + ") - Coords. (" +
                this.dragImage.getX() + ", " + this.dragImage.getY() + ") - Action: " + lastTouch);
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

        public float getTotal() {
            return this.currency.getValue() * this.count;
        }

        public void UpdateStacks(int amount) {
            this.count += amount;
            ArrayList list = new ArrayList<Integer>();
            int stack = this.count;
            if (this.count <= this.StackCount) {
                while (stack > 0) {
                    list.add(this.currency.getBaseImage());
                    stack--;
                }
            } else {
                while (stack > 0) {
                    int rem = this.StackCount;
                    if (stack < this.StackCount) rem = stack % this.StackCount;
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
            v.setMaxWidth(150);
            v.setMinimumWidth(1);
            // v.setScaleX(100);
            // v.setScaleY(1);
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
