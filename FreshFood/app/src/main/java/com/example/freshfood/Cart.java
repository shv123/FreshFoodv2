package com.example.freshfood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class Cart extends AppCompatActivity {

    //HashMap<String, Integer> cart = new HashMap<String, Integer>();
    //int[] cart = new int[4];
    ArrayList<Integer> cart = new ArrayList<Integer>();
    boolean empty = true;
    TextView orders;
    Button confirm, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        orders = findViewById(R.id.textView_orders);
        cart = getIntent().getIntegerArrayListExtra("Orders");
        confirm = findViewById(R.id.btn_confirm);
        cancel = findViewById(R.id.btn_cancel);

        for(Integer i : cart){
            if(i != 0){
                empty = false;
                break;
            }
        }

        if(empty){
            orders.setText("YOUR CART IS EMPTY");
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent back = new Intent(Cart.this, Foods.class);
                    startActivity(back);
                }
            });
        }
        else{
            String display = "";
            String pkgSize = "";
            int sizeNum = 1;
            for(Integer i : cart){
                if(i == 0){
                    sizeNum += 1;
                    continue;
                }
                switch(sizeNum){
                    case 1 : pkgSize = "SMALL";
                                break;
                    case 2 : pkgSize = "MEDIUM";
                                break;
                    case 3 : pkgSize = "LARGE";
                                break;
                }
                display += pkgSize + "     Quantity : " + Integer.toString(i) + "\n";
                sizeNum += 1;
            }
            orders.setText(display);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent back = new Intent(Cart.this, Foods.class);
                    startActivity(back);
                }
            });

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent confirm_order = new Intent(Cart.this, Address_.class);
                    confirm_order.putExtra("Orders", cart);
                    startActivity(confirm_order);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {

    }
}
