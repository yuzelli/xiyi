package com.example.yuzelli.yiai.uitls;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtils {
    public static String converTime(Long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date curDate = new Date(time);//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }
    /**
     * 转化时间输入时间与当前时间的间隔
     * @param timestamp
     * @return
     */
    public static String converTime2(long timestamp) {
        long currentSeconds = System.currentTimeMillis() / 1000;
        long timeGap = currentSeconds - timestamp/1000;// 与现在时间相差秒数
        String timeStr = null;
        if (timeGap > 24 * 60 * 60) {// 1天以上
            timeStr = timeGap / (24 * 60 * 60) + "天前";
        } else if (timeGap > 60 * 60) {// 1小时-24小时
            timeStr = timeGap / (60 * 60) + "小时前";
        } else if (timeGap > 60) {// 1分钟-59分钟
            timeStr = timeGap / 60 + "分钟前";
        } else {// 1秒钟-59秒钟
            timeStr = "刚刚";
        }
        return timeStr;
    }

    /**
     *  获取当前时间20170101
     * @return
     */
    public static int CurrentTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        long currentTime = System.currentTimeMillis();
        return Integer.valueOf(format.format(currentTime));
    }

    /**
     * 获取当前时间之前number天的日期
     * 20170101
     * @param number
     * @return
     */
    public static int beforeDate(int number){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -number);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return Integer.valueOf(format.format(calendar.getTime()));
    }
}
