package com.elgindy.ecommerceapp.ui.fragment.userAuth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.elgindy.ecommerceapp.R;
import com.elgindy.ecommerceapp.helper.HelperMethod;
import com.elgindy.ecommerceapp.ui.activity.HomeUserCycleActivity;
import com.elgindy.ecommerceapp.ui.fragment.BaseFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RegisterFragment extends BaseFragment {


    @BindView(R.id.register_username_input_ET)
    EditText registerUsernameInputET;
    @BindView(R.id.register_email_ET)
    EditText registerEmailET;
    @BindView(R.id.register_phone_number_ET)
    EditText registerPhoneNumberET;
    @BindView(R.id.register_password_ET)
    EditText registerPasswordET;
    @BindView(R.id.register_btn)
    Button registerBtn;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    // var
    private String name, email, password, phone, userId;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;

    public RegisterFragment() {
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);


        HelperMethod.disappearKeypad(getActivity(), view);

        firebaseAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        return view;
    }

    @Override
    public void onBack() {
        super.onBack();
    }

    @OnClick(R.id.register_btn)
    public void onViewClicked() {
        createAccount();
    }

    private void showDialog() {
        registerBtn.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideDialog() {
        registerBtn.setVisibility(View.VISIBLE);
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


    private void createAccount() {
        // get input fields in the string
        email = registerEmailET.getText().toString();
        name = registerUsernameInputET.getText().toString();
        password = registerPasswordET.getText().toString();
        phone = registerPhoneNumberET.getText().toString();


        // check if the user name or password or phone field empty
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "Please write your name....", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(getContext(), "Please write your phone....", Toast.LENGTH_SHORT).show();
        } else if (password.length() <= 6) {
            Toast.makeText(getContext(), "Please write your password....", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(baseActivity, "Please write your phone", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Please write your email....", Toast.LENGTH_SHORT).show();
        } else {
            // wait to check is phone number is available in database
            registerEmailAndPasswordAuth();
        }
    }

    private void registerEmailAndPasswordAuth() {
        showDialog();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        assert firebaseUser != null;
                        userId = firebaseUser.getUid();
                        //insert some default data
                        saveDataToRealTime(userId);
                    } else {
                        Toast.makeText(baseActivity, "Please use another email , this email used", Toast.LENGTH_SHORT).show();
                        hideDialog();
                    }
                }).addOnFailureListener(e -> {
            HelperMethod.internetState.isConnect(getContext());
        });

    }

    private void saveDataToRealTime(String userId) {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> userMap = new HashMap<>();
                userMap.put("name", name);
                userMap.put("email", email);
                userMap.put("password", password);
                userMap.put("phone", phone);
                userMap.put("userId", userId);
                userMap.put("image", "");
                userMap.put("address", "");

                userRef.child(userId).updateChildren(userMap);
                redirectHomeActivity();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                HelperMethod.internetState.isConnect(getContext());
            }
        });
    }

    private void redirectHomeActivity() {
        Intent intent = new Intent(getContext(), HomeUserCycleActivity.class);
        Toast.makeText(getContext(), "Welcome : " + name, Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}

