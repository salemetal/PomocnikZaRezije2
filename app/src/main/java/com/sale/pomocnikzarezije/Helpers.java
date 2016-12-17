package com.sale.pomocnikzarezije;

import android.widget.EditText;

/**
 * Created by Sale on 10.4.2016..
 */
public class Helpers {

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
}
