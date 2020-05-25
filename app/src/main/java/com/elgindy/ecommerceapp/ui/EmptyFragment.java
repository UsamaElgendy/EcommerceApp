package com.elgindy.ecommerceapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elgindy.ecommerceapp.R;
import com.elgindy.ecommerceapp.ui.fragment.BaseFragment;

import butterknife.ButterKnife;


public class EmptyFragment extends BaseFragment {

    public EmptyFragment() {
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
        View view =inflater.inflate(R.layout.fragment_xml, container, false) ;
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onBack() {
        super.onBack();
    }
}
