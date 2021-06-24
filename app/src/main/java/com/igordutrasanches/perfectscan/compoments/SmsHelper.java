package com.igordutrasanches.perfectscan.compoments;

import android.content.Context;

import com.igordutrasanches.perfectscan.R;

public class SmsHelper {
    private static String destinatario, mensagem;
    public static String getDestinatario(Context a, String code){   try{

        String dest="", men="";
        String[] re = code.split(":");
        if(re.length == 3){
            dest = getRes(R.string.desti, a) + " " + re[1];
            men = getRes(R.string.msg, a) + " " + re[2];
            destinatario = re[1];
            mensagem = re[2];
        }else if(re.length == 2){
            if(code.endsWith(":")){
                dest = getRes(R.string.desti, a) + " " + re[1];
                men = "";
                destinatario = re[1];
                mensagem = "";

            }else{
                dest = "";
                men = getRes(R.string.msg, a) + " " + re[1];
                destinatario = "";
                mensagem = re[1];
            }

        }

        if(!destinatario.equals("") && !mensagem.equals("")){
            resumo = dest + "\n" + men;
        }
        else if(!destinatario.equals(""))
            resumo = dest;
        else if(!mensagem.equals(""))
            resumo = men;
    }catch (Exception x){
    }
    return destinatario; }
    public static String getMensagem(){ return mensagem; }

    public static boolean isDestinatario(Context a, String code){
             try{

                String dest="", men="";
                String[] re = code.split(":");
                if(re.length == 3){
                    dest = getRes(R.string.desti, a) + " " + re[1];
                    men = getRes(R.string.msg, a) + " " + re[2];
                    destinatario = re[1];
                    mensagem = re[2];
                }else if(re.length == 2){
                    if(code.endsWith(":")){
                        dest = getRes(R.string.desti, a) + " " + re[1];
                        men = "";
                        destinatario = re[1];
                        mensagem = "";

                    }else{
                        dest = "";
                        men = getRes(R.string.msg, a) + " " + re[1];
                        destinatario = "";
                        mensagem = re[1];
                    }

                }

                if(!destinatario.equals("") && !mensagem.equals("")){
                    resumo = dest + "\n" + men;
                }
                else if(!destinatario.equals(""))
                    resumo = dest;
                else if(!mensagem.equals(""))
                    resumo = men;
            }catch (Exception x){
            }
        if(destinatario.equals("")) return false; else return true;
    }

    static String resumo;
    public String getResumo(){return resumo; }
    static String getRes(int index, Context c){
        return ResourceLoader.getString(c, index);
    }

    public SmsHelper(){

    }
}
