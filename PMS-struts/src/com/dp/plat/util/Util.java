package com.dp.plat.util;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.opensymphony.xwork2.util.reflection.ReflectionException;

public class Util {
    public static Date getTimeAddDay(Date date, int daysNum) {
        // SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, daysNum);
        return c.getTime();
    }

    public static String composeMessage(String template, Properties data) {
        Iterator<?> it = data.entrySet().iterator();
        while (it.hasNext()) {
            Object o = it.next();
            template = template.replaceFirst("\\$\\{" + o.toString().split("=")[0] + "}", o.toString().split("=")[1]);
        }

        return template;
    }

    public static String generateFileName(String fileName) {
        DateFormat format = new SimpleDateFormat("yyMMddHHmmss");
        String formatDate = format.format(new Date());
        int random = new Random().nextInt(10000);
        int position = fileName.lastIndexOf(".");
        String extension = fileName.substring(position);
        return formatDate + random + extension;
    }

    public static String formatString(String str) {
        if (str != null && !"".equals(str)) {
            if (str.indexOf(".") != -1) {
                return str.substring(0, str.indexOf("."));
            } else {
                return str;
            }
        } else {
            return "";
        }
    }

    /**
     * 工号补齐
     * 
     * @param username
     * @return
     */
    public static String checkUsername(String username) {
        String result = "";
        if (username.length() < 5) {
            for (int i = 0; i < 5 - username.length(); i++) {
                result += "0";
            }
        }
        result += username;
        return result;
    }

    public static double formatDouble(double d) {
        DecimalFormat df = new DecimalFormat("#.00");
        return Double.parseDouble(df.format(d));
    }

    public static BigDecimal parseDecimal(Object obj) {
        if (obj != null) {
            String amount = obj.toString();
            if (StringUtils.isNotBlank(amount)) {
                try {
                    return new BigDecimal(amount);
                } catch (Exception e) {
                    try {
                        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                        decimalFormat.setParseBigDecimal(true);
                        return (BigDecimal) decimalFormat.parse(amount);
                    } catch (Exception e2) {
                    }
                }
            }
        }
        return null;
    }

    public static String formatDecimal(Object obj) {
        if (obj != null) {
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            String amount = obj.toString();
            if (StringUtils.isNotBlank(amount)) {
                try {
                    BigDecimal b = new BigDecimal(amount);
                    return decimalFormat.format(b);
                } catch (Exception e) {
                    try {
                        decimalFormat.setParseBigDecimal(true);
                        Number b = decimalFormat.parse(amount);
                        return decimalFormat.format(b);
                    } catch (Exception e2) {
                        return amount;
                    }
                }
            }
        }
        return null;
    }

    public static String dateFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public static String dateFormat(Date date, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        return sdf.format(date);
    }

    public static Date dateParse(String str) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(str);
    }

    /**
     * 随机数
     * 
     * @return
     */
    public static String getRandNumber() {
        Date date = new Date();
        String time = Long.toString(date.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateformate = sdf.format(date);
        Random random = new Random(10);
        String num = Math.abs(random.nextInt()) + "";
        return dateformate + "-" + time + "-" + num;
    }

    public static boolean createDir(String path) {
        // getRandNumber()
        File file = new File(path);
        if (file.exists()) {
            System.out.println("error : create file false. file is exists");
            return false;
        }
        if (file.mkdirs()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean mkdir(String path) {
        File file = new File(path);
        if (file.exists()) {
            return true;
        } else {
            if (file.mkdirs()) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 取得当前时间所在周的开始时间和结束时间
     */
    public static Date getFirstDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
        return c.getTime();
    }

    public static Date getLastDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
        return c.getTime();
    }

    /**
     * 字符串分割拼接字符c
     * 
     * @param str
     * @param c
     * @return
     */
    public static String appendChar(String str, String c) {
        if (str == null) {
            return "";
        }
        String splitStr = ",";
        StringBuilder sb = new StringBuilder();
        if (str.indexOf(splitStr) != -1) {
            String[] split = str.split(splitStr);
            for (String s : split) {
                if (StringUtils.isNotBlank(s)) {
                    sb.append(c).append(s.trim()).append(c).append(splitStr);
                }
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        } else {
            sb.append(c).append(str).append(c);
        }
        return sb.toString();
    }

    public static String methodToProperty(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else if (name.startsWith("get") || name.startsWith("set")) {
            name = name.substring(3);
        } else {
            throw new ReflectionException(
                    "Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
        }
        if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
            name = name.substring(0, 1).toLowerCase(Locale.US) + name.substring(1);
        }
        return name;
    }
}
