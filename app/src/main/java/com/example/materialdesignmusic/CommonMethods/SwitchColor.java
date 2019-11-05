package com.example.materialdesignmusic.CommonMethods;

import android.graphics.Color;

public class SwitchColor {
    public static int switchColor(String color) {
        switch(color){
            case "red":
                return Color.RED;
            case "blue":
                return Color.rgb(3,169,244);
            case "yellow":
                return Color.YELLOW;

        }
        return Color.BLACK;
    }
}
