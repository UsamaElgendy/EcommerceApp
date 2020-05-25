package com.elgindy.ecommerceapp.ui.activity;

import android.os.Bundle;

import com.elgindy.ecommerceapp.R;
import com.elgindy.ecommerceapp.helper.HelperMethod;
import com.elgindy.ecommerceapp.ui.fragment.WelcomeFragment;

public class UserCycleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cycle);
        HelperMethod.replaceFragment(getSupportFragmentManager()
                , new WelcomeFragment()
                , R.id.user_cycle_activity);
    }
}
