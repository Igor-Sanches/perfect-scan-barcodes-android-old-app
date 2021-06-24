package com.igordutrasanches.perfectscan.activity.manager;

import android.content.Context;
import android.content.Intent;

import com.igordutrasanches.perfectscan.activity.CodeViewActivity;

public class CodeView {
    private Context context;
    public static CodeView by(Context context) {
        return new CodeView(context);
    }

    public CodeView(Context context){
        this.context = context;
    }

    public void show(String codigo, String formato, String titulo, int type, int historical) {
        Intent intent = new Intent(context, CodeViewActivity.class);
        intent.putExtra("codigo_formato", formato);
        intent.putExtra("codigo_titulo", titulo);
        intent.putExtra("codigo_resultado", codigo);
        intent.putExtra("viewpage", type);
        intent.putExtra("historical", historical);
        context.startActivity(intent);
    }
}
