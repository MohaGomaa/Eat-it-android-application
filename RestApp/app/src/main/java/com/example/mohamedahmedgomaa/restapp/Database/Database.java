package com.example.mohamedahmedgomaa.restapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.mohamedahmedgomaa.restapp.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {
    private  static  final  String DB_NAME="EatItDB.db";
    private  static  final  int DB_VER=1;

    public Database(Context context) {

        super(context, DB_NAME, null, DB_VER);
    }

   public List<Order> getCarts(){
        SQLiteDatabase db=getReadableDatabase();

        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();
        String sqlTable="OrderDetail";
        qb.setTables(sqlTable);

        String []sqlSelect={"ProductId","ProductName","Quantity","Price","Discount"};


       Cursor c=qb.query(db,sqlSelect,null,null,null,null,null);
       final List<Order>result=new ArrayList<>();
       while (c.moveToNext())
       {

               result.add(new Order(c.getString(c.getColumnIndex("ProductId")),
                                    c.getString(c.getColumnIndex("ProductName")),
                                    c.getString(c.getColumnIndex("Quantity")),
                                    c.getString(c.getColumnIndex("Price")),
                                    c.getString(c.getColumnIndex("Discount"))
                                   ));


       }
       return  result;
   }

   public void addToCart (Order order)
   {
       SQLiteDatabase db=getWritableDatabase();
       String query=String.format("INSERT INTO OrderDetail (ProductId,ProductName,Quantity,Price,Discount) VALUES ('%s','%s','%s','%s','%s');" ,
               order.getProductId(),
               order.getProductName(),
               order.getQuantity(),
               order.getPrice(),
               order.getDiscount());
       db.execSQL(query);
   }
    public void cleanCart ()
    {
        SQLiteDatabase db=getWritableDatabase();
        String query=String.format("DELETE FROM OrderDetail");
        db.execSQL(query);

    }

    public void addToFavorites(String FoodId)
    {
        SQLiteDatabase db=getWritableDatabase();
       /* String query=String.format("INSERT INTO Favorites (FoodId) VALUES ('%s');",FoodId);
        db.execSQL(query);
*/
     try {
         ContentValues cv = new ContentValues();
         cv.put("FoodId", FoodId);
         db.insert("Favorites", null, cv);
     }
     catch (Exception e)
     {
     }


    }

    public void removeFromFavorites(String FoodId)
    {
        SQLiteDatabase db=getWritableDatabase();
        String query=String.format("DELETE FROM Favorites WHERE FoodId='$s';",FoodId);
        db.execSQL(query);

    }

    public boolean isFavorites(String FoodId)
    {
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("SELECT * FROM Favorites WHERE FoodId='$s';",FoodId);
         Cursor cursor=db.rawQuery(query,null);

       if(cursor.getCount()<=0)
       {
           cursor.close();
           return false;
       }
     cursor.close();
       return  true;
    }
}
