package com.unclezs.utils;

/**
 * @author uncle
 * @date 2020/5/9 16:21
 */
public class TimeUtil {
    /**
     * 秒数转为时间格式(HH:mm:ss)<br>
     * 参考：https://github.com/iceroot
     *
     * @param seconds 需要转换的秒数
     * @return 转换后的字符串
     * @since 3.1.2
     */
    public static String secondToTime(double seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("Seconds must be a positive number!");
        }

        int hour = (int) (seconds / 3600);
        int other = (int) (seconds % 3600);
        int minute = other / 60;
        int second = other % 60;
        final StringBuilder sb = new StringBuilder();
        if (hour != 0) {
            if (hour < 10) {
                sb.append("0");
            }
            sb.append(hour);
            sb.append(":");
        }
        if (minute < 10) {
            sb.append("0");
        }
        sb.append(minute);
        sb.append(":");
        if (second < 10) {
            sb.append("0");
        }
        sb.append(second);
        return sb.toString();
    }
}
