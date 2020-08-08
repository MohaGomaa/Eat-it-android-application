package com.example.mohamedahmedgomaa.restapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mohamedahmedgomaa.restapp.Comman.Comman;
import com.example.mohamedahmedgomaa.restapp.Database.FavoritesData;
import com.example.mohamedahmedgomaa.restapp.Database.UserData;
import com.example.mohamedahmedgomaa.restapp.Interface.ItemClickListener;
import com.example.mohamedahmedgomaa.restapp.Model.Food;
import com.example.mohamedahmedgomaa.restapp.Model.Order;
import com.example.mohamedahmedgomaa.restapp.ViewHolder.FavoriteAdapter;
import com.example.mohamedahmedgomaa.restapp.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class Profile extends AppCompatActivity {
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    FavoriteAdapter favoriteAdapter;
    CircleImageView imgProfile;
    TextView userName,userPhone;
    RecyclerView recyclerView;
    Button DeleteFav,GotoFav;
    UserData userData;
    String imageProfile;
    FavoritesData fd=new FavoritesData(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Paper.init(this);
        userData=new UserData(this);
        String img = Paper.book().read(Comman.Img_Profile);
        Cursor c = userData.getImage();
        if (!c.moveToNext()) {
        }
        else{
             imageProfile =c.getString(0);
             }
        userName = findViewById(R.id.user_name);
        userPhone = findViewById(R.id.phone_Number);
        imgProfile = findViewById(R.id.imgProfile);
        recyclerView = findViewById(R.id.recycle_favorites);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager;
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        if(img !=null)
            Picasso.get().load(img).into(imgProfile);
         if(imageProfile !=null)
            Picasso.get().load(imageProfile).into(imgProfile);

        userName.setText(Comman.currentUser.getName());
        userPhone.setText(Comman.currentUser.getPhone());


        selAllFav();
    }

    public void selAllFav()
    {
        ArrayList<Order> arrayList =new ArrayList<>();
        Cursor c=fd.selectAllFav();
        if(c.moveToNext()) {
            while (c.moveToNext()) {
                arrayList.add(new Order(c.getString(0),c.getString(1),c.getString(2)));
            }
        }
        else{
            Toast.makeText(this,"No Data",Toast.LENGTH_LONG).show();

        }
        favoriteAdapter=new FavoriteAdapter(arrayList,this);

        recyclerView.setAdapter(favoriteAdapter);
        favoriteAdapter.notifyDataSetChanged();

    }


}
