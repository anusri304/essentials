package com.example.essentials.utils;

import androidx.room.TypeConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LocalDateTimeConverter {

    @TypeConverter
    public static Date fromTimestamp(String value) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ApplicationConstants.DATE_PATTERN, Locale.US);
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

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ApplicationConstants.DATE_PATTERN,Locale.US);
        return date == null ? null : simpleDateFormat.format(date);
    }

}
