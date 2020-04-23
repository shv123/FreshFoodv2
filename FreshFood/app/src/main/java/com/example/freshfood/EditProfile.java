package com.example.freshfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfile extends AppCompatActivity {

    Button update, cancel;
    EditText name, phone, pwd, confirmPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        update = findViewById(R.id.buttonUpdate);
        cancel = findViewById(R.id.buttonCancel);
        name = findViewById(R.id.editName);
        phone = findViewById(R.id.editPhoneNum);
        pwd = findViewById(R.id.editPassword);
        confirmPwd = findViewById(R.id.confirmPassword);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(EditProfile.this, Menu.class);
                startActivity(home);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String updatedName = name.getText().toString();
                final String updatedNum = phone.getText().toString();
                final String pwd1 = pwd.getText().toString();
                String pwd2 = confirmPwd.getText().toString();

                if(updatedName.matches("")) {
                    Toast.makeText(EditProfile.this, "Enter Your Name", Toast.LENGTH_SHORT).show();
                    name.requestFocus();
                }
                else if(updatedNum.length() != 10) {
                    Toast.makeText(EditProfile.this, "Enter A Valid Mobile Phone Number", Toast.LENGTH_SHORT).show();
                    phone.requestFocus();
                }
                else if(!pwd1.matches(pwd2)) {
                    Toast.makeText(EditProfile.this, "Passwords Must Match", Toast.LENGTH_SHORT).show();
                    pwd.requestFocus();
                }
                else if(!updatedName.matches("") && updatedNum.length() == 10 && pwd1.matches(pwd2)) {
                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    final ValueEventListener listener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            UserInfo userInfo = new UserInfo();
                            try {
                                userInfo = dataSnapshot.getValue(UserInfo.class);
                            } catch (Exception e) {
                                Toast.makeText(EditProfile.this, "Inside Listener", Toast.LENGTH_LONG).show();
                                Log.d("mytag", e.toString());
                            }
                            String name = userInfo.getName();
                            String mob_num = userInfo.getMob_num();
                            String email = userInfo.getEmail();
                            int numOrders = userInfo.getNum_orders();
                            String community = userInfo.getCommunity();
                            UserInfo updatedUserInfo = new UserInfo(updatedName, updatedNum, email, community, numOrders);
                            reference.setValue(updatedUserInfo);
                            Toast.makeText(EditProfile.this, "Profile Details Updated", Toast.LENGTH_SHORT).show();
                            reference.removeEventListener(this);

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                user.updatePassword(pwd1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(EditProfile.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                                            Intent backHome1 = new Intent(EditProfile.this, Menu.class);
                                            startActivity(backHome1);
                                        } else {
                                            Toast.makeText(EditProfile.this, "Failed To Update Password", Toast.LENGTH_SHORT).show();
                                            Intent backHome2 = new Intent(EditProfile.this, Menu.class);
                                            startActivity(backHome2);
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(EditProfile.this, "User Not Found", Toast.LENGTH_SHORT).show();
                                Intent backHome3 = new Intent(EditProfile.this, Menu.class);
                                startActivity(backHome3);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };
                    reference.addValueEventListener(listener);
                }
            }
        });
    }
}
