package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ecommerce.Model.Products;
import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private DatabaseReference ProductRef;
    private DrawerLayout drawer;
    private String type = "";
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerOptions<Products> options;
    FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        /*Android Bundle is used to pass data between activities. The values that are to be passed
        are mapped to String keys which are later used in the next activity to retrieve the values.*/
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            type = getIntent().getExtras().get("Admin").toString();
        }


        ProductRef = FirebaseDatabase.getInstance().getReference().child("Products");

        Paper.init(this);

        //Might be the problem
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("HappyShop.com");
        setSupportActionBar(toolbar);

        //Cart Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!type.equals("Admin")) {
                    Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                    startActivity(intent);
                }
            }
        });

        //Drawer Button
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //listen to item selection and implement your navigation logic.
        navigationView.setNavigationItemSelectedListener(this);//Handling selection

        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextViw = headerView.findViewById(R.id.user_profile_name);//Ref to nav header username
        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);

        if (!type.equals("Admin")) {
            //When user login, put user name on Navigation Drawer header.
            userNameTextViw.setText(Prevalent.currentOnlineUser.getName());
            Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);
        }

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }//end of onCreate()

    @Override
    protected void onStart() {
        super.onStart();
        //Using FirebaseUI to populate a RecyclerView First, configure the adapter by building FirebaseRecyclerOptions.
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductRef, Products.class).build(); //Contain DB query

        /* binds a Query to a RecyclerView and responds to all real-time events included items being added, removed,
        moved, or changed. Best used with small result sets since all results are loaded at once.*/
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {//options contains DB Query

                    //Bind the Products object to the ProductViewHolder
                    @Override //Holder - each item
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
                        holder.txtProductName.setText(model.getProductName());
                        //holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("$" + model.getPrice() + " AUD");
                        Picasso.get().load(model.getImage()).into(holder.imageView);
                        //when use click on products item
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (type.equals("Admin")) { //if it is admin, sent to another activity
                                    Intent intent = new Intent(HomeActivity.this, AdminMaintainProductsActivity.class);
                                    intent.putExtra("productId", model.getProductId());
                                    startActivity(intent);
                                } else {
                                    //putExtra() adds extended data to the intent. It has two parameters, first one specifies the name
                                    // which of the extra data,and the second parameter is the data itself
                                    Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                    //will get product id for specific product
                                    intent.putExtra("productId", model.getProductId());
                                    startActivity(intent);
                                }
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        //layout called R.layout.message for each item
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_items_layout, parent, false);
                        //a ViewHolder subclass for displaying each item
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);

        adapter.startListening();

    }//end of onStart()

    //when we press the back button while our navigation drawer is opened we don't want to leave the
    //activity immediately and instead we want to close our navigation drawer
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        super.onBackPressed(); //close activity as usual
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        // if (id == R.id.action_settings)
        // {
        //   return true;
        // }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_cart) {
            if (!type.equals("Admin")) {
                //when user click on cart button will be directed to cart activity
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        }
        else if (id == R.id.nav_search) {
            Intent intent = new Intent(HomeActivity.this, SearchProductsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_Categories) {

        }
        else if (id == R.id.nav_settings) {
           if (!type.equals("Admin")) {
               Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
               startActivity(intent);
           }
        }
        else if (id == R.id.nav_logout) { //once logout, will direct back to MainActivity
            if (!type.equals("Admin")) {
                Paper.book().destroy();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                //Add additional flags to the intent
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }

        DrawerLayout drawer =  (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
