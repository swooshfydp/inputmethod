package com.sighs.imputmethod.CashPager;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sighs.imputmethod.CashTableGridViewAdapter;
import com.sighs.imputmethod.Coordinates;
import com.sighs.imputmethod.Currency;
import com.sighs.imputmethod.DragNDropImageView;
import com.sighs.imputmethod.OnDropEventListener;
import com.sighs.imputmethod.R;

import java.util.HashMap;

public class CashPagerAdapter extends PagerAdapter implements OnDropEventListener, View.OnClickListener {
    private final static String LOGKEY = "SWOOSH_INPUT";

    private LayoutInflater mInflater;
    private Currency[] mNotes;
    private View table;
    private CashTableGridViewAdapter tableAdapter;

    public CashPagerAdapter(Context context, Currency[] notes) {
        this.mNotes = notes;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mNotes.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        RelativeLayout itemView = (RelativeLayout) this.mInflater.inflate(R.layout.cashpager_view,
                container, false);
        DragNDropImageView imageView = new DragNDropImageView(container.getContext(),
                this.mNotes[position].getBaseImage(), this.mNotes[position].getBaseImage(),
                container);
        imageView.setOnClickListener(this);
        itemView.addView(imageView);
        container.addView(itemView);
        imageView.setTag(mNotes[position].getId());
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    @Override
    public void onItemDrop(View view, Coordinates c) {
        Coordinates tableCoor = new Coordinates((int) table.getX(), (int) table.getY(),
                table.getWidth(), table.getHeight());
        if(this.tableAdapter != null) {
            if(tableCoor.contains(c)) {
                int res = (int) view.getTag();
                tableAdapter.updateCashGrid((String) view.getTag(), 1);
            }
        }
    }

    public void setTable(View table) {
        this.table = table;
    }

    public void setTableAdapter(CashTableGridViewAdapter adapter) {
        this.tableAdapter = adapter;
    }

    @Override
    public void onClick(View view) {
        tableAdapter.updateCashGrid((String) view.getTag(), 1);
    }
}
