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
import android.widget.Toast;

import com.example.ecommerce.Model.Cart;
import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextProcessBtn;
    private TextView textTotalAmount, txtMsg1;
    private int overTotalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true); //doesn't depend on the adapter content:
        layoutManager = new LinearLayoutManager(this); //Create a List with RecyclerView
        recyclerView.setLayoutManager(layoutManager);

        NextProcessBtn = (Button) findViewById(R.id.next_process_btn);
        textTotalAmount = (TextView) findViewById(R.id.total_price);
        txtMsg1 = (TextView) findViewById(R.id.msg1);

        NextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //textTotalAmount.setText("Total Price: $" + String.valueOf(overTotalPrice));
                Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price", String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();
            }
        });
    }
    // Using FirebaseUI to populate a RecyclerView - displaying a list of data by binding the Cart
    // objects to a recyclerview. Display the item on the card list
    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderState();
        //using firebaseRecyclerAdapter to retrieve the items
        final DatabaseReference cartListRef  = FirebaseDatabase.getInstance().getReference().child("Cart List");
        //Pass in the model class to receive data - First, configure the adapter by building FirebaseRecyclerOptions
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>() //A builder for Data objects.
                .setQuery(cartListRef.child("User View")
                .child(Prevalent.currentOnlineUser.getPhone()) //get loggedIn user phone number
                .child("Products"), Cart.class)
                .build();
        //binds a Query to a RecyclerView and responds to all real-time events included items being added, removed, moved, or changed.
        //a ViewHolder subclass for displaying each item.
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                // Bind the Cart object to the CartViewHolder
                holder.txtProductQuantity.setText("Quantity: " + model.getQuantity());
                holder.txtProductPrice.setText(model.getPrice());
                holder.txtProductName.setText(model.getProductName());

                //get product quantity of each product and multiply the quantity with specific product price
                //int oneTypeProductTotalPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                int oneTypeProductTotalPrice = (Integer.parseInt(model.getPrice().replaceAll("\\D+",""))) * Integer.parseInt(model.getQuantity());
                overTotalPrice = overTotalPrice + oneTypeProductTotalPrice;
                textTotalAmount.setText("Total Price: $" + String.valueOf(overTotalPrice));

                //if user tap on any card product then...
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //create dialog box which will have two options edit and remove
                        //A CharSequence is a readable sequence of char values
                        CharSequence options[] = new CharSequence[]{"Edit", "Remove"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        //Now set click listener on these two button
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) { //if user click on Edit button
                                    Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("productId", model.getProductId());
                                    startActivity(intent);
                                }
                                if (i == 1) { //if user click on Remove button
                                    cartListRef.child("User View")
                                            .child(Prevalent.currentOnlineUser.getPhone())
                                            .child("Products")
                                            .child(model.getProductId())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(CartActivity.this, "Item removed successfully" ,Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                } //end of if statement
                            } //end of onClick()
                        }); //end of builder.setItems
                        builder.show();
                    } //end of onClick()
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // layout called R.layout.cart_items_layout for each item
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items, parent, false);
                //Create a new instance of the ViewHolder
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };
        // event listener to monitor changes to the Firebase query.
        recyclerView.setAdapter(adapter);
        adapter.startListening(); //To begin listening for data
    } //end of onStart()

    private void CheckOrderState() {
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() { //listen for value changes
            //This method is triggered once when the listener is attached and again every time the data,
            // including children, changes.
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //if dataSnapshot contain child data at that location
                if (dataSnapshot.exists()) {
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    if (shippingState.equals("shipped")) { //if order has been shipped
                        textTotalAmount.setText("Dear " + userName + "\n order is shipped successfully.");
                        recyclerView.setVisibility(View.GONE);
                        txtMsg1.setVisibility(View.VISIBLE);
                        txtMsg1.setText("Congratulations, your order has been placed successfully. Soon it will be verified");
                        NextProcessBtn.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "You can purchase more products, once you received your first order", Toast.LENGTH_SHORT).show();
                    }
                    else if (shippingState.equals("not shipped")) {
                        textTotalAmount.setText("Shipping State: Not Shipped");
                        recyclerView.setVisibility(View.GONE);
                        txtMsg1.setVisibility(View.VISIBLE);
                        NextProcessBtn.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "You can purchase more products, once you received your first order", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } //end of CheckOrderState()

}
