package com.example.mohamedahmedgomaa.restapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mohamedahmedgomaa.restapp.Comman.Comman;
import com.example.mohamedahmedgomaa.restapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onurkaganaldemir.ktoastlib.KToast;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
   Button btnSignIn,btnSignUp;
   TextView txtSlogan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn=findViewById(R.id.btnSignIn);
        btnSignUp=findViewById(R.id.btnSignUp);
        txtSlogan=findViewById(R.id.txtSlogan);
        Typeface typeface=Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
        txtSlogan.setTypeface(typeface);

        Paper.init(this);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,SignIn.class);
                startActivity(intent);

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,SignUp.class);
                startActivity(intent);

            }
        });
        //functionallty
        SharedPreferences sharedPreferences =getSharedPreferences("LoginData",MODE_PRIVATE);
        String  phone=sharedPreferences.getString(Comman.USER_KEY,null);
        String  pwd=sharedPreferences.getString(Comman.PWD_KEY,null);

       /* String  phone=Paper.book().read(Comman.USER_KEY);
        String  pwd=Paper.book().read(Comman.PWD_KEY);
        */
        if(phone!=null && pwd !=null)
        {
            if(!phone.isEmpty()&& !pwd.isEmpty())
              login(phone,pwd);
        }
    }

    private void login(final String phone, final String pwd) {
        final ProgressDialog mProgressDialog=new ProgressDialog(MainActivity.this);
        mProgressDialog.setMessage("Please waiting.....");
        final FirebaseDatabase database= FirebaseDatabase.getInstance();
        final DatabaseReference table=database.getReference("users");

        if (Comman.isConnectedToInternet(getApplicationContext())) {


            mProgressDialog.show();

            table.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(phone).exists()) {

                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);
                        if (pwd.equals(user.getPassword())) {
                            Intent intent = new Intent(MainActivity.this, Home.class);
                            Comman.currentUser = user;

                           // KToast.successToast(MainActivity.this, "Success Login ", Gravity.BOTTOM, KToast.LENGTH_AUTO);
                            Toast.makeText(getApplicationContext(), "Success Login ", Toast.LENGTH_SHORT).show();

                            mProgressDialog.dismiss();
                            startActivity(intent);


                            finish();
                        } else {
                            mProgressDialog.dismiss();
                          //  KToast.errorToast(MainActivity.this, "Wrong Password!", Gravity.BOTTOM, KToast.LENGTH_AUTO);

                            Toast.makeText(getApplicationContext(), "Wrong Password!", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        mProgressDialog.dismiss();
                      //  KToast.errorToast(MainActivity.this, "User not Exists in Database!!", Gravity.BOTTOM, KToast.LENGTH_AUTO);

                        Toast.makeText(getApplicationContext(), "User not Exists in Database!!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
          //  KToast.warningToast(MainActivity.this, "Please check your connection!!", Gravity.BOTTOM, KToast.LENGTH_AUTO);
            Toast.makeText(getApplicationContext(),"Please check your connection!!",Toast.LENGTH_SHORT).show();

            return;
        }
    }





}
