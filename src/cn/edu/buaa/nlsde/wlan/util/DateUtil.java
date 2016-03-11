package cn.edu.buaa.nlsde.wlan.util;

//~--- JDK imports ------------------------------------------------------------
import java.util.Date;
import java.util.Calendar;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * <DT><B>名称： </B>日期处理
 * <DD>
 * <DT><B>概要： </B>日期处理方法集
 * <DD>
 * <DT><B>说明： </B>日期串处理
 * <DD>
 */
public class DateUtil {

    /**
     * 日期检查错误信息
     */
    private static String errorMsg;
    /**
     * 是否合法日期字符串
     */
    private static boolean isValidDate;

    /**
     * 构造函数
     */
    public DateUtil() {
    }

    /**
     * 获取当前时间的String字串
     *
     * @return
     */
    public static String getDateTime() {
        Date date = new Date();
        String log_time = (new SimpleDateFormat("yyyyMMddHHmmss")).format(date);
        return log_time;
    }

    /**
     * 获取当前时间的String字串,毫秒级
     *
     * @return
     */
    public static String getDateTimeMilli() {
        Date date = new Date();
        String log_time = (new SimpleDateFormat("yyyyMMddHHmmssSSS")).format(date);
        return log_time;
    }

    public static String getCurrentDate() {
        Date date = new Date();
        String log_time = (new SimpleDateFormat("yyyyMMdd")).format(date);
        return log_time;
    }

    /**
     * 获取当天起始时间
     *
     * @return (00:00:00.000)
     */
    public static Date getTodayStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    /**
     * 获取当天结束时间
     *
     * @return(23:59:59.999)
     */
    public static Date getTodayEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    /**
     * 获取某天后对应的时间
     *
     * @param ts 输入时间
     * @param dayCount 天数
     * @return ts N天后时间
     */
    public static Timestamp getNextTime(Timestamp ts, int dayCount) {
        Timestamp newTs = new Timestamp(ts.getTime() + 86400000 * dayCount);

        return newTs;
    }

    /**
     * 说明: 日期转化为字符串(默认格式 yyyy-mm-dd hh24:mi:ss)
     *
     * @param dt 日期
     * @return dateString 转化后的字符串
     */
    public static String formatDate(Date dt) {
        return formatDate(dt, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 说明: 日期转化为字符串(默认格式 yyyy-mm-dd hh24:mi:ss)
     *
     * @param ts 日期
     * @return dateString 转化后的字符串
     */
    public static String formatDate(java.sql.Timestamp ts) {
        String dateString = "";
        if (ts != null) {
            Date dt = new Date(ts.getTime());
            dateString = formatDate(dt, "yyyy-MM-dd HH:mm:ss");
        }
        return dateString;
    }

    /**
     * 说明: 日期转化为字符串(默认格式 yyyy-mm-dd)
     *
     * @param dt 日期
     * @return dateString 转化后的字符串
     */
    public static String formatShortDate(Date dt) {
        String dateString;

        if (dt == null) {
            dateString = "";
        } else {
            dateString = formatDate(dt, "yyyy-MM-dd");
        }

        return dateString;
    }

    /**
     * 说明: 日期转化为字符串(默认格式 yyyy-mm-dd)
     *
     * @param ts 日期
     * @return dateString 转化后的字符串
     */
    public static String formatShortDate(Timestamp ts) {
        String dateString;
        Date dt = new Date(ts.getTime());
        dateString = formatDate(dt, "yyyy-MM-dd");

        return dateString;
    }

    /**
     * 说明： 获取今天的时间，按 2001年1月10日 星期一格式
     *
     * @return showExpDay 今天对应的时间
     */
    public static String showExpDate() {
        Date dt = new Date();
        String showExpDay;
        int dayOfWeek;

        showExpDay = formatDate(dt, "yyyy年MM月dd日 ");
        Calendar rightNow = Calendar.getInstance();
        dayOfWeek = rightNow.get(Calendar.DAY_OF_WEEK);
        System.out.println(dayOfWeek);
        showExpDay += getWeekName(dayOfWeek);

        return showExpDay;
    }

    /**
     * 说明: 日期转化为字符串
     *
     * @param dt 日期
     * @param sf 日期格式化定义
     * @return dateString 转化后的字符串
     */
    public static String formatDate(Date dt, String sf) {

        // Format the current time.
        SimpleDateFormat sdf = new SimpleDateFormat(sf);

        return sdf.format(dt);
    }

    /**
     * 说明: 日期转化为字符串
     *
     * @param ts 日期
     * @param sf 日期格式化定义
     * @return dateString 转化后的字符串
     */
    public static String formatDate(Timestamp ts, String sf) {
        String dateString = "";
        if (ts != null) {
            Date dt = new Date(ts.getTime());
            // Format the current time.
            SimpleDateFormat sdf = new SimpleDateFormat(sf);
            dateString = sdf.format(dt);
        }

        return dateString;
    }

    /**
     * 说明: 日期转化为SQL字符串(默认格式 yyyy-mm-dd hh24:mi:ss)
     *
     * @param dt 日期
     * @return dateString 转化后的字符串
     */
    public static String formatDateSQL(Date dt) {
        String sqlString = formatDate(dt);
        sqlString = " to_date('" + sqlString + "', 'yyyy-mm-dd hh24:mi:ss') ";
        return sqlString;
    }

    /**
     * 说明: 字符串转化为SQL字符串(默认格式 yyyy-mm-dd hh24:mi:ss)
     *
     * @param dtString 日期字符串
     * @return dateString 转化后的SQL字符串
     */
    public static String formatDateSQL(String dtString) {
        String sqlString;
        // 防止 2-29等日期溢出错误。
        Date dt;
        // 将提交的日期转化为JAVA日期
        dt = DateUtil.parseString(dtString, "yyyy-MM-dd");
        // 将JAVA日期转化为提交的日期
        dtString = DateUtil.formatDate(dt, "yyyy-MM-dd");
        sqlString = " to_date('" + dtString + "', 'yyyy-mm-dd') ";

        return sqlString;
    }

    /**
     * 说明: 字符串转化为SQL字符串
     *
     * @param dtString 日期字符串
     * @param sf 数据库日期格式
     * @return dateString 转化后的SQL字符串
     */
    public static String formatDateSQL(String dtString, String sf) {
        String sqlString;
        // 防止 2-29等日期溢出错误。
        Date dt;
        // 将提交的日期转化为JAVA日期
        dt = DateUtil.parseString(dtString, "yyyy-MM-dd");
        // 将JAVA日期转化为提交的日期
        dtString = DateUtil.formatDate(dt, "yyyy-MM-dd");
        sqlString = " to_date('" + dtString + "', '" + sf + "') ";

        return sqlString;
    }

    /**
     * 说明: 字符串转换为日期 (默认格式 yyyy-MM-dd)
     *
     * @param dateString 日期格式字符串
     * @return 转换后的日期
     */
    public static Date parseString(String dateString) {
//        String sf = "yyyy-MM-dd HH:mm:ss";
        String sf = "yyyyMMddHHmmss";
        Date dt = parseString(dateString, sf);

        return dt;
    }

    /**
     * 说明: 字符串转换为日期
     *
     * @param dateString 日期格式字符串
     * @param sf 日期格式化定义
     * @return 转换后的日期
     */
    public static Date parseString(String dateString, String sf) {
        // Parse the previous string back into a Date.
        ParsePosition pos = new ParsePosition(0);
        // Format the current time.
        SimpleDateFormat sdf = new SimpleDateFormat(sf);
        Date dt = sdf.parse(dateString, pos);

        return dt;
    }

    /**
     * 将日期转换为毫秒数
     *
     * @param dateStr 日期格式的字符串
     * @return 毫秒数
     */
    public static long dateStringToLong(String dateStr) {
        long tempDate = 0;
        SimpleDateFormat simpleformat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date new_date;
            new_date = simpleformat.parse(dateStr);
            tempDate = new_date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return tempDate;
    }

    /**
     * 将毫秒数转换为yyyy-mm-dd格式的日期字符串
     *
     * @param dateLong 毫秒数
     * @return 日期字符串
     */
    public static String dateLongToString(long dateLong) {
        String tempStr = "";
        SimpleDateFormat simpleformat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date new_date = new Date(dateLong);
            tempStr = simpleformat.format(new_date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempStr;
    }

    public static String getTimeStringFromUnixTime(long unix_time) {
        String tempStr = "";
        SimpleDateFormat simpleformat = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            Date new_date = new Date(unix_time);
            tempStr = simpleformat.format(new_date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tempStr;
    }

    /**
     * 说明: 是否合法日期
     *
     * @return 是否合法日期
     */
    public static boolean isValidDate() {
        return isValidDate;
    }

    /**
     * 说明: 获取错误信息提示
     *
     * @return errorMsg 错误信息
     */
    public static String getErrorMsg() {
        return errorMsg;
    }

    /**
     * 说明： 获取当前时间
     *
     * @return curTimestamp 当前时间
     */
    public static Timestamp getCurTime() {
        return new Timestamp(new java.util.Date().getTime());
    }

    /**
     * 说明： 获取星期几名称
     *
     * @param weekNo 第几天
     * @return weekName 中文星期几
     */
    private static String getWeekName(int weekNo) {
        String[] weekNames = {
            "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"
        };
        String weekName = weekNames[weekNo - 1];
        return weekName;
    }

    /**
     * 返回新的日期类型 date : 原始日期 value : 须增加的值 VAR : 修改日期类型 比如: 修改分钟 小时 天 星期 月 年 例如
     * Calendar.DAY_OF_MONTH Calendar.DAY_OF_WEEK Calendar.DAY_OF_YEAR
     * Calendar.HOUR_OF_DAY Calendar.MONDAY
     *
     * @param date
     * @param value
     * @param VAR
     * @return
     */
    public static Date getNewDate(Date date, int value, int VAR) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int temp = cal.get(VAR);
        cal.set(VAR, temp + value);
        return cal.getTime();
    }

    /**
     * Return timestamp from string Example : 2002-07-03 12:22:22 create by
     * chuanyun tian
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static Timestamp stringToTimestamp(String time) throws java.text.ParseException {
        java.text.SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = sdt.parse(time);
        java.util.Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return new Timestamp(cal.getTime().getTime());
    }

    /**
     * If begindate is before of the todate, return true; otherwise return false
     * chaunyun tian
     *
     * @param beginDate
     * @param toDate
     * @return
     */
    public static boolean compDate(Date beginDate, Date toDate) {
        return beginDate.before(toDate);
    }

    /**
     * If system time is before of the compareDate, return true; otherwise
     * return false chaunyun tian
     *
     * @param compareDate
     * @return
     */
    public static boolean compNow(Date compareDate) {
        return (new Date()).before(compareDate);
    }

    /**
     * 将字符串转换为yyyy-mm-dd格式的日期
     *
     * @param time 字符串
     * @throws java.text.ParseException
     * @return yyyy-mm-dd格式的日期
     */
    public static String stringToMySQL(String time) throws java.text.ParseException {
        java.text.SimpleDateFormat sdt = new SimpleDateFormat("MM/dd/yyyy");
        java.text.SimpleDateFormat sdt_after = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdt.parse(time);

        return sdt_after.format(date);
    }

    /**
     * Return timestamp from string Example : 03/07/2002 12:22:22 create by
     * chuanyun tian
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static Timestamp strToTimestamp(String time) throws java.text.ParseException {
        java.text.SimpleDateFormat sdt = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = sdt.parse(time);
        java.util.Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        return new Timestamp(cal.getTime().getTime());
    }

    /**
     * 返回新的日期类型 date : 原始日期长整形 value : 须增加的值 VAR : 修改日期类型 比如: 修改分钟 小时 天 星期 月 年
     * Calendar.DAY_OF_MONTH Calendar.DAY_OF_WEEK Calendar.DAY_OF_YEAR
     * Calendar.HOUR_OF_DAY Calendar.MONDAY
     *
     * @param date
     * @param value
     * @param VAR
     * @return
     */
    public static long getNewDate(long date, int value, int VAR) {
        java.util.Date dd = new java.util.Date();
        dd.setTime(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dd);
        int temp = cal.get(VAR);
        cal.set(VAR, temp + value);
        return cal.getTime().getTime();
    }

    /**
     * 获取时间对应的名称
     *
     * @param ts 时间
     * @return tname 时间名称
     */
    public static String getTimeName(Timestamp ts) {
        return null;
    }

    public static long getUnixTime(String time_string) throws Exception {
        DateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = date_format.parse(time_string);
        long unix_time = date.getTime();
        return unix_time;
    }

    //毫秒级转换
    public static long getUnixTimeMilli(String time_string) throws Exception {
        DateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
        Date date = date_format.parse(time_string);
        long unix_time = date.getTime();
        return unix_time;
    }

    //毫秒级转换(没有符号".")
    public static long getUnixTimeMilliWithoutPoint(String time_string) throws Exception {
        DateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date date = date_format.parse(time_string);
        long unix_time = date.getTime();
        return unix_time;
    }

    public static String getExpDate(String time_string) throws Exception {
        DateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = date_format.parse(time_string);
        String showExpDay;
        showExpDay = formatDate(date, "MM月dd日HH时mm分");
        return showExpDay;
    }

    //获取时间差, 单位: 秒
    public static int getTimeDifference(String time_1, String time_2) throws Exception {
        long time_1_long = DateUtil.getUnixTime(time_1);
        long time_2_long = DateUtil.getUnixTime(time_2);
        int time_d = (int) ((time_1_long - time_2_long) / 1000);
        return time_d;
    }

    public static String getOneSecondLater(String time) throws Exception {
        long time_long = DateUtil.getUnixTime(time);
        time_long = time_long + 1000;
        String time_string = DateUtil.getTimeFromUnix(time_long);
        return time_string;
    }

    public static String getOneSecondBefore(String time) throws Exception {
        long time_long = DateUtil.getUnixTime(time);
        time_long = time_long - 1000;
        String time_string = DateUtil.getTimeFromUnix(time_long);
        return time_string;
    }

    public static String getNSecondBefore(String time, int n) throws Exception {
        long time_long = DateUtil.getUnixTime(time);
        time_long = time_long - 1000 * n;
        String time_string = DateUtil.getTimeFromUnix(time_long);
        return time_string;
    }

    public static String getTimeFromUnix(long dateLong) {
        String tempStr = "";
        SimpleDateFormat simpleformat = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            Date new_date = new Date(dateLong);
            tempStr = simpleformat.format(new_date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tempStr;
    }

    public static String getMore5Min() throws Exception {//返回5分钟后的时间字符串
        String now_time = DateUtil.getDateTime();
        long unix_time_5 = DateUtil.getUnixTime(now_time) + 5 * 60 * 1000;
        String new_time = DateUtil.getTimeFromUnix(unix_time_5);
        return new_time;
    }

    public static long dateMargin(Date bef, Date aft) {
        long margin = aft.getTime() - bef.getTime();
        return margin;
    }

    /**
     * 获取N天后对应的时间
     *
     * @param date 输入时间
     * @param dayCount 天数,负数代表向前，正数代表向后
     * @return ts 一天后时间
     */
    public static Date getNDay(Date date, int dayCount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, dayCount);
        Date nba = cal.getTime();
        return nba;
    }

    /*public static void main(String args[]) {
     try {
     //String b = DateUtil.getExpDate("20120608164700");
     Date b = DateUtil.getTodayStartTime();
     b = DateUtil.getNDay(b, 1);
     System.out.println(b);
     } catch (Exception ex) {
     ex.printStackTrace();
     }
     }*/
}
