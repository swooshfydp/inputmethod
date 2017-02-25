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

public class CashPagerAdapter extends PagerAdapter implements OnDropEventListener, View.OnClickListener  {

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
        container.addView(itemView);
        imageView.setTag(mNotes[position].getId());
        return itemView;
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
        tableAdapter.updateCashGrid((String) view.getTag(), 1);
    }

}