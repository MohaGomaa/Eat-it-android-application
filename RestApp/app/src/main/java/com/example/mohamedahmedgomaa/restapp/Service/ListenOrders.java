package com.example.mohamedahmedgomaa.restapp.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.mohamedahmedgomaa.restapp.Comman.Comman;
import com.example.mohamedahmedgomaa.restapp.Model.Request;
import com.example.mohamedahmedgomaa.restapp.OrderStatus;
import com.example.mohamedahmedgomaa.restapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListenOrders extends Service implements ChildEventListener {
    FirebaseDatabase db;
    DatabaseReference requests;

    public ListenOrders() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Requests");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        requests.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);

    }


    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Request request = dataSnapshot.getValue(Request.class);
        showNotification(dataSnapshot.getKey(),request);
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    private void showNotification(String key, Request request) {
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(getBaseContext(), OrderStatus.class);
        intent.putExtra("userPhone",request.getPhone());
        PendingIntent contentIntnet = PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("Eat It")
                .setContentInfo("Your order was updated")
                .setContentText("order #"+key+"was "+ Comman.convertCodeToStatus(request.getStatus()))
                .setContentIntent(contentIntnet)
                .setContentInfo("info")
                .setSound(uri)
                .setSmallIcon(R.drawable.ic_notifications_none_black_24dp);
        NotificationManager notificationManager = (NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());


    }

}
