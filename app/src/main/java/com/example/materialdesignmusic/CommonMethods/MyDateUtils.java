package com.example.materialdesignmusic.CommonMethods;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyDateUtils {
    static public String timeStampToYearDate(long timestamp) {
        Long timeStamp = timestamp;  //获取当前时间戳
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp))));      // 时间戳转换成时间


        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
        String sd2 = sdf2.format(new Date(Long.parseLong(String.valueOf(timeStamp))));
        return sd2;
    }
}
