package com.sale.pomocnikzarezije;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

/**
 * Created by Sale on 19.10.2016..
 */
public class TabMjesecno extends Fragment{

    private DBHandler dbHandler;
    private ArrayList<Rezije> rezijeList;
    int longClickedItemIndex;
    private static final int EDIT = 0, DELETE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view  = inflater.inflate(R.layout.tab_mjesecno, container, false);

        bindData(view);

        return view;
    }

    private void bindData(final View view) {
        Spinner spinnerMonth = (Spinner)view.findViewById(R.id.spinnerMonth);
        Spinner spinnerYear = (Spinner)view.findViewById(R.id.spinnerYear);

        Calendar calendar = Calendar.getInstance();
        int thisMonth = calendar.get(Calendar.MONTH);
        int thisYear = calendar.get(Calendar.YEAR);

        //add months
        Integer[] monthItems = new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12};
        ArrayAdapter<Integer> monthAdapter = new ArrayAdapter(this.getContext(),android.R.layout.simple_spinner_item, monthItems);
        spinnerMonth.setAdapter(monthAdapter);

        //add years
        DBHandler dbHandler = new DBHandler(getContext());
        Integer[] yearItems = dbHandler.getAllYearsInDB();

        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter(this.getContext(),android.R.layout.simple_spinner_item, yearItems);
        spinnerYear.setAdapter(yearAdapter);

        //spinners set now
        spinnerMonth.setSelection(monthAdapter.getPosition(thisMonth+1));
        spinnerYear.setSelection(yearAdapter.getPosition(thisYear));

        //set button listener
        Button button = (Button)view.findViewById(R.id.btnGetMonthly);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spinnerMonth = (Spinner)view.findViewById(R.id.spinnerMonth);
                Spinner spinnerYear = (Spinner)view.findViewById(R.id.spinnerYear);

                int month = Integer.parseInt(spinnerMonth.getSelectedItem().toString());
                int year = Integer.parseInt(spinnerYear.getSelectedItem().toString());

                setData(view, month, year);
            }
        });

        setData(view, thisMonth + 1, thisYear);
    }

    private void setData(final View view, int month, int year) {

        dbHandler = new DBHandler(this.getContext());
        rezijeList = dbHandler.getRezijeByMonthYear(month, year);
        ListView lv = (ListView) view.findViewById(R.id.listViewMjesecno);

        if (rezijeList.isEmpty())
        {
            lv.setAdapter(null);

            LinearLayout llTotal = (LinearLayout)view.findViewById(R.id.llMonthlyTotal);
            llTotal.setVisibility(View.GONE);

            Toast.makeText(this.getContext(), R.string.msgNemaRezultataZaMjesec, Toast.LENGTH_LONG).show();
        }

        else {

            AdapterMonthly adapter = new AdapterMonthly(
                    this.getContext(),
                    android.R.layout.simple_list_item_1,
                    rezijeList);

            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view1, int position, long id) {
                    longClickedItemIndex = position;
                    return false;
                }
            });

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                    Rezije rezija = rezijeList.get(position);

                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View dialoglayout = inflater.inflate(R.layout.rezija_dialog, null);

                    TextView textView = (TextView)dialoglayout.findViewById(R.id.tvRezijaDialogIznos);
                    textView.setText(Float.toString(rezija.getAmount()) + " HRK");

                    textView = (TextView)dialoglayout.findViewById(R.id.tvRezijaDialogDatum);
                    textView.setText(Utils.dateFormatter.format(rezija.getDatePayed()));

                    textView = (TextView)dialoglayout.findViewById(R.id.tvRezijaDialogInfo);
                    textView.setText(rezija.getPaymentInfo());

                    textView = (TextView)dialoglayout.findViewById(R.id.tvRezijaDialogPlatitelj);
                    textView.setText(rezija.getPlatitelj());

                    textView = (TextView)dialoglayout.findViewById(R.id.tvRezijaDialogAdrPlat);
                    textView.setText(rezija.getAdresaPlatitelja());

                    textView = (TextView)dialoglayout.findViewById(R.id.tvRezijaDialogPrimatelj);
                    textView.setText(rezija.getPrimatelj());

                    textView = (TextView)dialoglayout.findViewById(R.id.tvRezijaDialogAdrPrim);
                    textView.setText(rezija.getAdresaPrimatelja());

                    textView = (TextView)dialoglayout.findViewById(R.id.tvRezijaDialogIbanPrim);
                    textView.setText(rezija.getIbanPrimatelja());

                    textView = (TextView)dialoglayout.findViewById(R.id.tvRezijaDialogModel);
                    textView.setText(rezija.getModel());

                    textView = (TextView)dialoglayout.findViewById(R.id.tvRezijaPnbp);
                    textView.setText(rezija.getPozivNaBrojPrimatelja());

                    textView = (TextView)dialoglayout.findViewById(R.id.tvRezijaSifraNamjene);
                    textView.setText(rezija.getSifraNamjene());

                    textView = (TextView)dialoglayout.findViewById(R.id.tvRezijaOpisPlacanja);
                    textView.setText(rezija.getOpisPlacanja());

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setView(dialoglayout);
                    builder.setTitle(rezija.getCategoryName());

                    builder.setNegativeButton(R.string.zatvori, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                        }
                    }).show();

                }
            });

            lv.setAdapter(adapter);
            registerForContextMenu(lv);

            //get total
            float totalAmount = 0;
            for (Rezije item:rezijeList)
            {
                totalAmount += item.getAmount();
            }

            LinearLayout llTotal = (LinearLayout)view.findViewById(R.id.llMonthlyTotal);
            llTotal.setVisibility(View.VISIBLE);

            TextView total = (TextView)view.findViewById(R.id.tvMonthlyTotal);
            total.setText(String.format("%.2f", totalAmount));
        }
    }

    public void refresh() {
        bindData(getView());
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, view, menuInfo);

        menu.add(Menu.NONE, EDIT, menu.NONE, R.string.uredi);
        menu.add(Menu.NONE, DELETE, menu.NONE, R.string.obrisi);
    }

    public boolean onContextItemSelected(MenuItem item) {

        if (getUserVisibleHint())
        {
            Rezije rezije = rezijeList.get(longClickedItemIndex);

            switch (item.getItemId()) {
                case EDIT:
                    Intent intent = new Intent(getActivity(), AddEditRezija.class);
                    intent.putExtra("rezija_id", rezije.getId());
                    startActivity(intent);
                    break;

                case DELETE:
                    confirmDialogDeleteRezija(rezije, dbHandler, this.getView());
                    break;
            }
            return super.onContextItemSelected(item);
        }
        return false;
    }

    private void confirmDialogDeleteRezija(final Rezije rezije, final DBHandler dbHandler, final View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());

        try {
            builder
                    .setMessage("Jeste li sigurni?")
                    .setPositiveButton(R.string.da, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dbHandler.deleteRezija(rezije);

                            Toast.makeText(v.getContext(), R.string.obrisano, Toast.LENGTH_SHORT)
                                    .show();

                            bindData(v);
                        }
                    })
                    .setNegativeButton(R.string.ne, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .show();
        } catch (Exception ex) {
            throw ex;
        }
    }
}
