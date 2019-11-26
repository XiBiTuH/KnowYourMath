package com.example.CONTApelosDEDOSdosPES;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "usersmath.db";


    //Users table

    public static final String TABLE_NAME = "users";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "USERNAME";
    public static final String COL_3 = "PASSWORD";


    //Scores table

    public static final String SCORE_TABLE = "scores";
    public static final String COL_1_s = "ID_player";
    public static final String COL_2_s = "LEVEL";
    public static final String COL_3_s = "TOTAL";
    public static final String COL_4_s = "SCORE_NUMBER";



    // Scores por sessão
    public static final String SESSION_TABLE = "sessions";
    public static final String col_1_t = "session_number";
    public static final String col_2_t = "username";
    public static final String col_3_t = "hits";
    public static final String col_4_t = "misses";




    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("+ COL_1+ " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_2 +" TEXT,"+COL_3+" TEXT)");

        db.execSQL("CREATE TABLE " + SCORE_TABLE + " ("+COL_4_s+" INTEGER PRIMARY KEY AUTOINCREMENT,"+ COL_1_s+" INTEGER ,"+COL_2_s+" INTEGER,"+COL_3_s+" INTEGER)");

        db.execSQL("CREATE TABLE "  + SESSION_TABLE + "(" + col_1_t +  " INTEGER PRIMARY KEY AUTOINCREMENT,"+ col_2_t +" TEXT,"+ col_3_t +" INTEGER,"+col_4_t +" INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + SCORE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SESSION_TABLE);

            onCreate(db);
    }



    public boolean insertDataIntoUsers(String username, String password){

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues  cValues = new ContentValues();
            cValues.put(COL_2,username);
            cValues.put(COL_3,password);
            long res = db.insert(TABLE_NAME,null,cValues);
            if(res == -1){
                return false;
            }
            return true;

    }


    public Cursor getAllDataFromUsers(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME ,null);
        return res;
    }


    public Cursor getAllDataFromScores(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + SCORE_TABLE,null);
        return res;
    }


    public boolean InserDataIntoScores(int ID , int level,int score){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cValues = new ContentValues();
        cValues.put(COL_1_s,ID);
        cValues.put(COL_2_s,level);
        cValues.put(COL_3_s,score);

        Cursor cSearch = getAllDataFromScores();


        while (cSearch.moveToNext()){

            //check if the user has already a score for that level , if so then update it , if not then add it
            if(cSearch.getString(1).equals(String.valueOf(ID)) && Integer.valueOf(cSearch.getString(2)) == level){
                db.update(SCORE_TABLE,cValues,"SCORE_NUMBER = " + cSearch.getString(0),null);
                return true;
            }

        }

        long res = db.insert(SCORE_TABLE,null,cValues);
        if(res == -1){
            return false;
        }
        cSearch.close();

        return true;

    }

    public int maxScore(int ID_c,int level_c){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT TOTAL FROM scores  WHERE ID_player =  ? AND LEVEL =  ?",new String[] { String.valueOf(ID_c),String.valueOf(level_c)});

        int result = 0;
        if(res.moveToFirst()){

            result = res.getInt(0);

        }

        res.close();
        return result;
    }


    public void InsertSessionScores(int misses,int hits,String username){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c_values = new ContentValues();
        c_values.put(col_2_t,username);
        c_values.put(col_3_t,hits);
        c_values.put(col_4_t,misses);
        long res = db.insert(SESSION_TABLE,null,c_values);
        if(res == -1){
            System.out.println("Mal inserido");
        }
        else {
            System.out.println("Bem inserido");
        }


    }

    public Cursor getAllSessionsUser(String username){
        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery("SELECT * FROM sessions WHERE username = '"+ username+"'" ,null);
    }
}
