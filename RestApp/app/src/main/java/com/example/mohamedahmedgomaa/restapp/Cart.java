package com.example.mohamedahmedgomaa.restapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mohamedahmedgomaa.restapp.Comman.Comman;
import com.example.mohamedahmedgomaa.restapp.Database.Database;
import com.example.mohamedahmedgomaa.restapp.Model.Order;
import com.example.mohamedahmedgomaa.restapp.Model.Request;
import com.example.mohamedahmedgomaa.restapp.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Cart extends AppCompatActivity {
      RecyclerView recyclerView;
      RecyclerView.LayoutManager layoutManager;
      FirebaseDatabase database;
      DatabaseReference requests;

      TextView txtTotalPrice;
      Button btnPlace;

      List<Order> cart= new ArrayList<>();
      CartAdapter adapter;
    String currentDate,currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");

        recyclerView=findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice=findViewById(R.id.total);
        btnPlace=findViewById(R.id.btnPlaceOrder);
        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if(cart.size()>0)
                showAlertDialog();
             else
                // KToast.infoToast(Cart.this, "Your Cart is Empty", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                 Toast.makeText(getBaseContext(),"Your Cart is Empty",Toast.LENGTH_SHORT).show();

            }
        });
      loadListFood();
    }

    private void showAlertDialog() {
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more Step !");
        alertDialog.setMessage("Enter your Address :   ");

        final EditText edtAddress=new EditText(Cart.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT );
        edtAddress.setLayoutParams(layoutParams);
        alertDialog.setView(edtAddress);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                if( edtAddress.getText().toString().equals(null )|| edtAddress.getText().toString().equals(""))
                {        edtAddress.setError("Enter Address");
                    Toast.makeText(getApplicationContext(),"please Enter your address",Toast.LENGTH_SHORT).show();
                   return;

                }
                dialog.dismiss();
                Calendar calForDate = Calendar.getInstance();
                SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd,yyyy");
                currentDate = currentDateFormat.format(calForDate.getTime());

                Calendar calForTime = Calendar.getInstance();
                SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                currentTime = currentTimeFormat.format(calForTime.getTime());
                Request request=new Request(Comman.currentUser.getPhone(),Comman.currentUser.getName(),
                        edtAddress.getText().toString(),txtTotalPrice.getText().toString(),currentDate,currentTime,cart)  ;

                requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);
                new Database(getApplicationContext()).cleanCart();
              //  KToast.successToast(Cart.this, "Thank you, Order Place", Gravity.BOTTOM, KToast.LENGTH_AUTO);
               Toast.makeText(getApplicationContext(),"Thank you, Order Place",Toast.LENGTH_SHORT).show();
                finish();

            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void loadListFood() {


        cart = new Database(this).getCarts();

        if (cart.isEmpty()) {
           // KToast.infoToast(Cart.this, " Cart Empty", Gravity.BOTTOM, KToast.LENGTH_SHORT);
            Toast.makeText(this, " Cart Empty", Toast.LENGTH_LONG).show();
        }
        adapter = new CartAdapter(cart, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        int total = 0;

        for (Order order : cart) {
            total += ((Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity())));
        }
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        txtTotalPrice.setText(fmt.format(total));
    }
           @Override
             public boolean  onContextItemSelected(MenuItem item)
             {
                 if(item.getTitle().equals(Comman.DELETE))
                     deleteCart(item.getOrder());
                   return true;

             }

    private void deleteCart(int order) {
        cart.remove(order);
        new Database(this).cleanCart();

        for(Order item:cart)
        {
            new Database(this).addToCart(item);
        }
        loadListFood();
    }

}
