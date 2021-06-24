package com.igordutrasanches.perfectscan.compoments;


import android.content.Context;

import com.igordutrasanches.perfectscan.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeVerificador {
    private String format = "";

    public CodeVerificador(String codigo){
        this.codigo = codigo;
    }

    public CodeVerificador(String codigo, String f){
        this.codigo = codigo;
        this.format = f;
    }

    public static CodeVerificador by(String codigo){
        return new CodeVerificador(codigo);
    }
    public static CodeVerificador by(String codigo, String format){
        return new CodeVerificador(codigo, format);
    }

    public boolean isProduct(){
        if(!format.equals("UPC_A") && !format.equals("UPC_E") && !format.equals("EAN_8") && !format.equals("EAN_13")){
            return false;
        }
        if(!isStringOfDigits(codigo.length())){
            return false;
        }
        if(format.equals("UPC_E") && codigo.length() == 8){
            return true;
        }else return true;
    }

    private boolean isPossibleURI() {
        return ALLOWE.matcher(codigo).matches() || HOST.matcher(codigo).find();
    }

    private boolean isBasicValueURI() {
        if(codigo.contains(" ")){
            return false;
        }
        Matcher m = URI_W.matcher(codigo);
        if(m.find() && m.start() == 0){
            return true;
        }
        Matcher m2 = URI_WH.matcher(codigo);
        if(m2.find() && m2.start() != 0){
            return false;
        }

        return true;
    }

    public boolean isPhone() {
        String tel;
        if(codigo.startsWith("tel:") || codigo.startsWith("TEL:")){
            return true;
        } return false;
    }

    private static Pattern URI_W = Pattern.compile("[a-zA-Z][a-zAZ0-9+->]+:");
    private static Pattern URI_WH = Pattern.compile("([a-zA-Z0-9-]+\\.){1,6}[a-zA-Z]{2,}(:\\d{1,5})?(/|\\?|$)");
    private static Pattern DIGITO = Pattern.compile("\\d+");
    private static Pattern ALLOWE = Pattern.compile("[-._~:/?\\[\\]@!$&'()*+,;=Aa-z0-9]+");
    private static Pattern HOST = Pattern.compile(":/*([^/@]+)@[^/]+");
    private boolean isStringOfDigits(int length) {
        return codigo != null && length > 0 && length == codigo.length() && DIGITO.matcher(codigo).matches();
    }

    public boolean isSMS(){
        if(codigo.startsWith("smsto:") || codigo.startsWith("SMSTO:")){
            return true;
        }else return false;
    }

    public boolean isWhatsapp(){
        if(codigo.contains("api.whatsapp.com") || codigo.contains("send") || codigo.contains("v.whatsapp.com") || codigo.contains("chat.whatsapp.com")){
            return true;
        }else return false;
    }
    public boolean isApp() {
        if(codigo.contains("play.google.com") || codigo.contains("market://") || codigo.contains("market.android.com")){
            return true;
        }else return false;
    }

    public boolean isYoutube() {
        if(codigo.contains("youtu.be") || codigo.contains("m.youtube.com") || codigo.contains("youtube.com") || codigo.contains("www.youtube.com")){
            return true;
        }else return false;
    }
    public boolean isFacebook(){
        if(codigo.contains("fb.com")
                || codigo.contains("m.fb.gg")
                || codigo.contains("facebook.com")
                || codigo.contains("m.facebook.com")
                || codigo.contains("fb.gg")
                || codigo.contains("www.alpha.facebook.com")
                || codigo.contains("m.fbwat.ch")
                || codigo.contains("www.facebook.com")
                || codigo.contains("www.fbwat.ch")
                || codigo.contains("fbwat.ch")){
            return true;
        }else return false;
    }

    public boolean isUri(){
        if(codigo.startsWith("http://") || codigo.startsWith("https://")){
            if(codigo.contains(" "))
                return false;
            else {
                if(!codigo.endsWith(".")){
                    return true;
                }else return false;
            }
        }else if(codigo.startsWith("URL:") || codigo.startsWith("URI:")){
            return true;
        }
        if(!isBasicValueURI() || isPossibleURI()){
            return false;
        }else return false;

    }

    public boolean isEmail(){
        if(codigo.startsWith("mailto:") || codigo.startsWith("MAILTO:") && isEmailValido(codigo)){
            if(codigo.contains("@") && !codigo.contains("/") || codigo.contains("@") && !codigo.contains(" ")){
                String[] aar = codigo.split("@");
                String number = aar[0];
                if(codigo.substring(number.length() + 1).contains("@") || codigo.substring(number.length() + 1).contains(",")){
                    return false;
                }

                if(aar[1].contains("."))
                {
                    if(aar[1].endsWith(".") || aar[1].startsWith(".") || aar[1].contains(","))
                        return false;
                    else return true;
                }
                else return false;
            } return false;
        }else if(codigo.contains("@") && !codigo.contains(":") && isEmailValido(codigo)){
            String[] aar = codigo.split("@");
            String number = aar[0];
            if(codigo.substring(number.length() + 1).contains("@") || codigo.substring(number.length() + 1).contains(",")){
                return false;
            }

            if(aar[1].contains("."))
            {
                if(aar[1].endsWith(".") || aar[1].startsWith(".") || aar[1].contains(","))
                    return false;
                else return true;
            }
            else return false;
        }else return false;
    }

    Pattern EMAIL_VALIDO = Pattern.compile("[a-zA-Z0-9@.!#$&%'*+\\-/?^_`{|}~]+");
    private boolean isEmailValido(String codigo) {
        return EMAIL_VALIDO.matcher(codigo).matches();
    }

    public boolean isInstagram() {
        if(codigo.contains("www.instagram.com")
                || codigo.contains("instagram.com")
                || codigo.contains("ig.me")){
            return true;
        }else return false;
    }

    public boolean isTelegram() {
        if(codigo.contains("telegram.dog")
                || codigo.contains("t.me")
                || codigo.contains("telegram.me")){
            return true;
        }else return false;
    }

    private String codigo = "";
    public boolean isWIFI() {
        if(codigo.startsWith("WIFI:") || codigo.startsWith("wifi:")){
            if(codigo.contains(";")){
                return true;

            }else return false;
        }else return false;
    }

    public String isResumed(Context a) {
        if(CodeVerificador.by(codigo).isWIFI()){

            return Wifi.from(codigo, a).getDisplayResult();
        }else if(CodeVerificador.by(codigo).isPhone()){
            return getRes(R.string.number, a) + " " + codigo.substring(4, codigo.length());
        }
        else if(CodeVerificador.by(codigo).isEmail()) {
            String mail = codigo.contains("mailto:") ? codigo.substring(6, codigo.length()) : codigo;
            if(mail.contains("mailto:") || mail.contains("MAILTO:")){
                mail = mail.substring(7);
            }
            if(mail.startsWith(":")) mail = mail.replace(":", "");
            return "E-Mail:" + " " + mail;
        } else if(CodeVerificador.by(codigo).isSMS()) {
            String sms = codigo;
            String resumo = "";

            String code = sms;
            String[] re = code.split(":");
            String dest = "", msg = "";
            if(re.length == 3){
                dest = getRes(R.string.desti, a) + " " + re[1];
                msg = getRes(R.string.msg, a) + " " + re[2];
            }else if(re.length == 2){
                if(code.endsWith(":")){
                    dest = getRes(R.string.desti, a) + " " + re[1];
                    msg = "";

                }else{
                    dest = "";
                    msg = getRes(R.string.msg, a) + " " + re[1];
                }

            }

            if(!dest.equals("") && !msg.equals("")){
                resumo = dest + "\n" + msg;
            }
            else if(!dest.equals(""))
                resumo = dest;
            else if(!msg.equals(""))
                resumo = msg;


            return resumo.trim();
        }else return codigo;
    }

    String getRes(int index, Context c){
        return ResourceLoader.getString(c, index);
    }

    public String getNameCode(Context c) {
        String name = "";
        switch (format){
            case "AZTEC":{
                name = getRes(R.string.aztec, c);

            }break;
            case "CODABAR":{
                name = getRes(R.string.barcode, c);

            }break;
            case "CODE_128":{
                name = getRes(R.string.code128, c);

            }break;
            case "CODE_39":{
                name = getRes(R.string.code39, c);

            }break;
            case "CODE_93":{
                name = getRes(R.string.code93, c);

            }break;
            case "EAN_13":{
                name = getRes(R.string.ean_13, c);

            }break;
            case "EAN_8":{
                name = getRes(R.string.ean_8, c);

            }break;
            case "ITF":{
                name = getRes(R.string.itf, c);

            }break;
            case "PDF_417":{
                name = getRes(R.string.pdf, c);

            }break;
            case "QR_CODE":{
                name = getRes(R.string.qr, c);

            }break;
            case "UPC_E":{
                name = getRes(R.string.upc_e, c);

            }break;
            case "UPC_A":{
                name = getRes(R.string.upc_a, c);

            }break;
        } return name;
    }

    public String getTypeCode(Context mc){
        String type = "";
        switch (CodeTypeReturn.result(codigo, format)){
            case email:{
                type = getRes(R.string.mail, mc);
                break;
            }
            case facebook:{
                type = "Facebook";
                break;
            }
            case instagram:{
                type = "Instagram";
                break;
            }
            case sms:{
                type = getRes(R.string.sms, mc);
                break;
            }
            case whatsapp:{
                type = "WhatsApp";
                break;
            }
            case produto:{
                type = getRes(R.string.product, mc);
                break;
            }
            case url:{
                type = "Url";
                break;
            }
            case texto:{
                type = getRes(R.string.text, mc);
                break;
            }
            case youtube:{
                type = "Youtube";
                break;
            }
            case wifi:{
                type = "WI-FI";
                break;
            }
            case phone:{
                type = getRes(R.string.phone, mc);
                break;
            }
            case app:{
                type = getRes(R.string.app, mc);
                break;
            }
        } return type;
    }
}