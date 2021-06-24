package com.igordutrasanches.perfectscan.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

public class Scan extends SQLiteOpenHelper {
    private static final String TABELA = "scan_scan", ID = "rowid", TITLE = "title_scan", DATE = "date_scan", CODE = "code_scan", TYPE = "typ_scan", FORMAT = "format_scan";

    private final String[] dados = new String[]{ID, TITLE, DATE, CODE, TYPE, FORMAT};

    public Scan(Context mContext){
        super(mContext, "IGOR_SANCHES", null, 3);
    }

    public long getID(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT count(*) FROM " + TABELA, null);
        long id = -1L;
        if(cursor.moveToFirst()){
            do {
                id = cursor.getLong(0);
            }while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return id;
    }

    public HistoricalInfo getID(long id){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        HistoricalInfo info;
        String[] isDados = dados;;
        String[] isDadosDB = new String[]{String.valueOf(id)};
        Cursor cursor = sqLiteDatabase.query(TABELA, isDados, ID + " = ?", isDadosDB, null, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
            info = new HistoricalInfo(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
            cursor.close();
        }
        else{
            info = null;
        }
        sqLiteDatabase.close();
        return info;
    }

    public List<HistoricalInfo> lista(int position){
        SQLiteDatabase sqLiteDatabase;
        String ordem = "ASC";
        String por = TITLE;
        Cursor cursor;
        LinkedList lista = new LinkedList();
        switch (position){
            case 1:
                por = TITLE;
                ordem = "ASC";
                break;
            case 4:
                por = TITLE;
                ordem = "DESC";
                break;
            case 2:
                por = DATE;
                ordem = "DESC";
                break;
            case 5:
                por = DATE;
                ordem = "ASC";
                break;
            case 3:
                por = TYPE;
                ordem = "ASC";
                break;
            case 6:
                por = FORMAT;
                ordem = "ASC";
                break;
        }
        if((cursor = (sqLiteDatabase = this.getWritableDatabase()).rawQuery("SELECT rowid,* FROM " + TABELA + " ORDER BY " + por + " " + ordem, null)).moveToFirst()){
            do{
                lista.add(new HistoricalInfo(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return lista;
    }

    public void adicionar(HistoricalInfo info){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(TITLE, info.getTitle());
        valores.put(DATE, info.getData());
        valores.put(CODE, info.getCode());
        valores.put(TYPE, info.getCodeType());
        valores.put(FORMAT, info.getBarcodeFormat());
        sqLiteDatabase.insert(TABELA, null, valores);
        sqLiteDatabase.close();
        //Toast.makeText(mContext, Resource.getString(R.string.salvar_nota, mContext), Toast.LENGTH_LONG).show();
    }

    public void atualizar(HistoricalInfo info){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(TITLE, info.getTitle());
        valores.put(DATE, info.getData());
        valores.put(CODE, info.getCode());
        valores.put(TYPE, info.getCodeType());
        valores.put(FORMAT, info.getBarcodeFormat());
        String[] id = new String[]{String.valueOf(info.getId())};
        sqLiteDatabase.update(TABELA, valores, ID + " = ?", id);
        sqLiteDatabase.close();
        //Toast.makeText(mContext, Resource.getString(R.string.nota_atualizada, mContext), Toast.LENGTH_LONG).show();
    }

    public void apagar(long id, boolean deletaAll){
        SQLiteDatabase sql = this.getReadableDatabase();
        if(deletaAll){
            sql.delete(TABELA, null, null);
        }else{
            String[] ids = new String[]{String.valueOf(id)};
            sql.delete(TABELA, ID + " = ?", ids);
            sql.close();
        }
        // if(show)
        //   Toast.makeText(mContext, Resource.getString(R.string.nota_apagada, mContext), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABELA + " (" + TITLE + " TEXT, " + DATE + " TEXT, " + CODE + " TEXT, " + TYPE + " TEXT, " + FORMAT + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if(i == 2 && i1 == 3){
            sqLiteDatabase.execSQL("ALTER TABLE " + TABELA + " ADD COLUMN " + TYPE + " TEXT;");
        }
    }
}
