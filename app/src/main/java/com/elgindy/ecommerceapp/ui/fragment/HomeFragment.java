package com.elgindy.ecommerceapp.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elgindy.ecommerceapp.R;
import com.elgindy.ecommerceapp.adapter.ProductViewHolder;
import com.elgindy.ecommerceapp.helper.HelperMethod;
import com.elgindy.ecommerceapp.model.Products;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HomeFragment extends BaseFragment {

    @BindView(R.id.home_recycler_view)
    RecyclerView homeRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference ProductsRef;

    private FloatingActionButton fab;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);


        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);

        fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        homeRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        homeRecyclerView.setLayoutManager(layoutManager);
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //can retrieve all data
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef, Products.class)
                .build();


        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
                        holder.txtProductName.setText(model.getProductName());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText(model.getPrice() + "$");
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        // here we can make a on click listener on a list view because i have a positio

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @SuppressLint("RestrictedApi")
                            @Override
                            public void onClick(View v) {
                                fab.setVisibility(View.GONE);
                                ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("productId", model.getProductId());
                                productDetailsFragment.setArguments(bundle);
                                HelperMethod.replaceFragment(getFragmentManager(), productDetailsFragment, R.id.activity_home_user_cycle);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        //we need to make a item layout to inflate here
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_product_items, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        // put adapter in recyclerView
        homeRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBack() {
        super.onBack();
    }
}
