package com.igordutrasanches.perfectscan.compoments;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.regex.Pattern;

class WifiHelper extends AsyncTask<Void, Void, Void> {

    private WifiManager wifiManager;
    private Context context;
    private String codigo;
    public WifiHelper(String codigoResult, WifiManager wifiManager, Context mContext) {
        codigo = codigoResult;
        this.wifiManager = wifiManager;
        context = mContext;
    }

    public static void atualizarRede(WifiManager wifiManager, WifiConfiguration configuration){
        Integer redeWifiID = findNetworkInExistingConfig(wifiManager, configuration.SSID);
        if(redeWifiID != null){
            wifiManager.removeNetwork(redeWifiID);
            wifiManager.saveConfiguration();
        }
        int id = wifiManager.addNetwork(configuration);
        if(id >= 0){
            if(wifiManager.enableNetwork(id, true)) {
                wifiManager.saveConfiguration();
            }
        }
    }

    private WifiConfiguration carregarRedeCommon(Context context){
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.allowedAuthAlgorithms.clear();
        wifiConfiguration.allowedGroupCiphers.clear();
        wifiConfiguration.allowedKeyManagement.clear();
        wifiConfiguration.allowedProtocols.clear();
        wifiConfiguration.SSID = keyView(Wifi.from(codigo, context).getSsid());
        wifiConfiguration.hiddenSSID = Wifi.from(codigo, context).isHidden();
        return wifiConfiguration;
    }

    private void changeNetworkWEP(WifiManager wifiManager) {
        try{  WifiConfiguration config = carregarRedeCommon(context);
            config.wepKeys[0] = keyView(Wifi.from(codigo, context).getPassword(), 10, 26, 58);
            config.wepTxKeyIndex = 0;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            atualizarRede(wifiManager, config);
        }catch (Exception x){
            Toast.makeText(context, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static String keyView(String value, int... allowedLengths) {
        return isHexOfLength(value, allowedLengths) ? value : convertToQuotedString(value);
    }

    private void changeNetworkWPA(WifiManager wifiManager, Context context) {
        WifiConfiguration config = carregarRedeCommon(context);
        // Hex passwords that are 64 bits long are not to be quoted.
        config.preSharedKey = keyView(Wifi.from(codigo, context).getPassword(), 64);
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        config.allowedProtocols.set(WifiConfiguration.Protocol.WPA); // For WPA
        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN); // For WPA2
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        atualizarRede(wifiManager, config);
    }

    // Adding a WPA2 enterprise (EAP) network
    private void changeNetworkWPA2EAP(WifiManager wifiManager, Context context) {
        WifiConfiguration config = carregarRedeCommon(context);
        // Hex passwords that are 64 bits long are not to be quoted.
        config.preSharedKey = keyView(Wifi.from(codigo, context).getPassword(), 64);
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN); // For WPA2
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.enterpriseConfig.setIdentity(Wifi.from(codigo, context).getIdentity());
        config.enterpriseConfig.setAnonymousIdentity(Wifi.from(codigo, context).getAnonymousIdentity());
        config.enterpriseConfig.setPassword(Wifi.from(codigo, context).getPassword());
        config.enterpriseConfig.setEapMethod(parseEap(Wifi.from(codigo, context).getEapMethod()));
        config.enterpriseConfig.setPhase2Method(parsePhase2(Wifi.from(codigo, context).getPhase2Method()));
        atualizarRede(wifiManager, config);
    }

    // Adding an open, unsecured network
    private void changeNetworkUnEncrypted(WifiManager wifiManager, Context context) {
        WifiConfiguration config = carregarRedeCommon(context);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        atualizarRede(wifiManager, config);
    }

    private static Integer findNetworkInExistingConfig(WifiManager wifiManager, String ssid) {
        Iterable<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        if (existingConfigs != null) {
            for (WifiConfiguration existingConfig : existingConfigs) {
                String existingSSID = existingConfig.SSID;
                if (existingSSID != null && existingSSID.equals(ssid)) {
                    return existingConfig.networkId;
                }
            }
        }
        return null;
    }

    private static String convertToQuotedString(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        // If already quoted, return as-is
        if (s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
            return s;
        }
        return '\"' + s + '\"';
    }


    private static final Pattern HEX_DIGITS = Pattern.compile("[0-9A-Fa-f]+");


    private static boolean isHexOfLength(CharSequence value, int... allowedLengths) {
        if (value == null || !HEX_DIGITS.matcher(value).matches()) {
            return false;
        }
        if (allowedLengths.length == 0) {
            return true;
        }
        for (int length : allowedLengths) {
            if (value.length() == length) {
                return true;
            }
        }
        return false;
    }

    private static int parseEap(String eapString) {
        if (eapString == null) {
            return WifiEnterpriseConfig.Eap.NONE;
        }
        switch (eapString) {
            case "NONE":
                return WifiEnterpriseConfig.Eap.NONE;
            case "PEAP":
                return WifiEnterpriseConfig.Eap.PEAP;
            case "PWD":
                return WifiEnterpriseConfig.Eap.PWD;
            case "TLS":
                return WifiEnterpriseConfig.Eap.TLS;
            case "TTLS":
                return WifiEnterpriseConfig.Eap.TTLS;
            default:
                throw new IllegalArgumentException("Unknown value for EAP method: " + eapString);
        }
    }

    private static int parsePhase2(String phase2String) {
        if (phase2String == null) {
            return WifiEnterpriseConfig.Phase2.NONE;
        }
        switch (phase2String) {
            case "GTC":
                return WifiEnterpriseConfig.Phase2.GTC;
            case "MSCHAP":
                return WifiEnterpriseConfig.Phase2.MSCHAP;
            case "MSCHAPV2":
                return WifiEnterpriseConfig.Phase2.MSCHAPV2;
            case "NONE":
                return WifiEnterpriseConfig.Phase2.NONE;
            case "PAP":
                return WifiEnterpriseConfig.Phase2.PAP;
            default:
                throw new IllegalArgumentException("Unknown value for phase 2 method: " + phase2String);
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try{
            if(!wifiManager.isWifiEnabled()){
                if(wifiManager.setWifiEnabled(true)){

                }else{
                    return null;
                }

                int count = 0;
                while (!wifiManager.isWifiEnabled()){
                    if(count == 10){
                        return null;
                    }
                    try{
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    count++;
                }
            }

            String type = Wifi.from(codigo, context).getType();
            NetWorkType typeNet = NetWorkType.NOPASS;
            try{
                typeNet = NetWorkType.forType(type);
            }catch (Exception x){

            }
            if(typeNet == NetWorkType.NOPASS){
                changeNetworkUnEncrypted(wifiManager, context);
            }else{
                String password = Wifi.from(codigo, context).getPassword();
                if(password != null && !password.isEmpty()){
                    switch (typeNet){
                        case WEP:
                            changeNetworkWEP(wifiManager);
                            break;
                        case WPA:
                            changeNetworkWPA(wifiManager, context);
                            break;
                        case WPA2_EAP:
                            changeNetworkWPA2EAP(wifiManager, context);
                            break;
                    }
                }
            }
        }catch (Exception x){
            Toast.makeText(context, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private enum NetWorkType {
        WEP, WPA, WPA2_EAP, NOPASS;

        static NetWorkType forType(String type){
            NetWorkType netWorkType = NetWorkType.NOPASS;
            if(type == null){
                netWorkType = NOPASS;
            }

            switch (type){
                case "WPA": netWorkType = WPA; break;
                case "WPA2": netWorkType = WPA; break;
                case "WPA2-EAP":  netWorkType = WPA2_EAP;break;
                case "WEP":  netWorkType = WEP;break;
                case "nopass":  netWorkType = NOPASS;break;
            } return  netWorkType;
        }
    }
}
