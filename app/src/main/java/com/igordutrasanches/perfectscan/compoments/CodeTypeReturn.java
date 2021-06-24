package com.igordutrasanches.perfectscan.compoments;

public class CodeTypeReturn {
    public static CodeType result(String codigo, String format){
        CodeType codeType = CodeType.texto;
        if(CodeVerificador.by(codigo).isEmail()){
            codeType = CodeType.email;
        } else if(CodeVerificador.by(codigo).isWhatsapp()){
            codeType = CodeType.whatsapp;
        } else if(CodeVerificador.by(codigo).isFacebook()){
            codeType = CodeType.facebook;
        } else if(CodeVerificador.by(codigo).isInstagram()){
            codeType = CodeType.instagram;
        } else if(CodeVerificador.by(codigo).isPhone()){
            codeType = CodeType.phone;
        }  else if(CodeVerificador.by(codigo).isTelegram()){
            codeType = CodeType.telegram;
        } else if(CodeVerificador.by(codigo).isSMS()){
            codeType = CodeType.sms;
        }else if(CodeVerificador.by(codigo).isWIFI()){
            codeType = CodeType.wifi;
        } else if(CodeVerificador.by(codigo).isYoutube()){
            codeType = CodeType.youtube;
        } else if(CodeVerificador.by(codigo, format).isProduct()){
            codeType = CodeType.produto;
        } else if(CodeVerificador.by(codigo).isApp()){
            codeType = CodeType.app;
        } else if(CodeVerificador.by(codigo).isUri()){
            codeType = CodeType.url;
        }  else{
            codeType = CodeType.texto;
        }

        return codeType;
    }
}
