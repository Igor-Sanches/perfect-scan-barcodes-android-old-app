package com.igordutrasanches.perfectscan.menuManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.igordutrasanches.perfectscan.R;
import com.igordutrasanches.perfectscan.activity.AboutActivity;
import com.igordutrasanches.perfectscan.activity.DonateActivity;
import com.igordutrasanches.perfectscan.activity.HistoricalActivity;
import com.igordutrasanches.perfectscan.activity.HomeActivity;
import com.igordutrasanches.perfectscan.activity.MakerActivity;
import com.igordutrasanches.perfectscan.activity.ScanActivity;
import com.igordutrasanches.perfectscan.activity.ScanImageActivity;
import com.igordutrasanches.perfectscan.activity.SettingsActivity;
import com.igordutrasanches.perfectscan.compoments.Go;
import com.igordutrasanches.perfectscan.compoments.Key;
import com.igordutrasanches.perfectscan.compoments.Services;
import com.igordutrasanches.perfectscan.manager.Manager;

public class NavigationMenuManager extends Activity {
    private Context context;
    public  NavigationMenuManager(Context context){
        this.context = context;
    }

    private String isPage(){
        return Key.getPageView(context);
    }

    public void goHome(){
        if(!isPage().equals("home"))
            context.startActivity(new Intent(context, HomeActivity.class));
    }



    public void goScan() {
        try{
            if(!isPage().equals("scan")){
                context.startActivity(new Intent(context, ScanActivity.class));
            }
        }catch (Exception x){
            Toast.makeText(context, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void goAbout(){
        context.startActivity(new Intent(context, AboutActivity.class));
    }

    public void goImageScan() {
        if(!isPage().equals("scan_image"))
            context.startActivity(new Intent(context, ScanImageActivity.class));
    }

    public void goMaker() {
        if(!isPage().equals("maker"))
            context.startActivity(new Intent(context, MakerActivity.class));
    }

    public void goHistorical() {
        if(!isPage().equals("historical"))
            context.startActivity(new Intent(context, HistoricalActivity.class));
    }

    public void goShareApp() {
        Go.by(context).onShareApp();
    }

    public void goRate() {
        Go.by(context).onRateApp();
    }

    public void goFeedback() {
        Manager.from(context).faleConosco();
    }

    public void goSettings() {
        try{
            context.startActivity(new Intent(context, SettingsActivity.class));
        }catch (Exception x){
            Toast.makeText(context, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private AlertDialog dialog;
    public void goBuy() {
        String[] buys = new String[]{ context.getString(R.string.buy), context.getString(R.string.doar) };
        dialog = new AlertDialog.Builder(context).setTitle(R.string.buy_doar).setItems(buys, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        Go.by(context).goBuyApp();
                        break;
                    case 1:
                      try{
                          Services.from(context).putDonate();
                      }catch (Exception x){
                          Toast.makeText(context, x.getMessage(), Toast.LENGTH_SHORT).show();
                      }
                       break;
                }
            }
        }).create();
        dialog.show();
    }


}
