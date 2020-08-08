package com.example.mohamedahmedgomaa.restapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mohamedahmedgomaa.restapp.Comman.Comman;
import com.example.mohamedahmedgomaa.restapp.Database.Database;
import com.example.mohamedahmedgomaa.restapp.Database.FavoritesData;
import com.example.mohamedahmedgomaa.restapp.Interface.ItemClickListener;
import com.example.mohamedahmedgomaa.restapp.Model.Food;
import com.example.mohamedahmedgomaa.restapp.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.onurkaganaldemir.ktoastlib.KToast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference food;
    TextView txtFullname;
    String CategoryId ;
    FavoritesData favoritesData;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    RecyclerView.LayoutManager layoutManager;

    //searchbar function
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList =new ArrayList<>();
    MaterialSearchBar materialSearchBar;
      Database localDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        database=FirebaseDatabase.getInstance();
        food=database.getReference("Foods");
       favoritesData=new FavoritesData(this);
        localDB=new Database(this);

        recyclerView=findViewById(R.id.recycle_food);
        recyclerView.setHasFixedSize(true);
       layoutManager =new LinearLayoutManager(this);
       recyclerView.setLayoutManager(layoutManager);

        if(getIntent()!=null)
        {
            CategoryId =getIntent().getStringExtra("CategoryId");
        }
        if(!CategoryId.isEmpty()&&CategoryId !=null)
        {
            if(Comman.isConnectedToInternet(getApplicationContext())) {
                loadListFood(CategoryId);

            }
            else
            {
                //KToast.warningToast(FoodList.this, "Please check your connection!!", Gravity.BOTTOM, KToast.LENGTH_AUTO);

                Toast.makeText(getApplicationContext(),"Please check your connection!!",Toast.LENGTH_SHORT).show();
                return;
            }
        }

         // search  coding
         materialSearchBar =findViewById(R.id.searchBar);
         materialSearchBar.setHint("Enter your Food");

         loadSuggest();
         materialSearchBar.setLastSuggestions(suggestList);
         materialSearchBar.setCardViewElevation(10);
         materialSearchBar.addTextChangeListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
                 //when user type change suggestList hint for me to remember
               List<String>suggest=new ArrayList<String>();
               for(String search:suggestList)
               {
                   if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                   {
                       suggest.add(search);
                   }
               }
               materialSearchBar.setLastSuggestions(suggest);
             }

             @Override
             public void afterTextChanged(Editable s) {

             }
         });
         materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
             @Override
             public void onSearchStateChanged(boolean enabled) {
      // when search bar is closed restore original adapter
                 if(!enabled)
                     recyclerView.setAdapter(adapter);
             }

             @Override
             public void onSearchConfirmed(CharSequence text) {
                           startSearch(text);
             }

             @Override
             public void onButtonClicked(int buttonCode) {

             }
         });



    }

    private void startSearch(CharSequence text) {
        searchAdapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                food.orderByChild("name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(final FoodViewHolder foodViewHolder, final Food food, final int position) {
                foodViewHolder.food_name.setText(food.getName());




                Picasso.get().load(food.getImage()).into(foodViewHolder.food_image);

                final Food clickItem=food;
                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent fooddetailes=new Intent(FoodList.this,FoodDetails.class);
                        fooddetailes.putExtra("FoodId",searchAdapter.getRef(position).getKey());
                        startActivity(fooddetailes);
                    }
                });
            }
        };
        recyclerView.setAdapter(searchAdapter);
    }



    private void loadSuggest() {
        food.orderByChild("menuId").equalTo(CategoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnaphot:dataSnapshot.getChildren())
                {
                    Food item=postSnaphot.getValue(Food.class);
                    suggestList.add(item.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadListFood(String categoryId) {
        //Toast.makeText(FoodList.this,""+CategoryId, Toast.LENGTH_SHORT).show();

        adapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                food.orderByChild("menuId").equalTo(CategoryId)
                ) {
            @Override
            protected void populateViewHolder(final FoodViewHolder foodViewHolder, final Food food, final int i) {
                foodViewHolder.food_name.setText(food.getName());

                Picasso.get().load(food.getImage()).into(foodViewHolder.food_image);

                if(favoritesData.isFavorites(String.valueOf(adapter.getRef(i).getKey())))
                    foodViewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);

                foodViewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!favoritesData.isFavorites(adapter.getRef(i).getKey()))
                        {

                            favoritesData.addFavorites(String.valueOf(adapter.getRef(i).getKey()),food.getName(),food.getImage());
                            foodViewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                          //  KToast.infoToast(FoodList.this, ""+food.getName()+" was added to Favorites", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                            Toast.makeText(getApplicationContext(),""+food.getName()+" was added to Favorites",Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            favoritesData.removeFavorites(String.valueOf(adapter.getRef(i).getKey()));
                            foodViewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                          //  KToast.infoToast(FoodList.this, ""+food.getName()+" was removed from Favorites", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                           Toast.makeText(getApplicationContext(),""+food.getName()+" was removed from Favorites",Toast.LENGTH_SHORT).show();

                        }

                    }
                });

                final Food clickItem=food;
                 foodViewHolder.setItemClickListener(new ItemClickListener() {
                     @Override
                     public void onClick(View view, int position, boolean isLongClick) {
                         Intent fooddetailes=new Intent(FoodList.this,FoodDetails.class);
                         fooddetailes.putExtra("FoodId",adapter.getRef(position).getKey());
                         startActivity(fooddetailes);
                     }
                 });
            }
        };
        recyclerView.setAdapter(adapter);

    }
}
