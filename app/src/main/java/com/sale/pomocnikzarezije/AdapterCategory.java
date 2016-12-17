package com.sale.pomocnikzarezije;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Sale on 19.10.2016..
 */
public class AdapterCategory extends ArrayAdapter<Category>{

    private Context context;
    private int layoutResourceId;
    private ArrayList<Category> data = null;
    private static LayoutInflater inflater = null;

    public AdapterCategory(Context context, int resource, ArrayList<Category> objects)
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
            view = inflater.inflate(R.layout.row_categories, null);

        TextView tv = (TextView) view.findViewById(R.id.tvCategoriesName);
        tv.setText(data.get(position).getName());

        ImageView iv = (ImageView) view.findViewById(R.id.ivCategoriesIsPayedFlag);
        if(data.get(position).isPayedThisMonth())
            iv.setBackgroundResource(R.drawable.green_small);
        else
            iv.setBackgroundResource(R.drawable.red_small);

        return view;
    }
}
