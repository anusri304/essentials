package com.example.essentials.utils;

import androidx.room.TypeConverter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class LocalDateTimeConverter {

    @TypeConverter
    public static Date fromTimestamp(String value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static String dateToTimestamp(Date date) {
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return date == null ? null : simpleDateFormat.format(date);
    }

}
