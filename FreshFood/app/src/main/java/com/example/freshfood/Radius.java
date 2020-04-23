package com.example.freshfood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class Radius extends AppCompatActivity {

    Button pref, noPref, back;
    TextView radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radius);

        pref = findViewById(R.id.btnPreference);
        noPref = findViewById(R.id.btnNoPreference);
        back = findViewById(R.id.btnBackToDeliveryHome);
        radius = findViewById(R.id.txtRadius);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToDeliveryHome = new Intent(Radius.this, DeliveryHome.class);
                startActivity(backToDeliveryHome);
            }
        });

        pref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radius.getText().toString().matches("")) {
                    Toast.makeText(Radius.this, "Enter a radius of coverage", Toast.LENGTH_SHORT).show();
                    radius.requestFocus();
                }
                else {
                    double rad = Double.parseDouble(radius.getText().toString());
                    Intent toAssignOrder1 = new Intent(Radius.this, AssignOrder.class);
                    toAssignOrder1.putExtra("Radius", rad);
                    startActivity(toAssignOrder1);
                }
            }
        });

        noPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toAssignOrder2 = new Intent(Radius.this, AssignOrder.class);
                toAssignOrder2.putExtra("Radius", -1.0);
                startActivity(toAssignOrder2);
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
