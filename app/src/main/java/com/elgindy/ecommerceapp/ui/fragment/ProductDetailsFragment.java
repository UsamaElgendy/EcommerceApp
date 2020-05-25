package com.elgindy.ecommerceapp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.elgindy.ecommerceapp.R;
import com.elgindy.ecommerceapp.helper.HelperMethod;
import com.elgindy.ecommerceapp.model.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ProductDetailsFragment extends BaseFragment {


    @BindView(R.id.product_image_details_TV)
    ImageView productImageDetailsTV;
    @BindView(R.id.product_name_details_TV)
    TextView productNameDetailsTV;
    @BindView(R.id.product_description_details_TV)
    TextView productDescriptionDetailsTV;
    @BindView(R.id.product_price_details_TV)
    TextView productPriceDetailsTV;
    @BindView(R.id.product_number_btn)
    ElegantNumberButton productNumberBtn;
    @BindView(R.id.product_details_add_to_cart_btn)
    Button productDetailsAddToCartBtn;

    private String productID = "", state = "Normal", userId;


    public ProductDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);
        ButterKnife.bind(this, view);

        productID = getArguments().getString("productId");

        getProductDetails(productID);
        return view;
    }

    private void getProductDetails(String productID) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        // here pass id to data base
        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // DataSnapshot contains data from a Firebase Database location
                    Products products = dataSnapshot.getValue(Products.class);
                    productNameDetailsTV.setText(products.getProductName());
                    productPriceDetailsTV.setText(products.getPrice());
                    productDescriptionDetailsTV.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImageDetailsTV);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBack() {
        HelperMethod.replaceFragment(getFragmentManager(), new HomeFragment(), R.id.activity_home_user_cycle);
    }

    @Override
    public void onStart() {
        super.onStart();
        checkOrderState();
    }

    private void checkOrderState() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(userId);
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // the child in the Order Child database
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    if (shippingState.equals("shipped")) {
                        state = "Orders Shipped";
                    } else if (shippingState.equals("not shipped")) {
                        state = "Orders Placed";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.product_details_add_to_cart_btn)
    public void onViewClicked() {
        if (state.equals("Orders Placed") || state.equals("Orders Shipped")) {
            Toast.makeText(getContext(), "you can purchase your product, once your order is shipped or confirm ", Toast.LENGTH_LONG).show();
        } else {
            addingToCartList();
        }
    }

    private void addingToCartList() {
        String saveCurrentTime, saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = currentUser.getUid();

        final HashMap<String, Object> carMap = new HashMap<>();
        carMap.put("productId", productID);
        carMap.put("productName", productNameDetailsTV.getText().toString());
        carMap.put("price", productPriceDetailsTV.getText().toString());
        carMap.put("date", saveCurrentDate);
        carMap.put("time", saveCurrentTime);
        carMap.put("quantity", productNumberBtn.getNumber());
        carMap.put("discount", "");


        cartListRef.child("User View").child(uId)
                .child("Products").child(productID)
                .updateChildren(carMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            cartListRef.child("Admin View").child(uId).child("Products").child(productID)
                                    .updateChildren(carMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getContext(), "Added to cart List.", Toast.LENGTH_LONG).show();
                                            HelperMethod.replaceFragment(getFragmentManager(), new HomeFragment(), R.id.activity_home_user_cycle);
                                        }
                                    });
                        }
                    }
                });
    }
}
