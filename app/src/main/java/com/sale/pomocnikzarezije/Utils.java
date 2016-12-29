package com.sale.pomocnikzarezije;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

/**
 * Created by Sale on 10.4.2016..
 */
public class Utils{

    public static final int BACKUP_NEDDED = 1;
    public static final int BACKUP_NOT_NEDDED = 0;
    public static final String PREFS_FILE_NAME  = "prefs";
    public static final String PREF_BCKP_NAME  = "is_bckp_needed";

    public static boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    public static String getMonth(int month) {

        String monthString;

        switch (month) {
            case 0:  monthString = "Siječanj";
                break;
            case 1:  monthString = "Veljača";
                break;
            case 2:  monthString = "Ožujak";
                break;
            case 3:  monthString = "Travanj";
                break;
            case 4:  monthString = "Svibanj";
                break;
            case 5:  monthString = "Lipanj";
                break;
            case 6:  monthString = "Srpanj";
                break;
            case 7:  monthString = "Kolovoz";
                break;
            case 8:  monthString = "Rujan";
                break;
            case 9: monthString = "Listopad";
                break;
            case 10: monthString = "Studeni";
                break;
            case 11: monthString = "Prosinac";
                break;
            default: monthString = "Invalid month";
                break;
        }

        return monthString;

    }

    public void writeToSharedPrefsInt(Context context, String prefName, int setValue)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(prefName, setValue);
        editor.commit();
    }

    public int readFromSharedPrefsInt(Context context, String prefName, int defaultValue)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPref.getInt(prefName, defaultValue);
    }
}
