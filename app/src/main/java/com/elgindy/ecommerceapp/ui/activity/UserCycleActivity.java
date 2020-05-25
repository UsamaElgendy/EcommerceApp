package com.elgindy.ecommerceapp.ui.activity;

import android.os.Bundle;

import com.elgindy.ecommerceapp.R;
import com.elgindy.ecommerceapp.helper.HelperMethod;
import com.elgindy.ecommerceapp.ui.fragment.userAuth.LoginFragment;

public class UserCycleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cycle);
        HelperMethod.replaceFragment(getSupportFragmentManager()
                , new LoginFragment()
                , R.id.user_cycle_activity);
    }
}
