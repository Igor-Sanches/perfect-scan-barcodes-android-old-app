package com.igordutrasanches.perfectscan.compoments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Browser;
import android.provider.ContactsContract;
import androidx.appcompat.app.AlertDialog;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.igordutrasanches.perfectscan.R;
import com.igordutrasanches.perfectscan.activity.BuyActivity;
import com.igordutrasanches.perfectscan.scan.Contents;
import com.igordutrasanches.perfectscan.scan.LocaleManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Go {
    public static void putExtra(Intent intent, String key, String value) {
        if (value != null && !value.isEmpty()) {
            intent.putExtra(key, value);
        }
    }    private Context mContext;
    public Go(Context mContext){
        this.mContext = mContext;
    }

    public static Go by(Context mContext){
        return new Go(mContext);
    }

    public void onCopy(String codigo){
        ((ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE)).setText(codigo);
        Toast.makeText(mContext, ResourceLoader.getString(mContext, R.string.copy), Toast.LENGTH_SHORT).show();
    }

    public boolean chackAppsToIntent(List<ApplicationInfo> installedApplications, String _package) {
        try {
            PackageManager pacotes = mContext.getPackageManager();
            for (ApplicationInfo info : installedApplications) {
                if (pacotes.getLaunchIntentForPackage(info.packageName) != null) {
                    if(_package.equals(info.packageName))
                        return true;
                }else return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } return false;
    }

    public boolean getPackage(String _package){
        PackageManager pacotes = mContext.getPackageManager();

        return chackAppsToIntent(pacotes.getInstalledApplications(PackageManager.GET_META_DATA), _package);
    }

    public void goBrowser(String codigo, Activity activity){
        try{
            Intent intent= new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(codigo));
            ResolveInfo info = activity.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            String package_ = null;
            if(info != null && info.activityInfo != null){
                package_ = info.activityInfo.packageName;
            }

            if(package_ != null){
                switch (package_){
                    case "com.android.browser":
                    case "com.android.chrome":
                    case "com.microsoft.bing":
                    case "com.UCMobile.intl":
                    case "org.mozilla.firefox":
                    case "com.microsoft.emmx":
                        intent.setPackage(package_);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Browser.EXTRA_APPLICATION_ID, package_);
                        break;
                }
            }

            try{
                mContext.startActivity(intent);
            }catch (ActivityNotFoundException x){
                mContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(codigo)));
            }


        }catch (Exception x){
            Toast.makeText(mContext, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    String getRes(int index){
        return ResourceLoader.getString(mContext, index);
    }

    public void goYoutube(String codigo){

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(codigo));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.google.android.youtube");
        try{
            mContext.startActivity(intent);
        }catch (ActivityNotFoundException x){
            Toast.makeText(mContext, getRes(R.string.not_app), Toast.LENGTH_SHORT).show();
        }

    }

    public void goInstagram(String codigo){

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(codigo));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.instagram.android");
        try{
            mContext.startActivity(intent);
        }catch (ActivityNotFoundException x){
            Toast.makeText(mContext, getRes(R.string.not_app), Toast.LENGTH_SHORT).show();
        }

    }

    public void goWhatsapp(String codigo){

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(codigo));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.whatsapp");
        try{
            mContext.startActivity(intent);
        }catch (ActivityNotFoundException x){
            intent.setPackage("com.yowhatsapp");
            try{
                mContext.startActivity(intent);
            }catch (ActivityNotFoundException x2){
                intent.setPackage(null);
                mContext.startActivity(intent);
            }
        }

    }


    public void goSave(String code, String format, Bitmap codigo, int i) {
        File file = GoName.by(mContext).getFile(code, format, i);
        if(i == 1){
            try{
                if(!file.exists()){
                    file.createNewFile();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                codigo.compress(Bitmap.CompressFormat.PNG, 0, fileOutputStream);
                fileOutputStream.close();
                Toast.makeText(mContext, ResourceLoader.getString(mContext, R.string.save), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception x){
                Toast.makeText(mContext, x.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else{
            try{
                if(!file.exists()){
                    file.createNewFile();
                }

                if(file.canWrite()) {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                    writer.write(code);
                    writer.close();
                    Toast.makeText(mContext, ResourceLoader.getString(mContext, R.string.save), Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception x){
                Toast.makeText(mContext, x.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void goTelegram(String code) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(code));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("org.telegram.messenger");
        try{
            mContext.startActivity(intent);
        }catch (ActivityNotFoundException x){
            Toast.makeText(mContext, getRes(R.string.not_app), Toast.LENGTH_SHORT).show();
        }
    }

    public void goPlayStore(String code) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(code));
        intent.setClassName("com.android.vending", "com.android.vending.AssetBrowserActivity");
        try{
            mContext.startActivity(intent);
        }catch (ActivityNotFoundException x){
            Toast.makeText(mContext, getRes(R.string.not_app), Toast.LENGTH_SHORT).show();
        }
    }

    public void goDial(String code) {
        try{
            if(code != null){

                String numero = code;
                if(code.startsWith("tel:")){
                    numero = code.substring(4);
                }

                numero = PhoneNumberUtils.formatNumber(numero);

                mContext.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + numero)));
            }
        }catch (Exception x){
            Toast.makeText(mContext, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void goCall(String code) {
        try{
            if(code != null){

                String numero = code;
                if(code.startsWith("tel:")){
                    numero = code.substring(4);
                }

                numero = PhoneNumberUtils.formatNumber(numero);

                mContext.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numero)));
            }
        }catch (Exception x){
            Toast.makeText(mContext, x.getMessage(), Toast.LENGTH_SHORT).show();
        }}

    public void goAdicionarContatoNumero(String codigo, boolean is) {

        String[] numeros, tipos = null;
        numeros = new String[1];
        if(!is) {
            if(codigo != null){

                String numero = codigo;
                if( numero.startsWith("tel:")){
                    numero = codigo.substring(4);
                }

                numero = PhoneNumberUtils.formatNumber(numero);
                numeros[0] = numero;
            }
        }else {
            codigo = PhoneNumberUtils.formatNumber(codigo);
            numeros[0] = codigo;
        }
        adicionar(null, null, null, numeros, tipos, null, null, null, null, null, null, null, null, null, null, null);


    }

    private static int toEmailContractType(String typeString) {
        return doToContractType(typeString, EMAIL_TYPE_STRINGS, EMAIL_TYPE_VALUES);
    }

    private static int toPhoneContractType(String typeString) {
        return doToContractType(typeString, PHONE_TYPE_STRINGS, PHONE_TYPE_VALUES);
    }

    private static int toAddressContractType(String typeString) {
        return doToContractType(typeString, ADDRESS_TYPE_STRINGS, ADDRESS_TYPE_VALUES);
    }
    private static final String[] EMAIL_TYPE_STRINGS = {"home", "work", "mobile"};
    private static final String[] PHONE_TYPE_STRINGS = {"home", "work", "mobile", "fax", "pager", "main"};
    private static final String[] ADDRESS_TYPE_STRINGS = {"home", "work"};
    private static final int[] EMAIL_TYPE_VALUES = {
            ContactsContract.CommonDataKinds.Email.TYPE_HOME,
            ContactsContract.CommonDataKinds.Email.TYPE_WORK,
            ContactsContract.CommonDataKinds.Email.TYPE_MOBILE,
    };
    private static final int[] PHONE_TYPE_VALUES = {
            ContactsContract.CommonDataKinds.Phone.TYPE_HOME,
            ContactsContract.CommonDataKinds.Phone.TYPE_WORK,
            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
            ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK,
            ContactsContract.CommonDataKinds.Phone.TYPE_PAGER,
            ContactsContract.CommonDataKinds.Phone.TYPE_MAIN,
    };
    private static final int[] ADDRESS_TYPE_VALUES = {
            ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME,
            ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK,
    };
    private static final int NO_TYPE = -1;

    public static final int MAX_BUTTON_COUNT = 4;

    private static int doToContractType(String typeString, String[] types, int[] values) {
        if (typeString == null) {
            return NO_TYPE;
        }
        for (int i = 0; i < types.length; i++) {
            String type = types[i];
            if (typeString.startsWith(type) || typeString.startsWith(type.toUpperCase(Locale.ENGLISH))) {
                return values[i];
            }
        }
        return NO_TYPE;
    }

    private void adicionar(String[] names, String[] nicknames, String pronunciation, String[] phoneNumbers, String[] phoneTypes, String[] emails, String[] emailTypes, String note, String instantMessenger, String address, String addressType, String org, String title, String[] urls, String birthday, String[] geo) {

        try{
            Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT, ContactsContract.Contacts.CONTENT_URI);
            intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
            putExtra(intent, ContactsContract.Intents.Insert.NAME, names != null && names.length > 0 ? names[0] : null);

            putExtra(intent, ContactsContract.Intents.Insert.PHONETIC_NAME, pronunciation);

            if (phoneNumbers != null) {
                int phoneCount = Math.min(phoneNumbers.length, Contents.PHONE_KEYS.length);
                for (int x = 0; x < phoneCount; x++) {
                    putExtra(intent, Contents.PHONE_KEYS[x], phoneNumbers[x]);
                    if (phoneTypes != null && x < phoneTypes.length) {
                        int type = toPhoneContractType(phoneTypes[x]);
                        if (type >= 0) {
                            intent.putExtra(Contents.PHONE_TYPE_KEYS[x], type);
                        }
                    }
                }
            }

            if (emails != null) {
                int emailCount = Math.min(emails.length, Contents.EMAIL_KEYS.length);
                for (int x = 0; x < emailCount; x++) {
                    putExtra(intent, Contents.EMAIL_KEYS[x], emails[x]);
                    if (emailTypes != null && x < emailTypes.length) {
                        int type = toEmailContractType(emailTypes[x]);
                        if (type >= 0) {
                            intent.putExtra(Contents.EMAIL_TYPE_KEYS[x], type);
                        }
                    }
                }
            }

            ArrayList<ContentValues> data = new ArrayList<>();
            if (urls != null) {
                for (String url : urls) {
                    if (url != null && !url.isEmpty()) {
                        ContentValues row = new ContentValues(2);
                        row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
                        row.put(ContactsContract.CommonDataKinds.Website.URL, url);
                        data.add(row);
                        break;
                    }
                }
            }

            if (birthday != null) {
                ContentValues row = new ContentValues(3);
                row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE);
                row.put(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY);
                row.put(ContactsContract.CommonDataKinds.Event.START_DATE, birthday);
                data.add(row);
            }

            if (nicknames != null) {
                for (String nickname : nicknames) {
                    if (nickname != null && !nickname.isEmpty()) {
                        ContentValues row = new ContentValues(3);
                        row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE);
                        row.put(ContactsContract.CommonDataKinds.Nickname.TYPE,
                                ContactsContract.CommonDataKinds.Nickname.TYPE_DEFAULT);
                        row.put(ContactsContract.CommonDataKinds.Nickname.NAME, nickname);
                        data.add(row);
                        break;
                    }
                }
            }

            if (!data.isEmpty()) {
                intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);
            }

            StringBuilder aggregatedNotes = new StringBuilder();
            if (note != null) {
                aggregatedNotes.append('\n').append(note);
            }
            if (geo != null && geo.length >= 2) {
                aggregatedNotes.append('\n').append(geo[0]).append(',').append(geo[1]);
            }

            if (aggregatedNotes.length() > 0) {
                // Remove extra leading '\n'
                putExtra(intent, ContactsContract.Intents.Insert.NOTES, aggregatedNotes.substring(1));
            }

            if (instantMessenger != null && instantMessenger.startsWith("xmpp:")) {
                intent.putExtra(ContactsContract.Intents.Insert.IM_PROTOCOL, ContactsContract.CommonDataKinds.Im.PROTOCOL_JABBER);
                intent.putExtra(ContactsContract.Intents.Insert.IM_HANDLE, instantMessenger.substring(5));
            } else {
                putExtra(intent, ContactsContract.Intents.Insert.IM_HANDLE, instantMessenger);
            }

            putExtra(intent, ContactsContract.Intents.Insert.POSTAL, address);
            if (addressType != null) {
                int type = toAddressContractType(addressType);
                if (type >= 0) {
                    intent.putExtra(ContactsContract.Intents.Insert.POSTAL_TYPE, type);
                }
            }
            putExtra(intent, ContactsContract.Intents.Insert.COMPANY, org);
            putExtra(intent, ContactsContract.Intents.Insert.JOB_TITLE, title);
            mContext.startActivity(intent);
        }catch (Exception x){
            Toast.makeText(mContext, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void shareBySMS(String contents) {
        sendSMSFromUri("smsto:", contents);
    }

    final void sendSMS(String phoneNumber, String body) {
        sendSMSFromUri("smsto:" + phoneNumber, body);
    }

    private void sendSMSFromUri(String uri, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
        putExtra(intent, "sms_body", body);
        intent.putExtra("compose_mode", true);
        mContext.startActivity(intent);
    }

    public void goSendSMS(String code) {
        try{
            String[] re = code.split(":");
            String dest = "", msg = "";
            if(re.length == 3){
                dest = re[1];
                msg = re[2];
            }else if(re.length == 2){
                if(code.endsWith(":")){
                    dest = re[1];
                    msg = "";

                }else{
                    dest = "";
                    msg = re[1];
                }

            }

            if(!dest.equals("") && !msg.equals("")){
                sendSMS(dest, msg);
            }
            else if(!dest.equals(""))
                sendSMS(dest, "");

            else if(!msg.equals(""))
                sendSMS("", msg);


        }catch (Exception x){
            Toast.makeText(mContext, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    final void sendMMS(String code) {
        try{
            String[] re = code.split(":");
            String dest = "", msg = "";
            if(re.length == 3){
                dest = re[1];
                msg = re[2];
            }else if(re.length == 2){
                if(code.endsWith(":")){
                    dest = re[1];
                    msg = "";

                }else{
                    dest = "";
                    msg = re[1];
                }

            }

            if(!dest.equals("") && !msg.equals("")){
                sendSMS(dest, msg);
            }
            else if(!dest.equals(""))
                sendSMS(dest, "");

            else if(!msg.equals(""))
                sendSMS("", msg);


            sendMMSFromUri("mmsto:" + dest, getRes(R.string.ola), msg);
        }catch (Exception x){
            Toast.makeText(mContext, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void share(Bitmap bitmap) {

        if (bitmap == null) {
            return;
        }

        File bsRoot = new File(Environment.getExternalStorageDirectory(), "BarcodeScanner");
        File barcodesRoot = new File(bsRoot, "Barcodes");
        if (!barcodesRoot.exists() && !barcodesRoot.mkdirs()) {
            //Log.w(TAG, "Couldn't make dir " + barcodesRoot);
            // showErrorMessage(R.string.msg_unmount_usb);
            return;
        }
        File barcodeFile = new File(barcodesRoot, "djd" + ".png");
        if (!barcodeFile.delete()) {
            //  Log.w(TAG, "Could not delete " + barcodeFile);
            // continue anyway
        }
        try (FileOutputStream fos = new FileOutputStream(barcodeFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, fos);
        } catch (IOException ioe) {
            //  Log.w(TAG, "Couldn't access file " + barcodeFile + " due to " + ioe);
            // showErrorMessage(R.string.msg_unmount_usb);
            return;
        }

    }

    private void sendMMSFromUri(String uri, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
        // The Messaging app needs to see a valid subject or else it will treat this an an SMS.
        if (subject == null || subject.isEmpty()) {
            putExtra(intent, "subject", subject);
        } else {
            putExtra(intent, "subject", subject);
        }
        putExtra(intent, "sms_body", body);
        intent.putExtra("compose_mode", true);
        mContext.startActivity(intent);
    }

    public void goSendMMS(String code) {
        sendMMS(code);
    }

    public void goSendEmail(String codigoResult) {
        try{
            String mail = codigoResult;
            if(CodeVerificador.by(codigoResult).isEmail() && codigoResult.contains(":"))
                mail = mail.substring(7);

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + mail));
            intent.putExtra(Intent.EXTRA_EMAIL, mail);
            intent.putExtra(Intent.EXTRA_SUBJECT, "");
            if(intent.resolveActivity(mContext.getPackageManager()) != null){
                mContext.startActivity(intent);
            }
        }catch (Exception x){
            Toast.makeText(mContext, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void goAddContactmail(String codigoResult) {
        String[] email = new String[1];
        String codigo = codigoResult;
        if(CodeVerificador.by(codigoResult).isEmail() && codigoResult.contains(":"))
            codigo = codigoResult.substring(7);

        email[0] = codigo;
        adicionar(null, null, null, null, null, email, null, null, null, null, null, null, null, null, null, null);
    }

    public void onFeedBack() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:perfectscan_feedback@outlook.com"));
        intent.putExtra(Intent.EXTRA_EMAIL, "perfectscan_feedback@outlook.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, assunto());
        if(intent.resolveActivity(mContext.getPackageManager()) != null){
            mContext.startActivity(intent);
        }
    }

    private String getPackage(){
        ApplicationInfo p = mContext.getApplicationInfo();
        return p.packageName;
    }

    private String assunto(){
        String string = "";
        try {
            string = mContext.getPackageManager().getPackageInfo((String) mContext.getPackageName(), (int) 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } return mContext.getString(R.string.app_name) + " " + mContext.getString(R.string.version) + " " + string;
    }

    public void onShareApp() {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.SUBJECT", "");
        intent.putExtra("android.intent.extra.TEXT", "https://play.google.com/store/apps/details?id=" + getPackage());
        mContext.startActivity(Intent.createChooser(intent, mContext.getString(R.string.share_menu)));
    }

    public void onRateApp() {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setClassName("com.android.vending", "com.android.vending.AssetBrowserActivity");
        intent.setData(Uri.parse("market://details?id=" + getPackage()));
        mContext.startActivity(intent);
    }

    public void goWifiConnect(String codigoResult, final Activity activity) {
        try{
            WifiManager wifiManager = (WifiManager)mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if(wifiManager == null){
                return;
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, mContext.getString(R.string.connect), Toast.LENGTH_SHORT).show();
                }
            });
            new WifiHelper(codigoResult, wifiManager, mContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }catch (Exception x){
            Toast.makeText(activity, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void goWifiAdd(String codigoResult, final Activity activity) {
        try{
            WifiManager wifiManager = (WifiManager)mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if(wifiManager == null){
                return;
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, mContext.getString(R.string.salved_wifi), Toast.LENGTH_SHORT).show();
                }
            });
            new WifiHelper(codigoResult, wifiManager, mContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }catch (Exception x){
            Toast.makeText(activity, x.getMessage(), Toast.LENGTH_SHORT).show();
        } }

    public void goWebFind(String codigoResult) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra("query", codigoResult);
        mContext.startActivity(intent);
    }

    public void goProductGo(String upc, Activity activity){
        Uri uri = Uri.parse("http://www.google." + LocaleManager.getProductSearchCountryTLD(activity) + "/m/products?q=" + upc);
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    public void goStore(String codigo){
        try{
            String city = Locale.getDefault().getCountry();
            boolean brazil = city.contains("BR") ? true : false;
            String Google = "Google", GoogleTag = "https://www.google.com/search?q=" + codigo;
            String Bing = "Bing", BingTag = "https://www.bing.com/search?q=" + codigo;
            String BuscaPé = "BuscaPé", BuscaPéTag = "http://busca.buscape.com.br/cprocura?produto=" + codigo;
            String DuckDuckGo = "DuckDuckGo", DuckDuckGoTag = "https://duckduckgo.com/?q=" + codigo;
            String MercadoLivre = "Mercado Livre", MercadoLivreTag = "http://pmstrk.mercadolivre.com.br/jm/PmsTrk?tool=5668605&go=/jm/search%3fas_word=" + codigo;
            String AliExpress = "AliExpress", AliExpressTag = "https://www.aliexpress.com/af/lumia.html?SearchText=" + codigo;
            String CasasBahia = "Casas Bahia", CasasBahiaTag = "https://buscas2.casasbahia.com.br/busca?q=" + codigo;
            String Extra = "Extra", ExtraTag = "https://buscando2.extra.com.br/busca?q=" + codigo;
            String Amazon = "Amazon", AmazonTag = "https://www.amazon.com.br/s?k=" + codigo;
            String MagazineLuiza = "Magazine Luiza", MagazineLuizaTag = "https://www.magazineluiza.com.br/busca/" + codigo;
            String Zoom = "Zoom", ZoomTag = "https://www.zoom.com.br/celular?q=" + codigo;
            String Americanas = "Americanas", AmericanasTag = "https://www.americanas.com.br/busca/" + codigo;

            String[] lojas = new String[]{Google, Bing, BuscaPé, DuckDuckGo, MercadoLivre, AliExpress, CasasBahia, Extra, Amazon, MagazineLuiza, Zoom, Americanas};

            final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
            ScrollView pane = new ScrollView(mContext);
            LinearLayout pane2 = new LinearLayout(mContext);
            pane2.setOrientation(LinearLayout.VERTICAL);
            pane.setBackgroundResource(R.color.colorPrimary);
            pane2.setPadding(30,10,30,10);
            for(String loja : lojas){
                Button btn = new Button(mContext);
                if(loja.equals(Google))
                    btn.setTag(GoogleTag);

                if(loja.equals(Bing))
                    btn.setTag(BingTag);

                if(loja.equals(Amazon))
                    btn.setTag(AmazonTag);

                if(loja.equals(Bing))
                    btn.setTag(BingTag);


                if(brazil){
                    if(loja.equals(CasasBahia))
                        btn.setTag(CasasBahiaTag);

                    if(loja.equals(Extra))
                        btn.setTag(ExtraTag);

                    if(loja.equals(MagazineLuiza))
                        btn.setTag(MagazineLuizaTag);

                    if(loja.equals(Americanas))
                        btn.setTag(AmericanasTag);

                    if(loja.equals(Zoom))
                        btn.setTag(ZoomTag);

                    if(loja.equals(BuscaPé))
                        btn.setTag(BuscaPéTag);

                    if(loja.equals(MercadoLivre))
                        btn.setTag(MercadoLivreTag);
                }
                if(loja.equals(DuckDuckGo))
                    btn.setTag(DuckDuckGoTag);


                if(loja.equals(AliExpress))
                    btn.setTag(AliExpressTag);


                btn.setText(loja);
                btn.setBackgroundResource(R.color.colorPrimary);
                btn.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                btn.setGravity(View.TEXT_ALIGNMENT_TEXT_START);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(((Button)v).getTag().toString()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        dialog.dismiss();
                    }
                });
                pane2.addView(btn);
            }
            pane.addView(pane2);
            dialog.setView(pane);
            dialog.show();
        }catch (Exception x){
            Toast.makeText(mContext, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    String uri;

    public void goBuyApp() {
        mContext.startActivity(new Intent(mContext, BuyActivity.class));
    }
}
