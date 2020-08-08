package com.example.mohamedahmedgomaa.restapp;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.mohamedahmedgomaa.restapp.Comman.Comman;
import com.example.mohamedahmedgomaa.restapp.Database.Database;
import com.example.mohamedahmedgomaa.restapp.Model.Food;
import com.example.mohamedahmedgomaa.restapp.Model.Order;
import com.example.mohamedahmedgomaa.restapp.Model.Rating;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.onurkaganaldemir.ktoastlib.KToast;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class FoodDetails extends AppCompatActivity  {
    TextView foodName,foodPrice, foodDescription;
    ImageView foodImage;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;
    String foodId="";
    FirebaseDatabase database;
    DatabaseReference foods;
    DatabaseReference ratingTbl;

    Food currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);


        database=FirebaseDatabase.getInstance();
        foods=database.getReference("Foods");
        ratingTbl=database.getReference("Rating");
        foodName=findViewById(R.id.food_name);
        foodPrice=findViewById(R.id.food_price);
        foodDescription=findViewById(R.id.food_description);
        foodImage=findViewById(R.id.food_image);
        numberButton=findViewById(R.id.number_button);

        btnCart=findViewById(R.id.btn_cart);
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getApplicationContext()).addToCart(new Order(foodId,currentFood.getName(),
                        numberButton.getNumber(),currentFood.getPrice(),currentFood.getDiscount()));
               // KToast.infoToast(FoodDetails.this, "Add To Cart", Gravity.BOTTOM, KToast.LENGTH_SHORT);
               Toast.makeText(getApplicationContext(),"Add To Cart",Toast.LENGTH_SHORT).show();
            }

        });
        collapsingToolbarLayout=findViewById(R.id.collapsing);
         collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandesAppbar);
         collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

          if(getIntent() !=null)
          {
              foodId=getIntent().getStringExtra("FoodId");
          }
          if(!foodId.isEmpty())
          {
              if(Comman.isConnectedToInternet(getApplicationContext())) {
                  getDetailsFood(foodId);
              //    getRatingFood(foodId);
              }
              else
              {
                 // KToast.warningToast(FoodDetails.this, "Please check your connection!!", Gravity.BOTTOM, KToast.LENGTH_AUTO);

                 Toast.makeText(getApplicationContext(),"Please check your connection!!",Toast.LENGTH_SHORT).show();
                  return;
              }
          }



    }



    private void getDetailsFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               currentFood=dataSnapshot.getValue(Food.class);
                foodImage=findViewById(R.id.img_food);
                Picasso.get().load(currentFood.getImage()).into(foodImage);
                collapsingToolbarLayout.setTitle(currentFood.getName());
                foodPrice.setText(currentFood.getPrice());
                foodName.setText(currentFood.getName());
                foodDescription.setText(currentFood.getDescription());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


/*   Rating function that not work and i try to post comments instead of it

    @Override
    public void onPositiveButtonClicked(int value, @NotNull String comments) {
        final Rating rating =new Rating(Comman.currentUser.getPhone(),foodId,String.valueOf(value),comments);

        ratingTbl.child(Comman.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Comman.currentUser.getPhone()).exists())
                {
                    ratingTbl.child(Comman.currentUser.getPhone()).removeValue();
                    ratingTbl.child(Comman.currentUser.getPhone()).setValue(rating);

                }
                else
                {
                    ratingTbl.child(Comman.currentUser.getPhone()).setValue(rating);

                }
                Toast.makeText(FoodDetails.this,"Thanks for rating !!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

     private void getRatingFood(String foodId) {
    Query foodRating=ratingTbl.orderByChild("foodId").equalTo("foodId");
    foodRating.addValueEventListener(new ValueEventListener() {
        int count=0,sum=0;
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
            {
                Rating item=postSnapshot.getValue(Rating.class);
                sum+=Integer.parseInt(item.getRateValue());
                count++;

            }
            if(count !=0) {
                float average = sum / count;
                ratingBar.setRating(average);
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
}

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quite Ok","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this food")
                .setDescription("Please select some stars and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here....")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAim)
                .create(FoodDetails.this)
                .show();
    }

    */
}
