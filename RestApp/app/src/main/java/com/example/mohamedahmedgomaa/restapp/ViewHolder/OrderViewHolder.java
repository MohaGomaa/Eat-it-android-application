package com.example.mohamedahmedgomaa.restapp.ViewHolder;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mohamedahmedgomaa.restapp.Interface.ItemClickListener;
import com.example.mohamedahmedgomaa.restapp.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress,txtOrderDate,txtOrderTime,txtOrderTotal;
    private ItemClickListener itemClickListener;


    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderId=itemView.findViewById(R.id.order_id);
        txtOrderStatus=itemView.findViewById(R.id.order_status);
        txtOrderPhone=itemView.findViewById(R.id.order_phone);
        txtOrderAddress=itemView.findViewById(R.id.order_address);
        txtOrderDate=itemView.findViewById(R.id.order_date);
        txtOrderTime=itemView.findViewById(R.id.order_time);
        txtOrderTotal=itemView.findViewById(R.id.order_price);
        itemView.setOnClickListener(this);
    }

    public ItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
           try {
               itemClickListener.onClick( v,getAdapterPosition(),false);
           }
           catch (Exception e)
           {
           }

    }
}
