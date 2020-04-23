package com.example.freshfood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class Foods extends AppCompatActivity {

    EditText qty1, qty2, qty3;
    Button place_order;

    //HashMap<String, Integer> cart = new HashMap<String, Integer>();
    //int[] cart = new int[4]; NOT SERIALIZABLE => CANNOT BE PUT INTO THE DB
    ArrayList<Integer> cart = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foods);

        qty1 = findViewById(R.id.qtyFood1);
        qty2 = findViewById(R.id.qtyFood2);
        qty3 = findViewById(R.id.qtyFood3);
        place_order = findViewById(R.id.btnPlaceOrder);

        place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp = qty1.getText().toString();
                if(tmp.matches("")){
                    cart.add(0);
                }
                else{
                    cart.add(Integer.parseInt(tmp));
                }

                tmp = qty2.getText().toString();
                if(tmp.matches("")){
                    //cart.put("Food2", 0);
                    cart.add(0);
                }
                else{
                    cart.add(Integer.parseInt(tmp));
                }

                tmp = qty3.getText().toString();
                if(tmp.matches("")){
                    cart.add(0);
                }
                else{
                    cart.add(Integer.parseInt(tmp));
                }

                Intent transfer = new Intent(Foods.this, Cart.class);
                transfer.putExtra("Orders", cart);
                startActivity(transfer);
            }
        });
    }

    public void toMenu(View view){
        Intent back = new Intent(Foods.this, Menu.class);
        startActivity(back);
    }

    @Override
    public void onBackPressed() {

    }
}
