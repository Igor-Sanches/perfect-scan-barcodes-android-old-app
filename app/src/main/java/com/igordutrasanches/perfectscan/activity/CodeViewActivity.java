package com.igordutrasanches.perfectscan.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.igordutrasanches.perfectscan.R;
import com.igordutrasanches.perfectscan.compoments.CodeTypeReturn;
import com.igordutrasanches.perfectscan.compoments.CodeVerificador;
import com.igordutrasanches.perfectscan.compoments.Go;
import com.igordutrasanches.perfectscan.compoments.HelperManager;
import com.igordutrasanches.perfectscan.compoments.Key;
import com.igordutrasanches.perfectscan.compoments.PrintHelper;
import com.igordutrasanches.perfectscan.compoments.ResourceLoader;
import com.igordutrasanches.perfectscan.compoments.SmsHelper;
import com.igordutrasanches.perfectscan.manager.StorageFile;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;

public class CodeViewActivity extends AppCompatActivity {

    private BottomNavigationView menuBar;
    private Toolbar toolbar;
    private MenuItem find;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_view);
        try{

            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            isvisible = (ImageView)findViewById(R.id.view_code_visible);
            menuBar = (BottomNavigationView)findViewById(R.id.menu_code_view_bottom);
            menuBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    int id = menuItem.getItemId();
                    if(id == R.id.menu_google){
                        Go.by(CodeViewActivity.this).goProductGo(codigoResult, CodeViewActivity.this);
                    }
                    if(id == R.id.menu_product){
                        Go.by(CodeViewActivity.this).goStore(codigoResult);
                    }
                    if(id == R.id.menu_eifi_conect){
                        Go.by(CodeViewActivity.this).goWifiConnect(codigoResult, CodeViewActivity.this);
                    }
                    if(id == R.id.menu_navegador_find){
                        Go.by(CodeViewActivity.this).goWebFind(codigoResult);
                    }
                    if(id == R.id.menu_share_find){
                        try{
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, codigoResult);
                            intent.setType("text/plain");
                            startActivity(Intent.createChooser(intent, toolbar.getTitle()));
                        }catch (Exception x){

                        } }
                    if(id == R.id.menu_add_wifi){
                        Go.by(CodeViewActivity.this).goWifiAdd(codigoResult, CodeViewActivity.this);
                    }
                    if(id == R.id.menu_send_email){
                        Go.by(CodeViewActivity.this).goSendEmail(codigoResult);
                    }
                    if(id == R.id.menu_add_contato_email){
                        Go.by(CodeViewActivity.this).goAddContactmail(codigoResult);
                    }
                    if(id == R.id.menu_phone_dial){
                        Go.by(CodeViewActivity.this).goDial(codigoResult);
                    }
                    if(id == R.id.menu_send_sms){
                        Go.by(CodeViewActivity.this).goSendSMS(codigoResult);
                    }
                    if(id == R.id.menu_send_mms){
                        Go.by(CodeViewActivity.this).goSendMMS(codigoResult);
                    }
                    if(id == R.id.menu_phone_call){
                        permisionCall();
                    }
                    if(id == R.id.menu_add_contato){
                        Go.by(CodeViewActivity.this).goAdicionarContatoNumero(codigoResult, false);
                    }
                    if(id == R.id.menu_add_contato_sms){
                        Go.by(CodeViewActivity.this).goAdicionarContatoNumero(SmsHelper.getDestinatario(CodeViewActivity.this, codigoResult), true);
                    }
                    if(id == R.id.menu_navegador){
                        Go.by(CodeViewActivity.this).goBrowser(codigoResult, CodeViewActivity.this);
                    }
                    if(id == R.id.menu_send_whatsapp){
                        Go.by(CodeViewActivity.this).goWhatsapp(codigoResult);
                    }
                    if(id == R.id.menu_youtube){
                        Go.by(CodeViewActivity.this).goYoutube(codigoResult);
                    }
                    if(id == R.id.menu_instagram){
                        Go.by(CodeViewActivity.this).goInstagram(codigoResult);
                    }
                    if(id == R.id.menu_telegram){
                        Go.by(CodeViewActivity.this).goTelegram(codigoResult);
                    }
                    if(id == R.id.menu_play_store){
                        Go.by(CodeViewActivity.this).goPlayStore(codigoResult);
                    }
                    return false;
                }
            });
            onView();
        }catch (Exception x){
            Toast.makeText(this, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void onView() {
        try{

            codigoResult = (String)getIntent().getExtras().get("codigo_resultado");
            codigoFormat = (String)getIntent().getExtras().get("codigo_formato");
            codigoTitulo = (String)getIntent().getExtras().get("codigo_titulo");


            int viewPage = (int)getIntent().getExtras().get("viewpage");
            int historical = (int)getIntent().getExtras().get("historical");
            LinearLayout adapter = (LinearLayout)findViewById(R.id.adapter_card_view);
            CardView viewResult = (CardView)findViewById(R.id.card_view_result);
            CardView viewCode = (CardView)findViewById(R.id.card_view_code);
            adapter.removeAllViews();
            if(viewPage == 0){
                adapter.addView(viewResult);
                adapter.addView(viewCode);
            }else{
                adapter.addView(viewCode);
                adapter.addView(viewResult);
            }

            if(codigoResult != null && codigoFormat != null && codigoTitulo != null){
                getSupportActionBar().setSubtitle(CodeVerificador.by(codigoResult, codigoFormat).getTypeCode(this));
                getSupportActionBar().setTitle(codigoTitulo.equals("") ? CodeVerificador.by(codigoResult, codigoFormat).getNameCode(this) : codigoTitulo);


                if(codigoResult.startsWith("URI:") || codigoResult.startsWith("URL:")){
                    f_codigoResult = codigoResult.substring(4);
                }else if(codigoResult.startsWith("TEL:")){
                    f_codigoResult = "tel:" + codigoResult.substring(4);
                } else {
                    f_codigoResult = codigoResult;
                }

                f_codigoResult = CodeVerificador.by(codigoResult).isResumed(this);

                ((TextView)findViewById(R.id.codeview)).setText(f_codigoResult);
                ((ImageView)findViewById(R.id.code_view)).setImageBitmap(codigo);
                if(CodeVerificador.by(codigoResult, codigoFormat).isSMS()
                        || CodeVerificador.by(codigoResult, codigoFormat).isWIFI()
                        || CodeVerificador.by(codigoResult, codigoFormat).isPhone()
                        || CodeVerificador.by(codigoResult, codigoFormat).isEmail()){
                    isvisible.setVisibility(View.VISIBLE);
                    getIsVisible();
                }
                helperManager = new HelperManager(this, this);
                helperManager.getLocutor(f_codigoResult, false, historical == 0 ? true : false);
            }
            showInfoCode();
            getCode();
        }catch (Exception x){
            Toast.makeText(this, getString(R.string.error_scan), Toast.LENGTH_SHORT).show();
        }}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_criar_codigos, menu);
        find = (MenuItem)menu.findItem(R.id.menu_browser);
        showMenu();
        return true;
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == android.R.id.home){
            onBackPressed();
        }
        else if(id == R.id.menu_share_is_image) {
            permisionSaveSharePNG();
        }
        else if(id == R.id.menu_share_is_text){
            try{
                 Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                intent.putExtra(Intent.EXTRA_TEXT, codigoResult);
                startActivity(Intent.createChooser(intent, getString(R.string.share)));
            }catch (Exception e){

            }  }
        else if(id == R.id.menu_copy){
            Go.by(this).onCopy(codigoResult);
        }
        else if(id == R.id.menu_browser){
            Go.by(this).goWebFind(codigoResult);
        }
        else if(id == R.id.menu_save_image){
            permisionSavePNG();
        }
        else if(id == R.id.menu_save_txt){
            permisionSaveTXT();
        }
        else if(id == R.id.menu_print_is_text){
            try{
                Intent intent = new Intent(this, PrintHelper.class);
                intent.putExtra("title", getSupportActionBar().getTitle().toString());
                intent.putExtra("code", codigoResult);
                startActivity(intent);
            }catch (Exception x){
                Toast.makeText(this, x.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void permisionSavePNG() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 5555);

            } catch (Exception x) {
              Go.by(this).goSave(codigoResult, codigoFormat, codigo, 1);
            }
        }
    }
    private void permisionSaveSharePNG() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

            } catch (Exception x) {
                shareImage();
            }
        }
    }

    private void shareImage() {
        try{
            File file = StorageFile.getFileSharing();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            codigo.compress(Bitmap.CompressFormat.PNG, 0, fileOutputStream);
            fileOutputStream.close();
            Uri uri = StorageFile.getFileShare(this, file);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_SUBJECT, "");
            intent.putExtra(Intent.EXTRA_TEXT, getSupportActionBar().getTitle().toString());
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, getString(R.string.share)));
        }catch (Exception e){

        }
    }

    private void permisionSaveTXT() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 5556);

            } catch (Exception x) {

                    Go.by(this).goSave(codigoResult, codigoFormat, codigo, 2);
            }
        }
    }

    private void permisionCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 554);

            } catch (Exception x) {
                onCall();
            }
        }
    }

    private void onCall() {
        Go.by(CodeViewActivity.this).goCall(codigoResult);
    }

    private Bitmap codigo;

    public void onCodeNarrardor(View view) {
        helperManager.getLocutor(f_codigoResult, true, false);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grandPermission) {
        if(requestCode == 5555 && grandPermission[0] == PackageManager.PERMISSION_GRANTED) {

            Go.by(this).goSave(codigoResult, codigoFormat, codigo, 1);

        }else if(requestCode == 100 && grandPermission[0] == PackageManager.PERMISSION_GRANTED) {
            shareImage();
        } else if(requestCode == 5556 && grandPermission[0] == PackageManager.PERMISSION_GRANTED) {
            Go.by(this).goSave(codigoResult, codigoFormat, codigo, 2);
        }else if(requestCode == 554 && grandPermission[0] == PackageManager.PERMISSION_GRANTED) {
            onCall();
        }
        else {
            Toast.makeText(this, getRes(R.string.msg_permission), Toast.LENGTH_SHORT).show();
        }
    }

    boolean type = false;
    private void showInfoCode() {
        menuBar.getMenu().clear();
        switch (CodeTypeReturn.result(codigoResult, codigoFormat)){
            case email:{
                menuBar.inflateMenu(R.menu.menu_email);
                type = true;
                break;
            }
            case facebook:{
                menuBar.inflateMenu(R.menu.menu_facebook);
                type = false;
                break;
            }
            case telegram:{
                menuBar.inflateMenu(R.menu.menu_telegram);
                type = false;
                break;
            }
            case instagram:{
                menuBar.inflateMenu(R.menu.menu_instagram);
                type = false;
                break;
            }
            case sms:{
                menuBar.inflateMenu(R.menu.menu_sms);
                type = true;
                ((MenuItem)menuBar.getMenu().findItem(R.id.menu_add_contato_sms)).setVisible(SmsHelper.isDestinatario(this, codigoResult));
                break;
            }
            case whatsapp:{
                menuBar.inflateMenu(R.menu.menu_whatsapp);
                type = false;
                break;
            }
            case produto:{
                menuBar.inflateMenu(R.menu.menu_product);
                type = true;
                break;
            }
            case url:{
                menuBar.inflateMenu(R.menu.menu_uri);
                type = false;
                break;
            }
            case texto:{
                menuBar.inflateMenu(R.menu.menu_text);
                type = false;
                break;
            }
            case youtube:{
                menuBar.inflateMenu(R.menu.menu_youtube);
                type = false;
                break;
            }
            case wifi:{
                menuBar.inflateMenu(R.menu.menu_wifi);
                type = true;
                break;
            }
            case phone:{
                menuBar.inflateMenu(R.menu.menu_phone);
                type = true;
                break;
            }
            case app:{
                menuBar.inflateMenu(R.menu.menu_app);
                type = false;
                break;
            }
        }

        showMenu();
    }

    private void showMenu() {
        if(find != null){
            find.setVisible(type);
        }
    }

    private static String codigoResult, codigoFormat, codigoTitulo;
    public void onResume(){
        super.onResume();
        onView();
    }

    private HelperManager helperManager;
    private void getIsVisible() {
        if(Key.getVisibleCodeInView(this))
        {
            isvisible.setImageResource(R.drawable.ic_action_visible);
            ((TextView)findViewById(R.id.codeview)).setText(f_codigoResult);
        }
        else{
            isvisible.setImageResource(R.drawable.ic_action_invisible);
            ((TextView)findViewById(R.id.codeview)).setText(codigoResult);
        }}

    String f_codigoResult;
    ImageView isvisible;
    public void onViewCode(View view){
        Key.setVisibleCodeInView(this, !Key.getVisibleCodeInView(this));
        getIsVisible();

    }

    private int width = 300, height = 190;
    String getRes(int index){
        return ResourceLoader.getString(this, index);
    }

    void getCode(){
        getSizeImageView();
        switch (codigoFormat){
            case "AZTEC":{
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    com.google.zxing.common.BitMatrix bit = null;
                    bit = multiFormatWriter.encode(codigoResult, BarcodeFormat.AZTEC, width, height);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    codigo = encoder.createBitmap(bit);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }break;
            case "CODABAR":{
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    com.google.zxing.common.BitMatrix bit = null;
                    bit = multiFormatWriter.encode(codigoResult, BarcodeFormat.CODABAR, width * 3, height);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    codigo = encoder.createBitmap(bit);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }break;
            case "CODE_128":{
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    com.google.zxing.common.BitMatrix bit = null;
                    bit = multiFormatWriter.encode(codigoResult, BarcodeFormat.CODE_128, width * 2, height);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    codigo = encoder.createBitmap(bit);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }break;
            case "CODE_39":{
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    com.google.zxing.common.BitMatrix bit = null;
                    bit = multiFormatWriter.encode(codigoResult, BarcodeFormat.CODE_39, width * 2, height);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    codigo = encoder.createBitmap(bit);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }break;
            case "CODE_93":{
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    com.google.zxing.common.BitMatrix bit = null;
                    bit = multiFormatWriter.encode(codigoResult, BarcodeFormat.CODE_93, width * 2, height);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    codigo = encoder.createBitmap(bit);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }break;
            case "EAN_13":{
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    com.google.zxing.common.BitMatrix bit = null;
                    bit = multiFormatWriter.encode(codigoResult, BarcodeFormat.EAN_13,width * 2,height);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    codigo = encoder.createBitmap(bit);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }break;
            case "EAN_8":{
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    com.google.zxing.common.BitMatrix bit = null;
                    bit = multiFormatWriter.encode(codigoResult, BarcodeFormat.EAN_8, width * 2, height);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    codigo = encoder.createBitmap(bit);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }break;
            case "ITF":{
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    com.google.zxing.common.BitMatrix bit = null;
                    bit = multiFormatWriter.encode(codigoResult, BarcodeFormat.ITF, width * 2, height);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    codigo = encoder.createBitmap(bit);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }break;
            case "PDF_417":{
                try {
                    try{
                        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                        com.google.zxing.common.BitMatrix bit = null;
                        bit = multiFormatWriter.encode(codigoResult, BarcodeFormat.PDF_417, width * 2, height);
                        BarcodeEncoder encoder = new BarcodeEncoder();
                        codigo = encoder.createBitmap(bit);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }catch (Exception x){
                    Toast.makeText(this, x.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }break;
            case "QR_CODE":{
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    com.google.zxing.common.BitMatrix bit = null;
                    bit = multiFormatWriter.encode(codigoResult, BarcodeFormat.QR_CODE, width, height);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    codigo = encoder.createBitmap(bit);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }break;
            case "UPC_E":{
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    com.google.zxing.common.BitMatrix bit = null;
                    bit = multiFormatWriter.encode(codigoResult, BarcodeFormat.UPC_E, width * 2, height);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    codigo = encoder.createBitmap(bit);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }break;
            case "UPC_A":{
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    com.google.zxing.common.BitMatrix bit = null;
                    bit = multiFormatWriter.encode(codigoResult, BarcodeFormat.UPC_A, width * 2, height);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    codigo = encoder.createBitmap(bit);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }break;
        }
    }

    private void getSizeImageView() {
        width = 500;
        height = 500;

    }

}