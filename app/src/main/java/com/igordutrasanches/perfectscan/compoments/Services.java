package com.igordutrasanches.perfectscan.compoments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.igordutrasanches.perfectscan.PerfectApplication;
import com.igordutrasanches.perfectscan.R;
import com.igordutrasanches.perfectscan.activity.DonateActivity;
import com.igordutrasanches.perfectscan.activity.HelpActivity;
import com.igordutrasanches.perfectscan.activity.TranslateActivity;
import com.igordutrasanches.perfectscan.manager.Manager;

public class Services {
    private Context mContext;

    public Services(Context context){
        mContext = context;
    }
    public static Services from(Context context) {
        return new Services(context);
    }

    public void putTranslate() {
        mContext.startActivity(new Intent(mContext, TranslateActivity.class));
    }

    public void putShowHelp() {
        mContext.startActivity(new Intent(mContext, HelpActivity.class));
    }

    public void putDonate() {
        mContext.startActivity(new Intent(mContext, DonateActivity.class));
    }
}
