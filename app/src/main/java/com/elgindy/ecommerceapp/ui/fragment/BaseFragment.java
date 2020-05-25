package com.elgindy.ecommerceapp.ui.fragment;

import androidx.fragment.app.Fragment;

import com.elgindy.ecommerceapp.ui.activity.BaseActivity;

public class BaseFragment extends Fragment {

    public BaseActivity baseActivity;

    public void setUpActivity() {
        baseActivity = (BaseActivity) getActivity();
        baseActivity.baseFragment = this;
    }

    public void onBack() {
        baseActivity.superBackPress();
    }
}
