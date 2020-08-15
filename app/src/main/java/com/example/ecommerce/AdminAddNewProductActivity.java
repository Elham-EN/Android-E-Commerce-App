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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {
    private String CategoryName, Description, Price, Pname, saveCurrentDate, saveCurrentTime;
    private Button AddNewProductButton;
    private ImageView InputProductImage;
    private EditText InputProductName, InputProductDescription, InputProductPrice;
    private  static final int GalleryPick = 1;
    private Uri ImageUri; //image link
    private String productRandomKey, downloadImageURL;
    private StorageReference ProductImagesRef; //storage reference to firebase storage, inside add all product images
    private DatabaseReference ProductsRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);
        //if user click on tshirt logo it will pass here category tshirt
        CategoryName = getIntent().getExtras().get("category").toString();
        //Create a reference to firebase storage
        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        //Create a reference to firebase Database
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        AddNewProductButton = (Button) findViewById(R.id.add_new_product_btn);
        InputProductImage = (ImageView) findViewById(R.id.select_product_image);
        InputProductName = (EditText) findViewById(R.id.product_name);
        InputProductDescription = (EditText) findViewById(R.id.product_description);
        InputProductPrice = (EditText) findViewById(R.id.product_price);
        loadingBar = new ProgressDialog(this);

        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery(); //open phone gallery app
            }
        });

        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });
    }//end of onCreate()

    //once admin select image for the product. once we have the image URL & store in the firebase storage
    private void OpenGallery() { //open phone gallery app
        Intent galleryIntent = new Intent(); //Intents are also used to transfer data between activities
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        //You can also start another activity and receive a result back
        startActivityForResult(galleryIntent, GalleryPick); //to get the result basically image URL
    }//end of OpenGallery()

    //get the image result and first store it first inside firebase storage
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == RESULT_OK && data !=null); {
            ImageUri = data.getData(); //get image information from the Gallery
            //Sets the content of this ImageView to the specified Uri. Note that you use this method to load images from a local Uri only.
            InputProductImage.setImageURI(ImageUri);//local Uri, ie a reference to a local disk file,
        }
    }//end of onActivityResult()

    private void ValidateProductData() {
        //get input value
        Description = InputProductDescription.getText().toString();
        Price = InputProductPrice.getText().toString();
        Pname = InputProductName.getText().toString();

        if (ImageUri == null ) { //if we do not select product image then
            Toast.makeText(this, "Product image is required", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Description)){
            Toast.makeText(this, "Please write product description", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Price)) {
            Toast.makeText(this, "Please write product price", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Pname)) {
            Toast.makeText(this, "Please write product name", Toast.LENGTH_SHORT).show();
        }
        else { //if everything is okay then store it to storage firebase
            StoreProductInformation();
        }
    }//end of ValidateProductDate()

     private void StoreProductInformation() {
        loadingBar.setTitle("Add New Product");
        loadingBar.setMessage("Please wait, while we are adding the new product");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
         //at which time admin is going to add new product so that we can display time of product to users
        Calendar calendar = Calendar.getInstance(); //getInstance() will give back the instance of that particular class
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM, dd, yyyy");
        //Returns a Date object representing this Calendar's time value
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a"); //a- get am & pm
        saveCurrentTime = currentTime.format(calendar.getTime());

        /*whenever the admin add a new product then this will be a unique random key and it will not be refutable because it contains
        * these minutes and seconds which will never repeat. (Refutable-To prove to be false or erroneous)*/
        productRandomKey = saveCurrentDate + saveCurrentTime;

        //image uri or product image inside firebase storage. Then we will be able to store the link of that image
         // in the firebase database and will display it to the users.
         //getLastPathSegment() - get me the ID, which appears at the end of the Uri example cat.jpg
         final StorageReference filePath = ProductImagesRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");
         //You can upload local files on the device, such as photos and videos from the camera, with the putFile() method.
         final UploadTask uploadTask = filePath.putFile(ImageUri);

         //in case if any failure occur (Listener called when a Task fails with an exception)
         uploadTask.addOnFailureListener(new OnFailureListener() {
             @Override //was not able to upload image successfully
             public void onFailure(@NonNull Exception e) {
                 String message = e.toString();
                 //Display this exception to user that why image is not going to upload & which errors occur
                 Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                 loadingBar.dismiss();
             }
         }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
             @Override //Image uploaded successfully
             public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                 Toast.makeText(AdminAddNewProductActivity.this, "Image Uploaded successfully", Toast.LENGTH_SHORT).show();
                 /*After uploading a file, you can get a URL to download the file by calling the getDownloadUrl() method on the StorageReference:*/
                 Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                     @Override
                     public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                         if (!task.isSuccessful()) {
                             throw task.getException(); //if task is not successful
                         }
                         // Continue with the task to get the download URL(Ready to get image URL)
                         downloadImageURL = filePath.getDownloadUrl().toString();
                         return filePath.getDownloadUrl(); //will get uri not the link
                     }
                 }).addOnCompleteListener(new OnCompleteListener<Uri>() { //Called when the Task completes
                     @Override
                     public void onComplete(@NonNull Task<Uri> task) {
                         if (task.isSuccessful()) {
                             downloadImageURL = task.getResult().toString(); //get image URL link
                             Toast.makeText(AdminAddNewProductActivity.this, " got the product image URL successfully", Toast.LENGTH_SHORT).show();
                             //Now store all the information of the new product inside the firebase database
                             saveProductInfoToDatabase();
                         }
                     }
                 });
             }//end of onSuccess()
         });//end of addOnSuccessListener()
         //Now once this image is uploaded successfully to the firebase storage and now have to get the link of that image and have to store it
         //inside the firebase database and then display these product to the user information
    }//end of storeProductInformation()

    private void saveProductInfoToDatabase() {
        //store items in "key/value" pairs, and you can access them by an index of another type
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("productId", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", Description);
        productMap.put("image", downloadImageURL);
        productMap.put("category", CategoryName);
        productMap.put("price", Price);
        productMap.put("productName", Pname);

        //under Products name, product random key work as id for each Product items in the database.
        ProductsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) { //Once product added to DB
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Intent intent = new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);
                            Toast.makeText(AdminAddNewProductActivity.this, "Product is added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            loadingBar.dismiss();
                            String message = task.getException().toString(); //throw error in string
                            Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }//end of saveProductInfoToDatabase()
}
