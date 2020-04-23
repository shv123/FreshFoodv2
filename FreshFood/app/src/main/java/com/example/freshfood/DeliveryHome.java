package com.example.freshfood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

public class DeliveryHome extends AppCompatActivity {

    Button takeOrder, signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_home);

        takeOrder = findViewById(R.id.btnTakeOrder);
        signOut = findViewById(R.id.btnDeliverySignOut);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        takeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rad = new Intent(DeliveryHome.this, Radius.class);
                startActivity(rad);
            }
        });
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();

        if(AccessToken.getCurrentAccessToken() != null) {
            Log.d("TAG", AccessToken.getCurrentAccessToken().toString());
            Toast.makeText(DeliveryHome.this, AccessToken.getCurrentAccessToken().toString(), Toast.LENGTH_LONG).show();
            LoginManager.getInstance().logOut();
        }
        else {
            Toast.makeText(DeliveryHome.this, "NULL", Toast.LENGTH_LONG).show();
        }

        /*if(LoginManager.getInstance() != null ) {
            LoginManager.getInstance().logOut();
        }*/
        finish();
        Intent toMain = new Intent(DeliveryHome.this, MainActivity.class);
        startActivity(toMain);
    }

    @Override
    public void onBackPressed() {

    }
}
