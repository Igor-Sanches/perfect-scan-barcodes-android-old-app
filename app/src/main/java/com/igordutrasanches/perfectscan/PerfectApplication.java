package com.igordutrasanches.perfectscan;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import com.igordutrasanches.perfectscan.activity.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class PerfectApplication extends Application {
    public static String FULL_APP = "9884gdhdgg8";
    public static String CODE_APP = "codigo883y4hrhe";
    public static final String produto_1 = "77837hhfdhfdhfdhfb";
    public static final String produto_2 = "ifhsgsfsf663hjfdhfd";
    public static final String produto_3 = "hhhdhshsdbbs7437437";
    public static final String produto_4 = "hhjfdhfdhfdbfb43732";
    public static final String ApplicationKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjLPEymJM68jjSZ3GwXhot6AAggLhzwhCxdeN1HvrONu+NWBVGFUJVd6SNOBuf/8Cy9FyI6nf4bqget0Y5qI3QymRQQiCrR0n7TKBgiyOinbF3GAERNvcgawhSlHG9CUW2PWQ8/2aoc2CYp3s8qshoaZZMHk5dfn+HbayS5bPeyCl9p79Nrdf4QMs7IFC4vJyDdZO80a7Rhiro0vGc0rCcNKvsxNfHKlJOwGWXbC44+R+1SY8m9J0zIEWeufq0F2Racyggh6+xG26xoh1py9GmwHjV3Renp8V6TGS//xLKU4owc82wa2XOnP9p87PuqIEyy5gMKjpMwpeFi7097zj9wIDAQAB";

    public static List listaProdutos(){
        List<String> produtos = new ArrayList<>();
        produtos.add(produto_1);
        produtos.add(produto_2);
        produtos.add(produto_3);
        produtos.add(produto_4);
        produtos.add(FULL_APP);
        produtos.add(CODE_APP);

        return produtos;
    }

    @Override
    public void onCreate(){
        super.onCreate();

    }


}
