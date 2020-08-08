package com.example.mohamedahmedgomaa.restapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUp extends AppCompatActivity {
   MaterialEditText etpass,etname,etphone;
   Button signUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final ProgressDialog mProgressDialog=new ProgressDialog(SignUp.this);
        mProgressDialog.setMessage("Please waiting.....");
        etname =findViewById(R.id.editName);
        etpass =findViewById(R.id.editPassword);
        etphone =findViewById(R.id.editPhone);
        signUp =findViewById(R.id.btnSignUp);

        final FirebaseDatabase database= FirebaseDatabase.getInstance();
        final DatabaseReference table=database.getReference("users");

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etname.getText().toString().equals(null )||etname.getText().toString().equals(""))
                {
                    etname.setError("Enter name");
                    return;

                }
                if(etphone.getText().toString().equals(null )||etphone.getText().toString().equals(""))
                {        etphone.setError("Enter Phone number");
                    return;
                }
                else
                {
                    String validPhone=etphone.getText().toString();
                    validPhone=validPhone.substring(0,2);
                    if(!validPhone.equals("01"))
                    {
                        etphone.setError("number must begin with 01");
                        return;
                    }
                }

                if(etphone.getText().toString().length()<etphone.getMaxCharacters()||etphone.getText().toString().length()>etphone.getMaxCharacters())
                {

                    etphone.setError("Only 11 Number");
                    return;
                }

                if(etpass.getText().toString().equals(null )||etpass.getText().toString().equals(""))
                {        etpass.setError("Enter Password");
                    return;
                }
                if(etpass.getText().toString().length()<8)
                {

                    etpass.setError("Password very small must > 8 ");

                        return;

                }


                if(Comman.isConnectedToInternet(getBaseContext())) {

                    final boolean[] flage = {false};
                    mProgressDialog.show();
                    table.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child(etphone.getText().toString()).exists() && !flage[0]) {
                                //KToast.errorToast(SignUp.this, "Phone Number Is already exist!", Gravity.BOTTOM, KToast.LENGTH_AUTO);

                                Toast.makeText(getApplicationContext(), "Phone Number Is already exist!", Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();


                            } else if (flage[0] == false) {
                                flage[0] = true;
                                User user = new User(etname.getText().toString(), etpass.getText().toString());
                                table.child(etphone.getText().toString()).setValue(user);
                                //KToast.successToast(SignUp.this, "Sign up Successfully", Gravity.BOTTOM, KToast.LENGTH_AUTO);
                                Toast.makeText(getApplicationContext(), "Sign up Successfully", Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                                finish();

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else
                {
                    KToast.warningToast(SignUp.this, "Please check your connection!!", Gravity.BOTTOM, KToast.LENGTH_AUTO);

                    //Toast.makeText(getApplicationContext(),"Please check your connection!!",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

    }
}
