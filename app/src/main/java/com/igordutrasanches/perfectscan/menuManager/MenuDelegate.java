package com.igordutrasanches.perfectscan.menuManager;

import android.app.Activity;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.igordutrasanches.perfectscan.R;
import com.igordutrasanches.perfectscan.compoments.Key;

public class MenuDelegate {
    private Activity activity;
    private DrawerLayout drawerLayout;
    private NavigationMenuManager navigationMenuManager;
    private NavigationView navigationView;
    private static MenuItem buy;

    public MenuDelegate(final Activity activity, Toolbar toolbar, final NavigationMenuManager manager){
        this.activity = activity;
        this.navigationMenuManager = manager;
        navigationView = (NavigationView)activity.findViewById(R.id.nav_view);
        buy = navigationView.getMenu().findItem(R.id.nav_buy);
        buy.setVisible(!Key.getBuyApp(activity.getApplicationContext()).equals("by:buy"));
        drawerLayout = (DrawerLayout)activity.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
            }
            public void onDrawerOpened(View view){
                super.onDrawerOpened(view);
            }
        };

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                MenuDelegate.this.drawerLayout.closeDrawers();
                int id = menuItem.getItemId();
                if(id == R.id.nav_buy){
                    navigationMenuManager.goBuy();
                }
                if(id == R.id.nav_about){
                    navigationMenuManager.goHome();
                }
                if(id == R.id.nav_home){
                    navigationMenuManager.goHome();
                }
                if(id == R.id.nav_scan){
                    navigationMenuManager.goScan();
                }
                if(id == R.id.nav_scan_image){
                    navigationMenuManager.goImageScan();
                }
                if(id == R.id.nav_maker){
                    navigationMenuManager.goMaker();
                }
                if(id == R.id.nav_historical){
                    navigationMenuManager.goHistorical();
                }
                if(id == R.id.nav_share){
                    navigationMenuManager.goShareApp();
                }
                if(id == R.id.nav_rate){
                    navigationMenuManager.goRate();
                }
                if(id == R.id.nav_talking){
                    navigationMenuManager.goFeedback();
                }
                if(id == R.id.nav_about){
                    navigationMenuManager.goAbout();
                }
                if(id == R.id.nav_settings){
                    navigationMenuManager.goSettings();
                }
                return false;
            }
        });
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

    }

}
