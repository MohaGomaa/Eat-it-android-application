package com.example.mohamedahmedgomaa.restapp.Comman;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.mohamedahmedgomaa.restapp.Model.User;

public class Comman {
    //final static User currentUser = null;
    public static User currentUser;

    public static  final String USER_KEY="User";
    public static  final String DELETE="Delete";
    public static  final String PWD_KEY="Password";
    public static  final String Img_Profile="Profile_Img";
    public static final int PICK_IMAGE_REQUEST=71;



    public static boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager !=null)
        {
            NetworkInfo []info=connectivityManager.getAllNetworkInfo();
            if(info!=null)
            {
                for (int i=0;i<info.length;i++)
                {
                    if(info[i].getState()== NetworkInfo.State.CONNECTED)
                        return  true;
                }
            }

        }
        return  false;
    }

    public static   String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return  "Placed";
        else  if(status.equals("1"))
            return "On my way";
        else
            return  "Shipped";
    }


}
