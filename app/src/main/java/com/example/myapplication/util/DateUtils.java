package com.example.myapplication.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    /**
     * Format UTC date string to Vietnam timezone (UTC+7)
     * @param utcDateString UTC date string in format "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
     * @return Formatted date string in Vietnam timezone "dd/MM/yyyy HH:mm"
     */
    public static String formatToVietnamTime(String utcDateString) {
        if (utcDateString == null || utcDateString.isEmpty()) {
            return "";
        }

        try {
            // Parse the UTC date
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = inputFormat.parse(utcDateString);
            
            // Convert to Vietnam timezone (UTC+7)
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            outputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            
            return outputFormat.format(date);
        } catch (ParseException e) {
            // Fallback: try different input formats
            return tryFallbackFormats(utcDateString);
        }
    }

    /**
     * Try different date formats as fallback
     */
    private static String tryFallbackFormats(String dateString) {
        String[] inputFormats = {
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss"
        };

        for (String format : inputFormats) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat(format, Locale.getDefault());
                inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = inputFormat.parse(dateString);
                
                // Convert to Vietnam timezone
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                outputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
                
                return outputFormat.format(date);
            } catch (ParseException ignored) {
                // Continue to next format
            }
        }

        // Last resort: manual addition of 7 hours
        return addSevenHoursManually(dateString);
    }

    /**
     * Manually add 7 hours to the date string
     */
    private static String addSevenHoursManually(String dateString) {
        try {
            SimpleDateFormat fallbackFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault());
            Date date = fallbackFormat.parse(dateString);
            
            // Add 7 hours for Vietnam timezone
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, 7);
            
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return outputFormat.format(calendar.getTime());
        } catch (ParseException ex) {
            return dateString; // Return original if all parsing fails
        }
    }

    /**
     * Format date for display with custom format
     * @param utcDateString UTC date string
     * @param outputFormat Custom output format (e.g., "dd/MM/yyyy", "HH:mm")
     * @return Formatted date string in Vietnam timezone
     */
    public static String formatToVietnamTime(String utcDateString, String outputFormat) {
        if (utcDateString == null || utcDateString.isEmpty()) {
            return "";
        }

        try {
            // Parse the UTC date
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = inputFormat.parse(utcDateString);
            
            // Convert to Vietnam timezone with custom format
            SimpleDateFormat customOutputFormat = new SimpleDateFormat(outputFormat, Locale.getDefault());
            customOutputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            
            return customOutputFormat.format(date);
        } catch (ParseException e) {
            return utcDateString; // Return original if parsing fails
        }
    }
}
