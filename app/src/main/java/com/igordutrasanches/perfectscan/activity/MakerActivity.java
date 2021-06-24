package com.igordutrasanches.perfectscan.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.igordutrasanches.perfectscan.R;
import com.igordutrasanches.perfectscan.activity.manager.CodeView;
import com.igordutrasanches.perfectscan.compoments.CodeVerificador;
import com.igordutrasanches.perfectscan.compoments.DateTime;
import com.igordutrasanches.perfectscan.compoments.Key;
import com.igordutrasanches.perfectscan.compoments.ResourceLoader;
import com.igordutrasanches.perfectscan.db.Historical;
import com.igordutrasanches.perfectscan.menuManager.MenuDelegate;
import com.igordutrasanches.perfectscan.menuManager.NavigationMenuManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class MakerActivity extends AppCompatActivity {

    private MenuItem update;
    private EditText codigo, phone_codigo, wifiName, wifiSenha;
    private Spinner lista_codigos, codes_type_input, wifi_input;
    private LinearLayout input_all, input_wifi, input_phone;
    private String codigo_string = "";
    private TextView text_view_inputType;
    public static final String PHONE = "tel:", WIFI = "WIFI:";

    void getTitle(String title){
        text_view_inputType.setText(title);
    }

    private int width = 50, height = 50;
    String getRes(int index){
        return ResourceLoader.getString(this, index);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maker);
        try{
            Toolbar toolbar = findViewById(R.id.toolbar);
            text_view_inputType = (TextView)findViewById(R.id.title_tool_codes);
            lista_codigos = (Spinner)findViewById(R.id.codigos);
            wifi_input = (Spinner)findViewById(R.id.wifi_type);
            input_all = (LinearLayout)findViewById(R.id.codetype_all);
            input_wifi = (LinearLayout)findViewById(R.id.codetype_wifi);
            input_phone = (LinearLayout)findViewById(R.id.codetype_phone);
            codes_type_input = (Spinner)findViewById(R.id.codes_type);
            lista_codigos.setOnItemSelectedListener(spinnerClick);
            codes_type_input.setOnItemSelectedListener(spinnerClickCodesInput);
            wifi_input.setOnItemSelectedListener(spinnerClickWifiInput);
            codigo = (EditText)findViewById(R.id.codigos_text);
            phone_codigo = (EditText)findViewById(R.id.codigos_phone);
            wifiName = (EditText)findViewById(R.id.wifi_name);
            wifiSenha = (EditText)findViewById(R.id.wifi_senha);
            toolbar.inflateMenu(R.menu.maker);
            update = toolbar.getMenu().findItem(R.id.action_update);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if(menuItem.getItemId() == R.id.action_update){
                        verficarPermissoes();

                    }
                    return false;
                }
            });
            //setSupportActionBar(toolbar);
            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    prepararCodigo();
                    boolean isL = false;
                    String isClear = "";
                    int position = codes_type_input.getSelectedItemPosition();
                    if(position == 0){
                        if (phone_codigo.getText().toString().trim().equals("")) {
                            Snackbar.make(view, getRes(R.string.error_code), Snackbar.LENGTH_LONG).show();
                            isL = false;
                        }else {
                         if(NOT.matcher(phone_codigo.getText().toString()).matches()){
                             Snackbar.make(view, getRes(R.string.error_code), Snackbar.LENGTH_LONG).show();
                             isL = false;
                         }isL = true;
                        }
                    }else{
                        if(position == 2){

                            if(wifi_input.getSelectedItemPosition() == 1 || wifi_input.getSelectedItemPosition() == 2){
                                if(wifiName.getText().toString().equals("")){
                                    Snackbar.make(view, getRes(R.string.error_wifi_name), Snackbar.LENGTH_LONG).show();
                                    isL = false;
                                } else if(wifiSenha.getText().toString().equals("")){
                                    Snackbar.make(view, getRes(R.string.error_wifi_key) + " " + wifiName.getText().toString(), Snackbar.LENGTH_LONG).show();
                                    isL = false;
                                }else{
                                    int lenght = wifiSenha.getText().toString().length();
                                    if(lenght < 8){
                                        Snackbar.make(view, getRes(R.string.error_wifi_key_minimun), Snackbar.LENGTH_LONG).show();
                                        isL = false;
                                    }else isL = true;
                                }
                            }else{
                                if(wifiName.getText().toString().equals("")){
                                    Snackbar.make(view, getRes(R.string.error_wifi_name), Snackbar.LENGTH_LONG).show();
                                    isL = false;
                                }else isL = true;
                            }
                        }else {
                            if (codigo.getText().toString().trim().equals("")) {
                                Snackbar.make(view, getRes(R.string.error_code), Snackbar.LENGTH_LONG).show();
                                isL = false;
                            }else isL = true;
                        }
                    }
                    if(isL){
                        gerarCodigos(Key.getCodigo(MakerActivity.this), view);
                    }
                }
            });


            NavigationMenuManager manager = new NavigationMenuManager(this);
            MenuDelegate menuDelegate = new MenuDelegate(this, toolbar, manager);
        }catch (Exception x){
            Toast.makeText(this, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static Pattern NOT = Pattern.compile("[^0-9+ -]");


    public void onResume(){
        Key.setPageView("maker", this);
        lista_codigos.setSelection(Key.getCodigo(this));
        wifi_input.setSelection(Key.getWifi(this));
        codes_type_input.setSelection(Key.getCodigoType(this));
        if(getIntent().getAction() != null){
        Intent my = getIntent();
        if(my != null){
            String action = my.getAction();
            if(action.equals(Intent.ACTION_SEND)){
                String codeMsg = my.getStringExtra(Intent.EXTRA_TEXT);
                if(codeMsg != null && !codeMsg.trim().equals("")){
                    codigo.setText(codeMsg.trim());
                    codigo.setSelection(codigo.getText().toString().length());
                }
            }
        }
        }
        super.onResume();
    }

    private void prepararCodigo() {
        switch (codes_type_input.getSelectedItemPosition()){
            case 0:{
                String code = phone_codigo.getText().toString();
                if(phone_codigo.getText().toString().startsWith(PHONE)){
                    code = phone_codigo.getText().toString().replace(PHONE, "");
                }
                codigo_string = PHONE + code;
            }break;
            case 1:{
                codigo_string = codigo.getText().toString();
            }break;
            case 2:{
                String code = codigo.getText().toString();
                if(codigo.getText().toString().startsWith(WIFI)){
                    code = codigo.getText().toString().replace(WIFI, "");
                }
                if(wifi_input.getSelectedItemPosition() == 0){
                    code = "T:nopass;S:" + wifiName.getText().toString() + ";;;";
                }else if(wifi_input.getSelectedItemPosition() == 1){
                    code = "T:WEP;S:" + wifiName.getText().toString() + ";P:" + wifiSenha.getText().toString() + ";;";
                }else{
                    code = "T:WPA;S:" + wifiName.getText().toString() + ";P:" + wifiSenha.getText().toString() + ";;";
                }

                codigo_string = WIFI + code;
            }break;
        }
    }

    AdapterView.OnItemSelectedListener spinnerClickCodesInput = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Key.setCodigoType(position, MakerActivity.this);
            if(position == 0){
                input_phone.setVisibility(View.VISIBLE);
                input_wifi.setVisibility(View.GONE);
                input_all.setVisibility(View.GONE);
                getTitle(getRes(R.string.error_code_input_phone));

            }else if(position == 1){
                input_phone.setVisibility(View.GONE);
                input_wifi.setVisibility(View.GONE);
                input_all.setVisibility(View.VISIBLE);
                getTitle(getRes(R.string.error_code_input_code));


            }else{
                input_phone.setVisibility(View.GONE);
                input_all.setVisibility(View.GONE);
                input_wifi.setVisibility(View.VISIBLE);
                getTitle(getRes(R.string.error_code_input_wifi));

            }

            if(codes_type_input.getSelectedItemPosition() == 1){
                update.setVisible(true);
            }else update.setVisible(false);



        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    AdapterView.OnItemSelectedListener spinnerClickWifiInput = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Key.setWifi(position, MakerActivity.this);
            if(position == 0){
                wifiSenha.setText("");
                wifiSenha.setEnabled(false);
            }else{
                wifiSenha.setEnabled(true);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void gerarCodigos(int position, View view) {
        String finalCodigo = codigo_string.trim();
        String format = "";
        boolean isTrue = false;
        switch (position) {
            case 0: {
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    multiFormatWriter.encode(finalCodigo, BarcodeFormat.AZTEC, width * 3, height * 3);
                    format = "AZTEC";
                    isTrue = true;
                } catch (Exception x) {
                    String message = getRes(R.string.error_code_genered_error);
                    Snackbar.make(view, message + "\n" + x.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
            break;
            case 1: {
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    multiFormatWriter.encode(finalCodigo, BarcodeFormat.CODABAR, width * 3, height * 2);
                    format = "CODABAR";
                    isTrue = true;
                } catch (Exception x) {
                    String message = getRes(R.string.error_code_genered_error);
                    Snackbar.make(view, message + "\n" + x.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
            break;
            case 2: {
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    multiFormatWriter.encode(finalCodigo, BarcodeFormat.CODE_128, width * 3, height * 2);
                    format = "CODE_128";
                    isTrue = true;
                } catch (Exception x) {
                    String message = getRes(R.string.error_code_genered_error);
                    Snackbar.make(view, message + "\n" + x.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
            break;
            case 3: {
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    multiFormatWriter.encode(finalCodigo, BarcodeFormat.CODE_39, width * 3, height * 2);
                    format = "CODE_39";
                    isTrue = true;
                } catch (Exception x) {
                    String message = getRes(R.string.error_code_genered_error);
                    Snackbar.make(view, message + "\n" + x.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
            break;
            case 4: {
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    multiFormatWriter.encode(finalCodigo, BarcodeFormat.CODE_93, width * 3, height * 2);
                    format = "CODE_93";
                    isTrue = true;
                } catch (Exception x) {
                    String message = getRes(R.string.error_code_genered_error);
                    Snackbar.make(view, message + "\n" + x.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
            break;
            case 5: {
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    multiFormatWriter.encode(finalCodigo, BarcodeFormat.EAN_13, width * 3, height * 2);
                    format = "EAN_13";
                    isTrue = true;
                } catch (Exception x) {
                    String message = getRes(R.string.error_code_genered_error);
                    Snackbar.make(view, message + "\n" + x.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
            break;
            case 6: {
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    multiFormatWriter.encode(finalCodigo, BarcodeFormat.EAN_8, width * 3, height * 2);
                    format = "EAN_8";
                    isTrue = true;
                } catch (Exception x) {
                    if (x.getMessage().toString().contains("Contents should only contain digits, but go")) {
                        String message = getRes(R.string.error_code_genered_error);
                        Snackbar.make(view, message + "\n" + x.getMessage(), Snackbar.LENGTH_LONG).show();
                    } else if (x.getMessage().toString().contains("Contents do not pass checksum")) {
                        Snackbar.make(view, getRes(R.string.error_code_genered_error_3), Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(view, getRes(R.string.error_code_genered_error_1213) + " " + codigo.getText().toString().length(), Snackbar.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case 7: {
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    multiFormatWriter.encode(finalCodigo, BarcodeFormat.ITF, width * 3, height * 2);
                    format = "ITF";
                    isTrue = true;
                } catch (Exception x) {
                    if (x.getMessage().toString().contains("Contents should only contain digits, but go")) {
                        String message = getRes(R.string.error_code_genered_error);
                        Snackbar.make(view, message + "\n" + x.getMessage(), Snackbar.LENGTH_LONG).show();
                    } else if (x.getMessage().toString().contains("Contents do not pass checksum")) {
                        Snackbar.make(view, getRes(R.string.error_code_genered_error_passcheckup), Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(view, getRes(R.string.error_code_genered_error_78) + " " + codigo.getText().toString().length(), Snackbar.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case 8: {
                try {

                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    multiFormatWriter.encode(finalCodigo, BarcodeFormat.PDF_417, width * 3, height * 2);
                    format = "PDF_417";
                    isTrue = true;
                } catch (Exception x) {
                    if (x.getMessage().toString().contains("Contents should only contain digits, but go")) {
                        String message = getRes(R.string.error_code_genered_error);
                        Snackbar.make(view, message + "\n" + x.getMessage(), Snackbar.LENGTH_LONG).show();
                    } else if (x.getMessage().toString().contains("Contents do not pass checksum")) {
                        Snackbar.make(view, getRes(R.string.error_code_genered_error_passcheckup), Snackbar.LENGTH_LONG).show();
                    } else if (x.getMessage().toString().contains("The length pf the input should be even")) {
                        Snackbar.make(view, getRes(R.string.error_code_genered_error2), Snackbar.LENGTH_LONG).show();
                    } else {
                        String message = getRes(R.string.error_code_genered_error);
                        Snackbar.make(view, message + x.getMessage(), Snackbar.LENGTH_LONG).show();

                    }
                }} break;
            case 9: {
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    multiFormatWriter.encode(finalCodigo, BarcodeFormat.QR_CODE, width * 3, height * 2);
                    format = "QR_CODE";
                    isTrue = true;
                } catch (Exception x) {
                    String message = getRes(R.string.error_code_genered_error);
                    Snackbar.make(view, message + "\n" + x.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
            break;
            case 10: {
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    multiFormatWriter.encode(finalCodigo, BarcodeFormat.UPC_E, width * 3, height * 2);
                    format = "UPC_E";
                    isTrue = true;
                } catch (Exception x) {
                    String message = getRes(R.string.error_code_genered_error);
                    Snackbar.make(view, message + "\n" + x.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
            break;
            case 11: {
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    multiFormatWriter.encode(finalCodigo, BarcodeFormat.UPC_A, width * 3, height * 2);
                    format = "UPC_A";
                    isTrue = true;
                } catch (Exception x) {
                    String message = getRes(R.string.error_code_genered_error);
                    Snackbar.make(view, message + "\n" + x.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
            break;
        }

        if(isTrue){

            Historical historical = new Historical(this);
            historical.adicionarFromCreate(finalCodigo, format);
            CodeView.by(this).show(finalCodigo, format, CodeVerificador.by(finalCodigo, format).getNameCode(this), 1, 0);
        }

    }

    private String getDateTime() {
        return DateTime.Now(this).toString();
    }

    boolean enabled = false;
    AdapterView.OnItemSelectedListener spinnerClick = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Key.setCodigo(position, MakerActivity.this);
            try{
                switch (position){
                    case 0:
                        enabled = true;
                        break;
                    case 1:
                        enabled = false;
                        break;
                    case 2:
                        enabled = false;
                        break;
                    case 3:
                        enabled = false;
                        break;
                    case 4:
                        enabled = false;
                        break;
                    case 5:
                        enabled = false;
                        break;
                    case 6:
                        enabled = false;
                        break;
                    case 7:
                        enabled = false;
                        break;
                    case 8:
                        enabled = true;
                        break;
                    case 9:
                        enabled = true;
                        break;
                    case 10:
                        enabled = false;
                        break;
                    case 11:
                        enabled = false;
                        break;
                }
                if(!enabled)
                    codes_type_input.setSelection(1);

                update.setVisible(enabled);

                codes_type_input.setEnabled(enabled);
            }catch (Exception x){
                Toast.makeText(MakerActivity.this, x.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void onOpenFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        String dados= "";
        if (requestCode == 101 ) {

            try{
                String caminho = data.getData().getPath();
                caminho = caminho.substring(caminho.lastIndexOf(":") + 1);
                File myfile = new File(Environment.getExternalStorageDirectory() + "/" + caminho);
                FileInputStream input= new FileInputStream(myfile);
                BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
                String dataRow = "";
                while ((dataRow = buffer.readLine()) != null){
                    dados += dataRow;
                }
                buffer.close();
                codes_type_input.setSelection(5);
                codigo.setText(dados);
            }catch (Exception x){
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void verficarPermissoes() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 555);

            } catch (Exception x) {
                onOpenFile();

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grandPermission) {
        if (requestCode == 555 && grandPermission[0] == PackageManager.PERMISSION_GRANTED) {
            onOpenFile();

        } else {
            Toast.makeText(this, getRes(R.string.msg_permission), Toast.LENGTH_SHORT).show();
        }
    }

}