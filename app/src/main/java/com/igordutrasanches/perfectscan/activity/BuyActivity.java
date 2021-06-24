package com.igordutrasanches.perfectscan.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.igordutrasanches.perfectscan.PerfectApplication;
import com.igordutrasanches.perfectscan.R;
import com.igordutrasanches.perfectscan.activity.splash.SplashActivity;
import com.igordutrasanches.perfectscan.compoments.DateTime;
import com.igordutrasanches.perfectscan.compoments.Go;
import com.igordutrasanches.perfectscan.compoments.Key;
import com.igordutrasanches.perfectscan.manager.tools.IabBroadcastReceiver;
import com.igordutrasanches.perfectscan.manager.tools.IabHelper;
import com.igordutrasanches.perfectscan.manager.tools.IabResult;
import com.igordutrasanches.perfectscan.manager.tools.Inventory;
import com.igordutrasanches.perfectscan.manager.tools.Purchase;

import static com.igordutrasanches.perfectscan.PerfectApplication.CODE_APP;
import static com.igordutrasanches.perfectscan.PerfectApplication.FULL_APP;

public class BuyActivity extends AppCompatActivity implements IabBroadcastReceiver.IabBroadcastListener {
    private void onSetup(){
        mHelper = new IabHelper(this, PerfectApplication.ApplicationKey);
        mHelper.iniciarInstalacao(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (mHelper != null && result.isSuccess()) {
                    mBroadcastReceiver = new IabBroadcastReceiver(BuyActivity.this);
                    IntentFilter filter = new IntentFilter(IabBroadcastReceiver.ACTION);
                    registerReceiver(mBroadcastReceiver, filter);

                    mHelper.consultarInventorioAsync(true, PerfectApplication.listaProdutos(), goInventario);
                }
            }
        });
    }


    private IabHelper.QueryInventoryFinishedListener goInventario = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inv) {
            if(result.isSuccess() && mHelper != null){
                if(inv.hasPurchase(CODE_APP)){
                    Key.setBuyAppType("by:code", BuyActivity.this);
                    viewCode();
                }
                if(inv.hasPurchase(FULL_APP)){
                    Key.setBuyAppType("by:buy", BuyActivity.this);
                    startActivity(new Intent(BuyActivity.this, SplashActivity.class));
                }
            }else{
                onSetup();
            }
        }
    };
    private IabHelper.OnIabPurchaseFinishedListener goCompra = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase info) {
            if(result.isSuccess()){
                if(!info.getSku().equals(CODE_APP)){
                    mHelper.consumeAsync(info, new IabHelper.OnConsumeFinishedListener() {
                        @Override
                        public void onConsumeFinished(Purchase purchase, IabResult result) {
                            if(result.isSuccess() && mHelper != null){
                                //Já pode comprar covamente todos os produtos menos a escrição
                            }
                        }
                    });
                }
            }

        }
    };

    @Override
    public void receivedBroadcast() {
        mHelper.consultarInventorioAsync(goInventario);
    }

    public void onBack(View view){
        finish();
    }
    private IabHelper mHelper;
    private IabBroadcastReceiver mBroadcastReceiver;

    private static final String START = "F1", CENTER = "H4", END = "I19";

    private String getCodeTimeUpdate(){
        String left = DateTime.Now(this).toString("dd").substring(1);
        String center = DateTime.Now(this).toString("MM").substring(1);
        String right = DateTime.Now(this).toString("HH").substring(1);
        String chave = START + left + CENTER + center + END + right;
        return chave;
    }

    private EditText chave;
    private TextView msg;
    int clicked = 1;

    public void onCodeBuy(View view){
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle(R.string.atencion);
        dialog.setMessage(getString(R.string.buy_code_desc));
        dialog.setButton(Dialog.BUTTON_POSITIVE, getString(R.string.buy), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                comprarCodigo();
            }
        });
        dialog.setButton(Dialog.BUTTON_NEUTRAL, getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void comprarCodigo() {
       try{
           token_code = DateTime.Now(this).toString("yyyy") + "-IHF-" + DateTime.Now(this).toString("ddMM-HHss-mm");
           mHelper.iniciarCompra(this, CODE_APP, 102, goCompra, token_code);
       }catch (Exception x){
           Toast.makeText(this, getString(R.string.buy_refress), Toast.LENGTH_SHORT).show();
       } }

    @Override
    public void onBackPressed() {
        clicked = clicked + 1;
        if(clicked == 3){
            finishAffinity();
            startActivity(new Intent(this, SplashActivity.class));
        }else {
            Toast.makeText(this, R.string.back_pressed, Toast.LENGTH_SHORT).show();
        }

    }

    String token_full = "", token_code;
    public void onBuy(View view){

           try{
               mHelper.consultarInventorioAsync(goInventario);
               token_full = DateTime.Now(this).toString("yyyy") + "-IFH-" + DateTime.Now(this).toString("ddMM-HHss-mm");
              mHelper.iniciarCompra(this, FULL_APP, 101, goCompra, token_full);
           }catch (Exception x){
               Toast.makeText(this, getString(R.string.buy_refress), Toast.LENGTH_SHORT).show();
           }

    }

    public void onCode(View view){
             Key.setBuyAppType("by:code", this);
            startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      //  if (mHelper == null) return;

        if (!true){//mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);

        } else {
            if(resultCode == RESULT_OK) {
                if (requestCode == 101) {
                    Key.setBuyAppType("by:buy", this);
                    startActivity(new Intent(this, HomeActivity.class));
                } else if (requestCode == 102) {
                    chave.setText(getCodeTimeUpdate());
                    getViewCode();
                }
            }
        }
        onSetup();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        chave = (EditText)findViewById(R.id.code_input);
        msg = (TextView)findViewById(R.id.code_input_error);
        btn_buy = (Button)findViewById(R.id.btnbuy_valid);
        btn_code = (Button)findViewById(R.id.btnbuy_valid2);
        viewCode();

        chave.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getViewCode();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        onSetup();
    }

    private void viewCode() {
        boolean code = Key.getBuyApp(this).equals("by:code") ? true : false;
        if(code){
            ((Button)findViewById(R.id.btnbuy_code)).setVisibility(View.GONE);
            ((LinearLayout)findViewById(R.id.layout_code)).setVisibility(View.GONE);
        }
    }

    public void onSupport(View view){
        Go.by(this).onFeedBack();
    }

    public void onZap(View view){
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse((String)"https://api.whatsapp.com/send?phone=5598985356501")));
    }

    private Button btn_buy, btn_code;
    private void getViewCode() {
        if(chave.getText().toString().equals(getCodeTimeUpdate(/* Gera um código a cada time do relogio */))){
            msg.setText(R.string.input_valid);
            msg.setTextColor(getColor(android.R.color.holo_green_dark));
            btn_buy.setVisibility(View.GONE);
            btn_code.setVisibility(View.VISIBLE);
            ((Button)findViewById(R.id.btnbuy_code)).setVisibility(View.GONE);
            chave.setEnabled(false);
        }else{
            msg.setText(R.string.input_invalid);
            msg.setTextColor(getColor(android.R.color.holo_red_light));
            btn_buy.setVisibility(View.VISIBLE);
            btn_code.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
