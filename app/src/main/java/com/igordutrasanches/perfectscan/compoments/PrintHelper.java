package com.igordutrasanches.perfectscan.compoments;

import android.app.Activity;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class PrintHelper extends Activity {
    WebView mWeb;
    PrintJob mPrint;
    boolean c, d;
    private void prepararImpressao(String s, WebView webView){
      try{
          PrintManager manager = (PrintManager)getSystemService(PRINT_SERVICE);
        PrintAdapter adapter = new PrintAdapter(webView.createPrintDocumentAdapter());
        mPrint = (PrintJob) manager.print(s, (PrintDocumentAdapter)adapter, new PrintAttributes.Builder().build());
      }catch (Exception x){
          Toast.makeText(this, x.getMessage(), Toast.LENGTH_SHORT).show();
      }}

    protected void Imprimir(String s, CharSequence c){
        try{
            final WebView webView = new WebView(this);
            webView.setWebViewClient(new WebViewClient(){
                public void onPageFinished(WebView view, String s){
                    prepararImpressao(s, view);
                    mWeb = null;
                }
                public boolean shouldOverrideUrlLoading(WebView view, String s){
                    return false;
                }
            });

            StringBuilder builder = new StringBuilder().append("<html><body><h1>");
            if(c == null) c = "";
            webView.loadDataWithBaseURL(null, builder.append(c).append("</p></body></html>").toString(), "text/HTML", "UTF-8", null);
            mWeb = webView;
        }catch (Exception x){
            Toast.makeText(this, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        try{
            Imprimir((String)getIntent().getExtras().get("title"), (String)getIntent().getExtras().get("code"));
        }catch (Exception x){
            Toast.makeText(this, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onPause(){
        super.onPause();
        d = false;
    }

    public void onResume(){
        super.onResume();
        d = true;
        if(c)
            finish();
    }


    private class PrintAdapter extends PrintDocumentAdapter {
        private final PrintDocumentAdapter adapter;
        public PrintAdapter(PrintDocumentAdapter printDocumentAdapter) {
            adapter = printDocumentAdapter;
        }

        public void onFinish(){
            adapter.onFinish();
            c = true;
            if(d){
                finish();
            }
        }

        public void onLayout(PrintAttributes printAttribut, PrintAttributes printAttributes, CancellationSignal cancellationSignal, LayoutResultCallback layoutResultCallback, Bundle bundle){
            adapter.onLayout(printAttribut, printAttributes, cancellationSignal, layoutResultCallback, bundle);
        }

        public void onStart(){
            adapter.onStart();
            c = false;
        }

        public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor descripto, CancellationSignal cancellationSignal, WriteResultCallback writeResultCallback){
            adapter.onWrite(pageRanges, descripto,cancellationSignal, writeResultCallback);
        }

    }
}