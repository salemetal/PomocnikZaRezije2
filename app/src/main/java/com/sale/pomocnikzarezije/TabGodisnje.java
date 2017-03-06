package com.sale.pomocnikzarezije;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.List;

/**
 * Created by Sale on 19.10.2016..
 */
public class TabGodisnje extends Fragment {

    private DBHandler dbHandler;
    private ArrayList<RezijeYear> rezijeList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.tab_godisnje, container, false);

        bindData(view);

        return view;
    }

    private void bindData(final View view) {
        Spinner spinnerYear = (Spinner) view.findViewById(R.id.spinnerYearYear);

        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);

        //add years
        DBHandler dbHandler = new DBHandler(getContext());
        Integer[] yearItems = dbHandler.getAllYearsInDB();

        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_item, yearItems);
        spinnerYear.setAdapter(yearAdapter);

        //spinners set now
        spinnerYear.setSelection(yearAdapter.getPosition(thisYear));

        //set button listener
        Button button = (Button) view.findViewById(R.id.btnGetYearly);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spinnerYear = (Spinner) view.findViewById(R.id.spinnerYearYear);

                int year = Integer.parseInt(spinnerYear.getSelectedItem().toString());

                setData(view, year);
            }
        });
        setData(view, thisYear);
    }

    private void setData(final View view, final int year) {

        dbHandler = new DBHandler(this.getContext());
        rezijeList = dbHandler.getRezijeByYear(year);
        ListView lv = (ListView) view.findViewById(R.id.listViewGodisnje);

        if (rezijeList.isEmpty()) {
            lv.setAdapter(null);

            LinearLayout llTotal = (LinearLayout) view.findViewById(R.id.llYearlyTotal);
            llTotal.setVisibility(View.GONE);

            Toast.makeText(this.getContext(), R.string.msgNemaRezultataZaGodinu, Toast.LENGTH_LONG).show();
        } else {
            AdapterYearly adapter = new AdapterYearly(
                    this.getContext(),
                    android.R.layout.simple_list_item_1,
                    rezijeList);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                    RezijeYear rezija = rezijeList.get(position);

                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View dialoglayout = inflater.inflate(R.layout.yearly_dialog, null);

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setView(dialoglayout);
                    builder.setTitle(rezija.getCategoryName());

                    List<Float> amountsMonthly = dbHandler.getMonthlyAmountsByYear(year, rezija.getCategoryId());

                    TextView textView = null;

                    if (amountsMonthly.get(0) != null && amountsMonthly.get(0) > 0) {
                        textView = (TextView) dialoglayout.findViewById(R.id.YearlyDialogTvSijecanj);
                        textView.setText(String.format("%.2f", amountsMonthly.get(0)) + " HRK");
                    }

                    if (amountsMonthly.get(1) != null && amountsMonthly.get(1) > 0) {
                        textView = (TextView) dialoglayout.findViewById(R.id.yearlyDialogTvVeljaca);
                        textView.setText(String.format("%.2f", amountsMonthly.get(1)) + " HRK");
                    }
                    if (amountsMonthly.get(2) != null && amountsMonthly.get(2) > 0) {
                        textView = (TextView) dialoglayout.findViewById(R.id.yearlyDialogTvOzujak);
                        textView.setText(String.format("%.2f", amountsMonthly.get(2)) + " HRK");
                    }
                    if (amountsMonthly.get(3) != null && amountsMonthly.get(3) > 0) {
                        textView = (TextView) dialoglayout.findViewById(R.id.yearlyDialogTvTravanj);
                        textView.setText(String.format("%.2f", amountsMonthly.get(3)) + " HRK");
                    }
                    if (amountsMonthly.get(4) != null && amountsMonthly.get(4) > 0) {
                        textView = (TextView) dialoglayout.findViewById(R.id.yearlyDialogTvSvibanj);
                        textView.setText(String.format("%.2f", amountsMonthly.get(4)) + " HRK");
                    }
                    if (amountsMonthly.get(5) != null && amountsMonthly.get(5) > 0) {
                        textView = (TextView) dialoglayout.findViewById(R.id.yearlyDialogTvLipanj);
                        textView.setText(String.format("%.2f", amountsMonthly.get(5)) + " HRK");
                    }
                    if (amountsMonthly.get(6) != null && amountsMonthly.get(6) > 0) {
                        textView = (TextView) dialoglayout.findViewById(R.id.yearlyDialogTvSrpanj);
                        textView.setText(String.format("%.2f", amountsMonthly.get(6)) + " HRK");
                    }
                    if (amountsMonthly.get(7) != null && amountsMonthly.get(7) > 0) {
                        textView = (TextView) dialoglayout.findViewById(R.id.yearlyDialogTvKolovoz);
                        textView.setText(String.format("%.2f", amountsMonthly.get(7)) + " HRK");
                    }
                    if (amountsMonthly.get(8) != null && amountsMonthly.get(8) > 0) {
                        textView = (TextView) dialoglayout.findViewById(R.id.yearlyDialogTvRujan);
                        textView.setText(String.format("%.2f", amountsMonthly.get(8)) + " HRK");
                    }
                    if (amountsMonthly.get(9) != null && amountsMonthly.get(9) > 0) {
                        textView = (TextView) dialoglayout.findViewById(R.id.yearlyDialogTvListopad);
                        textView.setText(String.format("%.2f", amountsMonthly.get(9)) + " HRK");
                    }
                    if (amountsMonthly.get(10) != null && amountsMonthly.get(10) > 0) {
                        textView = (TextView) dialoglayout.findViewById(R.id.yearlyDialogTvStudeni);
                        textView.setText(String.format("%.2f", amountsMonthly.get(10)) + " HRK");
                    }
                    if (amountsMonthly.get(11) != null && amountsMonthly.get(11) > 0) {
                        textView = (TextView) dialoglayout.findViewById(R.id.yearlyDialogTvProsinac);
                        textView.setText(String.format("%.2f", amountsMonthly.get(11)) + " HRK");
                    }

                    Float total = 0f;
                    for(int i = 0; i < amountsMonthly.size(); i++)
                    {
                        total += amountsMonthly.get(i);
                    }

                    textView = (TextView) dialoglayout.findViewById(R.id.yearlyDialogTvTotal);
                    textView.setText(String.format("%.2f", total) + " HRK");

                    builder.setNegativeButton(R.string.zatvori, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                        }
                    }).show();

                }
            });

            lv.setAdapter(adapter);

            //get total
            float totalAmount = 0;
            for (RezijeYear item : rezijeList) {
                totalAmount += item.getAmount();
            }

            LinearLayout llTotal = (LinearLayout) view.findViewById(R.id.llYearlyTotal);
            llTotal.setVisibility(View.VISIBLE);

            TextView total = (TextView) view.findViewById(R.id.tvYearlyTotal);
            total.setText("HRK " + String.format("%.2f", totalAmount));
        }
    }

    public void refresh() {
        bindData(this.getView());
    }
}
