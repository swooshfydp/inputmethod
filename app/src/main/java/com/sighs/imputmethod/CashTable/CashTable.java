package com.sighs.imputmethod.CashTable;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.sighs.imputmethod.models.Currency;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by stuart on 2/6/17.
 */

public class CashTable {
    private final static String LOGKEY = "SWOOSH_INPUT";
    private final TableLayout table;
    private HashMap<String, CashTable.ColumnItem> cashValues = new HashMap<String, CashTable.ColumnItem>();
    private OnCashTableUpdate updateListener = null;
    private ArrayList<TableRow> rows;
    private TableRow tableRow;

    public CashTable(TableLayout table, Currency[] currencies) {
        this.table = table;
        this.tableRow = new TableRow(table.getContext());
        for(Currency currency: currencies) {
            ColumnItem item = new CashTable.ColumnItem(table.getContext(), currency);
            cashValues.put(currency.getId(), item);
            ListView list = new ListView(this.table.getContext());
            list.setAdapter(item.getAdaptor());
            list.setTag(currency.getId());
            list.setOnTouchListener(new View.OnTouchListener() {
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
                        default:
                            lastTouch = motionEvent.getAction();
                            return false;
                    }
                    // return false;
                }
            });
            this.tableRow.addView(list);
        }
        this.table.addView(this.tableRow);
        // this.table.setHorizontalScrollBarEnabled(true);
        // this.table.setVerticalScrollBarEnabled(true);
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

    class ColumnItem {
        private CashTable.TableListAdaptor adaptor;
        private Currency currency;
        private int StackCount = 5;
        private int count = 0;


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
