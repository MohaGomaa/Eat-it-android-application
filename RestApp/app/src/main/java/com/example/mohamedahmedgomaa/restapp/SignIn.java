package com.example.mohamedahmedgomaa.restapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {
    EditText et_phone,et_password;
    Button btnSignIn;
    CheckBox chbReme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        final ProgressDialog mProgressDialog=new ProgressDialog(SignIn.this);
        mProgressDialog.setTitle("Please waiting.....");
        mProgressDialog.setTitle("waiting.....");
        et_password=findViewById(R.id.editPassword);
        et_phone=findViewById(R.id.editPhone);
        btnSignIn=findViewById(R.id.btnSignIn);

     chbReme =  findViewById(R.id.chbRemember);

       Paper.init(this);

       final  FirebaseDatabase database= FirebaseDatabase.getInstance();
        final DatabaseReference table=database.getReference("users");


                btnSignIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if(et_phone.getText().toString().equals(null )||et_phone.getText().toString().equals(""))
                        {
                            et_phone.setError("Enter Phone");
                            return;

                        }
                        if(et_phone.getText().toString().length()<11||et_phone.getText().toString().length()>11)
                        {
                            et_phone.setError("Only 11 Number");
                            return;
                        }
                            if(et_password.getText().toString().equals(null )||et_password.getText().toString().equals(""))
                            {        et_password.setError("Enter Password");
                                      return;
                            }

                                if (Comman.isConnectedToInternet(getApplicationContext())) {


                            mProgressDialog.setCancelable(false);
                            mProgressDialog.show();

                            table.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child(et_phone.getText().toString()).exists()) {

                                        User user = dataSnapshot.child(et_phone.getText().toString()).getValue(User.class);
                                        user.setPhone(et_phone.getText().toString());
                                        if (et_password.getText().toString().equals(user.getPassword().toString())) {
                                            if (chbReme.isChecked()) {
                                                SharedPreferences sharedPreferences =getSharedPreferences("LoginData", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor=sharedPreferences.edit();

                                                editor.putString(Comman.USER_KEY,et_phone.getText().toString());
                                                editor.putString(Comman.PWD_KEY,et_password.getText().toString());
                                                editor.commit();

                             /*   Paper.book().write(Comman.USER_KEY,et_phone.getText().toString());
                                Paper.book().write(Comman.PWD_KEY,et_phone.getText().toString());
                                */

                                            }

                                            Intent intent = new Intent(SignIn.this, Home.class);
                                            Comman.currentUser = user;
                                            mProgressDialog.dismiss();

                                            Toast.makeText(getApplicationContext(),"Success Login",Toast.LENGTH_SHORT).show();
                                            startActivity(intent);


                                            finish();
                                        } else {
                                            mProgressDialog.dismiss();
                                           // KToast.errorToast(SignIn.this, "Wrong Password!", Gravity.BOTTOM, KToast.LENGTH_AUTO);
                                            Toast.makeText(getApplicationContext(), "Wrong Password!", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        mProgressDialog.dismiss();
                                      //  KToast.errorToast(SignIn.this, "User not Exists in Database!!", Gravity.BOTTOM, KToast.LENGTH_AUTO);

                                       Toast.makeText(getApplicationContext(), "User not Exists in Database!!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {
                                    KToast.warningToast(SignIn.this, "Please check your connection!!", Gravity.BOTTOM, KToast.LENGTH_AUTO);

                           //         Toast.makeText(getApplicationContext(), "Please check your connection!!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                });


    }
}
