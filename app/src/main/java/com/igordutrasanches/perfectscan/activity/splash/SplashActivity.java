package com.igordutrasanches.perfectscan.activity.splash;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.igordutrasanches.perfectscan.PerfectApplication;
import com.igordutrasanches.perfectscan.R;
import com.igordutrasanches.perfectscan.activity.BuyActivity;
import com.igordutrasanches.perfectscan.activity.HomeActivity;
import com.igordutrasanches.perfectscan.compoments.Key;
import com.igordutrasanches.perfectscan.manager.tools.IabBroadcastReceiver;
import com.igordutrasanches.perfectscan.manager.tools.IabHelper;
import com.igordutrasanches.perfectscan.manager.tools.IabResult;
import com.igordutrasanches.perfectscan.manager.tools.Inventory;

import java.util.Timer;
import java.util.TimerTask;

import static com.igordutrasanches.perfectscan.PerfectApplication.CODE_APP;
import static com.igordutrasanches.perfectscan.PerfectApplication.FULL_APP;

public class SplashActivity extends AppCompatActivity implements IabBroadcastReceiver.IabBroadcastListener {
    private void onSetup(){
        mHelper = new IabHelper(this, PerfectApplication.ApplicationKey);
        mHelper.iniciarInstalacao(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (mHelper != null && result.isSuccess()) {
                    mBroadcastReceiver = new IabBroadcastReceiver(SplashActivity.this);
                    IntentFilter filter = new IntentFilter(IabBroadcastReceiver.ACTION);
                    registerReceiver(mBroadcastReceiver, filter);

                    mHelper.consultarInventorioAsync(true, PerfectApplication.listaProdutos(), goInventario);
                }
            }
        });
    }

    @Override
    public void receivedBroadcast() {
        mHelper.consultarInventorioAsync(goInventario);
    }

    private IabHelper mHelper;
    private IabBroadcastReceiver mBroadcastReceiver;

    private IabHelper.QueryInventoryFinishedListener goInventario = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inv) {
            if(result.isSuccess() && mHelper != null){
                if(inv.hasPurchase(CODE_APP)){
                    Key.setBuyAppType("by:code", SplashActivity.this);
                }
                if(inv.hasPurchase(FULL_APP)){
                    Key.setBuyAppType("by:buy", SplashActivity.this);
                }
            }
        }
    };
    private static int SPLASH_TIME = 700;

    private Timer timer;

    String key_donate = "dialog_doar";
    String key_rate = "dialog_rate";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       try{
           setContentView(R.layout.activity_splash);
         onSetup();
        int valorDonate = Key.get(key_donate, 1, this);
        int valorRate = Key.get(key_rate, 1, this);
        if(valorDonate != 100){
            Key.set(key_donate, valorDonate + 1, this);
        }
        if(valorRate != 100){
            Key.set(key_rate, valorRate + 1, this);
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doFunc();
                    }
                });
            }
        }, SPLASH_TIME);
    }catch (Exception x){
        Toast.makeText(this, x.getMessage(), Toast.LENGTH_SHORT).show();
    }
}


    private void doFunc() {
        boolean buy = Key.getBuyApp(this).equals("by:buy") ? true : false;
        boolean code = Key.getBuyApp(this).equals("by:code") ? true : false;

        if(!buy){
            if(code){
                loadForFull();
            }else{
                if(Key.getAppTest(this)){
                    loadForBuy();
                }else loadForFull();
            }
        }else{
            loadForFull();
        }


    }


    private void loadForBuy() {
        startActivity(new Intent(this, BuyActivity.class));
    }

    private void loadForFull() {
        startActivity(new Intent(this, HomeActivity.class));
    }

}
