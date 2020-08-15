package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Model.Users;
import com.example.ecommerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    //Declared Variables
    private EditText InputPhoneNumber, InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private String parent_DB_Name = "Users";
    private CheckBox chkBoxRememberMe;
    private TextView AdminLink, NotAdminLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Reference to Resources id and Initialization
        LoginButton = (Button) findViewById(R.id.login_btn);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        InputPhoneNumber = (EditText) findViewById(R.id.login_phone_num_input);
        AdminLink = (TextView) findViewById(R.id.admin_panel_link);
        NotAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);
        loadingBar = new ProgressDialog(this);

        chkBoxRememberMe = (CheckBox) findViewById(R.id.remember_me_ckkb);
        Paper.init(this); //Should be initialized one time in onCreate() in Application or Activity.

        //When user clicked on the login button
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }//end of onClick()
        });//end of setOnClickListener (LoginButton)

        //when clicked admin link change DB to Admins and login as admin
        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButton.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parent_DB_Name = "Admins";
            }
        });//end of setOnClickListener (AdminLink)

        //when clicked  not admin link change DB back to Users and login as user
        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parent_DB_Name = "Users";
            }
        });//end of setOnClickListener (AdminLink)

    }//end of onCreate()

    private  void LoginUser() {
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while checking for the credential info");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(phone, password);
        }
    }//end of LoginUser()
    //The Query class (and its subclass, DatabaseReference) are used for reading data. Listeners
    // are attached, and they will be triggered when the corresponding data changes.
    private void AllowAccessToAccount(final String phone, final String password) {
        if (chkBoxRememberMe.isChecked()) { //if checkbox is checked then return true value
            //When ever user login, then it pass phone number and password and store it to the phone memory
            Paper.book().write(Prevalent.UserPhoneKey, phone); //Save data object in Prevalent class variable
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }
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
                if (dataSnapshot.child(parent_DB_Name).child(phone).exists()) {
                    //Retrieve User info from firebase and pass the values to Users class
                    Users usersData = dataSnapshot.child(parent_DB_Name).child(phone).getValue(Users.class);
                    //check if user phone number is same as the phone number that was entered as input
                    if (usersData.getPhone().equals(phone)) { //if yes
                        if (usersData.getPassword().equals(password)) { //if yes
                            if (parent_DB_Name.equals("Admins")) {
                                Toast.makeText(LoginActivity.this, "Welcome Admin,   you are Logged in Successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);
                            }
                            else if (parent_DB_Name.equals("Users")) { //if the login is successful
                                Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);
                            }
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();

                        }
                    }
                } else { //else the user does not exist in the Users database
                    Toast.makeText(LoginActivity.this, "Account with this " + phone+ " does not exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }//end of onDataChange()

            @Override
            //This method will be triggered in the event that this listener either failed at the server,
            // or is removed as a result of the security and Firebase Database rules.
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }//end of onCancelled()
        });//end of addListenerForSingleValueEvent
    }//end of AllowAccessToAccount()

}//end of class LoginActivity
