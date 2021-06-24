package com.igordutrasanches.perfectscan.manager;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.igordutrasanches.perfectscan.R;
import com.igordutrasanches.perfectscan.compoments.Go;

public class Manager {
    private Context mContext;
    public Manager(Context mContext){
        this.mContext = mContext;
    }

    public static Manager from(Context mContext){
        return new Manager(mContext);
    }

    public static final String EMAIL_SUPPORT = "perfectscan_feedback@outlook.com";
    public static final String ZAP_SUPPORT = "https://wa.me/559885088674?text=";
    public static final String PHONE_SUPPORT = "+5598985088674";

    public void support(int res){
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ZAP_SUPPORT + mContext.getString(res))));
    }

    public void doar(){
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ZAP_SUPPORT + mContext.getString(R.string.doar_a))));
    }

    public void faleConosco(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle(R.string.menu_send);
        String[] suportOptions = new String[]{ "E-Mail", mContext.getString(R.string.ligacao), "WhatsApp"};
        dialog.setItems(suportOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:{
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_SUBJECT, mContext.getString(R.string.oq_deseja));
                        i.setData(Uri.parse("mailto:"+ EMAIL_SUPPORT));
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(i);
                    }break;
                    case 1:{
                        Go.by(mContext).goDial(PHONE_SUPPORT);
                    }break;
                    case 2:{
                       mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ZAP_SUPPORT + mContext.getString(R.string.oq_deseja))));
                    }break;
                }
            }
        });
        dialog.create().show();
    }
}
