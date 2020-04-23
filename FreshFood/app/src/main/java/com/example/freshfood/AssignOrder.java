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
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Locale;

public class AssignOrder extends AppCompatActivity {

    double radius;
    TextView deliveryDetails;
    Button acceptDelivery, backHome;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    double currLatitude;
    double currLongitude;
    String timestamp;
    double distance;
    String userID;
    //int userOrderNum;
    ArrayList<String> details = new ArrayList<String>(); //OrderID, PickUpAdd, DropAdd, UserPhone, UserName, cost, currOrderNum, DeliveryPhone, deliveryName, timestamp, userID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_order);

        radius = (double)getIntent().getDoubleExtra("Radius", -1.0);
        deliveryDetails = findViewById(R.id.txtDeliveryDetails);
        acceptDelivery = findViewById(R.id.btnAcceptDelivery);
        backHome = findViewById(R.id.btnBackToDeliveryHome1);

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backHome = new Intent(AssignOrder.this, DeliveryHome.class);
                startActivity(backHome);
            }
        });

        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    AssignOrder.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION
            );

        } else {
            getCurrentLocation();
        }

        FirebaseDatabase.getInstance().getReference("TobeAssignedOrders")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ToBeAssignedOrders order = snapshot.getValue(ToBeAssignedOrders.class);
                            double pickUpLatitude = order.getPickUpLatitude();
                            double pickupLongitude = order.getPickUpLongitude();
                            double dropLatitude = order.getDropLatitude();
                            double dropLongitude = order.getDropLongitude();
                            double dist = distance(pickUpLatitude, pickupLongitude, currLatitude, currLongitude);

                            String status = order.getStatus();
                            if((radius == -1 || dist <= radius) && status.matches("Not Assigned")) {
                                String pickUpAddress = getAddress(pickUpLatitude, pickupLongitude);
                                String dropAddress = getAddress(dropLatitude, dropLongitude);
                                //Toast.makeText(AssignOrder.this, pickUpAddress, Toast.LENGTH_SHORT).show();
                                if(!pickUpAddress.matches("") && !dropAddress.matches("")) {
                                    deliveryDetails.setText("Pick Up Location :\n" + pickUpAddress
                                            + "\nDrop Location :" + "\n" + dropAddress);

                                    userID = order.getUserId();
                                    timestamp = snapshot.getKey();
                                    //Toast.makeText(AssignOrder.this, timestamp, Toast.LENGTH_SHORT).show();
                                    distance = distance(pickUpLatitude, pickupLongitude, dropLatitude, dropLongitude);
                                    details.add(order.getOrderId());
                                    details.add(pickUpAddress);
                                    details.add(dropAddress);
                                    details.add(order.getUserMobile());
                                    details.add(order.getUserName());
                                    break;
                                }
                            }
                        }
                        noMatches();
                        Toast.makeText(AssignOrder.this, "end.....", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(AssignOrder.this, DeliveryHome.class);
                startActivity(home);
            }
        });

        acceptDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("HERE", "INSIDE BTN");
                final String deliveryAgentID = FirebaseAuth.getInstance().getCurrentUser().getUid();


                FirebaseDatabase.getInstance().getReference("TobeAssignedOrders")
                        .child(timestamp)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final ToBeAssignedOrders assignedOrder = dataSnapshot.getValue(ToBeAssignedOrders.class);
                                ArrayList<Integer> items = new ArrayList<>();
                                try {
                                    items = assignedOrder.getCart();
                                } catch (Exception e) {
                                    Log.d("ERROR", e.toString());
                                }

                                if (assignedOrder.getStatus().matches("Ongoing")) {
                                    Toast.makeText(AssignOrder.this, "This delivery is taken \n" +
                                            "Please try again...", Toast.LENGTH_SHORT).show();
                                    Intent toHome = new Intent(AssignOrder.this, DeliveryHome.class);
                                    finish();
                                    startActivity(toHome);
                                    FirebaseDatabase.getInstance().getReference("TobeAssignedOrders").child(timestamp).removeEventListener(this);
                                }
                                else {
                                double cost = (10 * items.get(0)) + (15 * items.get(1)) + (20 * items.get(2)) + (17 * distance);
                                details.add(Double.toString(cost));
                                ToBeAssignedOrders updatedOrder = new ToBeAssignedOrders(assignedOrder.getOrderId(), assignedOrder.getUserId(),
                                        assignedOrder.getPickUpLatitude(), assignedOrder.getPickUpLongitude(), assignedOrder.getDropLatitude(),
                                        assignedOrder.getDropLongitude(), assignedOrder.getUserMobile(), assignedOrder.getUserName(),
                                        assignedOrder.getCart(), "Ongoing", deliveryAgentID, cost);

                                FirebaseDatabase.getInstance().getReference("TobeAssignedOrders")
                                        .child(timestamp).setValue(updatedOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //updateUsersTable(deliveryAgentID);
                                            Toast.makeText(AssignOrder.this, "Delivery Started!!!", Toast.LENGTH_SHORT).show();
                                            Log.d("TTAAGG", "Delivery Started");


                                            final int assignedOrderNum = Integer.parseInt(details.get(0).substring(userID.length()));
                                            Log.d("AssignedOrderNum", assignedOrder.toString());
                                            details.add(Integer.toString(assignedOrderNum));
                                            FirebaseDatabase.getInstance().getReference("OrdersTable")
                                                    .child(userID).child(Integer.toString(assignedOrderNum))
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            Orders order = dataSnapshot.getValue(Orders.class);
                                                            order.setStatus("Ongoing");


                                                            FirebaseDatabase.getInstance().getReference("OrdersTable").child(userID)
                                                                    .child(Integer.toString(assignedOrderNum)).setValue(order)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                updateUsersTable(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                                //details.add(userID);
                                                                            } else {
                                                                                Toast.makeText(AssignOrder.this, "Updating Orders Table Unsuccessful", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            Toast.makeText(AssignOrder.this, "Cancelled", Toast.LENGTH_SHORT);
                                                        }
                                                    });

                                        } else {
                                            Toast.makeText(AssignOrder.this, "Could not assign order", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                        });
            }
        });
    }



    private void updateUsersTable(final String deliveryAgentID) {
        Log.d("Inside", "updateDB");
        FirebaseDatabase.getInstance().getReference("Users")
                .child(deliveryAgentID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                        if(userInfo != null) {
                            details.add(userInfo.getMob_num());
                            details.add(userInfo.getName());
                        }
                        else {
                            Toast.makeText(AssignOrder.this, "NULL PTR", Toast.LENGTH_SHORT).show();
                            Log.d("ERRR ", "user->NULL");
                            System.exit(0);
                        }
                        details.add(timestamp);
                        details.add(userID);
                        userInfo.setCommunity(timestamp);
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(deliveryAgentID).setValue(userInfo)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            Toast.makeText(AssignOrder.this, "Recorded Details in DB", Toast.LENGTH_SHORT).show();

                                            Log.d("BRFORE", details.toString());
                                            Intent i = new Intent(AssignOrder.this, FinishOrder.class);
                                            i.putExtra("Details", details);
                                            startActivity(i);
                                        }
                                        else {
                                            Toast.makeText(AssignOrder.this, "Failed to update user's table", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void noMatches() {
        if(deliveryDetails.getText().toString().matches("")) {
            Toast.makeText(AssignOrder.this, "No Available Orders Now. Try Again Later", Toast.LENGTH_SHORT).show();
            Intent deliveryHome = new Intent(AssignOrder.this, DeliveryHome.class);
            startActivity(deliveryHome);
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return(c * r);
    }

    private String getAddress(double latitude, double longitude) {
        String address = "";
        Geocoder geocoder = new Geocoder(AssignOrder.this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            address = addressList.get(0).getAddressLine(0);
        } catch (IOException e) {
            Log.d("ERROR : ", e.toString());
        }
        return address;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation (){
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(AssignOrder.this)
                .requestLocationUpdates(locationRequest, new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(AssignOrder.this)
                                .removeLocationUpdates(this);
                        if(locationRequest != null && locationResult.getLocations().size() > 0){
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            currLatitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            currLongitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLongitude();
                        }
                    }
                }, Looper.getMainLooper());
    }

    @Override
    public void onBackPressed() {

    }
}
