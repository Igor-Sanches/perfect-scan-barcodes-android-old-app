package com.igordutrasanches.perfectscan.compoments;

import android.content.Context;

import com.igordutrasanches.perfectscan.R;

public class GetColor {
    public static int laser(Context context) {
        String Color = Key.getLaserColor(context);
        int color = R.color.viewfinder_laser;
        switch (Color){
            case "0": color = R.color.viewfinder_laser; break;
            case "1": color = android.R.color.holo_blue_bright; break;
            case "2": color = android.R.color.holo_blue_dark; break;
            case "3": color = android.R.color.holo_blue_light; break;
            case "4": color = android.R.color.holo_green_dark; break;
            case "5": color = android.R.color.holo_green_light; break;
            case "6": color = android.R.color.holo_orange_dark; break;
            case "7": color = android.R.color.holo_orange_light; break;
            case "8": color = android.R.color.holo_purple; break;
            case "9": color = android.R.color.holo_red_dark; break;
            case "10": color = android.R.color.holo_red_light; break;
        }
        return color;
    }
}
