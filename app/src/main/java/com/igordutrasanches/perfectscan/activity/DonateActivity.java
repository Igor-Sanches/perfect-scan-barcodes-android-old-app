package com.igordutrasanches.perfectscan.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.igordutrasanches.perfectscan.PerfectApplication;
import com.igordutrasanches.perfectscan.R;
import com.igordutrasanches.perfectscan.compoments.DateTime;
import com.igordutrasanches.perfectscan.compoments.Key;
import com.igordutrasanches.perfectscan.manager.Manager;
import com.igordutrasanches.perfectscan.manager.tools.IabBroadcastReceiver;
import com.igordutrasanches.perfectscan.manager.tools.IabHelper;
import com.igordutrasanches.perfectscan.manager.tools.IabResult;
import com.igordutrasanches.perfectscan.manager.tools.Inventory;
import com.igordutrasanches.perfectscan.manager.tools.Purchase;

import static com.igordutrasanches.perfectscan.PerfectApplication.produto_1;
import static com.igordutrasanches.perfectscan.PerfectApplication.produto_2;
import static com.igordutrasanches.perfectscan.PerfectApplication.produto_3;
import static com.igordutrasanches.perfectscan.PerfectApplication.produto_4;

public class DonateActivity extends AppCompatActivity implements IabBroadcastReceiver.IabBroadcastListener {

    private boolean isEscrito = false;

    private void onSetup(){
        mHelper = new IabHelper(this, PerfectApplication.ApplicationKey);
        mHelper.iniciarInstalacao(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (mHelper != null && result.isSuccess()) {
                    mBroadcastReceiver = new IabBroadcastReceiver(DonateActivity.this);
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
                if(inv.hasPurchase(produto_1)){
                    isEscrito = true;
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
                if(!info.getSku().equals(produto_1)){
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_donate);
            ListView list1 = (ListView)findViewById(R.id.list_donate1);
            ListView list2 = (ListView)findViewById(R.id.list_donate2);
            list2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(position == 0){
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=2KWM2ATJ86R9N&source=url")));
                    }else{
                        Manager.from(DonateActivity.this).doar();
                    }
                }
            });
            list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String token_code = DateTime.Now(DonateActivity.this).toString("yyyy") + "-IHF-" + DateTime.Now(DonateActivity.this).toString("ddMM-HHss-mm");
                    switch (position){
                        case 0:{
                            try{
                                if(!isEscrito)
                                    mHelper.iniciarCompraDeEscrisoes(DonateActivity.this, produto_1, 100, goCompra, token_code);
                                else
                                    Toast.makeText(DonateActivity.this, getString(R.string.get_escrito), Toast.LENGTH_SHORT).show();
                            }catch (Exception c){
                                Toast.makeText(DonateActivity.this, getString(R.string.buy_refress), Toast.LENGTH_SHORT).show();
                            }
                        }break;
                        case 1:{
                            try{
                                mHelper.iniciarCompra(DonateActivity.this, produto_2, 100, goCompra, token_code);
                            }catch (Exception c){
                                Toast.makeText(DonateActivity.this, getString(R.string.buy_refress), Toast.LENGTH_SHORT).show();
                            }
                        }break;
                        case 2:{
                            try{
                                mHelper.iniciarCompra(DonateActivity.this, produto_3, 100, goCompra, token_code);
                            }catch (Exception c){
                                Toast.makeText(DonateActivity.this, getString(R.string.buy_refress), Toast.LENGTH_SHORT).show();
                            }
                        }break;
                        case 3:{
                            try{
                                mHelper.iniciarCompra(DonateActivity.this, produto_4, 100, goCompra, token_code);
                            }catch (Exception c){
                                Toast.makeText(DonateActivity.this, getString(R.string.buy_refress), Toast.LENGTH_SHORT).show();
                            }
                        }break;
                    }
                }
            });

            onSetup();
        }catch (Exception x){
            Toast.makeText(this, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       try{
           if (mHelper == null) return;
           if (!mHelper.onActivityResult(requestCode, resultCode, data)) {
               super.onActivityResult(requestCode, resultCode, data);
           } else {
               if(resultCode == RESULT_OK) {
                   Key.set("dialog_doar", 100,  this);
                   new AlertDialog.Builder(this)
                           .setTitle(getString(R.string.donate_title))
                           .setMessage(getString(R.string.donate_msg))
                           .setPositiveButton(R.string.btn_close, new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   dialog.dismiss();
                               }
                           }).show();
               }
           }
       }catch (Exception x){
           Toast.makeText(this, x.getMessage(), Toast.LENGTH_SHORT).show();
       }
        onSetup();
    }
}
