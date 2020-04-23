package com.example.freshfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class CompletedOrders extends AppCompatActivity {

    Button back;
    ListView listView;
    ArrayList<String> completedOrders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_orders);

        back = findViewById(R.id.btnBack1);
        listView = findViewById(R.id.completedOrdersList);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backHome = new Intent(CompletedOrders.this, Menu.class);
                startActivity(backHome);
            }
        });

        final String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("OrdersTable")
                .child(Uid);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numOrdersIter = 1;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                    Date dateTime = new Date(Long.parseLong(ds.getValue(Orders.class).getTimeStamp())*1000);
                    String date = simpleDateFormat.format(dateTime);
                    String status = ds.getValue(Orders.class).getStatus();
                    if(status.matches("Completed")) {
                        String tmp = "Order Number : " + Uid + Integer.toString(numOrdersIter) + "\n" +
                                "Small : " + ds.getValue(Orders.class).getCart().get(0) + "\n" +
                                "Medium : " + ds.getValue(Orders.class).getCart().get(1) + "\n" +
                                "Large : " + ds.getValue(Orders.class).getCart().get(2) + "\n" +
                                "Date : " + date + "\n";
                        completedOrders.add(tmp);
                        numOrdersIter++;
                    }
                }
                ref.removeEventListener(this);

                if(completedOrders.isEmpty())
                {
                    Toast.makeText(CompletedOrders.this, "There are no completed orders yet...", Toast.LENGTH_LONG).show();
                }
                else {
                    ArrayAdapter arrayAdapter = new ArrayAdapter(CompletedOrders.this, android.R.layout.simple_list_item_1, completedOrders);
                    listView.setAdapter(arrayAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
