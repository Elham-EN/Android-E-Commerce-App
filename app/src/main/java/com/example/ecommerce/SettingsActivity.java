package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.net.URI;
import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView profileImageView;
    private EditText fullNameEditText, userPhoneEditText, addressEditText;
    private TextView profileChangeTextBtn, closeTextBtn, saveTextButton;
    private Uri imageUri;
    private String myUrl = ""; //will store image URL after we upload profile picture to firebase storage
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureRef;
    private String checker = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        profileImageView = (CircleImageView) findViewById(R.id.settings_profile_image);
        fullNameEditText = (EditText) findViewById(R.id.settings_full_name);
        userPhoneEditText = (EditText) findViewById(R.id.settings_phone_number);
        addressEditText = (EditText) findViewById(R.id.settings_address);
        profileChangeTextBtn = (TextView) findViewById(R.id.profile_image_change_btn);
        closeTextBtn = (TextView) findViewById(R.id.close_settings_btn);
        saveTextButton = (TextView) findViewById(R.id.update_account_settings_btn);

        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText, addressEditText);

        //if user click on close button
        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Call this when your activity is done and should be closed. The ActivityResult is pass on back to whoever
                launched you via onActivityResult().*/
                finish();
            }
        });

        //if user pressed the saveTextButton
        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checker.equals("clicked")){ //if user want to change profile picture
                    userInfoSaved();//allow user to update whole info of the settings activity e.g profile image & input text
                }
                else {
                    //but if user has already set the picture & only want to set all the text information
                    updateOnlyUserInfo(); //if the user click on the update button
                }
            }//end of onClick()
        });

        //if click on profile image change btn then will call userInfoSaved
        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";
                // start cropping activity for pre-acquired image saved on the device
                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);
            }
        });

    }//end of onCreate()

    //Starting another activity, whether one within your app or from another app and receive a result back
    // For example, your app can start a camera app and receive the captured photo as a result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data != null) {
            //Then get crop image activity result
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri(); //store in image Uri
            //Display it on imageView, so user can see which image it has selected
            profileImageView.setImageURI(imageUri);
        } else { //if in case of error
            Toast.makeText(this, "Error Try Again.", Toast.LENGTH_SHORT).show();
            //Refreshing the settings
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
        }
    }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(fullNameEditText.getText().toString())) {
            Toast.makeText(this, ":Name is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(addressEditText.getText().toString())) {
            Toast.makeText(this, ":Address is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(userPhoneEditText.getText().toString())) {
            Toast.makeText(this, ":PHone number is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if (checker.equals("clicked")) {
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null) { //pass my storage profile picture reference
            /*This phone num is the image name */
            final StorageReference fileRef = storageProfilePictureRef.child(Prevalent.currentOnlineUser.getPhone() + ".jpg");

            //Asynchronously uploads from a content URI to this StorageReference.
            uploadTask = fileRef.putFile(imageUri);//Uri representing the download URL.
            //Returns a new Task that will be completed with the result of applying the specified Continuation to this Task
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) { //if task is not successful
                        throw task.getException(); //throw an error
                    }
                    return fileRef.getDownloadUrl();//Asynchronously retrieves a long lived download URL with a revokable token.
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) { //Called when the Task completes.
                    if (task.isSuccessful()) {
                        //it is returning getDownloadUrl() result
                        Uri downloadUrl = task.getResult();
                        //now convert this downloadUrl which is of type Uri
                        myUrl = downloadUrl.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("name", fullNameEditText.getText().toString());
                        userMap.put("address", addressEditText.getText().toString());
                        userMap.put("phoneOrder", userPhoneEditText.getText().toString());
                        userMap.put("image", myUrl);
                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(SettingsActivity.this, HomeActivity.class) );
                        Toast.makeText(SettingsActivity.this, "Profile info update successful", Toast.LENGTH_SHORT);
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT);
                    }
                }
            });
        } //end of if statement
        else {
            Toast.makeText(SettingsActivity.this, "Image is not selected", Toast.LENGTH_SHORT);
        }
    }//end of uploadImage()

    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", fullNameEditText.getText().toString());
        userMap.put("address", addressEditText.getText().toString());
        userMap.put("phoneOrder", userPhoneEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(SettingsActivity.this, HomeActivity.class) );
        Toast.makeText(SettingsActivity.this, "Profile info update successful", Toast.LENGTH_SHORT);
        finish();
    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEditText) {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        //Each time time the data changes, your listener will be called with an immutable snapshot of the data
        UsersRef.addValueEventListener(new ValueEventListener() {
            //will be called with a snapshot of the data at this location. It will also be called each time that data changes
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //If that user exists, because we are storing the info of users in the DB for each user by their unique phone number
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("image").exists()) {
                        //if image exists then we will fetch and display the information of user on the settingsActivity
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        addressEditText.setText(address);
                    }
                }
            }//end of onChange()
            /*will be triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.*/
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }//end of onCancelled()
        });
    }//end of userInfoDisplay()
}
