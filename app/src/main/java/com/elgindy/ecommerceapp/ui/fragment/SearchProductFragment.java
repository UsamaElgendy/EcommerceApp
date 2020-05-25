package com.elgindy.ecommerceapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SearchProductFragment extends BaseFragment {

    @BindView(R.id.search_product_name_ET)
    EditText searchProductNameET;
    @BindView(R.id.search_btn)
    Button searchBtn;
    @BindView(R.id.search_recycler_view)
    RecyclerView searchRecyclerView;

    String SearchInput;


    public SearchProductFragment() {
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
        View view = inflater.inflate(R.layout.fragment_search_product, container, false);
        ButterKnife.bind(this, view);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        searchRecyclerView.setLayoutManager(new LinearLayoutManager(baseActivity));

        return view;
    }

    @Override
    public void onBack() {
        HelperMethod.replaceFragment(getFragmentManager(), new HomeFragment(), R.id.activity_home_user_cycle);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.search_btn)
    public void onViewClicked() {
        SearchInput = searchProductNameET.getText().toString();
        onStart();
    }

    @Override
    public void onStart() {
        super.onStart();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        // here we make a query return the item order by child and start by all input
                        .setQuery(reference.orderByChild("productName").startAt(SearchInput), Products.class)
                        .build();


        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
                        holder.txtProductName.setText(model.getProductName());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("Price = " + model.getPrice() + "$");
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        // here we can make a on click listener on a list view because i have a position
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
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
                        // here you pass the layout
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_product_items, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;

                    }
                };
        searchRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
