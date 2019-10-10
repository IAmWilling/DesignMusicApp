package com.example.materialdesignmusic.CommonMethods;

public class Methods {
    public String getGapTime(long time){
        long hours = time / (1000 * 60 * 60);
        long minutes = (time-hours*(1000 * 60 * 60 ))/(1000* 60);
        String diffTime="";
        if(minutes<10){
            diffTime=hours+":0"+minutes;
        }else{
            diffTime=hours+":"+minutes;
        }
        return diffTime;
    }
    public static String formattime(long time){
        String min= (time/(1000*60))+"";
        String second= (time%(1000*60)/1000)+"";
        if(min.length()<2){
            min=0+min;
        }
        if(second.length()<2){
            second=0+second;
        }
        return min+":"+second;
    }
    public static long minToSecond(String time) {
        String[] buffer = time.split(":");
        float a = Float.valueOf(buffer[0]) * 60;
        float b = Float.valueOf(buffer[1]) + a;
        return Float.valueOf(b).longValue();
    }
}
