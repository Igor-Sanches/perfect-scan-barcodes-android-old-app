package com.igordutrasanches.perfectscan.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.igordutrasanches.perfectscan.R;
import com.igordutrasanches.perfectscan.compoments.Go;
import com.igordutrasanches.perfectscan.compoments.Key;
import com.igordutrasanches.perfectscan.compoments.ResourceLoader;
import com.igordutrasanches.perfectscan.compoments.Services;
import com.igordutrasanches.perfectscan.menuManager.MenuDelegate;
import com.igordutrasanches.perfectscan.menuManager.NavigationMenuManager;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity{

    private NavigationMenuManager manager;


    String key_donate = "dialog_doar";
    String key_rate = "dialog_rate";

    private void show() {
        int valorDonate = Key.get(key_donate, 1, this);
        int valorRate = Key.get(key_rate, 1, this);

        if(valorDonate != 100 && valorDonate >= 8){
            showDialogDonate();
        }
        if(valorRate != 100 && valorRate >= 11){
            showDialogRating();
        }

    }

    void showDialogDonate(){
        try{
            AlertDialog builder = new AlertDialog.Builder(this).create();
            builder.setTitle(getString(R.string.app_name));
            builder.setMessage(getString(R.string.donate_dialog));
            builder.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.doar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Services.from(HomeActivity.this).putDonate();
                }
            });
            builder.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.doar_more), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Key.set(key_donate, 1, HomeActivity.this);
                    dialog.dismiss();
                }
            });
            builder.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.doar_app_not), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Key.set(key_donate, 100, HomeActivity.this);
                    dialog.dismiss();
                }
            });
            builder.show();

        }catch (Exception x){

        }
    }

    void showDialogRating(){
        try{

            AlertDialog builder = new AlertDialog.Builder(this).create();
            builder.setTitle(getString(R.string.app_name));
            builder.setMessage(getString(R.string.rating_dialog));
            builder.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.rate_app), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Key.set(key_rate, 100, HomeActivity.this);
                    Go.by(HomeActivity.this).onRateApp();
                }
            });
            builder.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.rate_more), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Key.set(key_rate, 1, HomeActivity.this);
                    dialog.dismiss();
                }
            });
            builder.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.rate_app_not), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Key.set(key_rate, 100, HomeActivity.this);
                    dialog.dismiss();
                }
            });
            builder.show();

        }catch (Exception x){

        }
    }

    //private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       try{
           setContentView(R.layout.activity_home);
           Toolbar toolbar = findViewById(R.id.toolbar);
           setSupportActionBar(toolbar);
           getSupportActionBar().setTitle(R.string.menu_home);
           show();
           manager = new NavigationMenuManager(this);
           MenuDelegate menuDelegate = new MenuDelegate(this, toolbar, manager);
       }catch (Exception x){
           Toast.makeText(this, x.getMessage(), Toast.LENGTH_SHORT).show();
       }
    }

    int clicked;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            clicked = clicked + 1;
            if(clicked == 3){
                finishAffinity();
            }else {
                Toast.makeText(this, R.string.back_pressed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    String getRes(int index){
        return ResourceLoader.getString(this, index);
    }

    @Override
    public void onResume() {
        clicked = 1;
        Key.setPageView("home", this);
        ListView menuList = (ListView) findViewById(R.id.menu_lista);

        List< Inicial > menu = new ArrayList<>();

        menu.add(new Inicial(getRes(R.string.menu_scan), R.drawable.ic_menu_scan));
        menu.add(new Inicial(getRes(R.string.menu_scan_image), R.drawable.ic_menu_scan_image));
        menu.add(new Inicial(getRes(R.string.menu_maker), R.drawable.ic_menu_maker));
        menu.add(new Inicial(getRes(R.string.menu_historical), R.drawable.ic_menu_historical));
        menuList.setAdapter(new MenuAdaptor(this, R.layout.menu_inicial, menu));
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        manager.goScan();
                        break;
                    case 1:
                        manager.goImageScan();
                        break;
                    case 2:
                        manager.goMaker();
                        break;
                    case 3:
                        manager.goHistorical();
                        break;
                }
            }
        });
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

    private class MenuAdaptor extends ArrayAdapter<Inicial> {

        private List<Inicial> appsLisMain = null;
        private Context context = null;

        public MenuAdaptor(Context context, int resource, List<Inicial> apps) {
            super(context, resource, apps);

            this.context = context;
            this.appsLisMain = apps;
        }

        @Override
        public int getCount() {
            return ((null != appsLisMain) ? appsLisMain.size() : 0);

        }

        @Override
        public Inicial getItem(int i) {
            return ((null != appsLisMain) ? appsLisMain.get(i) : null);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup g) {
            try {
                if (view == null) {
                    LayoutInflater l = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = l.inflate(R.layout.menu_inicial, null);
                }

                Inicial info = appsLisMain.get(i);
                ((TextView) view.findViewById(R.id.menu_text)).setText(info.title);
                ((ImageView) view.findViewById(R.id.menu_icon)).setImageResource(info.image);
                return view;

            } catch (Exception x) {
                Toast.makeText(HomeActivity.this, x.getMessage(), Toast.LENGTH_SHORT).show();
            }

            return view;
        }
    }

}

