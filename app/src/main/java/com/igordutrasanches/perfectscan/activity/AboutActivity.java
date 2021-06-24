package com.igordutrasanches.perfectscan.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.igordutrasanches.perfectscan.R;
import com.igordutrasanches.perfectscan.compoments.Go;
import com.igordutrasanches.perfectscan.compoments.ResourceLoader;
import com.igordutrasanches.perfectscan.compoments.Services;

import java.util.Locale;

public class AboutActivity extends AppCompatActivity {

    private Animation bottom, right, top;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.feedback);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.action_feedback){
                    Go.by(AboutActivity.this).onFeedBack();
                }
                return false;
            }
        });
        String version = "";
        try {
            version = getPackageManager().getPackageInfo((String)getPackageName(), (int) 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ((TextView)findViewById(R.id.version)).setText(getString(R.string.version) + " " + version);

        bottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        top = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        right = AnimationUtils.loadAnimation(this, R.anim.fromright);
        ((LinearLayout)findViewById(R.id.about_anim_top)).setAnimation(top);
        ((CardView)findViewById(R.id.list_carview)).setAnimation(right);
        ((NestedScrollView)findViewById(R.id.anim_bottom)).setAnimation(bottom);

    }

    private String loadUrl = "file:///android_asset/privacy/";
    private String loadUrl_license = "file:///android_asset/html/";
    private String pt = Locale.getDefault().getCountry();
    private boolean isPt = pt.equals("BR") ? true : false;
    public void onPrivacy(View view){
        try{
            WebView web = new WebView(this);
            String html = isPt ? "br.html" : "us.html";
            web.loadUrl(loadUrl + html);
            AlertDialog alertDialog = new AlertDialog.Builder(this).setView(web).create();
            alertDialog.show();
        }catch (Exception x){
            Toast.makeText(this, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onOpiniao(View v){
        Services.from(AboutActivity.this).putShowHelp();
    }
    public void onTranslate(View v){
        Services.from(AboutActivity.this).putTranslate();
    }

    public void onLicenca(View view){
        try{
            WebView web = new WebView(this);
            String html = isPt ? "br.txt" : "us.txt";
            web.loadUrl(loadUrl_license + html);
            AlertDialog alertDialog = new AlertDialog.Builder(this).setView(web).create();
            alertDialog.show();
        }catch (Exception x){
            Toast.makeText(this, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onShare(View view){
        Go.by(this).onShareApp();
    }
    public void onRate(View view){
        Go.by(this).onRateApp();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    public void onBack(View view){
        onBackPressed();
    }

    public void onFacebook(View view){
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse((String)"http://www.facebook.com/igor.dutra.3557")));
    }

    public void onMore(View view){
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse((String)"https://meu-windows10channel.blogspot.com/2019/10/meus-apps.html?m=1")));
    }

    public void onWhatsapp(View view){
        try
        {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse((String)"https://api.whatsapp.com/send?phone=5598985356501")));

        }catch(Exception x) {
            Toast.makeText(this, x.getMessage(), Toast.LENGTH_SHORT).show(); }
    }

    String getRes(int index){
        return ResourceLoader.getString(this, index);
    }

    @Override
    public void onResume() {

        super.onResume();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }


    public class Inicial{
        String title;
        int image;
        public Inicial(String title, int i) {
            this.title = title;
            this.image=i;
        } }

}
