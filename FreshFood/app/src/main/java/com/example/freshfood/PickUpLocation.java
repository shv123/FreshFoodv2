package com.example.freshfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.telephony.SmsManager;
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
import java.util.List;
import java.util.Locale;

public class PickUpLocation extends AppCompatActivity {

    Button placeOrder;
    EditText pickUpLocation;
    Orders order;
    ToBeAssignedOrders toBeAssignedOrder = new ToBeAssignedOrders();
    String timestamp;
    String smsMessage;
    private static final int REQUEST_SMS_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up_location);

        pickUpLocation = findViewById(R.id.setPickUpAddress);
        placeOrder = findViewById(R.id.btnPickUpLocation);
        order = (Orders)getIntent().getSerializableExtra("Order");



        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pickUpAdd = pickUpLocation.getText().toString();
                if(pickUpAdd.matches("")){
                    Toast.makeText(PickUpLocation.this, "Please Enter a pick up location", Toast.LENGTH_SHORT).show();
                    pickUpLocation.requestFocus();
                }
                else{
                    Geocoder geocoder = new Geocoder(PickUpLocation.this, Locale.getDefault());
                    try {
                        List<Address> pickUpAddressList = geocoder.getFromLocationName(pickUpAdd, 1);

                        if(pickUpAddressList.size() > 0){
                            Address pickUpAddress = pickUpAddressList.get(0);

                            order.setPickUpLatitude(pickUpAddress.getLatitude());
                            order.setPickUpLongitude(pickUpAddress.getLongitude());
                            timestamp = Long.toString(System.currentTimeMillis()/1000);
                            order.setTimeStamp(timestamp);


                            final String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uId);
                            final ValueEventListener listener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    UserInfo userInfo = new UserInfo("name","mob", "email", "comm", 99);//dummy init
                                    int updatedNumOrders = -1;
                                    try {
                                        userInfo = dataSnapshot.getValue(UserInfo.class);
                                        updatedNumOrders = userInfo.getNum_orders() + 1;
                                    }catch (Exception e){
                                        Toast.makeText(PickUpLocation.this, "Inside Listener", Toast.LENGTH_LONG).show();
                                        Log.d("mytag", e.toString());
                                    }
                                    String name = userInfo.getName();
                                    String mob_num = userInfo.getMob_num();
                                    String email = userInfo.getEmail();
                                    String community = userInfo.getCommunity();
                                    UserInfo updatedUserInfo = new UserInfo(name, mob_num, email, community, updatedNumOrders);
                                    reference.setValue(updatedUserInfo);
                                    reference.removeEventListener(this);

                                    toBeAssignedOrder.setUserName(name);
                                    toBeAssignedOrder.setUserMobile(mob_num);
                                    toBeAssignedOrder.setOrderId(uId + Integer.toString(updatedNumOrders));
                                    toBeAssignedOrder.setUserId(uId);
                                    toBeAssignedOrder.setPickUpLatitude(order.getPickUpLatitude());
                                    toBeAssignedOrder.setPickUpLongitude(order.getPickUpLongitude());
                                    toBeAssignedOrder.setDropLatitude(order.getDropLatitude());
                                    toBeAssignedOrder.setDropLongitude(order.getDropLongitude());
                                    toBeAssignedOrder.setCart(order.getCart());
                                    toBeAssignedOrder.setStatus("Not Assigned");
                                    toBeAssignedOrder.setDeliveryAgentId("");
                                    toBeAssignedOrder.setCost(-1);

                                    FirebaseDatabase.getInstance().getReference("OrdersTable")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child(Integer.toString(updatedNumOrders))
                                            .setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(PickUpLocation.this, "Order Loaded Successfully", Toast.LENGTH_SHORT).show();
                                                saveOrder();
                                            }
                                            else{
                                                Toast.makeText(PickUpLocation.this, "Unable To Load Order", Toast.LENGTH_SHORT).show();
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
            }
        });
    }

    private void saveOrder() {
        FirebaseDatabase.getInstance().getReference("TobeAssignedOrders").child(timestamp)
                .setValue(toBeAssignedOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(PickUpLocation.this, "Order Will be Assigned Soon...", Toast.LENGTH_SHORT).show();
                    showPhoneStatePermission();
                    Intent order_done = new Intent(PickUpLocation.this, Final.class);
                    startActivity(order_done);
                }
                else {
                    Toast.makeText(PickUpLocation.this, "Unable To Load Order...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showPhoneStatePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(PickUpLocation.this, Manifest.permission.READ_PHONE_STATE);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(PickUpLocation.this, Manifest.permission.READ_PHONE_STATE)) {
                Toast.makeText(PickUpLocation.this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
            else {
                requestPermission(Manifest.permission.READ_PHONE_STATE, REQUEST_SMS_PERMISSION);
            }
        }
        else {
            //Toast.makeText(PickUpLocation.this, "Permission already exists", Toast.LENGTH_SHORT).show();
            sendSMS();
        }
    }

    private void requestPermission(String permissionName, int permissionCode) {
        ActivityCompat.requestPermissions(PickUpLocation.this, new String[]{permissionName}, permissionCode);
    }

    private void sendSMS() {
        SmsManager smsManager = android.telephony.SmsManager.getDefault();
        smsMessage = "Order ID : " + toBeAssignedOrder.getOrderId() + " is confirmed. " +
                "Delivery details will be sent shortly";
        try {
            smsManager.sendTextMessage(toBeAssignedOrder.getUserMobile().trim(), null, smsMessage, null, null);
            Toast.makeText(PickUpLocation.this, "Sending Confirmation SMS...", Toast.LENGTH_SHORT).show();
        }catch (Exception e) {
            e.printStackTrace();
            Log.d("Tag", e.toString());
            Toast.makeText(PickUpLocation.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_SMS_PERMISSION : if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(PickUpLocation.this, "SMS Permission Obtained!", Toast.LENGTH_SHORT).show();
                sendSMS();
            }
            else {
                Toast.makeText(PickUpLocation.this, "Permission Denied To Send Confirmation SMS", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {

    }
}
