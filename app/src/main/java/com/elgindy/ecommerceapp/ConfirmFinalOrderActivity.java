package com.elgindy.ecommerceapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import Prevalent.Prevalent;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText nameEditText, phoneEdittext, addressEdittext, cityEdittext;
    private Button confirOrderBtn;

    // variable to put total price come from intent
    private String totalAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        // variable have a total price
        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Price = " + totalAmount + "$", Toast.LENGTH_SHORT).show();

        confirOrderBtn = findViewById(R.id.confirm_final_order);
        nameEditText = findViewById(R.id.shipment_name);
        phoneEdittext = findViewById(R.id.shipment_Phone_number);
        addressEdittext = findViewById(R.id.shipment_address);
        cityEdittext = findViewById(R.id.shipment_city);

        confirOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // to check that editText if not empty
                Check();
            }
        });
    }

    private void Check() {
        if (TextUtils.isEmpty(nameEditText.getText().toString())) {
            Toast.makeText(this, "Please Provide your full name.. ", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phoneEdittext.getText().toString())) {
            Toast.makeText(this, "Please provide your phone number ", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(addressEdittext.getText().toString())) {
            Toast.makeText(this, "Please provide your address  ", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cityEdittext.getText().toString())) {
            Toast.makeText(this, "Please provide your city name ", Toast.LENGTH_SHORT).show();
        } else {
            ConformOrder();
        }
    }

    private void ConformOrder() {
        final String saveCurrentTime, saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        // here we make another chided in database
        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                // here we get the current user data who make a login
                .child(Prevalent.CurrentOnlineUser.getPhone());

        HashMap<String, Object> ordersMap = new HashMap<>();

        ordersMap.put("totalAmount", totalAmount);
        ordersMap.put("name", nameEditText.getText().toString());
        ordersMap.put("phone", phoneEdittext.getText().toString());
        ordersMap.put("address", addressEdittext.getText().toString());
        ordersMap.put("city", cityEdittext.getText().toString());
        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        // because the admin will approve this order by contacting with this user
        ordersMap.put("state", "not shipped");

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // remember when user are confirm the order we want to clear his list --> empty cart
                    FirebaseDatabase.getInstance().getReference()
                            .child("Cart List")
                            .child("User View")
                            .child(Prevalent.CurrentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "your final order has been placed successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);

                                        // this to make the user can't come in this activity again
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

    }
}
