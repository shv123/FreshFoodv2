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

public class Menu extends AppCompatActivity {

    Button btnLogOut;
    Button btnCurrentOrders;
    Button btnCompletedOrders;
    Button editProfile;
    //FirebaseAuth mFireBaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnLogOut = findViewById(R.id.button_log_out);
        btnCurrentOrders = findViewById(R.id.buttonCurrentOrders);
        btnCompletedOrders = findViewById(R.id.buttonCompletedOrders);
        editProfile = findViewById(R.id.btnEditProfile);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        btnCurrentOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent currentOrders = new Intent(Menu.this, CurrentOrders.class);
                startActivity(currentOrders);
            }
        });

        btnCompletedOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent completedOrders = new Intent(Menu.this, CompletedOrders.class);
                startActivity(completedOrders);
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit = new Intent(Menu.this, EditProfile.class);
                startActivity(edit);
            }
        });

    }

    @Override
    public void onBackPressed() {

    }

    public void openItemsPage(View view){
        Intent food = new Intent(Menu.this, Foods.class);
        startActivity(food);
    }

  /*  @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            logOut();
        }
    }*/

    private void logOut() {
        FirebaseAuth.getInstance().signOut();

        if(AccessToken.getCurrentAccessToken() != null) {
            Log.d("TAG", AccessToken.getCurrentAccessToken().toString());
            //Toast.makeText(Menu.this, AccessToken.getCurrentAccessToken().toString(), Toast.LENGTH_LONG).show();
            LoginManager.getInstance().logOut();
        }
        else {
            //Toast.makeText(Menu.this, "NULL", Toast.LENGTH_LONG).show();
        }

        /*if(LoginManager.getInstance() != null ) {
            LoginManager.getInstance().logOut();
        }*/
        finish();
        Intent toMain = new Intent(Menu.this, MainActivity.class);
        startActivity(toMain);
    }
}
