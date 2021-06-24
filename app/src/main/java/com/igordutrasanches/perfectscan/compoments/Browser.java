package com.igordutrasanches.perfectscan.compoments;

import android.content.Context;

public class Browser{

    public static final String GOOGLE = "https://www.google.com/search?q=";
    public static final String YAHOO = "https://br.search.yahoo.com/search?ei=";
    public static final String BING = "https://www.bing.com/search?q=";
    public static final String ASK = "https://br.ask.com/web?q=";
    public static final String DEFAULD_BROWSER = "com.android.chrome";

    public static String onBuscador(Context m){
        String navegador = "";
        switch (Key.getBuscador(m)){
            case 0: navegador = GOOGLE; break;
            case 1: navegador = YAHOO; break;
            case 2: navegador = BING; break;
            case 3: navegador = ASK; break;
        }

        return navegador;
    }

    public static String onBrowser(Context m){
        String browser = "";
        switch (Key.getBuscador(m)){
            case 0: browser = DEFAULD_BROWSER; break;
            case 1: browser = YAHOO; break;
            case 2: browser = BING; break;
            case 3: browser = ASK; break;
        }

        return browser;
    }
}
