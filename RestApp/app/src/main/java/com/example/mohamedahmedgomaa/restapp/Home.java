package com.example.mohamedahmedgomaa.restapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mohamedahmedgomaa.restapp.Comman.Comman;
import com.example.mohamedahmedgomaa.restapp.Database.UserData;
import com.example.mohamedahmedgomaa.restapp.Interface.ItemClickListener;
import com.example.mohamedahmedgomaa.restapp.Model.Category;
import com.example.mohamedahmedgomaa.restapp.Model.Food;
import com.example.mohamedahmedgomaa.restapp.Service.ListenOrder;
import com.example.mohamedahmedgomaa.restapp.Service.ListenOrders;
import com.example.mohamedahmedgomaa.restapp.ViewHolder.FoodViewHolder;
import com.example.mohamedahmedgomaa.restapp.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.onurkaganaldemir.ktoastlib.KToast;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String CategoryId ;
    UserData userData;
    FirebaseDatabase database;
    DatabaseReference category;
    TextView txtFullname;
    CircleImageView profileImg;
    ImageView ProfileImgLay;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter <Category, MenuViewHolder>adapter;
    Uri saveUri;
   Button btnSelect,btnUpload;


    FirebaseRecyclerAdapter<Category, MenuViewHolder> searchAdapter;
    List<String> suggestList =new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Paper.init(this);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);
        database=FirebaseDatabase.getInstance();
        category=database.getReference("Category");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent cartIntent=new Intent(Home.this,Cart.class);
              startActivity(cartIntent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View headerview=navigationView.getHeaderView(0);
        txtFullname=headerview.findViewById(R.id.txtFullName);
        profileImg=headerview.findViewById(R.id.imageview);
//  another way to save photo internal
        userData=new UserData(this);
        Cursor c = userData.getImage();
        if (!c.moveToNext()) {

        } else {

            String iamge = c.getString(0);
            Picasso.get().load(iamge).into(profileImg);

        }
        Paper.init(this);
        String img=Paper.book().read(Comman.Img_Profile);
        if(img !=null)
        {
            Picasso.get().load(img).into(profileImg);
        }
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();

            }
        });

        txtFullname.setText(txtFullname.getText()+Comman.currentUser.getName());
        recyclerView=findViewById(R.id.recycle_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(Comman.isConnectedToInternet(getApplicationContext())) {
            loadMenu();


            Intent service=new Intent(Home.this, ListenOrders.class);
            startService(service);

        } else
        {
           // KToast.warningToast(Home.this, "Please check your connection!!", Gravity.BOTTOM, KToast.LENGTH_AUTO);

         Toast.makeText(getApplicationContext(),"Please check your connection!!",Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void loadMenu() {
       adapter=new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class,R.layout.menu_item,MenuViewHolder.class,category) {

            @Override
            protected void populateViewHolder(MenuViewHolder menuViewHolder, Category category, int i) {
                menuViewHolder.txtMenuName.setText(category.getName());

                Picasso.get().load(category.getImage()).into(menuViewHolder.imageView);
                final Category clickItem=category;
                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                     //   Toast.makeText(Home.this,""+clickItem.getName(),Toast.LENGTH_SHORT).show();
                        Intent foodIntentt=new Intent(Home.this,FoodList.class);
                        foodIntentt.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(foodIntentt);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
     if(item.getItemId()==R.id.refresh)
     {
         loadMenu();
     }
        return super.onOptionsItemSelected(item);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_profile) {
            Intent ProfileIntent=new Intent(this,Profile.class);
            startActivity(ProfileIntent);

        }
       else if (id == R.id.nav_menu) {
        } else if (id == R.id.nav_cart) {
             Intent cartIntent=new Intent(this,Cart.class);
                 startActivity(cartIntent);
        } else if (id == R.id.nav_orders) {
            Intent orderIntent=new Intent(this, OrderStatus.class);
            startActivity(orderIntent);
        } else if (id == R.id.nav_log_out) {
            //destroy remember me
            SharedPreferences sharedPreferences=getSharedPreferences("LoginData",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.clear();
            editor.commit();

            Intent SignIn=new Intent(this, SignIn.class);
            SignIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(SignIn);

        }
        else if(id==R.id.nav_changePass){
            showChangePasswordDialog();

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showChangePasswordDialog() {
        final AlertDialog.Builder alertdialog=new AlertDialog.Builder(Home.this);
        alertdialog.setTitle("Change Password");
        alertdialog.setMessage("Please fill all Fields");
        LayoutInflater layoutInflater=LayoutInflater.from(Home.this);
        View passLayout=layoutInflater.inflate(R.layout.change_password_layout,null);

        final MaterialEditText pass=passLayout.findViewById(R.id.edtPassword);
        final MaterialEditText newPass=passLayout.findViewById(R.id.edtNewPassword);
        final MaterialEditText conPass=passLayout.findViewById(R.id.edtConPassword);
         alertdialog.setView(passLayout);

         alertdialog.setPositiveButton("Change", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {

                 if(pass.getText().toString().equals(null )||pass.getText().toString().equals(""))
                 {
                     Toast.makeText(getApplicationContext(),"You missed Current Password Empty",Toast.LENGTH_SHORT).show();

                     pass.setError("Enter Password");
                     return;
                 }
                 if(newPass.getText().toString().equals(null )||newPass.getText().toString().equals(""))
                 {
                     Toast.makeText(getApplicationContext(),"You missed New Password Empty",Toast.LENGTH_SHORT).show();

                     newPass.setError("Enter New  Password");
                     return;
                 }
                 if(newPass.getText().toString().length()<8)
                 {
                     Toast.makeText(getApplicationContext(),"New Password very small must > 8 ",Toast.LENGTH_SHORT).show();

                     newPass.setError("Rang password between 8 & 20");
                     return;
                 }
                 if(conPass.getText().toString().equals(null )||conPass.getText().toString().equals(""))
                 {
                     Toast.makeText(getApplicationContext(),"You missed Confirm Password Empty",Toast.LENGTH_SHORT).show();

                     conPass.setError("Enter confirm Password");
                     return;
                 }
                 if(pass.getText().toString().equals(Comman.currentUser.getPassword()))
                 {
                     if(newPass.getText().toString().equals(conPass.getText().toString()))
                     {
                         Map<String,Object> passwordUpdate=new HashMap<>();
                         passwordUpdate.put("password",newPass.getText().toString());
                         DatabaseReference user=FirebaseDatabase.getInstance().getReference("users");
                         user.child(Comman.currentUser.getPhone()).updateChildren(passwordUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                 //KToast.infoToast(Home.this, "Password was Change", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                                 SharedPreferences sharedPreferences=getSharedPreferences("LoginData",MODE_PRIVATE);
                                 SharedPreferences.Editor editor=sharedPreferences.edit();
                                 editor.clear();
                                 editor.commit();


                               Toast.makeText(getApplicationContext(),"Password was Change",Toast.LENGTH_SHORT).show();

                             }
                         }).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 //KToast.infoToast(Home.this, ""+e.getMessage(), Gravity.BOTTOM, KToast.LENGTH_SHORT);
                                 Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();

                             }
                         });

                     }
                     else
                     {
                       //  KToast.warningToast(Home.this, "Not Matched Password", Gravity.BOTTOM, KToast.LENGTH_AUTO);
                        Toast.makeText(getApplicationContext(),"Not Matched Password",Toast.LENGTH_SHORT).show();

                     }
                 }
                 else
                 {

                   //  KToast.warningToast(Home.this, "Worng Current Password", Gravity.BOTTOM, KToast.LENGTH_AUTO);

                     Toast.makeText(getApplicationContext(),"Worng Current Password",Toast.LENGTH_SHORT).show();
                 }

             }
         });
         alertdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
             }
         });

         alertdialog.show();
    }

    private void showDialog() {
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(Home.this);
        aBuilder.setTitle("Setect Profile Photo");
        LayoutInflater inflater=this.getLayoutInflater();
        View add_Profile_layout=inflater.inflate(R.layout.add_profile_image_layout,null);

        ProfileImgLay=add_Profile_layout.findViewById(R.id.profileLay);
        String img=Paper.book().read(Comman.Img_Profile);

        if(img !=null) {
            Picasso.get().load(img).into(ProfileImgLay);
        }
        btnSelect=add_Profile_layout.findViewById(R.id.btnSelect);
        btnUpload=add_Profile_layout.findViewById(R.id.btnUpload);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
                if(saveUri !=null) {
                    Picasso.get().load(saveUri.toString()).into(ProfileImgLay);
                }

            }
        });
        aBuilder.setView(add_Profile_layout);


        aBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(saveUri !=null)
                {
                    String img= Paper.book().read(Comman.Img_Profile);
                    if(img !=null)
                    {
                        Paper.book().destroy();
                    }

                        Cursor c = userData.getImage();
                        if (!c.moveToNext()) {

                        } else {

                            userData.clear();

                        }

                    Paper.book().write(Comman.Img_Profile,saveUri.toString());
                    userData.addImg(saveUri.toString());
                    Picasso.get().load(saveUri.toString()).into(profileImg);
                   // KToast.infoToast(Home.this, "Image of  Profile  changed", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                    Toast.makeText(getApplicationContext(),"Image of  Profile  changed",Toast.LENGTH_SHORT).show();

                }
            }
        });
        aBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //KToast.infoToast(Home.this, "Image of  Profile Not changed", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                Toast.makeText(getApplicationContext(),"Image of  Profile Not changed",Toast.LENGTH_SHORT).show();
            }
        });
        aBuilder.show();
    }

    private void uploadImage() {
        if(saveUri !=null)
        {
            final ProgressDialog mProgressDialog=new ProgressDialog(this);
            mProgressDialog.setMessage("Uploading.....");
            mProgressDialog.show();
            String imageName= UUID.randomUUID().toString();
         //   final  StorageReference imageFolder= storageReference.child("images/"+imageName);

            mProgressDialog.dismiss();
            //KToast.infoToast(Home.this, "Uploaded !!", Gravity.BOTTOM, KToast.LENGTH_SHORT);
          Toast.makeText(getApplicationContext(),"Uploaded !!",Toast.LENGTH_SHORT).show();



        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Comman.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data !=null && data.getData() !=null)
        {
            saveUri=data.getData();
            btnSelect.setText("Image Selected !");
        }
    }
    private void chooseImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),Comman.PICK_IMAGE_REQUEST );
    }

    private void startSearch(CharSequence text) {
        searchAdapter=new FirebaseRecyclerAdapter<Category, MenuViewHolder>(
                Category.class,
                R.layout.menu_item,
                MenuViewHolder.class,
                category.orderByChild("name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(MenuViewHolder menuViewHolder, Category category, int i) {

                menuViewHolder.txtMenuName.setText(category.getName());




                Picasso.get().load(category.getImage()).into(menuViewHolder.imageView);

                final Category clickItem=category;
                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodlist=new Intent(getApplicationContext(),FoodList.class);
                        foodlist.putExtra("FoodId",searchAdapter.getRef(position).getKey());
                        startActivity(foodlist);
                    }
                });
            }
        };
        recyclerView.setAdapter(searchAdapter);
    }



    private void loadSuggest() {
        category.orderByChild("menuId").equalTo( CategoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnaphot:dataSnapshot.getChildren())
                {
                    Category item=postSnaphot.getValue(Category.class);
                    suggestList.add(item.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
