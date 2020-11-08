package com.example.exchange_rate.util;

import android.icu.util.Calendar;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class XAxisValueFormatter extends IndexAxisValueFormatter {

    private SimpleDateFormat mFormat = new SimpleDateFormat("yy/MM/dd");

    private Date startDate;

    public XAxisValueFormatter(Date date) {
        this.startDate = date;
    }

    @Override
    public String getFormattedValue(float value) {
//        long millis = TimeUnit.DAYS.toMillis((long) value);
//        Date date = new Date((long) value);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, (int) value);
        Date date = calendar.getTime();

        return mFormat.format(date);
//        return mFormat.format(new Date((long) value));
    }



}
