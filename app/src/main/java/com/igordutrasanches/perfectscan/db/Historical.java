package com.igordutrasanches.perfectscan.db;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.igordutrasanches.perfectscan.R;
import com.igordutrasanches.perfectscan.activity.manager.CodeView;
import com.igordutrasanches.perfectscan.compoments.CodeTypeReturn;
import com.igordutrasanches.perfectscan.compoments.CodeVerificador;
import com.igordutrasanches.perfectscan.compoments.DateTime;
import com.igordutrasanches.perfectscan.compoments.Key;
import com.igordutrasanches.perfectscan.compoments.ResourceLoader;
import com.igordutrasanches.perfectscan.fragment.CreatedFragment;
import com.igordutrasanches.perfectscan.fragment.ScannedFragment;

import java.text.DateFormat;
import java.util.List;

public class Historical {

    public AdapterView.OnItemClickListener listenerScanned = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           try{
               HistoricalInfo hist = new Scan(mContext).getID(id);

               CodeView.by(mContext).show(hist.getCode(), hist.getBarcodeFormat(), hist.getTitle(), 0, 0);
           }catch (Exception x){
               Toast.makeText(mContext, x.getMessage(), Toast.LENGTH_SHORT).show();
           }
        }
    };
    public AdapterView.OnItemClickListener listenerCreate = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try{
                HistoricalInfo hist = new Create(mContext).getID(id);

                CodeView.by(mContext).show(hist.getCode(), hist.getBarcodeFormat(), hist.getTitle(), 1, 0);
            }catch (Exception x){
                Toast.makeText(mContext, x.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };
    private static Context mContext;

    public Historical(Context context){
        this.mContext = context;
    }
    DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    public void adicionarFromScan(Result result) {
        if(Key.getScannedSaved(mContext) || Key.getMassa(mContext)){
            if (result != null) {
            try {
                String codigo = result.getText();
                Scan scan = new Scan(mContext);
                if (!codigo.trim().equals("")) {
                    List lista = scan.lista(1);
                    if (lista != null) {
                        for (HistoricalInfo info : scan.lista(1)) {
                            if (info.getCode().contains(result.getText()) && info.getBarcodeFormat().endsWith(result.getBarcodeFormat().toString())) {
                                scan.apagar(info.getId(), false);
                            }
                        }
                        HistoricalInfo historicalInfo = new HistoricalInfo();
                        historicalInfo.setTitle(CodeVerificador.by(result.getText(), result.getBarcodeFormat().toString()).getNameCode(mContext));
                        historicalInfo.setData(formatter.format(result.getTimestamp()));
                        historicalInfo.setBarcodeFormat(result.getBarcodeFormat().toString());
                        historicalInfo.setCode(result.getText());
                        historicalInfo.setCodeType(CodeTypeReturn.result(result.getText(), result.getBarcodeFormat().toString()).toString());
                        scan.adicionar(historicalInfo);
                    }
                }
            } catch (Exception x) {
                Toast.makeText(mContext, x.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
        Key.setMaxCodeScan(mContext);
    }

    public void adicionarFromCreate(String codigo, String barcodeType){
       if(Key.getCreateSaved(mContext)){
           if(codigo != null){
               try{
                   Create create = new Create(mContext);
                   if(!codigo.trim().equals("")){
                       List lista = create.lista(1);
                       if(lista != null){
                           for(HistoricalInfo info : create.lista(1)){
                               if (info.getCode().contains(codigo) && info.getBarcodeFormat().contains(barcodeType)) {
                                   create.apagar(info.getId(), false);
                               }
                           }
                           HistoricalInfo historicalInfo = new HistoricalInfo();
                           historicalInfo.setTitle(CodeVerificador.by(codigo, barcodeType).getNameCode(mContext));
                           historicalInfo.setData(DateTime.Now(mContext).toString("dd/MM/yyyy HH:mm"));
                           historicalInfo.setBarcodeFormat(barcodeType);
                           historicalInfo.setCode(codigo);
                           historicalInfo.setCodeType(CodeTypeReturn.result(codigo, barcodeType).toString());
                           create.adicionar(historicalInfo);
                       }
                   }
               }catch (Exception x){
                   Toast.makeText(mContext,  x.getMessage(), Toast.LENGTH_SHORT).show();
               }
           }
       }
        Key.setMaxCodeGerado(mContext);
    }

    public void apagarTudoEmScanned() {
             Scan scan1 = new Scan(mContext);
            scan1.apagar(-1, true);
    }

    public void apagarTudoEmCreated() {
            Create create = new Create(mContext);
            create.apagar(-1, true);
    }

    String getRes(int index){
        return ResourceLoader.getString(mContext, index);
    }
    public void onCreateContextMenuScanned(ContextMenu menu, View view, ContextMenu.ContextMenuInfo info, long id) {
        HistoricalInfo historical = new Scan(mContext).getID(id);
        menu.setHeaderTitle(historical.getTitle());
        menu.add(0, 1, 0, getRes(R.string.delete_historical)).setIcon(R.drawable.ic_action_clear);
        menu.add(0, 2, 0, getRes(R.string.edit_historical)).setIcon(R.drawable.ic_menu_maker);
    }

    public void onCreateContextMenuCreate(ContextMenu menu, View view, ContextMenu.ContextMenuInfo info, long id) {
        HistoricalInfo historical = new Create(mContext).getID(id);
        menu.setHeaderTitle(historical.getTitle());
        menu.add(1, 3, 0, getRes(R.string.delete_historical)).setIcon(R.drawable.ic_action_clear);
        menu.add(2, 4, 0, getRes(R.string.edit_historical)).setIcon(R.drawable.ic_menu_maker);
    }

    private AlertDialog editorDialog;
    private EditText editor_Text;
    private TextView editor_error;

    public boolean onContextItemSelectedIsScanned(MenuItem item, long id) {
        final HistoricalInfo hist = new Scan(mContext).getID(id);
        switch (item.getItemId()){
            case 1:{
                    Scan scan = new Scan(mContext);
                    scan.apagar(hist.getId(), false);
                    ScannedFragment.updated();
            }break;
            case 2:{
                try{
                      View editor = LayoutInflater.from(mContext).inflate(R.layout.dialog_edit_historical, null);
                  if(editor != null){
                        editorDialog = new AlertDialog.Builder(mContext).create();
                        editor_Text = (EditText)editor.findViewById(R.id.editor_historical);
                        if(editor_Text != null){
                            editor_Text.setText(hist.getTitle());
                            ((Toolbar)editor.findViewById(R.id.toolbar_historical)).setNavigationIcon(hist.getIcon());
                            editor_Text.setSelection(editor_Text.getText().toString().length());
                        }
                        editor_error = (TextView)editor.findViewById(R.id.error_text);
                        ((Button)editor.findViewById(R.id.btn_save_editor)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(editor_Text.getText().toString().trim().equals("")){
                                    editor_error.setText("Digite algo");
                                }else if(checkInvalidCharsShorting(editor_Text.getText().toString())){
                                    editor_error.setText("Digite algo");
                                }else{
                                        atualizarFromScan(hist.getId());
                                }
                            }
                        });
                        ((Button)editor.findViewById(R.id.btn_cancel_editor)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(editorDialog != null){
                                    editorDialog.cancel();
                                    editorDialog.dismiss();
                                } }
                        });
                        ((ImageButton)editor.findViewById(R.id.btn_clear_editor)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(editor_Text != null){
                                    editor_Text.setText("");
                                    editor_Text.getText().clear();
                                } }
                        });
                    }
                    editorDialog.setView(editor);
                    editorDialog.show();

                }catch (Exception x){
                    Toast.makeText(mContext, x.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }break;
        }
        return false;
    }

    public boolean onContextItemSelectedIsCreate(MenuItem item, long id) {
        final HistoricalInfo hist = new Create(mContext).getID(id);
        switch (item.getItemId()){
            case 3:{
                    Create create = new Create(mContext);
                    create.apagar(hist.getId(), false);
                    CreatedFragment.updated();
             }break;
            case 4:{
                try{
                    View editor = LayoutInflater.from(mContext).inflate(R.layout.dialog_edit_historical, null);
                    if(editor != null){
                        editorDialog = new AlertDialog.Builder(mContext).create();
                        editor_Text = (EditText)editor.findViewById(R.id.editor_historical);
                        if(editor_Text != null){
                            editor_Text.setText(hist.getTitle());
                            ((Toolbar)editor.findViewById(R.id.toolbar_historical)).setNavigationIcon(hist.getIcon());
                            editor_Text.setSelection(editor_Text.getText().toString().length());
                        }
                        editor_error = (TextView)editor.findViewById(R.id.error_text);
                        ((Button)editor.findViewById(R.id.btn_save_editor)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(editor_Text.getText().toString().trim().equals("")){
                                    editor_error.setText("Digite algo");
                                }else if(checkInvalidCharsShorting(editor_Text.getText().toString())){
                                    editor_error.setText("Digite algo");
                                }else{
                                   atualizarFromCreate(hist.getId());
                                }
                            }
                        });
                        ((Button)editor.findViewById(R.id.btn_cancel_editor)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(editorDialog != null){
                                    editorDialog.cancel();
                                    editorDialog.dismiss();
                                } }
                        });
                        ((ImageButton)editor.findViewById(R.id.btn_clear_editor)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(editor_Text != null){
                                    editor_Text.setText("");
                                    editor_Text.getText().clear();
                                } }
                        });
                    }
                    editorDialog.setView(editor);
                    editorDialog.show();

                }catch (Exception x){
                    Toast.makeText(mContext, x.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }break;
        }
        return false;
    }

    private void atualizarFromScan(long id) {
        Scan scan = new Scan(mContext);
        String titulo = editor_Text.getText().toString();
        HistoricalInfo info = scan.getID(id);
        info.setTitle(titulo);
        scan.atualizar(info);
        ScannedFragment.updated();
        editorDialog.cancel();
        editorDialog.dismiss();
    }

    private void atualizarFromCreate(long id) {
        Create create = new Create(mContext);
        String titulo = editor_Text.getText().toString();
        HistoricalInfo info = create.getID(id);
        info.setTitle(titulo);
        create.atualizar(info);
        CreatedFragment.updated();
        editorDialog.cancel();
        editorDialog.dismiss();
    }

    private boolean checkInvalidCharsShorting(String string) {
        boolean bl = false;
        String[] arrstring = new String[]{"\\", "/", ":", "*", "?", "\"", "<", ">", "|"};   int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String string2 = arrstring[n2];
            if (string.contains((CharSequence) string2)) {
                bl = true;
            } else {
                char c2 = string2.charAt(0);
                if (c2 >= '!' && c2 < '~' && c2 != '?' && string.indexOf((int) ((char) (65248 + c2))) >= 0) {
                    bl = true;
                }
            }
            ++n2;
        }
        return bl;
    }
}

