package com.sale.pomocnikzarezije;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Sale on 6.11.2016..
 */
public class AdapterMonthly extends ArrayAdapter<Rezije> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<Rezije> data = null;
    private static LayoutInflater inflater = null;

    public AdapterMonthly(Context context, int resource, ArrayList<Rezije> objects)
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
            view = inflater.inflate(R.layout.row_monthly, null);

        TextView tv = (TextView) view.findViewById(R.id.tvMonthlyCategory);
        tv.setText(data.get(position).getCategoryName());

        tv = (TextView) view.findViewById(R.id.tvMonthlyAmount);
        tv.setText(String.format("%.2f", data.get(position).getAmount()));

        tv = (TextView) view.findViewById(R.id.tvMonthlyDatePayed);
        tv.setText(context.getString(R.string.placeno) + new SimpleDateFormat("dd.MM.yyyy").format(data.get(position).getDatePayed()));

        return view;
    }


}
