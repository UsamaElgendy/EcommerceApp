package com.elgindy.ecommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// here we put the code make the delete and update and edit in the product
public class AdminMaintainProductsActivity extends AppCompatActivity {
    private Button appluChangesBtn;
    private EditText name, price, description;
    private ImageView imageView;

    private String productID = "";
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);
        // in this string i have a id of product
        productID = getIntent().getStringExtra("pid");

        // product id is refer to the id of the product
        productsRef = FirebaseDatabase.getInstance().getReference().child(productID);

        appluChangesBtn = findViewById(R.id.apply_chances_button);
        name = findViewById(R.id.product_name_maintain);
        price = findViewById(R.id.product_price_maintain);
        description = findViewById(R.id.product_description_maintain);
        imageView = findViewById(R.id.product_image_maintain);

        displaySpecificProductInfo();

    }

    private void displaySpecificProductInfo() {

        /*
        * addValueEventListener() keep listening to query or database reference it is attached to.
        * But addListenerForSingleValueEvent() executes onDataChange method immediately and after executing that method once,
        * it stops listening to the reference location it is attached to.

*/
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
