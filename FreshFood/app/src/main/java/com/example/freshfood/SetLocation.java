package com.example.freshfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SetLocation extends AppCompatActivity {

    ArrayList<Integer> cart = new ArrayList<Integer>();
    EditText setAdd;
    Button next;
    int updatedNumOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);

        cart = getIntent().getIntegerArrayListExtra("Orders");
        setAdd = findViewById(R.id.setAddress);
        next = findViewById(R.id.buttonSetLocation);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = setAdd.getText().toString();
                if(address.matches("")){
                    Toast.makeText(SetLocation.this, "Please enter drop location", Toast.LENGTH_SHORT).show();
                    setAdd.requestFocus();
                }

                else{
                    Geocoder geocoder = new Geocoder(SetLocation.this, Locale.getDefault());
                    try {

                        List<Address> addressList = geocoder.getFromLocationName(address, 1);
                        if(addressList.size() > 0){
                            Address add = addressList.get(0);

                            Orders orders = new Orders(-1, -1, add.getLatitude(), add.getLongitude(), cart, "", "Not Assigned");
                            Intent getPickUp = new Intent(SetLocation.this, PickUpLocation.class);
                            getPickUp.putExtra("Order", orders);
                            startActivity(getPickUp);
                        }

                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        /*next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = setAdd.getText().toString();

                Geocoder geocoder = new Geocoder(SetLocation.this, Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocationName(address, 1);

                    if(addressList.size() > 0){
                        Address add = addressList.get(0);
                        final Orders orders = new Orders(add.getLatitude(), add.getLongitude(),-1, -1, cart, "Not Assigned");

                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        final ValueEventListener listener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                UserInfo userInfo = new UserInfo("name","mob", "email", "comm", 99);
                                try {
                                    userInfo = dataSnapshot.getValue(UserInfo.class);
                                    updatedNumOrders = userInfo.getNum_orders() + 1;
                                }catch (Exception e){
                                    Toast.makeText(SetLocation.this, "Inside Listener", Toast.LENGTH_LONG).show();
                                    Log.d("mytag", e.toString());
                                }
                                String name = userInfo.getName();
                                String mob_num = userInfo.getMob_num();
                                String email = userInfo.getEmail();
                                String community = userInfo.getCommunity();
                                UserInfo updatedUserInfo = new UserInfo(name, mob_num, email, community, updatedNumOrders);
                                reference.setValue(updatedUserInfo);
                                reference.removeEventListener(this);

                                FirebaseDatabase.getInstance().getReference("OrdersTable")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(Integer.toString(updatedNumOrders))
                                        .setValue(orders).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            Toast.makeText(SetLocation.this, "Order Loaded Successfully", Toast.LENGTH_SHORT).show();
                                            Intent order_done = new Intent(SetLocation.this, Final.class);
                                            startActivity(order_done);
                                        }
                                        else{
                                            Toast.makeText(SetLocation.this, "Unable To Load Order", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        };
                        reference.addValueEventListener(listener);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });*/

    }
    @Override
    public void onBackPressed() {

    }
}
