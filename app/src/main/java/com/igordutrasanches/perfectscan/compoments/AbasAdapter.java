package com.igordutrasanches.perfectscan.compoments;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class AbasAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titulos = new ArrayList<>();

    public AbasAdapter(FragmentManager fm){
        super(fm);
    }

    public void adicionar(Fragment fragment, String titulo){
        this.fragments.add(fragment);
        this.titulos.add(titulo);
    }

    @Override
    public Fragment getItem(int possition){
        return this.fragments.get(possition);
    }

    @Override
    public int getCount(){
        return this.fragments.size();
    }

    @Override
    public String getPageTitle(int position){
        return this.titulos.get(position);
    }
}
