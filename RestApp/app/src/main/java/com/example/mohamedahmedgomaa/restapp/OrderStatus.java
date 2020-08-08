package com.example.mohamedahmedgomaa.restapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mohamedahmedgomaa.restapp.Comman.Comman;
import com.example.mohamedahmedgomaa.restapp.Model.Request;
import com.example.mohamedahmedgomaa.restapp.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.internal.service.Common;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderStatus extends AppCompatActivity {
     RecyclerView recyclerView;
     RecyclerView.LayoutManager layoutManager;
     FirebaseRecyclerAdapter<Request, OrderViewHolder>adapter;
     FirebaseDatabase database;
     DatabaseReference requests;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");

        recyclerView=findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);

        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


            loadOrders(Comman.currentUser.getPhone());




    }

    private void loadOrders(String phone) {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(Request.class,R.layout.order_layout
                ,OrderViewHolder.class,requests.orderByChild("phone").equalTo(phone)) {
            @Override
            protected void populateViewHolder(OrderViewHolder orderViewHolder, Request requests, int i) {
                orderViewHolder.txtOrderId.setText(orderViewHolder.txtOrderId.getText()+adapter.getRef(i).getKey());
                orderViewHolder.txtOrderStatus.setText(orderViewHolder.txtOrderStatus.getText()+Comman.convertCodeToStatus(requests.getStatus()));
                orderViewHolder.txtOrderAddress.setText(orderViewHolder.txtOrderAddress.getText()+requests.getAddress());
                orderViewHolder.txtOrderPhone.setText(orderViewHolder.txtOrderPhone.getText()+requests.getPhone());
                orderViewHolder.txtOrderDate.setText(orderViewHolder.txtOrderDate.getText()+requests.getDate());
                orderViewHolder.txtOrderTime.setText(orderViewHolder.txtOrderTime.getText()+requests.getTime());
                orderViewHolder.txtOrderTotal.setText(orderViewHolder.txtOrderTotal.getText()+requests.getTotal());




            }
        };

        recyclerView.setAdapter(adapter);

    }


}
