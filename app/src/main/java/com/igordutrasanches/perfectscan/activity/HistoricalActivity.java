package com.igordutrasanches.perfectscan.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.igordutrasanches.perfectscan.R;
import com.igordutrasanches.perfectscan.compoments.AbasAdapter;
import com.igordutrasanches.perfectscan.compoments.Key;
import com.igordutrasanches.perfectscan.db.Create;
import com.igordutrasanches.perfectscan.db.Historical;
import com.igordutrasanches.perfectscan.db.Scan;
import com.igordutrasanches.perfectscan.fragment.CreatedFragment;
import com.igordutrasanches.perfectscan.fragment.ScannedFragment;
import com.igordutrasanches.perfectscan.menuManager.MenuDelegate;
import com.igordutrasanches.perfectscan.menuManager.NavigationMenuManager;

public class HistoricalActivity extends AppCompatActivity {

    private AbasAdapter adapter;
    private TabLayout tab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_hstorical);
        toolbar.setOnMenuItemClickListener(toolbarMenu);

        NavigationMenuManager manager = new NavigationMenuManager(this);
        MenuDelegate menuDelegate = new MenuDelegate(this, toolbar, manager);
        try{
            adapter = new AbasAdapter(getSupportFragmentManager());
            adapter.adicionar(new ScannedFragment(), getString(R.string.scanned));
            adapter.adicionar(new CreatedFragment(), getString(R.string.created));

            ViewPager viewPager = (ViewPager)findViewById(R.id.pagina_tabs);
            viewPager.setAdapter(adapter);

            tab = (TabLayout)findViewById(R.id.tabs);
            tab.setupWithViewPager(viewPager);
        }catch(Exception x){
            new AlertDialog.Builder(this).setMessage(x.getMessage()).show();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        Key.setPageView("historical", this);
    }

    private Toolbar.OnMenuItemClickListener toolbarMenu = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            int id = menuItem.getItemId();
            if(id == R.id.list_clear){
                if(tab.getSelectedTabPosition() == 0){
                    Scan scan = new Scan(HistoricalActivity.this);
                    int itens = scan.lista(1).size();
                    if(itens == 0){
                        Toast.makeText(HistoricalActivity.this, getString(R.string.list_clear), Toast.LENGTH_SHORT).show();
                    }else{
                        AlertDialog.Builder dialog = new AlertDialog.Builder(HistoricalActivity.this);
                        dialog.setTitle(getString(R.string.scanned));
                        dialog.setMessage(getString(R.string.delte_message));
                        dialog.setNeutralButton(getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                dialog.dismiss();
                            }
                        });
                        dialog.setPositiveButton(getString(R.string.delete_historical), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Historical historical = new Historical(HistoricalActivity.this);
                                historical.apagarTudoEmScanned();
                                adapter.notifyDataSetChanged();
                                ScannedFragment.updated();
                            }
                        });
                        dialog.create().show();
                    }
                }else{
                    Create create = new Create(HistoricalActivity.this);
                    int itens = create.lista(1).size();
                    if(itens == 0){
                        Toast.makeText(HistoricalActivity.this, getString(R.string.list_clear), Toast.LENGTH_SHORT).show();
                    }else{
                        AlertDialog.Builder dialog = new AlertDialog.Builder(HistoricalActivity.this);
                        dialog.setTitle(getString(R.string.created));
                        dialog.setMessage(getString(R.string.delte_message));
                        dialog.setNeutralButton(getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                dialog.dismiss();
                            }
                        });  dialog.setPositiveButton("Apagar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Historical historical = new Historical(HistoricalActivity.this);
                                historical.apagarTudoEmCreated();
                                adapter.notifyDataSetChanged();
                                CreatedFragment.updated();
                            }
                        });
                        dialog.create().show();
                    }
                }
            }
            if(id == R.id.list_shorting){
                ScrollView scrollView = new ScrollView(HistoricalActivity.this);
                scrollView.setBackgroundResource(R.color.colorPrimary);
                LinearLayout linearLayout = new LinearLayout(HistoricalActivity.this);
                linearLayout.setPadding(20,10,20,10);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                String[] sort = new String[]{
                        getString(R.string.name_a_z), getString(R.string.name_z_a),
                        getString(R.string.date_nova), getString(R.string.data_antiga),
                        getString(R.string.by_type), getString(R.string.by_type_barcode)};

                final AlertDialog dialog = new AlertDialog.Builder(HistoricalActivity.this).create();
                for(String is : sort){
                    final Button btn = new Button(HistoricalActivity.this);
                    if(is.equals(getString(R.string.name_a_z))) btn.setTag(1);
                    if(is.equals(getString(R.string.name_z_a))) btn.setTag(4);
                    if(is.equals(getString(R.string.date_nova))) btn.setTag(2);
                    if(is.equals(getString(R.string.data_antiga))) btn.setTag(5);
                    if(is.equals(getString(R.string.by_type))) btn.setTag(3);
                    if(is.equals(getString(R.string.by_type_barcode))) btn.setTag(6);
                    int isTab = tab.getSelectedTabPosition() == 0 ? Key.getShortingScanned(HistoricalActivity.this) : Key.getShortingCreaated(HistoricalActivity.this);

                    btn.setBackgroundResource(R.color.colorPrimary);
                    btn.setText(is);
                    btn.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    btn.setGravity(View.TEXT_ALIGNMENT_TEXT_START);

                    if((int)btn.getTag() == isTab){
                        btn.setTextColor(getResources().getColor(R.color.sortingSelected));
                    }
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v){
                            if(tab.getSelectedTabPosition() == 0){
                                Key.setShortingScanned(HistoricalActivity.this, (int)btn.getTag());
                                ScannedFragment.updated();
                            }
                            else {
                                Key.setShortingCreaated(HistoricalActivity.this, (int)btn.getTag());
                                CreatedFragment.updated();
                            }


                            dialog.cancel();
                            dialog.dismiss();
                        }
                    });
                    linearLayout.addView(btn);
                }
                scrollView.addView(linearLayout);
                dialog.setView(scrollView);
                dialog.show();
            }
            return false;
        }
    };

}
