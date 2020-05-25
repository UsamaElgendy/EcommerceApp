package com.elgindy.ecommerceapp.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.elgindy.ecommerceapp.R;
import com.elgindy.ecommerceapp.helper.HelperMethod;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class PersonalInfoFragment extends BaseFragment {

    @BindView(R.id.toolbar_settings)
    Toolbar toolbarSettings;
    @BindView(R.id.app_bar_settings)
    AppBarLayout appBarSettings;
    @BindView(R.id.settings_profile_image)
    CircleImageView settingsProfileImage;
    @BindView(R.id.profile_image_change_btn)
    TextView profileImageChangeBtn;
    @BindView(R.id.settings_phone_number_ET)
    EditText settingsPhoneNumberET;
    @BindView(R.id.settings_full_name_ET)
    EditText settingsFullNameET;
    @BindView(R.id.settings_address_ET)
    EditText settingsAddressET;
    @BindView(R.id.update_btn)
    Button updateBtn;


    private Uri imageUri;
    private String myUrl = "";


    private StorageTask uploadTask;
    private StorageReference storageProfilePrictureRef;
    private String checker = "";
    private String userId;


    public PersonalInfoFragment() {
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
        View view = inflater.inflate(R.layout.fragment_personal_info, container, false);
        ButterKnife.bind(this, view);
        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");
        userInfoDisplay(settingsProfileImage, settingsFullNameET, settingsPhoneNumberET, settingsAddressET);

        return view;
    }

    @Override
    public void onBack() {
        HelperMethod.replaceFragment(getFragmentManager(), new HomeFragment(), R.id.activity_home_user_cycle);
    }

    @OnClick({R.id.profile_image_change_btn, R.id.update_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.profile_image_change_btn:
                checker = "clicked";

                // library let you crop image uploaded
                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(baseActivity);
                break;
            case R.id.update_btn:
                if (checker.equals("clicked")) {
                    userInfoSaved();
                } else {
                    updateOnlyUserInfo();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            settingsProfileImage.setImageURI(imageUri);
        } else {
            Toast.makeText(baseActivity, "Error, Try Again.", Toast.LENGTH_SHORT).show();

            HelperMethod.replaceFragment(getFragmentManager(), new PersonalInfoFragment(), R.id.activity_home_user_cycle);
        }
    }

    private void updateOnlyUserInfo() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", settingsFullNameET.getText().toString());
        userMap.put("address", settingsAddressET.getText().toString());
        userMap.put("phoneOrder", settingsPhoneNumberET.getText().toString());
        ref.child(userId).updateChildren(userMap);

        HelperMethod.replaceFragment(getFragmentManager(), new HomeFragment(), R.id.activity_home_user_cycle);
        Toast.makeText(baseActivity, "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
    }


    private void userInfoSaved() {
        if (TextUtils.isEmpty(settingsFullNameET.getText().toString())) {
            Toast.makeText(baseActivity, "Name is mandatory.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(settingsAddressET.getText().toString())) {
            Toast.makeText(baseActivity, "Name is address.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(settingsPhoneNumberET.getText().toString())) {
            Toast.makeText(baseActivity, "Name is mandatory.", Toast.LENGTH_SHORT).show();
        } else if (checker.equals("clicked")) {
            // if it is clicked it means that image are be selected
            uploadImage();
        }
    }


    private void uploadImage() {
        // ProgressDialog to wait and info
        final ProgressDialog progressDialog = new ProgressDialog(baseActivity);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        if (imageUri != null) {
            final StorageReference fileRef = storageProfilePrictureRef
                    .child(userId + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("name", settingsFullNameET.getText().toString());
                        userMap.put("address", settingsAddressET.getText().toString());
                        userMap.put("phoneOrder", settingsPhoneNumberET.getText().toString());
                        userMap.put("image", myUrl);
                        ref.child(userId).updateChildren(userMap);

                        progressDialog.dismiss();

                        HelperMethod.replaceFragment(getFragmentManager(), new HomeFragment(), R.id.activity_home_user_cycle);
                        Toast.makeText(baseActivity, "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(baseActivity, "Error.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(baseActivity, "image is not selected.", Toast.LENGTH_SHORT).show();
        }
    }


    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEditText) {

        // retrieve data from database
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // make sure that data is right
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.child("image").exists()) {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        // to display image
                        Picasso.get().load(image).into(profileImageView);
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        addressEditText.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
