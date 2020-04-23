package com.example.freshfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class AdditionalDetails extends AppCompatActivity {

    EditText emailId, userName, phoneNum;
    Button btnContinue;
    RadioGroup radioGrp;
    RadioButton radioBtn;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_details);

        emailId = findViewById(R.id.emailFBSignIn);
        userName = findViewById(R.id.nameFBSignIn);
        phoneNum = findViewById(R.id.phoneNumFBSignIn);
        btnContinue = findViewById(R.id.buttonContinueFBSignIn);
        radioGrp = findViewById(R.id.radio_grpFBSignIn);
        mFirebaseAuth = FirebaseAuth.getInstance();

        Profile profile = Profile.getCurrentProfile();
        userName.setText(profile.getFirstName() + profile.getLastName());

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnContinue.setEnabled(false);
                final String email = emailId.getText().toString();
                final String name = userName.getText().toString();
                final String phone = phoneNum.getText().toString();
                int radio_id = radioGrp.getCheckedRadioButtonId();
                radioBtn = findViewById(radio_id);
                final String community = radioBtn.getText().toString();

                if(email.isEmpty()){
                    emailId.setError("Enter an email id");
                    emailId.requestFocus();
                }
                else if(phone.length() != 10) {
                    Toast.makeText(AdditionalDetails.this, "Please Enter A Valid Mobile Phone Number", Toast.LENGTH_SHORT).show();
                    phoneNum.requestFocus();
                }
                else if(community.matches("")) {
                    Toast.makeText(AdditionalDetails.this, "Please Select A Community", Toast.LENGTH_SHORT).show();
                    radioGrp.requestFocus();
                }
                else if(!email.matches("") && !phone.matches("") && !community.matches("")) {
                    UserInfo userInfo = new UserInfo(name, phone, email, community, 0);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                btnContinue.setEnabled(true);
                                Toast.makeText(AdditionalDetails.this, "Details Recorded Successfully", Toast.LENGTH_SHORT).show();
                                if(community.matches("Customer")) {
                                    Intent toMenu = new Intent(AdditionalDetails.this, Menu.class);
                                    startActivity(toMenu);
                                }
                                else {
                                    Intent toDeliveryHome = new Intent(AdditionalDetails.this, DeliveryHome.class);
                                    startActivity(toDeliveryHome);
                                }
                            }
                            else {
                                Toast.makeText(AdditionalDetails.this, "Error", Toast.LENGTH_SHORT).show();
                                btnContinue.setEnabled(true);
                            }
                        }
                    });
                }
            }
        });
    }
}
