package com.example.freshfood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.Login;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SignIn extends AppCompatActivity {

    EditText emailId, password;
    Button btnSignIn, facebookSignIn;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private CallbackManager mCallbackManager;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.email_sign_in);
        password = findViewById(R.id.pwd_sign_in);
        btnSignIn = findViewById(R.id.button_sign_in);
        facebookSignIn = findViewById(R.id.fb_login_button);
        mCallbackManager = CallbackManager.Factory.create();
        mFirebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.signInProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        if(AccessToken.getCurrentAccessToken() != null) {
            Toast.makeText(SignIn.this, "Access token already exists",Toast.LENGTH_SHORT).show();
            handleFacebookAccessToken(AccessToken.getCurrentAccessToken());
        }

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if(mFirebaseUser != null){
                    Toast.makeText(SignIn.this, "You are signed in", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(SignIn.this, Menu.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(SignIn.this, "Please log in", Toast.LENGTH_SHORT).show();
                }
            }
        };

        facebookSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_VIEW_ONLY).logInWithReadPermissions(SignIn.this, Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //mDialog.setMessage("Loggin in...");
                        //mDialog.show();
                        Toast.makeText(SignIn.this, "Inside in success",Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "Inside Call Back");
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(SignIn.this, "FB Sign In Cancelled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(SignIn.this, "FB Sign In Error", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                if(email.isEmpty()){
                    emailId.setError("Enter an email id");
                    emailId.requestFocus();
                }
                else if(pwd.isEmpty()){
                    password.setError("Please enter a password");
                    password.requestFocus();
                }
                else if(!(email.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(SignIn.this, "Unable To Sign In Now",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                updateUI();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(SignIn.this, "Error",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void handleFacebookAccessToken(AccessToken token) {
        progressBar.setVisibility(View.VISIBLE);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        Log.d("TAG", "Inside handle fb access token");
        Toast.makeText(SignIn.this, "Inside handle fb access token",Toast.LENGTH_SHORT).show();
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    final FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(user.getUid())) {
                                //mDialog.dismiss();
                                progressBar.setVisibility(View.INVISIBLE);
                                updateUI();
                            }
                            else {
                                //mDialog.dismiss();
                                progressBar.setVisibility(View.INVISIBLE);
                                Intent addlDetails = new Intent(SignIn.this, AdditionalDetails.class);
                                startActivity(addlDetails);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                else {
                    Toast.makeText(SignIn.this, "Auth Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser != null) {
            updateUI();
        }
    }

    private void updateUI() {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try{
                            final UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                            if(userInfo.getCommunity().matches("Customer")) {
                                Intent toHome = new Intent(SignIn.this, Menu.class);
                                finish();
                                startActivity(toHome);
                            }
                            else {
                                if(userInfo.getCommunity().matches("Delivery")) {
                                    Intent toDeliveryHome = new Intent(SignIn.this, DeliveryHome.class);
                                    startActivity(toDeliveryHome);
                                }
                                else {
                                    final String timestamp = userInfo.getCommunity();
                                    final ArrayList<String> details = new ArrayList<String>();
                                    FirebaseDatabase.getInstance().getReference("TobeAssignedOrders")
                                            .child(timestamp).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            ToBeAssignedOrders order = dataSnapshot.getValue(ToBeAssignedOrders.class);
                                            details.add(order.getOrderId());
                                            details.add(address(order.getPickUpLatitude(), order.getPickUpLongitude()));
                                            details.add(address(order.getDropLatitude(), order.getDropLongitude()));
                                            details.add(order.getUserMobile());
                                            details.add(order.getUserName());
                                            details.add(Double.toString(order.getCost()));
                                            details.add(order.getOrderId().substring(order.getUserId().length()));
                                            details.add(userInfo.getMob_num());
                                            details.add(userInfo.getName());
                                            details.add(timestamp);
                                            details.add(order.getUserId());
                                            Intent toFinishDelivery = new Intent(SignIn.this, FinishOrder.class);
                                            toFinishDelivery.putStringArrayListExtra("Details", details);
                                            startActivity(toFinishDelivery);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        } catch (Exception e) {
                            Log.d("ERROR : ", e.toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private String address(double latitude, double longitude) {
        String address = "";
        Geocoder geocoder = new Geocoder(SignIn.this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            address = addressList.get(0).getAddressLine(0);
        } catch (IOException e) {
            Log.d("ERROR : ", e.toString());
        }
        return address;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", "Inside Avtivity Result");
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
