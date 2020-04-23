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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    EditText emailId, password, user_name, phoneNum;
    Button btnSignUp;
    RadioGroup radioGrp;
    RadioButton radioBtn;
    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase database;
    private DatabaseReference databaseref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.email_sign_up);
        password = findViewById(R.id.pwd_sign_up);
        btnSignUp = findViewById(R.id.button_sign_up);
        user_name = findViewById(R.id.name_sign_up);
        phoneNum = findViewById(R.id.phone_num_sign_up);
        radioGrp = findViewById(R.id.radio_grp);
        database = FirebaseDatabase.getInstance();
        databaseref = database.getReference("Users");

        btnSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final String email = emailId.getText().toString();
                final String pwd = password.getText().toString();
                final String name = user_name.getText().toString();
                final String phone = phoneNum.getText().toString();
                int radio_id = radioGrp.getCheckedRadioButtonId();
                radioBtn = findViewById(radio_id);
                final String community = radioBtn.getText().toString();

                if(email.isEmpty()){
                    emailId.setError("Enter an email id");
                    emailId.requestFocus();
                }
                else if(pwd.isEmpty()){
                    password.setError("Please enter a password");
                    password.requestFocus();
                }
                else if(!(email.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(SignUp.this, "Sign Up Unsuccessful, Try Again Later",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                //
                                UserInfo user_info = new UserInfo(name, phone, email, community, 0);
                                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                Toast.makeText(SignUp.this, "UID:"+uid,Toast.LENGTH_LONG).show();
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user_info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(SignUp.this, "SUCCESS!!!!",Toast.LENGTH_SHORT).show();
                                            if(community.matches("Customer")) {
                                                Intent to_menu = new Intent(SignUp.this, Menu.class);
                                                startActivity(to_menu);
                                            }
                                            else {
                                                Intent toDeliveryHome = new Intent(SignUp.this, DeliveryHome.class);
                                                startActivity(toDeliveryHome);
                                            }
                                        }
                                        else{
                                            Toast.makeText(SignUp.this, "NOT A SUCCESS!!!!",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                //
                                /*Toast.makeText(SignUp.this, "Account Created",Toast.LENGTH_SHORT).show();
                                mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            UserInfo user_info = new UserInfo(name, phone, community);
                                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            Toast.makeText(SignUp.this, "UID:"+uid,Toast.LENGTH_LONG).show();
                                            FirebaseDatabase.getInstance().getReference("Users")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .setValue(user_info);
                                            //Toast.makeText(SignUp.this, "Sign Up Successful",Toast.LENGTH_SHORT).show();
                                            Intent to_menu = new Intent(SignUp.this, Menu.class);
                                            startActivity(to_menu);
                                            /*FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user_info).addOnCompleteListener(SignUp.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(SignUp.this, "Sign Up Successful",Toast.LENGTH_SHORT).show();
                                                        Intent to_menu = new Intent(SignUp.this, Menu.class);
                                                        startActivity(to_menu);
                                                    }
                                                    else{
                                                        Toast.makeText(SignUp.this, "Sign Up Unsuccessful",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                        else{
                                            Toast.makeText(SignUp.this, "Not Again...",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });*/
                                /*UserInfo user_info = new UserInfo(name, phone, community);
                                //FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user_info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(SignUp.this, "Sign Up Successful",Toast.LENGTH_SHORT).show();
                                            Intent to_menu = new Intent(SignUp.this, Menu.class);
                                            startActivity(to_menu);
                                        }
                                        else{
                                            Toast.makeText(SignUp.this, "Sign Up Unsuccessful",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });*/
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(SignUp.this, "Error",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    }
