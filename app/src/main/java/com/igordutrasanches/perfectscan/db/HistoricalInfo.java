package com.igordutrasanches.perfectscan.db;

import com.igordutrasanches.perfectscan.R;
import com.igordutrasanches.perfectscan.compoments.CodeTypeReturn;

public class HistoricalInfo {

    private long id;
    private String title, data, codeType, code, barcodeFormat;

    public HistoricalInfo(){
        id = -1L;
        title = "";
        data = "";
        code = "";
        codeType = "";
        barcodeFormat = "";
    }

    public HistoricalInfo(long id, String title, String data, String code, String codeType, String barcodeFormat){
        this.id = id;
        this.title = title;
        this.data = data;
        this.code = code;
        this.codeType = codeType;
        this.barcodeFormat = barcodeFormat;
    }


    public void setId(long id){ this.id = id; }
    public void setTitle(String title){ this.title = title; }
    public void setData(String data){ this.data = data; }
    public void setCodeType(String type){ this.codeType = type; }
    public void setBarcodeFormat(String format){ this.barcodeFormat = format; }
    public void setCode(String code){ this.code = code; }

    public long getId(){ return this.id; }
    public String getTitle(){ return this.title; }
    public String getData(){return this.data; }
    public String getCodeType(){ return this.codeType; }
    public String getBarcodeFormat(){ return this.barcodeFormat; }
    public String getCode(){ return this.code; }

    public int getIcon(){
        int _return = 0;
        switch (CodeTypeReturn.result(code, barcodeFormat)){
              case email:{
                    _return = R.drawable.ic_action_send_email;
                    break;
                }
                case facebook:{
                    _return = R.drawable.ic_action_facebook;
                    break;
                }
                case instagram:{
                    _return = R.drawable.ic_action_instagram;
                    break;
                }
                case sms:{
                    _return = R.drawable.ic_action_sms;
                    break;
                }
                case whatsapp:{
                    _return = R.drawable.ic_action_whatsapp;
                    break;
                }
                case produto:{
                    _return = R.drawable.ic_action_store;
                    break;
                }
                case url:{
                    _return = R.drawable.ic_action_web;
                    break;
                }
                case texto:{
                    _return = R.drawable.ic_action_text;
                    break;
                }
                case youtube:{
                    _return = R.drawable.ic_action_youtube;
                    break;
                }
                case wifi:{
                    _return = R.drawable.ic_action_wifi_conect;
                    break;
                }
            case phone:{
                _return = R.drawable.ic_action_phone_call;
                break;
            }
            case telegram:{
                _return = R.drawable.ic_action_telegram;
                break;
            }
            case app:{
                    _return = R.drawable.ic_action_app_play;
                    break;
                }
            } return _return;
        }
}
