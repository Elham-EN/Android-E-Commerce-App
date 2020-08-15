package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity
{
    //Declared Variables
    private Button CreateAccountButton;
    private EditText InputName, InputPhoneNumber, InputPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Initialize
        //Reference value to Resources ID (basically in the XML Design)
        CreateAccountButton = (Button) findViewById(R.id.register_btn);
        InputName = (EditText) findViewById(R.id.register_username_input);
        InputPhoneNumber = (EditText) findViewById(R.id.register_phone_num_input);
        InputPassword = (EditText) findViewById(R.id.register_password_input);
        //this refer to this class that extends from context
        loadingBar = new ProgressDialog(this);

        //When user clicked on button, then execute function CreateAccount()
        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount()
    {
        //Get inputs
        String name = InputName.getText().toString();
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();
        //check if any og these value are empty
        //TextUtils - a set of utility functions to do operations on String objects
        if (TextUtils.isEmpty(name))
        {
            // toast provides simple feedback about an operation in a small popup.
            Toast.makeText(this, "Please enter your name!", Toast.LENGTH_SHORT ).show();
        }
        else if (TextUtils.isEmpty(phone))
        {
            // toast provides simple feedback about an operation in a small popup.
            Toast.makeText(this, "Please enter your mobile number!", Toast.LENGTH_SHORT ).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            // toast provides simple feedback about an operation in a small popup.
            Toast.makeText(this, "Please enter your password!", Toast.LENGTH_SHORT ).show();
        }
        else {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            //so the user click on the screen then this dialog will not disappear until complete the process
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            validatePhoneNumber(name, phone, password);
        }
    }
    public void validatePhoneNumber(final String name, final String phone, final String password)
    {
        //represents a particular location in your Database and can be used for reading or writing data to that Database location.
        final DatabaseReference RootRef;
        //getInstance()-Gets the default FirebaseDatabase instance. getRef()-Gets a DatabaseReference for the database root node
        RootRef = FirebaseDatabase.getInstance().getReference();

        /*DataSnapshot instance contains data from a Firebase Database location. Any time you read Database data, you receive the
        data as a DataSnapshot. DataSnapshots are passed to the methods in listeners that you attach Add a listener for a single
        change in the data at this location*/
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            /*read a static snapshot of the contents at a given path, as they existed at the time of the event. This method is triggered
            once when the listener is attached and again every time the data, including children, changes*/
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //if this phone number not exist then create an account
                if (!(dataSnapshot.child("Users").child(phone).exists()))
                {
                    //store items in "key/value" pairs. add items to array collection
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("phone", phone);
                    userDataMap.put("password", password);
                    userDataMap.put("name", name);
                    //Create parent node for all the users in the Database
                    RootRef.child("Users").child(phone).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Congratulations your account has been created", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    } else { //what if account was not created at that time (e.g internet issue)
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Network Error: Please try again after some time", Toast.LENGTH_SHORT).show();
                                    }//end if else statement
                                }//end of onComplete()
                            });//end of addOnCompleteListener()
                } else { //if this phone number exist
                    Toast.makeText(RegisterActivity.this, "This " + phone + " already exist", Toast.LENGTH_SHORT ).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please try again using another phone number", Toast.LENGTH_SHORT ).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                } //if else statement
            }//end of onDataChange()

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }//end of onCancelled()
        });//end of addListenerForSingleValueEvent()
    }//end of validatePhoneNumber()
}//End of class RegisterActivity
