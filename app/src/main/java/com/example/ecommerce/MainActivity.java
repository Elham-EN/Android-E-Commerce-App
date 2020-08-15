package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ecommerce.Model.Users;
import com.example.ecommerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {//define Activity Java Class
    //variable declared
    private Button joinNowButton, loginButton;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //MainActivity being created/ initialization
        super.onCreate(savedInstanceState);//saves data only for this instance of activity during current session
        setContentView(R.layout.activity_main);//connect MainActivity with layout
        //value reference to xml id
        joinNowButton = (Button) findViewById(R.id.main_join_now_btn);
        loginButton = (Button) findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);

        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //Retrieve User Key
        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);
        if (UserPhoneKey != null && UserPasswordKey != null) { //if it is not empty, must contain value
            //utility functions to do operations on String objects
            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)) {
                AllowAccess(UserPhoneKey, UserPasswordKey);
                loadingBar.setTitle("Already Logged In");
                loadingBar.setMessage("Please wait....");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }
    }//end of onCreate() method
    private void AllowAccess(final String phone, final String password) {
        //Get Database root reference in order to access the Firebase Database
        final DatabaseReference RootRef; //cannot be overridden/modified
        RootRef = FirebaseDatabase.getInstance().getReference();

        //Going to retrieve either this user is available or not
        //Add a listener for a single change in the data at this location.
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            //This method will be called with a snapshot of the data at this location.
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //if the user phone exist in Users database in Firebase
                if (dataSnapshot.child("Users").child(phone).exists()) {
                    //Retrieve User info from firebase and pass the values to Users class
                    Users usersData = dataSnapshot.child("Users").child(phone).getValue(Users.class);
                    //check if user phone number is same as the phone number that was entered as input
                    if (usersData.getPhone().equals(phone)) { //if yes
                        if (usersData.getPassword().equals(password)) { //if yes
                            Toast.makeText(MainActivity.this, "Already logged in", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            Prevalent.currentOnlineUser = usersData;
                            startActivity(intent);
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();

                        }
                    }
                } else { //else the user does not exist in the Users database
                    Toast.makeText(MainActivity.this, "Account with this " + phone+ " does not exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }//end of onDataChange()

            @Override
            //This method will be triggered in the event that this listener either failed at the server,
            // or is removed as a result of the security and Firebase Database rules.
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }//end of onCancelled()
        });//end of addListenerForSingleValueEvent
    }
}//end of MainActivity class
