package com.sighs.imputmethod;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
            if(updateListener != null) updateListener.OnTotalUpdate("" + total);
        }
    }

    public int updateTotals() {
        int total = 0;
        for (ColumnItem item: cashValues.values()) {
            total += item.getTotal();
        }
        return total;
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
            gridView.setPadding(8,8,8,8);
            gridView.setNumColumns(1);
        } else {
            gridView = (NonScrollingGridView) view;
        }
        ColumnItem item = (ColumnItem) getItem(i);
        gridView.setTag(item.currency.getId());
        gridView.setAdapter(item.getAdaptor());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                updateCashGrid((String) view.getTag(), -1);
            }
        });
        return gridView;
    }

    class NonScrollingGridView extends GridView {
        public NonScrollingGridView(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int heightSpec;

            if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
                // The great Android "hackatlon", the love, the magic.
                // The two leftmost bits in the height measure spec have
                // a special meaning, hence we can't use them to describe height.
                heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
            } else {
                heightSpec = heightMeasureSpec;
            }

            super.onMeasure(widthMeasureSpec, heightSpec);
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
            int stack = count;
            if (this.count <= this.StackCount) {
                while (stack > 0) {
                    list.add(this.currency.getBaseImage());
                    stack--;
                }
            } else {
                while (stack > 0) {
                    int rem = stack % this.StackCount;
                    if (rem == 0) list.add(this.currency.getImageSrc(this.StackCount));
                    else list.add(this.currency.getImageSrc(rem));
                    stack -= this.StackCount;
                }
            }
            this.adaptor.items = list;
            this.adaptor.notifyDataSetChanged();
        }

        public TableListAdaptor getAdaptor() {
            return adaptor;
        }
    }

    class TableListAdaptor extends BaseAdapter {
        private ArrayList<Integer> items = new ArrayList<Integer>();
        private LayoutInflater mInflator;
        private Context mContext;

        public TableListAdaptor(Context context) {
            mContext = context;
            mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            DragNDropImageView v = new DragNDropImageView(mContext, items.get(i), items.get(i), viewGroup);
            v.setDimensions(10, 10);
            return v;
        }

        public void addItem(int value) {
            items.add(value);
        }
    }
}
