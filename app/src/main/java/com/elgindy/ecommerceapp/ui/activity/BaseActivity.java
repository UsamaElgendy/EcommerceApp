package com.elgindy.ecommerceapp.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import com.elgindy.ecommerceapp.ui.fragment.BaseFragment;

public class BaseActivity extends AppCompatActivity {
    public BaseFragment baseFragment;

    public void superBackPress() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        baseFragment.onBack();
    }
}
