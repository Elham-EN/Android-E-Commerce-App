package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ecommerce.Model.AdminOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//Admin Check the order from customer
public class AdminNewOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);
        //Reference to Orders table
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        ordersList = findViewById(R.id.orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    //Using FirebaseUI to populate a RecyclerView
    @Override
    protected void onStart() {
        super.onStart();
        /*The FirebaseRecyclerOptions class controls how FirebaseUI populates the RecyclerView with data from the
        Realtime Database. At a minimum you pass it a query or location to get the data from, and a Java class that
        will be instantiated to hold the data from each row in the view.*/
        FirebaseRecyclerOptions<AdminOrders> options = new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(ordersRef, AdminOrders.class)
                .build();
        /*create the FirebaseRecyclerAdapter object. Must have a ViewHolder subclass for displaying each
        item. FirebaseRecyclerAdapter binds a Query to a RecyclerView.**/
        FirebaseRecyclerAdapter<AdminOrders, AdminOrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrderViewHolder holder, final int position, @NonNull final AdminOrders model) {
                        //Bind the AdminOrders object to the AdminOrderViewHolder
                        holder.userName.setText("Name: " + model.getName());
                        holder.userPhoneNumber.setText("Phone Number: " + model.getPhone());
                        holder.userTotalPrice.setText("Total Amount: $" + model.getTotalAmount());
                        holder.userDateTime.setText("Order at: " + model.getDate() + " " + model.getTime());
                        holder.userShippingAddress.setText("Shipping Address: " + model.getAddress() + " " + model.getCity());
                        holder.showOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //You would have to assign the database reference to the position of each item by using getRef:
                                //get a reference of the position of your item from your ViewHolder which is directly referencing the item from the database
                                //And there after use the reference to your position and database reference and assign it to a String which you
                                // will use to get the unique key of your item:
                                String userID = getRef(position).getKey();
                                Intent intent = new Intent(AdminNewOrdersActivity.this, AdminUserProductActivity.class);
                                intent.putExtra("userId", userID);
                                startActivity(intent);
                            }
                        });
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Dialog box contain two options
                                CharSequence options[] = new CharSequence[] {"Yes", "No"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                                builder.setTitle("Have you shipped this order products ?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0) {
                                            String UserID = getRef(position).getKey();
                                            RemoveOrder(UserID);
                                        }
                                        else { //otherwise pressed no
                                            finish(); //when your activity is done and should be closed
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AdminOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        // Create a new instance of the ViewHolder, in this case we are using a custom
                        // layout called R.layout.orders_layout for each item
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.orders_layout, parent, false);
                        return new AdminOrderViewHolder(view);
                    }
                };
        ordersList.setAdapter(adapter);
        adapter.startListening();
    }

    //A class can be made static only if it is a nested class. The ViewHolder class that contains the Views
    // in the layout that is shown for each object
    public static class AdminOrderViewHolder extends RecyclerView.ViewHolder {
        public TextView userName, userPhoneNumber, userTotalPrice, userDateTime, userShippingAddress;
        public Button showOrdersBtn;
        public AdminOrderViewHolder(@NonNull View itemView) {
            super(itemView); //in order to access the variables & methods of parent class

            userName = itemView.findViewById(R.id.order_user_name);
            userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userShippingAddress = itemView.findViewById(R.id.order_address_city);
            showOrdersBtn = itemView.findViewById(R.id.show_all_products_btn);
        }
    }

    private void RemoveOrder(String UserID) {
        ordersRef.child(UserID).removeValue();
    }
}
