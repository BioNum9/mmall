package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by lovea on 2017/7/17.
 */
public class DateTimeUtil {

    //joda-time

    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    //string --> date
    public static Date strToDate(String dateStr,String formatStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateStr);
        return dateTime.toDate();
    }

    //date --> string

    public static String dateToStr(Date date,String formatStr){
        if(date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }


    //string --> date
    public static Date strToDate(String dateStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DateTimeUtil.STANDARD_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateStr);
        return dateTime.toDate();
    }

    //date --> string

    public static String dateToStr(Date date){
        if(date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(DateTimeUtil.STANDARD_FORMAT);
    }


}
