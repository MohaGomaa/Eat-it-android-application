package com.example.mohamedahmedgomaa.restapp.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mohamedahmedgomaa.restapp.Database.FavoritesData;
import com.example.mohamedahmedgomaa.restapp.FoodDetails;
import com.example.mohamedahmedgomaa.restapp.FoodList;
import com.example.mohamedahmedgomaa.restapp.Home;
import com.example.mohamedahmedgomaa.restapp.Interface.ItemClickListener;
import com.example.mohamedahmedgomaa.restapp.Model.Food;
import com.example.mohamedahmedgomaa.restapp.Model.Order;
import com.example.mohamedahmedgomaa.restapp.Profile;
import com.example.mohamedahmedgomaa.restapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FavoriteAdapter extends  RecyclerView.Adapter<FavoriteViewHolder>{
    public MyAdapterListener onClickListener;

    ArrayList<Order>arrayList=new ArrayList<>();
     ArrayList<Food>test=new ArrayList<>();

     Context context;
    public FavoriteAdapter( MyAdapterListener listener) {
        onClickListener = listener;
    }
    public FavoriteAdapter(ArrayList<Order> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }
    public FavoriteAdapter( Context context) {
        this.context = context;
    }


     @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View itemView=inflater.inflate(R.layout.favorites_item_layout,parent,false);
        return  new FavoriteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
            holder.foodName.setText(arrayList.get(position).getProductName());
            holder.foodid.setText(arrayList.get(position).getProductId());
         Picasso.get().load(arrayList.get(position).getImg()).into(holder.foodImage);


    }

    @Override
    public int getItemCount() {
      return   arrayList.size();
    }
}

 class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
     public MyAdapterListener onClickListener;

     TextView foodName, foodid;
     CircleImageView foodImage;
     Button btngo, btndel;
     private ItemClickListener itemClickListener;

     public void setItemClickListener(ItemClickListener itemClickListener) {
         this.itemClickListener = itemClickListener;
     }


     public FavoriteViewHolder(@NonNull View itemView) {
         super(itemView);
         foodName = itemView.findViewById(R.id.food_name);
         foodid = itemView.findViewById(R.id.foodId);
         foodImage = itemView.findViewById(R.id.food_image);
         btngo = itemView.findViewById(R.id.btn_goFood);
         btndel = itemView.findViewById(R.id.btn_deleteFav);


         btngo.setOnClickListener(this);
         btndel.setOnClickListener(this);
     }

     @Override
     public void onClick(View v) {

         if (v.getId() == btngo.getId()) {

             Intent fooddetailes = new Intent(v.getContext(), FoodDetails.class);
             fooddetailes.putExtra("FoodId", foodid.getText().toString());
             v.getContext().startActivity(fooddetailes);

         } else if (v.getId() == btndel.getId()) {
             FavoriteAdapter favoriteAdapter = new FavoriteAdapter(v.getContext());
             FoodViewHolder foodViewHolder =new FoodViewHolder(v.getRootView());
             FavoritesData favoritesData = new FavoritesData(v.getContext());
             favoritesData.removeFavorites(foodid.getText().toString());

               Toast.makeText(v.getContext(),foodName.getText().toString()+" is removed",Toast.LENGTH_SHORT).show();
               Intent intent=new Intent(v.getContext(), Home.class);
               v.getContext().startActivity(intent);




         }
     }

 }

interface MyAdapterListener {

    void iconTextViewOnClick(View v, int position);
    void iconImageViewOnClick(View v, int position);
}
