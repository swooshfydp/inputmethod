package com.sighs.imputmethod.CashPager;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sighs.imputmethod.CashTable.CashTable;
import com.sighs.imputmethod.MainActivity;
import com.sighs.imputmethod.R;
import com.sighs.imputmethod.ServiceInputMethod;
import com.sighs.imputmethod.customviews.DragNDropImageView;
import com.sighs.imputmethod.customviews.OnDropEventListener;
import com.sighs.imputmethod.models.Coordinates;
import com.sighs.imputmethod.models.Currency;

public class CashPagerAdapter extends PagerAdapter implements View.OnClickListener  {

    private final static String LOGKEY = "SWOOSH_INPUT";

    private Currency[] mNotes;
    private LayoutInflater mInflater;
    private View table;
    private CashTable tableAdapter;

    public CashPagerAdapter(Context context, Currency[] notes) {
        this.mNotes = notes;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LinearLayout itemView = (LinearLayout) this.mInflater.inflate(R.layout.cashpager_view,
                container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.currency);
        imageView.setImageResource(this.mNotes[position].getBaseImage());
        TextView noteText = (TextView) itemView.findViewById(R.id.currencyText);
        noteText.setText("" +this.mNotes[position].getValue());
        itemView.setGravity(Gravity.CENTER);
        imageView.setScaleX(0.8f);
        imageView.setScaleY(0.8f);
        imageView.setOnClickListener(this);
        imageView.setTag(position + "|" + mNotes[position].getId());
        container.addView(itemView);
        return itemView;
    }

    public void setTable(View table) {
        this.table = table;
    }

    public void setTableAdapter(CashTable adapter) {
        this.tableAdapter = adapter;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return mNotes.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public void onClick(View view) {
        String tag = (String) view.getTag();
        tableAdapter.focusOnView(Integer.parseInt(tag.split("\\|")[0]));
        tableAdapter.updateCashGrid(tag.split("\\|")[1], 1);
    }

}