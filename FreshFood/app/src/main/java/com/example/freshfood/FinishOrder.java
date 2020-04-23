package com.example.freshfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FinishOrder extends AppCompatActivity {

    private static final int REQUEST_SMS_PERMISSION = 1;
    private static final int REQUEST_SMS_PERMISSION1 = 2;
    ArrayList<String> details = new ArrayList<>();
    TextView txtDetails;
    Button complete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_order);
        try {
            txtDetails = findViewById(R.id.txtDetails1);
            complete = findViewById(R.id.btnComplete);
            details = getIntent().getStringArrayListExtra("Details");
            showPhoneStatePermission();

            String toBeDisplayed = "Pick up : " + details.get(1) + "\n" +
                    "Drop : " + details.get(2) + "\n" +
                    "Customer Name" + details.get(4) + "\n" +
                    "Customer Phone Number" + details.get(3);

            txtDetails.setText(toBeDisplayed);
            complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPhoneStatePermission1();
                    FirebaseDatabase.getInstance().getReference("TobeAssignedOrders")
                            .child(details.get(9)).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseDatabase.getInstance().getReference("OrdersTable")
                                                .child(details.get(10)).child(details.get(6))
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        Orders order = dataSnapshot.getValue(Orders.class);
                                                        order.setStatus("Completed");

                                                        FirebaseDatabase.getInstance().getReference("OrdersTable")
                                                                .child(details.get(10)).child(details.get(6)).setValue(order)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {

                                                                            updateUsers();

                                                                        }
                                                                        else {
                                                                            Toast.makeText(FinishOrder.this, "Error 1", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                    }
                                    else {
                                        Toast.makeText(FinishOrder.this, "Error 2", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });

        } catch (Exception e) {
            Log.d("ERRO : ", e.toString());
        }
    }

    private void updateUsers() {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                        userInfo.setCommunity("Delivery");

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(FinishOrder.this, "DELIVERY COMPLETED!!!", Toast.LENGTH_LONG).show();
                                    Intent home = new Intent(FinishOrder.this, DeliveryHome.class);
                                    startActivity(home);
                                }
                                else {
                                    Toast.makeText(FinishOrder.this, "Error 3", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void sendArrivalSMS() {
        SmsManager smsManager = android.telephony.SmsManager.getDefault();
        String SENT = "SMS_SENT";
        String smsMessage = "Order ID : " + details.get(0) + "has arrived " + "\n" +
                "Total amount is " + details.get(5) + "\n" +
                "Thank you";
        try {
            smsManager.sendTextMessage(details.get(3).trim(), null, smsMessage, null, null);
            Toast.makeText(FinishOrder.this, "Sending Arrival SMS...", Toast.LENGTH_SHORT).show();
        }catch (Exception e) {
            e.printStackTrace();
            Log.d("Tag", e.toString());
            Toast.makeText(FinishOrder.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void showPhoneStatePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(FinishOrder.this, Manifest.permission.READ_PHONE_STATE);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(FinishOrder.this, Manifest.permission.READ_PHONE_STATE)) {
                Toast.makeText(FinishOrder.this, "Permission not granted to send SMS", Toast.LENGTH_SHORT).show();
            }
            else {
                requestPermission(Manifest.permission.READ_PHONE_STATE, REQUEST_SMS_PERMISSION);
            }
        }
        else {
            //Toast.makeText(FinishOrder.this, "Permission already exists", Toast.LENGTH_SHORT).show();
            sendSMS();
        }
    }

    private void showPhoneStatePermission1() {
        int permissionCheck = ContextCompat.checkSelfPermission(FinishOrder.this, Manifest.permission.READ_PHONE_STATE);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(FinishOrder.this, Manifest.permission.READ_PHONE_STATE)) {
                Toast.makeText(FinishOrder.this, "Permission not granted to send SMS", Toast.LENGTH_SHORT).show();
            }
            else {
                requestPermission(Manifest.permission.READ_PHONE_STATE, REQUEST_SMS_PERMISSION1);
            }
        }
        else {
            //Toast.makeText(FinishOrder.this, "Permission already exists", Toast.LENGTH_SHORT).show();
            sendArrivalSMS();
        }
    }

    private void requestPermission(String permissionName, int permissionCode) {
        ActivityCompat.requestPermissions(FinishOrder.this, new String[]{permissionName}, permissionCode);
    }

    private void sendSMS() {
        SmsManager smsManager = android.telephony.SmsManager.getDefault();
        String smsMessage = "Order ID : " + details.get(0) + " is going to be delivered by " +
                details.get(8) + ".\n" +
                "Delivery Agent Phone : " + details.get(7);
        try {
            smsManager.sendTextMessage(details.get(3).trim(), null, smsMessage, null, null);
            Toast.makeText(FinishOrder.this, "Sending Delivery Start SMS...", Toast.LENGTH_SHORT).show();
        }catch (Exception e) {
            e.printStackTrace();
            Log.d("Tag", e.toString());
            Toast.makeText(FinishOrder.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_SMS_PERMISSION : if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(FinishOrder.this, "SMS Permission Obtained!", Toast.LENGTH_SHORT).show();
                sendSMS();
            }
            else {
                Toast.makeText(FinishOrder.this, "Permission Denied To Send Confirmation SMS", Toast.LENGTH_SHORT).show();
            }
                break;

            case REQUEST_SMS_PERMISSION1 : if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(FinishOrder.this, "SMS Permission Obtained!", Toast.LENGTH_SHORT).show();
                sendArrivalSMS();
            }
            else {
                Toast.makeText(FinishOrder.this, "Permission Denied To Send Confirmation SMS", Toast.LENGTH_SHORT).show();
            }
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }
}
