package com.sale.pomocnikzarezije.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sale.pomocnikzarezije.Category;
import com.sale.pomocnikzarezije.Rezije;
import com.sale.pomocnikzarezije.RezijeYear;
import com.sale.pomocnikzarezije.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Sale on 10.4.2016..
 */
public class DBHandler extends SQLiteOpenHelper {

    Context context;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Database Version
    private static final int DATABASE_VERSION = 3;
    // Database Name
    public static final String DATABASE_NAME = "REZIJE_DB";

    /**********CATEGORIES TABLE***********************************/
    // Categories table name
    private static final String TABLE_CATEGORIES = "KATEGORIJE";
    // Categories Table Columns names
    private static final String ID = "ID";
    private static final String NAME = "NAME";
    private static final String DATE_CREATE = "DATUM_UNOSA";
    private static final String DATE_EDIT = "DATUM_AZURIRANJA";
    /*************************************************************/

    /**********REZIJE TABLE***********************************/
    // Rezije table name
    private static final String TABLE_REZIJE = "REZIJE";
    // Rezije Table Columns names
    private static final String ID_CATEGORY = "ID_KATEGORIJE";
    private static final String AMOUNT = "IZNOS";
    private static final String DATE_PAYED = "DATUM_PLACANJA";
    //Dodatni podaci -  scan PDF417
    private static final String PLATITELJ = "PLATITELJ";
    private static final String ADR_PLAT = "ADRESA_PLAT";
    private static final String PRIMATELJ = "PRIMATELJ";
    private static final String ADR_PRIM = "ADRESA_PRIM";
    private static final String IBAN_PRIM = "IBAN_PRIM";
    private static final String MODEL = "MODEL";
    private static final String PNBPR = "PNBPR";
    private static final String SIFRA = "SIFRA_NAMJENE";
    private static final String OPIS_PL = "OPIS_PL";


    /*************************************************************/


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**********KATEGORIJE TABLE***********************************/
        String CREATE_KATEGORIJE_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + ID + " INTEGER PRIMARY KEY,"
                + NAME + " TEXT NOT NULL UNIQUE,"
                + DATE_CREATE + " DATETIME,"
                + DATE_EDIT + " DATETIME)";
        db.execSQL(CREATE_KATEGORIJE_TABLE);
        /**********REZIJE TABLE***********************************/
        String CREATE_REZTIJE_TABLE = "CREATE TABLE " + TABLE_REZIJE + "("
                + ID + " INTEGER PRIMARY KEY,"
                + ID_CATEGORY + " INTEGER NOT NULL,"
                + DATE_PAYED + " DATETIME NOT NULL,"
                + AMOUNT + " REAL NOT NULL,"
                + PLATITELJ + " TEXT,"
                + ADR_PLAT + " TEXT,"
                + PRIMATELJ + " TEXT,"
                + ADR_PRIM + " TEXT,"
                + IBAN_PRIM + " TEXT,"
                + MODEL + " TEXT,"
                + PNBPR + " TEXT,"
                + SIFRA + " TEXT,"
                + OPIS_PL + " TEXT,"
                + DATE_CREATE + " DATETIME,"
                + DATE_EDIT + " DATETIME,"
                + " FOREIGN KEY (" + ID_CATEGORY + ") REFERENCES " + TABLE_CATEGORIES + "(" + ID +"))";
        db.execSQL(CREATE_REZTIJE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REZIJE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);

        // Creating tables again
        onCreate(db);
    }

    public void addCategory(Category category) {

        Date date = new Date();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, category.getName().toUpperCase());
        values.put(DATE_CREATE, dateFormat.format(date));
        values.put(DATE_EDIT, dateFormat.format(date));
        // Inserting Row
        db.insert(TABLE_CATEGORIES, null, values);
        db.close();

        Utils utils = new Utils();
        setBackupNeeded();
    }



    public ArrayList<Category> getAllCategories() {

        ArrayList<Category> categoryList = new ArrayList();

        String selectQuery = "SELECT " + ID + ", "
                + NAME + ", "
                + "(select count(*) from " + TABLE_REZIJE
                + " where strftime('%Y-%m', date('now')) = strftime('%Y-%m', " + DATE_PAYED + ")"
                + " and " + ID_CATEGORY + " = " + TABLE_CATEGORIES + "." + ID + ")"
                + " FROM "
                + TABLE_CATEGORIES
                + " ORDER BY " + NAME;

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    Category category = new Category();
                    category.setId(cursor.getInt(0));
                    category.setName(cursor.getString(1));

                    boolean isPayedThisMonth = cursor.getInt(2) > 0 ? true : false;
                    category.setPayedThisMonth(isPayedThisMonth);

                    categoryList.add(category);
                } while (cursor.moveToNext());
            }

            return categoryList;
    }

    public int updateCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, category.getName());
        // updating row
        return db.update(TABLE_CATEGORIES, values, ID + " = ?",
                new String[]{String.valueOf(category.getId())});
    }

    public void deleteCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REZIJE, ID_CATEGORY + " = ?",
                new String[]{String.valueOf(category.getId())});
        db.delete(TABLE_CATEGORIES, ID + " = ?",
                new String[]{String.valueOf(category.getId())});
        db.close();
        setBackupNeeded();
    }

    public void addEditRezije(Rezije rezija)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Date date = new Date();
        ContentValues values = new ContentValues();

        values.put(DATE_PAYED, dateFormat.format(rezija.getDatePayed()));
        values.put(AMOUNT, rezija.getAmount());
        values.put(PLATITELJ, rezija.getPlatitelj());
        values.put(ADR_PLAT, rezija.getAdresaPlatitelja());
        values.put(PRIMATELJ, rezija.getPrimatelj());
        values.put(ADR_PRIM, rezija.getAdresaPrimatelja());
        values.put(IBAN_PRIM, rezija.getIbanPrimatelja());
        values.put(MODEL, rezija.getModel());
        values.put(PNBPR, rezija.getPozivNaBrojPrimatelja());
        values.put(SIFRA, rezija.getSifraNamjene());
        values.put(OPIS_PL, rezija.getOpisPlacanja());
        values.put(DATE_EDIT, dateFormat.format(date));

        if (rezija.getId() > 0) //update
        {
            db.update(TABLE_REZIJE, values, ID + " = " + rezija.getId(), null);
        }
        else //add
        {
            values.put(DATE_CREATE, dateFormat.format(date));
            values.put(ID_CATEGORY, rezija.getCategoryId());
            // Inserting Row
            db.insert(TABLE_REZIJE, null, values);
        }
        db.close();
        setBackupNeeded();
    }

    public Rezije getRezijeById(int id)
    {
        Rezije rezija = new Rezije();

        String selectQuery = "SELECT "
                + ID_CATEGORY + ","
                + DATE_PAYED + ","
                + AMOUNT + ","
                + PLATITELJ + ","
                + ADR_PLAT + ","
                + PRIMATELJ + ","
                + ADR_PRIM + ","
                + IBAN_PRIM + ","
                + MODEL + ","
                + PNBPR + ","
                + SIFRA + ","
                + OPIS_PL + ","
                + DATE_CREATE + ","
                + DATE_EDIT
                + " FROM " + TABLE_REZIJE
                + " WHERE " + ID +  "= " + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst())
        {
            rezija.setId(id);
            rezija.setCategoryId(cursor.getInt(0));
            try{
                rezija.setDatePayed(dateFormat.parse(cursor.getString(1)));
            } catch (Exception ex) {}
            rezija.setAmount(cursor.getFloat(2));
            rezija.setPlatitelj(cursor.getString(3));
            rezija.setAdresaPlatitelja(cursor.getString(4));
            rezija.setPrimatelj(cursor.getString(5));
            rezija.setAdresaPrimatelja(cursor.getString(6));
            rezija.setIbanPrimatelja(cursor.getString(7));
            rezija.setModel(cursor.getString(8));
            rezija.setPozivNaBrojPrimatelja(cursor.getString(9));
            rezija.setSifraNamjene(cursor.getString(10));
            rezija.setOpisPlacanja(cursor.getString(11));
            try{
                rezija.setDateCreated(dateFormat.parse(cursor.getString(12)));
            } catch (Exception ex) {}
            try{
                rezija.setDateEdited(dateFormat.parse(cursor.getString(13)));
            } catch (Exception ex) {}

        }

        return rezija;
    }

    public ArrayList<Rezije> getRezijeByMonthYear(int month, int year) {

        String monthStr = String.format("%02d", month);
        String yearMonth = year + "-" + monthStr;

        ArrayList<Rezije> rezijeList = new ArrayList();

        String selectQuery = "SELECT " + TABLE_REZIJE + "." + ID + ","
                + TABLE_REZIJE + "." + AMOUNT + ","
                + TABLE_REZIJE + "." + DATE_PAYED + ","
                + TABLE_CATEGORIES + "." + NAME
                + " FROM " + TABLE_REZIJE
                + " INNER JOIN " + TABLE_CATEGORIES + " ON " + TABLE_REZIJE + "." + ID_CATEGORY + " = " + TABLE_CATEGORIES + "." + ID
                + " WHERE strftime('%Y-%m'," + TABLE_REZIJE + "." + DATE_PAYED + ") " + " = " + "'" + yearMonth +"'"
                + " ORDER BY " + TABLE_REZIJE + "." + DATE_PAYED + " DESC " + "," + TABLE_CATEGORIES + "." + NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Rezije rezije = new Rezije();
                rezije.setId(cursor.getInt(0));
                rezije.setAmount(cursor.getFloat(1));
                try {
                    rezije.setDatePayed(dateFormat.parse(cursor.getString(2)));
                } catch (Exception ex){}

                rezije.setCategoryName(cursor.getString(3));
                rezijeList.add(rezije);
            } while (cursor.moveToNext());
        }

        return rezijeList;
    }

    public ArrayList<RezijeYear> getRezijeByYear(int year) {

        ArrayList<RezijeYear> rezijeList = new ArrayList();

        String selectQuery = "SELECT "
                + "SUM(" + TABLE_REZIJE + "." + AMOUNT + ") AS TOTAL,"
                + TABLE_CATEGORIES + "." + NAME
                + " FROM " + TABLE_REZIJE
                + " INNER JOIN " + TABLE_CATEGORIES + " ON " + TABLE_REZIJE + "." + ID_CATEGORY + " = " + TABLE_CATEGORIES + "." + ID
                + " WHERE strftime('%Y'," + TABLE_REZIJE + "." + DATE_PAYED + ") " + " = " + "'" + year +"'"
                + " GROUP BY " + TABLE_CATEGORIES + "." + NAME
                + " ORDER BY TOTAL DESC, " + TABLE_CATEGORIES + "." + NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                RezijeYear rezije = new RezijeYear();
                rezije.setAmount(cursor.getFloat(0));
                rezije.setCategoryName(cursor.getString(1));
                rezijeList.add(rezije);
            } while (cursor.moveToNext());
        }

        cursor.close();

        if(!rezijeList.isEmpty()) {
            String selectQueryMonthspayed = "SELECT "
                    + TABLE_CATEGORIES + "." + NAME + ", "
                    + "strftime('%m'," + DATE_PAYED + ") AS MONTH_PAYED"
                    + " FROM " + TABLE_REZIJE
                    + " INNER JOIN " + TABLE_CATEGORIES + " ON " + TABLE_REZIJE + "." + ID_CATEGORY + " = " + TABLE_CATEGORIES + "." + ID
                    + " WHERE strftime('%Y'," + TABLE_REZIJE + "." + DATE_PAYED + ") " + " = " + "'" + year + "'"
                    + " ORDER BY " + TABLE_CATEGORIES + "." + NAME + ", " + "MONTH_PAYED";

            cursor = db.rawQuery(selectQueryMonthspayed, null);

            if (cursor.moveToFirst()) {
                do {
                    for (RezijeYear rezijeYear : rezijeList)
                    {
                        if(rezijeYear.getCategoryName().equals(cursor.getString(0)))
                        {
                            rezijeYear.addToMonthsPayed(cursor.getInt(1));
                        }
                    }

                } while (cursor.moveToNext());
            }
        }
        return rezijeList;
    }

    public void deleteRezija(Rezije rezije) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REZIJE, ID + " = ?",
                new String[]{String.valueOf(rezije.getId())});
        db.close();
        setBackupNeeded();
    }

    public Integer[] getAllYearsInDB()
    {
        List<Integer> years = new ArrayList();

        String selectQuery = "select distinct strftime('%Y', " + DATE_PAYED + ") as year from " + TABLE_REZIJE + " union select strftime('%Y', CURRENT_TIMESTAMP) as year order by year desc";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                years.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        Integer[] yearsArray = new Integer[years.size()];
        for (int i=0; i < yearsArray.length; i++)
        {
            yearsArray[i] = years.get(i).intValue();
        }
        return yearsArray;
    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
    }

    private void setBackupNeeded()
    {
        SharedPreferences sharedPref = context.getSharedPreferences(Utils.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(Utils.PREF_BCKP, true);
        editor.commit();
    }
}
