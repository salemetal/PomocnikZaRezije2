package com.sale.pomocnikzarezije;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sale.pomocnikzarezije.db.DBHandler;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Sale on 19.10.2016..
 */
public class TabGodisnje extends Fragment{

    private DBHandler dbHandler;
    private ArrayList<RezijeYear> rezijeList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view  = inflater.inflate(R.layout.tab_godisnje, container, false);

        bindData(view);

        return view;
    }

    private void bindData(final View view)
    {
        Spinner spinnerYear = (Spinner)view.findViewById(R.id.spinnerYearYear);

        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);

        //add years
        Integer[] yearItems = new Integer[25];
        for(int i = 0; i <= 24; i++)
        {
            yearItems[i] = thisYear - i;
        }
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter(this.getContext(),android.R.layout.simple_spinner_item, yearItems);
        spinnerYear.setAdapter(yearAdapter);

        //spinners set now
        spinnerYear.setSelection(0);

        //set button listener
        Button button = (Button)view.findViewById(R.id.btnGetYearly);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spinnerYear = (Spinner)view.findViewById(R.id.spinnerYearYear);

                int year = Integer.parseInt(spinnerYear.getSelectedItem().toString());

                setData(view, year);
            }
        });
        setData(view, thisYear);
    }

    private void setData(View view, int year) {

        dbHandler = new DBHandler(this.getContext());
        rezijeList = dbHandler.getRezijeByYear(year);
        ListView lv = (ListView) view.findViewById(R.id.listViewGodisnje);

        if (rezijeList.isEmpty())
        {
            lv.setAdapter(null);

            LinearLayout llTotal = (LinearLayout)view.findViewById(R.id.llYearlyTotal);
            llTotal.setVisibility(View.GONE);

            Toast.makeText(this.getContext(), R.string.msgNemaRezultataZaGodinu, Toast.LENGTH_LONG).show();
        }

        else {
            AdapterYearly adapter = new AdapterYearly(
                    this.getContext(),
                    android.R.layout.simple_list_item_1,
                    rezijeList);

            lv.setAdapter(adapter);

            //get total
            float totalAmount = 0;
            for (RezijeYear item:rezijeList)
            {
                totalAmount += item.getAmount();
            }

            LinearLayout llTotal = (LinearLayout)view.findViewById(R.id.llYearlyTotal);
            llTotal.setVisibility(View.VISIBLE);

            TextView total = (TextView)view.findViewById(R.id.tvYearlyTotal);
            total.setText(String.format("%.2f", totalAmount));
        }
    }

    public void refresh() {
        bindData(this.getView());
    }
}
