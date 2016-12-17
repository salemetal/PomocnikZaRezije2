package com.sale.pomocnikzarezije;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Sale on 20.11.2016..
 */
public class AdapterYearly extends ArrayAdapter<RezijeYear> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<RezijeYear> data = null;
    private static LayoutInflater inflater = null;

    public AdapterYearly(Context context, int resource, ArrayList<RezijeYear> objects)
    {
        super(context, resource, objects);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = objects;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null)
            view = inflater.inflate(R.layout.row_yearly, null);

        TextView tv = (TextView) view.findViewById(R.id.tvYearlyCategory);
        tv.setText(data.get(position).getCategoryName());

        tv = (TextView) view.findViewById(R.id.tvYearlyAmount);
        tv.setText(String.format("%.2f", data.get(position).getAmount()));

        Calendar c = Calendar.getInstance();
        int currentMont = c.get(Calendar.MONTH) + 1;

        LinearLayout llMonths = (LinearLayout)view.findViewById(R.id.llRowYearlyMonths);
        llMonths.removeAllViews();

        for (int i = 1; i<=12; i++)
        {
            TextView tvMonth = new TextView(this.getContext());

            if(i<10) tvMonth.setText("  " + Integer.toString(i) + "  ");
            else tvMonth.setText(" " + Integer.toString(i) + " ");


            //coloring
            if(data.get(position).getMonthsPayed().contains(new Integer(i)))
            {
                tvMonth.setBackgroundResource(R.drawable.tv_month_payed);
            }
            else if(i>currentMont)
            {
                tvMonth.setBackgroundResource(R.drawable.tv_month_future);
            }
            else
            {
                tvMonth.setBackgroundResource(R.drawable.tv_month_not_payed);
            }

            tvMonth.setTextSize(12);
            llMonths.addView(tvMonth);
        }
        return view;
    }
}
