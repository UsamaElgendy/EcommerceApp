package com.elgindy.ecommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import Prevalent.Prevalent;

public class ResetPasswordActivity extends AppCompatActivity {

    private String check = "";
    private TextView pageTitle, titleQuestion;
    private EditText phoneNumber, question1, question2;
    private Button vertifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        check = getIntent().getStringExtra("check");

        pageTitle = findViewById(R.id.page_title);
        titleQuestion = findViewById(R.id.title_questions);
        phoneNumber = findViewById(R.id.find_phone_number);
        question1 = findViewById(R.id.question_1);
        question2 = findViewById(R.id.question_2);
        vertifyButton = findViewById(R.id.verify_btn);

    }

    @Override
    protected void onStart() {
        super.onStart();
        phoneNumber.setVisibility(View.GONE);


        if (check.equals("settings")) {

            pageTitle.setText("Set Questions");
            titleQuestion.setText("Please set Answers for the Following Security Questions?");
            vertifyButton.setText("Set");

            displayPreviousAnswers();

            vertifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    setAnswers();

                }
            });

        } else if (check.equals("login")) {
            phoneNumber.setVisibility(View.VISIBLE);

        }
    }
    private void setAnswers(){

        // lowerCase here because when the data store in the firebase . don't look for lower and upper
        String answer1 = question1.getText().toString().toLowerCase();
        String answer2 = question2.getText().toString().toLowerCase();

        if (question1.equals("") && question2.equals("")) {
            Toast.makeText(ResetPasswordActivity.this, "Please answer all the questions.", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference ref = FirebaseDatabase
                    .getInstance().getReference()
                    .child("Users")
                    .child(Prevalent.CurrentOnlineUser.getPhone());

            HashMap<String, Object> userdataMap = new HashMap<>();
            userdataMap.put("answer1", answer1);
            userdataMap.put("answer2", answer2);

            ref.child("Security Questions").updateChildren(userdataMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ResetPasswordActivity.this, "you have set Security Questions Successfully.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ResetPasswordActivity.this,HomeActivity.class);
                                startActivity(intent);


                            }
                        }
                    });

        }

    }

    private void displayPreviousAnswers(){
        DatabaseReference ref = FirebaseDatabase
                .getInstance().getReference()
                .child("Users")
                .child(Prevalent.CurrentOnlineUser.getPhone());

        // to retrieve value form database
        ref.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String ans1 = dataSnapshot.child("answer1").getValue().toString();
                    String ans2 = dataSnapshot.child("answer2").getValue().toString();

                    question1.setText(ans1);
                    question2.setText(ans2);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
