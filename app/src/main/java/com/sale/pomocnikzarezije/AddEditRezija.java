package com.sale.pomocnikzarezije;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.sale.pomocnikzarezije.db.DBHandler;

import java.text.ParseException;
import java.util.Calendar;

/**
 * Created by Sale on 16.10.2016..
 */
public class AddEditRezija extends AppCompatActivity implements View.OnClickListener {


    private DatePickerDialog pickDateDialog;
    private EditText editDate;
    private EditText editIznos;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_rezije);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //filter na polje iznos - 2 decimalna mjesta
        editIznos = (EditText)findViewById(R.id.editTextIznos);
        editIznos.setFilters(new InputFilter[] {new MoneyValueFilter()});

        //input datum polje s dialogom onClick
        editDate = (EditText)findViewById(R.id.editTextDatum);
        editDate.setInputType(InputType.TYPE_NULL);
        editDate.requestFocus();
        editDate.setOnClickListener(this);

        //default date today
        Calendar calendar = Calendar.getInstance();
        editDate.setText(Utils.dateFormatter.format(calendar.getTime()));

        pickDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                editDate.setText(Utils.dateFormatter.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        id = (getIntent().getIntExtra("rezija_id", 0));
        //ako dolazi edit id rezije
        if (id > 0)
        {
            DBHandler dbHandler = new DBHandler(this);
            Rezije rezija =  dbHandler.getRezijeById(id);

            getSupportActionBar().setTitle(rezija.getCategoryName());

            EditText editText = (EditText)findViewById(R.id.editTextIznos);
            editText.setText(Float.toString(rezija.getAmount()));

            editText = (EditText)findViewById(R.id.editTextDatum);
            editText.setText(Utils.dateFormatter.format(rezija.getDatePayed()));

            editText = (EditText)findViewById(R.id.editTextInfoPlacanje);
            editText.setText(rezija.getPaymentInfo());

            editText = (EditText)findViewById(R.id.editTextPLatitelj);
            editText.setText(rezija.getPlatitelj());

            editText = (EditText)findViewById(R.id.editTextAdrPLatitelj);
            editText.setText(rezija.getAdresaPlatitelja());

            editText = (EditText)findViewById(R.id.editTextPrimatelj);
            editText.setText(rezija.getPrimatelj());

            editText = (EditText)findViewById(R.id.editTextAdrPrimatelj);
            editText.setText(rezija.getAdresaPrimatelja());

            editText = (EditText)findViewById(R.id.editTextIbanPrimatelj);
            editText.setText(rezija.getIbanPrimatelja());

            editText = (EditText)findViewById(R.id.editTextModel);
            editText.setText(rezija.getModel());

            editText = (EditText)findViewById(R.id.editTextPnbp);
            editText.setText(rezija.getPozivNaBrojPrimatelja());

            editText = (EditText)findViewById(R.id.editTextSifraNamjene);
            editText.setText(rezija.getSifraNamjene());

            editText = (EditText)findViewById(R.id.editTextOpisPl);
            editText.setText(rezija.getOpisPlacanja());
        }
        else
        {
            getSupportActionBar().setTitle(getIntent().getExtras().getString("item_name"));
        }
    }

    public void activatePDF417Scanner(View view)
    {
        //provjera instalacije Zxinga
        boolean isZxingInstalled;
        try
        {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo("com.google.zxing.client.android", 0 );
            isZxingInstalled = true;
        }
        catch(PackageManager.NameNotFoundException ex)
        {
            isZxingInstalled = false;
        }

        if(isZxingInstalled)
        {
            try {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.setPackage("com.google.zxing.client.android");
                intent.putExtra("SCAN_FORMATS", "PDF_417");
                startActivityForResult(intent, 0);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            //download zxing
            try
            {
                Toast.makeText(this,getString(R.string.instalirajte_zxing), Toast.LENGTH_SHORT).show();
                Intent DownloadZxing = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.zxing.client.android"));
                startActivity(DownloadZxing);
            }
            catch (Exception e)
            {
                Toast.makeText(this,getString(R.string.instalirajte_zxing), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                try
                {
                    String contents = intent.getStringExtra("SCAN_RESULT");
                    PDF417 pdf417 = new PDF417(contents);

                    EditText editText = (EditText)findViewById(R.id.editTextIznos);
                    editText.setText(Float.toString(pdf417.getAmount()));

                    editText = (EditText)findViewById(R.id.editTextPLatitelj);
                    editText.setText(pdf417.getPlatitelj());

                    editText = (EditText)findViewById(R.id.editTextAdrPLatitelj);
                    editText.setText(pdf417.getAdresaPlatitelja());

                    editText = (EditText)findViewById(R.id.editTextPrimatelj);
                    editText.setText(pdf417.getPrimatelj());

                    editText = (EditText)findViewById(R.id.editTextAdrPrimatelj);
                    editText.setText(pdf417.getAdresaPrimatelja());

                    editText = (EditText)findViewById(R.id.editTextIbanPrimatelj);
                    editText.setText(pdf417.getIbanPrimatelja());

                    editText = (EditText)findViewById(R.id.editTextModel);
                    editText.setText(pdf417.getModel());

                    editText = (EditText)findViewById(R.id.editTextPnbp);
                    editText.setText(pdf417.getPozivNaBrojPrimatelja());

                    editText = (EditText)findViewById(R.id.editTextSifraNamjene);
                    editText.setText(pdf417.getSifraNamjene());

                    editText = (EditText)findViewById(R.id.editTextOpisPl);
                    editText.setText(pdf417.getOpisPlacanja());

                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), R.string.scan_nije_uspio, Toast.LENGTH_LONG).show();
                    return;
                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), R.string.scan_nije_uspio, Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), R.string.scan_nije_uspio, Toast.LENGTH_LONG).show();
        }
    }

    public void addEditRezije(View view) throws ParseException {
        float iznos;

        EditText textIznos = (EditText)findViewById(R.id.editTextIznos);
        iznos = Float.parseFloat(textIznos.getText().toString().trim());

        if (textIznos.getText().toString().trim().isEmpty() || iznos == 0f)
        {
            Toast.makeText(getApplicationContext(), "Niste unijeli iznos!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Rezije rezija = new Rezije();
            rezija.setId(id);
            rezija.setAmount(iznos);

            EditText editText = (EditText)findViewById(R.id.editTextInfoPlacanje);
            rezija.setPaymentInfo(editText.getText().toString().trim());

            editText = (EditText)findViewById(R.id.editTextDatum);
            rezija.setDatePayed(Utils.dateFormatter.parse(editText.getText().toString()));

            editText = (EditText)findViewById(R.id.editTextPLatitelj);
            rezija.setPlatitelj(editText.getText().toString().trim());

            editText = (EditText)findViewById(R.id.editTextAdrPLatitelj);
            rezija.setAdresaPlatitelja(editText.getText().toString().trim());

            editText = (EditText)findViewById(R.id.editTextPrimatelj);
            rezija.setPrimatelj(editText.getText().toString().trim());

            editText = (EditText)findViewById(R.id.editTextAdrPrimatelj);
            rezija.setAdresaPrimatelja(editText.getText().toString().trim());

            editText = (EditText)findViewById(R.id.editTextIbanPrimatelj);
            rezija.setIbanPrimatelja(editText.getText().toString().trim());

            editText = (EditText)findViewById(R.id.editTextModel);
            rezija.setModel(editText.getText().toString().trim());

            editText = (EditText)findViewById(R.id.editTextPnbp);
            rezija.setPozivNaBrojPrimatelja(editText.getText().toString().trim());

            editText = (EditText)findViewById(R.id.editTextSifraNamjene);
            rezija.setSifraNamjene(editText.getText().toString().trim());

            editText = (EditText)findViewById(R.id.editTextOpisPl);
            rezija.setOpisPlacanja(editText.getText().toString().trim());

            rezija.setCategoryId(getIntent().getIntExtra("item_id", 0));

            DBHandler dbHandler = new DBHandler(this);
            try
            {
                dbHandler.addEditRezije(rezija);
                Toast.makeText(getApplicationContext(), R.string.spremljeno, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);

            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(), R.string.greska_baza, Toast.LENGTH_SHORT);
            }
        }

    }

    @Override
    public void onClick(View view)
    {
        pickDateDialog.show();
    }
}
