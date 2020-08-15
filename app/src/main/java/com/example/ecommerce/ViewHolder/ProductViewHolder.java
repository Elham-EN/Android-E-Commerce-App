package com.example.ecommerce.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.Interface.ItemClickListner;
import com.example.ecommerce.R;
//ViewHolder has view information for displaying one item & handle mouse clicks
//ViewHolder provide us the getAdapterPosition
public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductDescription, txtProductPrice;
    public ImageView imageView;
    public ItemClickListner listener;

    public ProductViewHolder(View itemView) {
        //When a subclass calls super(), it is calling the constructor of its immediate superclass.
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.product_image);
        txtProductName = (TextView) itemView.findViewById(R.id.product_name);
        txtProductDescription = (TextView) itemView.findViewById(R.id.product_description);
        txtProductPrice = (TextView) itemView.findViewById(R.id.product_price);
    }

    public void setItemClickListener(ItemClickListner listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        /*whenever you clicked on an item (ViewHolder item) we ask the adapter about it’s position.
        so you will get the latest position of this item in terms of Adapter’s logic*/
        listener.onClick(view, getAdapterPosition(), false);
    }
}
