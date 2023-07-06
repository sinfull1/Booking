package com.example.demo.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateConvertor {

    public static Integer getToday() {
        long currentTimeMillis = System.currentTimeMillis();

        // Create a Date object using the milliseconds
        Date currentDate = new Date(currentTimeMillis);

        // Format the Date object into the desired format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = dateFormat.format(currentDate);

        // Convert the formatted date to an integer
        int dateAsInteger = Integer.parseInt(formattedDate);
        return dateAsInteger;
    }

    public static List<Integer> getListToday() {
        long currentTimeMillis = System.currentTimeMillis();
        List<Integer> dateIntegers = new ArrayList<>();
        // Create a Date object using the milliseconds
        LocalDate currentDate = LocalDate.now();

        // Get the end date (today + 1 month)
        LocalDate endDate = currentDate.plusMonths(1);
        // Format the Date object into the desired format
        LocalDate date = currentDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        while (!date.isAfter(endDate)) {
            int dateAsInteger = Integer.parseInt(date.format(formatter));
            dateIntegers.add(dateAsInteger);
            date = date.plusDays(1);
        }

        return dateIntegers;
    }

    public static Integer getDateInt(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = dateFormat.format(date);
        int dateAsInteger = Integer.parseInt(formattedDate);
        return dateAsInteger;
    }
}
