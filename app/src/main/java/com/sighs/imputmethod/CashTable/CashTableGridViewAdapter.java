package com.sighs.imputmethod.CashTable;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.sighs.imputmethod.models.Currency;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by stuart on 1/16/17.
 */

/*
    A Scrollable grid view that displays the current cash on the table
*/
public class CashTableGridViewAdapter extends BaseAdapter {
    private final static String LOGKEY = "SWOOSH_INPUT";
    private Context mContext;
    private HashMap<String, ColumnItem> cashValues = new HashMap<String, ColumnItem>();
    private final Currency[] mValues;
    private OnCashTableUpdate updateListener = null;

    public CashTableGridViewAdapter(Context context, Currency[] values) {
        mContext = context;
        mValues = values;
        for (Currency value : values) {
            cashValues.put(value.getId(), new ColumnItem(context, value));
        }
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
        for (ColumnItem item: cashValues.values()) {
            total += item.getTotal();
        }
        return total;
    }

    public void clearTable() {
        for(ColumnItem item: cashValues.values()) {
            item.clear();
        }
        float total = updateTotals();
        updateListener.OnTotalUpdate(String.format("%.2f", total));
    }

    public void setOnCashTableUpdate(OnCashTableUpdate listener) {
        this.updateListener = listener;
    }

    @Override
    public int getCount() {
        return cashValues.size();
    }

    @Override
    public Object getItem(int i) {
        return cashValues.get(mValues[i].getId());
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        NonScrollingGridView gridView;
        if(view == null) {
            gridView = new NonScrollingGridView(mContext);
            gridView.setPadding(1,1,1,1);
            gridView.setNumColumns(1);
            gridView.setColumnWidth(60);
            gridView.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);;
        } else {
            gridView = (NonScrollingGridView) view;
        }
        ColumnItem item = (ColumnItem) getItem(i);
        gridView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        gridView.setTag(item.currency.getId());
        gridView.setAdapter(item.getAdaptor());
        gridView.setOnTouchListener(new View.OnTouchListener() {
            private int lastTouch = MotionEvent.ACTION_UP;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case (MotionEvent.ACTION_DOWN):
                        lastTouch = MotionEvent.ACTION_DOWN;
                        return true;
                    case (MotionEvent.ACTION_UP):
                        if (lastTouch == MotionEvent.ACTION_DOWN) {
                            updateCashGrid((String) view.getTag(), -1);
                        }
                        lastTouch = MotionEvent.ACTION_UP;
                        return true;
                }
                return false;
            }
        });
        return gridView;
    }

    class NonScrollingGridView extends GridView {
        public NonScrollingGridView(Context context) {
            super(context);
        }

        public NonScrollingGridView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public NonScrollingGridView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                    MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        }
    }

    class ColumnItem {
        private TableListAdaptor adaptor;
        private Currency currency;
        private int StackCount = 5;
        private int count = 0;


        public ColumnItem(Context context, Currency currency) {
            this.currency = currency;
            this.adaptor = new  TableListAdaptor(context);
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

        public TableListAdaptor getAdaptor() {
            return adaptor;
        }

        public void clear() {
            ArrayList list = new ArrayList<Integer>();
            this.adaptor.items = list;
            this.adaptor.notifyDataSetChanged();
            this.count = 0;
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
            v.setScaleX(2);
            v.setScaleY(2);
            // v.setPadding(0, 2, 0, 2);
            v.setOnTouchListener(null);
            v.setFocusableInTouchMode(false);
            return v;
        }

        public void addItem(int value) {
            items.add(value);
        }
    }
}
