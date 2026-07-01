package com.dp.plat.common.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 日期工具类 - 迁移自老系统 DateUtil (431行, 14个方法)
 *
 * 使用Java 8+ LocalDateTime替代老系统的Date/Calendar
 */
public class DateUtil {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_FORMAT = "HH:mm:ss";

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    /** 获取当前日期字符串 */
    public static String getCurrentDate() {
        return LocalDate.now().format(DATE_FMT);
    }

    /** 获取当前日期时间字符串 */
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DATETIME_FMT);
    }

    /** 字符串转LocalDate */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        return LocalDate.parse(dateStr, DATE_FMT);
    }

    /** 字符串转LocalDateTime */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) return null;
        return LocalDateTime.parse(dateTimeStr, DATETIME_FMT);
    }

    /** Date转LocalDateTime */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /** LocalDateTime转Date */
    public static Date toDate(LocalDateTime ldt) {
        if (ldt == null) return null;
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    /** 计算两个日期之间的天数差 */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /** 计算两个日期时间之间的天数差 */
    public static long daysBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /** 获取季度第一天 */
    public static LocalDate getQuarterFirstDay(LocalDate date) {
        int month = date.getMonthValue();
        int quarterFirstMonth = ((month - 1) / 3) * 3 + 1;
        return LocalDate.of(date.getYear(), quarterFirstMonth, 1);
    }

    /** 获取季度最后一天 */
    public static LocalDate getQuarterLastDay(LocalDate date) {
        LocalDate quarterFirst = getQuarterFirstDay(date);
        return quarterFirst.plusMonths(3).minusDays(1);
    }

    /** 获取本月第一天 */
    public static LocalDate getMonthFirstDay(LocalDate date) {
        return date.withDayOfMonth(1);
    }

    /** 获取本月最后一天 */
    public static LocalDate getMonthLastDay(LocalDate date) {
        return date.withDayOfMonth(date.lengthOfMonth());
    }

    /** 日期加减天数 */
    public static LocalDate addDays(LocalDate date, int days) {
        return date.plusDays(days);
    }

    /** 格式化日期 */
    public static String formatDate(LocalDate date) {
        return date == null ? null : date.format(DATE_FMT);
    }

    /** 格式化日期时间 */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DATETIME_FMT);
    }

    /** 判断日期是否在指定范围内 */
    public static boolean isBetween(LocalDate target, LocalDate start, LocalDate end) {
        return !target.isBefore(start) && !target.isAfter(end);
    }

    /** 获取年龄 */
    public static int getAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
