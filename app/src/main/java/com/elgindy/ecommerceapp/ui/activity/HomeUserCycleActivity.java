package com.elgindy.ecommerceapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.elgindy.ecommerceapp.R;
import com.elgindy.ecommerceapp.helper.HelperMethod;
import com.elgindy.ecommerceapp.model.Users;
import com.elgindy.ecommerceapp.ui.fragment.CartFragment;
import com.elgindy.ecommerceapp.ui.fragment.HomeFragment;
import com.elgindy.ecommerceapp.ui.fragment.PersonalInfoFragment;
import com.elgindy.ecommerceapp.ui.fragment.SearchProductFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeUserCycleActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout_student)
    DrawerLayout drawer;
    @BindView(R.id.fab)
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user_cycle);
        ButterKnife.bind(this);

        HelperMethod.replaceFragment(getSupportFragmentManager()
                , new HomeFragment()
                , R.id.activity_home_user_cycle);

        toolbar.setTitle("Home");
        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        this.setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);

        final View headerView = navView.getHeaderView(0);
        final TextView user_name = headerView.findViewById(R.id.nav_header_home_user_name);
        final CircleImageView circleImageView = headerView.findViewById(R.id.nav_header_home_user_profile_image);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = currentUser.getUid();


        navView.setNavigationItemSelectedListener(this);

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        userReference.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users userData = dataSnapshot.getValue(Users.class);

                user_name.setText(userData.getName());
                String userImage = userData.getImage();
                if (userImage != null && !userImage.equals("")) {
                    Picasso.get().load(userImage).into(circleImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_card) {

            HelperMethod.replaceFragment(getSupportFragmentManager(), new CartFragment(), R.id.activity_home_user_cycle);
        } else if (id == R.id.nav_search) {

            HelperMethod.replaceFragment(getSupportFragmentManager(), new SearchProductFragment(), R.id.activity_home_user_cycle);


        } else if (id == R.id.nav_categories) {

        } else if (id == R.id.nav_persona_info) {

            HelperMethod.replaceFragment(getSupportFragmentManager(), new PersonalInfoFragment(), R.id.activity_home_user_cycle);
        } else if (id == R.id.nav_logout) {

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, UserCycleActivity.class);
            startActivity(intent);
            finish();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        HelperMethod.replaceFragment(getSupportFragmentManager(), new CartFragment(), R.id.activity_home_user_cycle);
    }
}
