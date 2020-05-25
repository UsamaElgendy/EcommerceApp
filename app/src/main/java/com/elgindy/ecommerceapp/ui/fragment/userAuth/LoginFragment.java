package com.elgindy.ecommerceapp.ui.fragment.userAuth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.elgindy.ecommerceapp.R;
import com.elgindy.ecommerceapp.helper.HelperMethod;
import com.elgindy.ecommerceapp.model.Users;
import com.elgindy.ecommerceapp.ui.activity.HomeUserCycleActivity;
import com.elgindy.ecommerceapp.ui.fragment.BaseFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.text.TextUtils.isEmpty;


public class LoginFragment extends BaseFragment {


    @BindView(R.id.login_email_ET)
    EditText loginEmailET;
    @BindView(R.id.login_password_ET)
    EditText loginPasswordET;
    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.register_link_btn)
    TextView registerLinkBtn;

    private String email, password, studentName;

    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;

    public LoginFragment() {
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);


        FirebaseApp.initializeApp(getContext());
        setupFirebaseAuth();


        HelperMethod.disappearKeypad(getActivity(), view);

        return view;
    }

    private void showDialog() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideDialog() {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBack() {
        getActivity().finish();
    }


    @OnClick({R.id.login_btn, R.id.register_link_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                signIn();
                break;
            case R.id.register_link_btn:
                HelperMethod.replaceFragment(getFragmentManager(), new RegisterFragment(), R.id.user_cycle_activity);
        }
    }

    private void setupFirebaseAuth() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();
                    String uId = currentUser.getUid();

                    userRef.child("Users").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                hideDialog();
                                Users userData = dataSnapshot.getValue(Users.class);
                                studentName = userData.getName();

                                Intent intent = new Intent(getActivity(), HomeUserCycleActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    private void signIn() {
        //check if the fields are filled out
        if (!isEmpty(loginEmailET.getText().toString())
                && !isEmpty(loginPasswordET.getText().toString())) {

            showDialog();

            FirebaseAuth.getInstance().signInWithEmailAndPassword(loginEmailET.getText().toString(),
                    loginPasswordET.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            hideDialog();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("tagmessage", e.getMessage());
                    Toast.makeText(getContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                    hideDialog();
                }
            });
        } else {
            Toast.makeText(getContext(), "You didn't fill in all the fields.", Toast.LENGTH_SHORT).show();
        }
    }

}
