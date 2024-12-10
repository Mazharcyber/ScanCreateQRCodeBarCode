package com.example.barcodeapplictaion.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Time {

    public static String time(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a dd-MMM-yyyy ");
        String dateTime = simpleDateFormat.format(calendar.getTime());
        return dateTime;
    }
}
