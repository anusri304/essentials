package com.example.essentials.utils;

import androidx.room.TypeConverter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocalDateTimeConverter {

    @TypeConverter
    public static Date fromTimestamp(String value) {
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date formatedDate =null;
        try {
            formatedDate = simpleDateFormat.parse(value);
        }
        catch(Exception e){
            APIUtils.getFirebaseCrashlytics().recordException(e);
        }
        return formatedDate;
    }

    @TypeConverter
    public static String dateToTimestamp(Date date) {
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return date == null ? null : simpleDateFormat.format(date);
    }

}
