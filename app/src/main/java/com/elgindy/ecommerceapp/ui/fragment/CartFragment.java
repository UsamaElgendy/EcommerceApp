package com.elgindy.ecommerceapp.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elgindy.ecommerceapp.R;
import com.elgindy.ecommerceapp.adapter.CartViewHolder;
import com.elgindy.ecommerceapp.helper.HelperMethod;
import com.elgindy.ecommerceapp.model.Cart;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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


public class CartFragment extends BaseFragment {

    @BindView(R.id.total_price_TV)
    TextView totalPrice;
    @BindView(R.id.rlll)
    RelativeLayout rlll;
    @BindView(R.id.msg1)
    TextView msg1;
    @BindView(R.id.next_process_button)
    Button nextProcessButton;
    @BindView(R.id.cart_recycler_view)
    RecyclerView cartRecyclerView;

    private RecyclerView.LayoutManager layoutManager;

    // here variable to calculate a total price
    private int overTotalPrice = 0;
    private String userId;

    public CartFragment() {
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
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        ButterKnife.bind(this, view);

        cartRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        cartRecyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onBack() {
        super.onBack();
    }

    @OnClick(R.id.next_process_button)
    public void onViewClicked() {
        totalPrice.setText("Total Price =  $" + String.valueOf(overTotalPrice));

        CartFragment cartFragment = new CartFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Total Price", String.valueOf(overTotalPrice));
        HelperMethod.replaceFragment(getFragmentManager(), cartFragment, R.id.activity_home_user_cycle);
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();

        CheckOrderState();


        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef.child("User View")
                                .child(userId).child("Products"), Cart.class)
                        .build();


        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                holder.txtProductQuantaty.setText("Quantity = " + model.getQuantity());
                holder.txtProductPrice.setText("product price = " + model.getPrice());
                holder.txtProductName.setText(model.getProductName());

                // here we make a total price of purchases
                int oneTypeProductTPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());

                overTotalPrice = overTotalPrice + oneTypeProductTPrice;

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence option[] = new CharSequence[]{
                                "Edit",
                                "Remove"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Cart Options");
                        builder.setItems(option, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("productId", model.getProductId());
                                    productDetailsFragment.setArguments(bundle);
                                    HelperMethod.replaceFragment(getFragmentManager(), productDetailsFragment, R.id.activity_home_user_cycle);
                                }
                                if (i == 1) {
                                    cartListRef.child("User View")
                                            .child(userId)
                                            .child("Products")
                                            .child(model.getProductId())
                                            .removeValue()
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getContext(), "Item removed successfully", Toast.LENGTH_SHORT).show();
                                                    HelperMethod.replaceFragment(getFragmentManager(), new HomeFragment(), R.id.activity_home_user_cycle);
                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_cart_item, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };
        cartRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void CheckOrderState() {
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(userId);
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // the child in the Order Child database
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    String username = dataSnapshot.child("name").getValue().toString();
                    if (shippingState.equals("shipped")) {
                        totalPrice.setText("Dear" + username + "\n order is shipped successfully.");
                        cartRecyclerView.setVisibility(View.GONE);
                        msg1.setVisibility(View.VISIBLE);
                        msg1.setText("Congratulation , your final order has been Shipped successfully . soon you will received your order at your door step .");
                        nextProcessButton.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "you can purchase your products , ones you receive your first final order  ", Toast.LENGTH_SHORT).show();
                    } else if (shippingState.equals("not shipped")) {
                        totalPrice.setText("Shipping state = not Shipped ");
                        cartRecyclerView.setVisibility(View.GONE);
                        msg1.setVisibility(View.VISIBLE);

                        nextProcessButton.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "you can purchase your products , ones you receive your first final order  ", Toast.LENGTH_SHORT).show();


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
