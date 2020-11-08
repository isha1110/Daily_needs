package com.skinfotech.dailyneeds;

import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class Utility {

    public static boolean isEmpty(String text) {
        return (text == null || text.isEmpty());
    }

    public static boolean isNotEmpty(String text) {
        return !isEmpty(text);
    }

    public static boolean isEmpty(EditText view) {
        return (view == null || "".equals(view.getText().toString()) || view.getText().toString().isEmpty());
    }

    public static boolean isEmpty(List list) {
        return (list == null || list.isEmpty());
    }

    public static boolean isNotEmpty(List list) {
        return !(isEmpty(list));
    }

    public static String toCamelCase(final String init) {
        if (init == null) {
            return "";
        }
        final StringBuilder ret = new StringBuilder(init.length());
        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(Character.toUpperCase(word.charAt(0)));
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length() == init.length())) {
                ret.append(" ");
            }
        }
        return ret.toString();
    }

    public static String getAmountInCurrencyFormat(String amountStr) {
        if (isEmpty(amountStr)) {
            return "";
        }
        double amount = Double.parseDouble(amountStr);
        if (amount < 0) {
            amount *= -1;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String retVal = NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(Double.parseDouble(decimalFormat.format(amount)));
        return retVal.substring(2);
    }
}
