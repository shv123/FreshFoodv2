package com.example.freshfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CurrentOrders extends AppCompatActivity {

    Button back;
    ListView listView;
    ArrayList<String> currentOrders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_orders);

        back = findViewById(R.id.btnBack);
        listView = findViewById(R.id.currentOrdersList);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backHome = new Intent(CurrentOrders.this, Menu.class);
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
                    if(status.matches("Not Assigned") || status.matches("Ongoing")) {
                        String tmp = "Order Number : " + Uid + Integer.toString(numOrdersIter) + "\n" +
                                "Small : " + ds.getValue(Orders.class).getCart().get(0) + "\n" +
                                "Medium : " + ds.getValue(Orders.class).getCart().get(1) + "\n" +
                                "Large : " + ds.getValue(Orders.class).getCart().get(2) + "\n" +
                                "Date : " + date + "\n";
                        currentOrders.add(tmp);
                        numOrdersIter++;
                    }
                }
                ref.removeEventListener(this);

                if(currentOrders.isEmpty())
                {
                    Toast.makeText(CurrentOrders.this, "There are no ongoing orders", Toast.LENGTH_LONG).show();
                }
                else {
                    ArrayAdapter arrayAdapter = new ArrayAdapter(CurrentOrders.this, android.R.layout.simple_list_item_1, currentOrders);
                    listView.setAdapter(arrayAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
