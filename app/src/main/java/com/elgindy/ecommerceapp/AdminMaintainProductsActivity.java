package com.elgindy.ecommerceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

// here we put the code make the delete and update and edit in the product
public class AdminMaintainProductsActivity extends AppCompatActivity {
    private Button appluChangesBtn;
    private EditText name, price, description;
    private ImageView imageView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);

        appluChangesBtn = findViewById(R.id.apply_chances_button);
        name = findViewById(R.id.product_name_maintain);
        price = findViewById(R.id.product_price_maintain);
        description = findViewById(R.id.product_description_maintain);
        imageView = findViewById(R.id.product_image_maintain);

    }
}
