package com.example.mohamedahmedgomaa.restapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoritesData extends SQLiteOpenHelper {
    private static final String TableName="Favorites";
    private  static  final String col1="FoodId";
    private  static  final String col2="FoodName";
    private  static  final String col3="FoodImg";
    private  boolean flage=false;

    public FavoritesData(Context context) {
        super(context, TableName, null, 1);

        if(flage==false)
        {
            addFavorites("Null&NULL","Null&NULL","Null&NULL");
            flage=true;
        }

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TableName+" ("+col1+" TEXT PRIMARY KEY UNIQUE , "+col2+" TEXT ,"+col3+" TEXT)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
     db.execSQL("DROP TABLE IF EXISTS "+TableName);
        onCreate(db);
    }

    public  void addFavorites(String id,String Name,String Url)
    {  try {


        ContentValues cv = new ContentValues();
        cv.put(col1, id);
        cv.put(col2, Name);
        cv.put(col3, Url);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TableName, null, cv);
          }
        catch (Exception e)
        {
          }
    }

    public Cursor selectAllFav()
    {

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM "+TableName,null);
        return c;
    }
    public void removeFavorites(String id)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+TableName+" WHERE "+col1+" = '"+id+"'");
        }
        catch (Exception e)
        {
        }

    }


    public boolean isFavorites(String FoodId)
    {
        SQLiteDatabase db=getReadableDatabase();
        //String query=String.format("SELECT * FROM Favorites WHERE FoodId='$s';",FoodId);
      //  Cursor cursor=db.rawQuery(query,null);
        Cursor c = db.rawQuery("SELECT * FROM " + TableName + " WHERE " + col1 + " = '" + FoodId+"'", null);

        if(c.getCount()<=0)
        {
            c.close();
            return false;
        }
        c.close();
        return  true;
    }


}
