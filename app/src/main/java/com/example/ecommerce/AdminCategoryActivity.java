package com.example.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity {
    //Variables for clicking on the item and display the item
    private ImageView tShirts, sportTShirts, femaleDresses, sweathers;
    private ImageView glasses, hatCaps, walletsBagPurses, shoes;
    private ImageView headPhonesHandFree, Laptops, watches, mobilePhones, game;
    private Button LogoutBtn, CheckOrdersBtn, maintainProductsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        LogoutBtn = (Button) findViewById(R.id.admin_logout_btn);
        CheckOrdersBtn = (Button) findViewById(R.id.check_orders_btn);
        maintainProductsBtn = (Button) findViewById(R.id.maintain_product_btn);

        maintainProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, HomeActivity.class);
                intent.putExtra("Admin", "Admin");
                startActivity(intent);
            }
        });

        LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        CheckOrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminNewOrdersActivity.class);
                startActivity(intent);
            }
        });

        tShirts = (ImageView) findViewById(R.id.t_shirts);
        sportTShirts = (ImageView) findViewById(R.id.sports_t_shirts);
        femaleDresses = (ImageView) findViewById(R.id.female_dresses);
        sweathers = (ImageView) findViewById(R.id.sweathers);
        glasses = (ImageView) findViewById(R.id.glasses);
        hatCaps = (ImageView) findViewById(R.id.hats_caps);
        walletsBagPurses = (ImageView) findViewById(R.id.purses_bags);
        shoes = (ImageView) findViewById(R.id.shoes);
        headPhonesHandFree = (ImageView) findViewById(R.id.headphones_handfree);
        Laptops = (ImageView) findViewById(R.id.laptop_pc);
        watches = (ImageView) findViewById(R.id.watches);
        mobilePhones = (ImageView) findViewById(R.id.mobilephones);
        game = (ImageView) findViewById(R.id.games);

        tShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                //need data from an activity to be in another activity, you can pass data between then while starting the activities
                intent.putExtra("category", "tShirts");
                startActivity(intent);
            }
        }); //end of tShirts

        sportTShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                //need data from an activity to be in another activity, you can pass data between then while starting the activities
                intent.putExtra("category", "Sports Shirts");
                startActivity(intent);
            }
        });//end  of sportTShirts

        femaleDresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                //need data from an activity to be in another activity, you can pass data between then while starting the activities
                intent.putExtra("category", "Female Dresses");
                startActivity(intent);
            }
        });//end  of femaleDresses

        sweathers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                //need data from an activity to be in another activity, you can pass data between then while starting the activities
                intent.putExtra("category", "Sweathers");
                startActivity(intent);
            }
        });//end  of sweathers

        glasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                //need data from an activity to be in another activity, you can pass data between then while starting the activities
                intent.putExtra("category", "Glasses");
                startActivity(intent);
            }
        });//end  of glasses

        hatCaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                //need data from an activity to be in another activity, you can pass data between then while starting the activities
                intent.putExtra("category", "Hat Caps");
                startActivity(intent);
            }
        });//end  of hatCaps

        walletsBagPurses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                //need data from an activity to be in another activity, you can pass data between then while starting the activities
                intent.putExtra("category", "wallets Bags Purses");
                startActivity(intent);
            }
        });//end  of walletBagPurses

        shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                //need data from an activity to be in another activity, you can pass data between then while starting the activities
                intent.putExtra("category", "Shoes");
                startActivity(intent);
            }
        });//end  of shoes

        headPhonesHandFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                //need data from an activity to be in another activity, you can pass data between then while starting the activities
                intent.putExtra("category", "HeadPhones HandFree");
                startActivity(intent);
            }
        });//end  of headPhonesHandFree

        Laptops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                //need data from an activity to be in another activity, you can pass data between then while starting the activities
                intent.putExtra("category", "Laptops");
                startActivity(intent);
            }
        });//end  of Laptops

        watches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                //need data from an activity to be in another activity, you can pass data between then while starting the activities
                intent.putExtra("category", "Watches");
                startActivity(intent);
            }
        });//end  of headPhonesHandFree

        mobilePhones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                //need data from an activity to be in another activity, you can pass data between then while starting the activities
                intent.putExtra("category", "Mobile Phones");
                startActivity(intent);
            }
        });//end  of headPhonesHandFree

        game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                //need data from an activity to be in another activity, you can pass data between then while starting the activities
                intent.putExtra("category", "Gaming");
                startActivity(intent);
            }
        });//end  of headPhonesHandFree
    }
}
