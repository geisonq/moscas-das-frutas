package com.geisonquevedo.moscadasfrutas.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by GeisonQuevedo on 25/04/2016.
 */
public class DatabaseHandler  extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "moscadasfrutasDb";
    private static final String TABLE_COLETA = "coleta";
    private static final String KEY_ID = "id";
    private static final String KEY_ID_ARM = "id_armadilha";
    private static final String KEY_DATA = "data";
    private static final String TA = "ta";
    private static final String TC = "tc";
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_COLETA_TABLE = "CREATE TABLE " + TABLE_COLETA + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ID_ARM + " INTEGER,"
                + KEY_DATA + " TEXT," +  TA + " INTEGER,"  + TC + " INTEGER )";
        db.execSQL(CREATE_COLETA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COLETA);
        onCreate(db);
    }

    public void addColeta(Coleta coleta) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID_ARM, coleta.getIdArmadilha());
        values.put(KEY_DATA, coleta.getData());
        values.put(TC, coleta.getTc());
        values.put(TA, coleta.getTa());
        db.insert(TABLE_COLETA, null, values);
        db.close();
    }

    public Coleta getColeta(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_COLETA, new String[] { KEY_ID,
                        KEY_ID_ARM, KEY_DATA, TA, TC }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Coleta coleta = new Coleta(Integer.parseInt(cursor.getString(0)),
                                   Integer.parseInt(cursor.getString(1)),
                                   cursor.getString(2),
                                   Integer.parseInt(cursor.getString(3)),
                                   Integer.parseInt(cursor.getString(4)));
        return coleta;
    }
    public List<Coleta> getColetas() {
        List<Coleta> coletaList = new ArrayList<Coleta>();
        String selectQuery = "SELECT  * FROM " + TABLE_COLETA;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Coleta coleta = new Coleta();
                coleta.setID(Integer.parseInt(cursor.getString(0)));
                coleta.setIdArmadilha(Integer.parseInt(cursor.getString(1)));
                coleta.setData(cursor.getString(2));
                coleta.setTa(Integer.parseInt(cursor.getString(3)));
                coleta.setTc(Integer.parseInt(cursor.getString(4)));
                coletaList.add(coleta);
            } while (cursor.moveToNext());
        }
        return coletaList;
    }
    public int updateColeta(Coleta coleta) {
        return 1;
    }
    public void deleteColeta(Coleta coleta) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COLETA, KEY_ID + " = ?",
                new String[] { String.valueOf(coleta.getID()) });
        db.close();
    }
    public int getColetaCount() {
        String countQuery = "SELECT  * FROM " + TABLE_COLETA;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

    public String getLastId(){
        String id = "0";

        String countQuery = "select max(id)+1 as masx_id from " + TABLE_COLETA;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        if (cursor.moveToFirst()){
            do{
                id = cursor.getString(0);
            } while (cursor.moveToNext());
        }

        if(id == null || cursor.getCount() == 0) {
            id= "1";
        }

        cursor.close();

        return id;

    }

    public String getCurrDate() {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        return df.format(today);
    }

    private void demo() {
        //DatabaseHandler db = new DatabaseHandler(this);

        SQLiteDatabase db = this.getReadableDatabase();

        List<Coleta> coletas = this.getColetas();
        for (Coleta cn : coletas) {
            String log = "Id: " + cn.getID() + " ,IdArmadilha: " + cn.getIdArmadilha() + " ,Data: " + cn.getData();
            Log.i("Database", log);
        }

    }
}