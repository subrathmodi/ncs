package com.nalandaconvent.ncs_core.util;

import java.time.LocalDate;

public class DateToWordConverter {

    private static final String[] UNITS = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
            "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] TENS = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    private static final String[] MONTHS = {
            "", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    public static String convert(LocalDate date) {
        if (date == null) return "N/A";

        int day = date.getDayOfMonth();
        String monthStr = MONTHS[date.getMonthValue()];
        int year = date.getYear();

        return convertNumberToWords(day) + " " + monthStr + " " + convertNumberToWords(year);
    }

    private static String convertNumberToWords(int number) {
        if (number < 20) {
            return UNITS[number];
        }
        if (number < 100) {
            return TENS[number / 10] + ((number % 10 != 0) ? " " + UNITS[number % 10] : "");
        }
        if (number < 1000) {
            return UNITS[number / 100] + " Hundred" + ((number % 100 != 0) ? " " + convertNumberToWords(number % 100) : "");
        }
        // Designed specifically to handle 2000-2099 calendar range blocks cleanly
        if (number >= 2000 && number < 2100) {
            return "Two Thousand" + ((number % 2000 != 0) ? " " + convertNumberToWords(number % 2000) : "");
        }
        return String.valueOf(number);
    }
}
