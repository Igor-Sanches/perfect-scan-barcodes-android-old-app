package com.igordutrasanches.perfectscan.compoments;

import android.content.Context;
import android.content.SharedPreferences;

import com.igordutrasanches.perfectscan.camera.FrontLightMode;
import com.igordutrasanches.perfectscan.scan.KeyInfo;

public class Key {
    public static String PREFERENCE_NAME = "Igor_Sanches";

    private static final String PREFERENCE_FILE_NAME = "Picadas_db";
    private static SharedPreferences mSharedPreferences;

    private static SharedPreferences getmSharedPreferencesEditor(Context context){
        if(mSharedPreferences ==null){
            mSharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);

        }
        return mSharedPreferences;
    }

    public static void set(String key, int value, Context context){
        SharedPreferences.Editor editor = getmSharedPreferencesEditor(context).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int get(String key, int Default, Context context){
        return getmSharedPreferencesEditor(context).getInt(key, Default);
    }

    public static void set(String key, boolean value, Context context){
        SharedPreferences.Editor editor = getmSharedPreferencesEditor(context).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private static boolean get(String key, boolean Default, Context context){
        return getmSharedPreferencesEditor(context).getBoolean(key, Default);
    }


    public static void remove(String key, Context context){
        getmSharedPreferencesEditor(context).edit().remove(key).commit();
    }

    public static void set(String key, String value, Context context){
        SharedPreferences.Editor editor = getmSharedPreferencesEditor(context).edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static void set(String key, long value, Context context){
        SharedPreferences.Editor editor = getmSharedPreferencesEditor(context).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    private static long get(String key, long Default, Context context){
        return getmSharedPreferencesEditor(context).getLong(key, Default);
    }

    public static String get(String key, String Default, Context context){
        return getmSharedPreferencesEditor(context).getString(key, Default);
    }

    public static int getBuscador(Context context){
        return get("BuscadorDefault", 0, context);
    }
    public static void setBuscador(int position, Context context){
        set("BuscadorDefault", position, context);
    }

    public static int getBrowser(Context context){
        return get("BrowserDefault", 0, context);
    }
    public static void setBrowser(int position, Context context){
        set("BrowserDefault", position, context);
    }

    public static int getCodigo(Context context){
        return get("codigosDefault", 9, context);
    }
    public static void setCodigo(int position, Context context){
        set("codigosDefault", position, context);
    }
    public static int getWifi(Context context){
        return get("wifi", 0, context);
    }
    public static void setWifi(int position, Context context){
        set("wifi", position, context);
    }

    public static int getCodigoType(Context context){
        return get("codigosDefaultType", 1, context);
    }
    public static void setCodigoType(int position, Context context){
        set("codigosDefaultType", position, context);
    }

    public static String getPageView(Context context){
        return get("pageView", "home", context);
    }
    public static void setPageView(String page, Context context){
        set("pageView", page, context);
    }

    public static boolean getAutoFocus(Context context) {
        return get(KeyInfo.KEY_AUTO_FOCUS, true, context);
    }

    public static boolean getDisableExposure(Context context) {
        return get(KeyInfo.KEY_DISABLE_EXPOSURE, true, context);
    }

    public static boolean getDisableMetering(Context context) {
        return get(KeyInfo.KEY_DISABLE_METERING, true, context);
    }

    public static boolean getDisableBarcodeEnceneMode(Context context) {
        return get(KeyInfo.KEY_DISABLE_BARCODE_SCENE_MODE, true, context);
    }

    public static boolean getDisableContinuesFocus(Context context) {
        return get(KeyInfo.KEY_DISABLE_CONTINUOUS_FOCUS, false, context);
    }

    public static boolean getDisableInvert(Context context){
        return get(KeyInfo.KEY_INVERT_SCAN, false, context);
    }

    public static boolean getVibrate(Context context){
        return get(KeyInfo.KEY_VIBRATE, false, context);
    }

    public static boolean getAutoWeb(Context context){
        return get(KeyInfo.KEY_AUTO_OPEN_WEB, false, context);
    }

    public static boolean getMassa(Context context){
        return get(KeyInfo.KEY_BULK_MODE, false, context);
    }

    public static boolean getOrientation(Context context){
        return get(KeyInfo.KEY_DISABLE_AUTO_ORIENTATION, false, context);
    }

    public static boolean getBeep(Context context){
        return get(KeyInfo.KEY_PLAY_BEEP, true, context);
    }

    public static String getFrontLightMode(Context context){
        return get(KeyInfo.KEY_FRONT_LIGHT_MODE, FrontLightMode.OFF.toString(), context);
    }

    public static String getLaserColor(Context context) {
        return get(KeyInfo.KEY_LASER_COLOR, "0", context);
    }

    public static boolean getScannedSaved(Context context) {
        return get(KeyInfo.KEY_SCANNED_ENABLE, true, context);
    }

    public static boolean getCreateSaved(Context context) {
        return get(KeyInfo.KEY_CREATE_ENABLE, true, context);
    }

    public static int getShortingScanned(Context context) {
        return get(KeyInfo.KEY_SHORTING_SCANNED, 2, context);
    }

    public static int getShortingCreaated(Context context) {
        return get(KeyInfo.KEY_SHORTING_CREATED, 2, context);
    }

    public static void setShortingScanned(Context context, int value) {
        set(KeyInfo.KEY_SHORTING_SCANNED, value, context);
    }

    public static void setShortingCreaated(Context context, int value) {
        set(KeyInfo.KEY_SHORTING_CREATED, value, context);
    }

    public static void setMassa(Context context, boolean value) {
        set(KeyInfo.KEY_BULK_MODE, value, context);
    }

    public static void setDisableInvert(Context context, boolean value) {
        set(KeyInfo.KEY_INVERT_SCAN, value, context);
    }

    public static boolean getVisibleCodeInView(Context context) {
        return get(KeyInfo.KEY_VISIBLE_CODE, true, context);
    }

    public static void setVisibleCodeInView(Context context, boolean value) {
        set(KeyInfo.KEY_VISIBLE_CODE, value, context);
    }

    public static boolean getPermisionCamera(Context context) {
        return get("cameraP", false, context);
    }

    public static void setPermisionCamera(Context context, boolean value) {
        set("cameraP", value, context);
    }

    public static int getCamera(Context context) {
        return get("cameraG", 0, context);
    }

    public static void setCamera(Context context, int value) {
        set("cameraG", value, context);
    }

    public static boolean getLocutorResultView(Context context) {
        return get(KeyInfo.KEY_LOCUTOR_VIEW_CODE, false, context);
    }

    public static void setBuyAppType(String key, Context context) {
        set("buy", key, context);
    }

    public static String getBuyApp(Context context) {
        return get("buy", "by:not", context);
    }

    public static boolean getAppTest(Context context) {
        if(Key.getMaxCodeGerado(context) >= 30 && Key.getMaxCodeScan(context)  >= 20){
            return true;
        }else return false;
    }

    public static int getMaxCodeScan(Context context) {
        return get("getMaxCodeScan", 0, context);
    }

    public static void setMaxCodeScan(Context context) {
        set("getMaxCodeScan", getMaxCodeScan(context) + 1, context);
    }

    public static int getMaxCodeGerado(Context context) {
        return get("getMaxCodeGerado", 0, context);
    }

    public static void setMaxCodeGerado(Context context) {
        set("getMaxCodeGerado", getMaxCodeGerado(context) + 1, context);
    }
}