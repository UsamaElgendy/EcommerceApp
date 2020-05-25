package com.elgindy.ecommerceapp.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.elgindy.ecommerceapp.R;
import com.elgindy.ecommerceapp.helper.HelperMethod;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ConfirmFinalOrderFragment extends BaseFragment {

    @BindView(R.id.txt)
    TextView txt;
    @BindView(R.id.shipment_name_ET)
    EditText shipmentNameET;
    @BindView(R.id.shipment_Phone_number_ET)
    EditText shipmentPhoneNumberET;
    @BindView(R.id.shipment_address_ET)
    EditText shipmentAddressET;
    @BindView(R.id.shipment_city_ET)
    EditText shipmentCityET;
    @BindView(R.id.confirm_final_order_btn)
    Button confirmFinalOrderBtn;

    private String totalAmount = "";


    public ConfirmFinalOrderFragment() {
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
        View view = inflater.inflate(R.layout.fragment_confirm_final_order, container, false);
        ButterKnife.bind(this, view);

        // variable have a total price
        totalAmount = getArguments().getString("Total Price");
        Toast.makeText(baseActivity, "Total Price = " + totalAmount + "$", Toast.LENGTH_SHORT).show();

        return view;
    }

    @Override
    public void onBack() {
        super.onBack();
    }

    @OnClick(R.id.confirm_final_order_btn)
    public void onViewClicked() {
        check();
    }

    private void check() {
        if (TextUtils.isEmpty(shipmentNameET.getText().toString())) {
            Toast.makeText(baseActivity, "Please Provide your full name.. ", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(shipmentPhoneNumberET.getText().toString())) {
            Toast.makeText(baseActivity, "Please provide your phone number ", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(shipmentAddressET.getText().toString())) {
            Toast.makeText(baseActivity, "Please provide your address  ", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(shipmentCityET.getText().toString())) {
            Toast.makeText(baseActivity, "Please provide your city name ", Toast.LENGTH_SHORT).show();
        } else {
            ConformOrder();
        }
    }

    private void ConformOrder() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();

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
                .child(userId);

        HashMap<String, Object> ordersMap = new HashMap<>();

        ordersMap.put("totalAmount", totalAmount);
        ordersMap.put("name", shipmentNameET.getText().toString());
        ordersMap.put("phone", shipmentPhoneNumberET.getText().toString());
        ordersMap.put("address", shipmentAddressET.getText().toString());
        ordersMap.put("city", shipmentCityET.getText().toString());
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
                            .child(userId)
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(baseActivity, "your final order has been placed successfully", Toast.LENGTH_SHORT).show();
                                        HelperMethod.replaceFragment(getFragmentManager(), new HomeFragment(), R.id.activity_home_user_cycle);
                                    }
                                }
                            });
                }
            }
        });

    }
}
