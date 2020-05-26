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
import com.elgindy.ecommerceapp.ui.activity.HomeUserCycleActivity;
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

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class PersonalInfoFragment extends BaseFragment {
    private static final int PICK_IMAGE_REQUEST_FIRST = 1;

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
    private String uId;


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

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uId = currentUser.getUid();


        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference().child("profilePictures");

        userInfoDisplay();

        return view;
    }

    @Override
    public void onBack() {
        Intent intent = new Intent(getActivity(), HomeUserCycleActivity.class);
        startActivity(intent);
    }

    @OnClick({R.id.profile_image_change_btn, R.id.update_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.profile_image_change_btn:
                checker = "clicked";
                openImageChooser();
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

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_FIRST);

    }

    private void updateOnlyUserInfo() {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", settingsFullNameET.getText().toString());
        userMap.put("address", settingsAddressET.getText().toString());
        userMap.put("phone", settingsPhoneNumberET.getText().toString());
        ref.child(uId).updateChildren(userMap);

        HelperMethod.replaceFragment(getFragmentManager(), new HomeFragment(), R.id.activity_home_user_cycle);
        Toast.makeText(getContext(), "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
    }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(settingsFullNameET.getText().toString())) {
            Toast.makeText(getContext(), "please write name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(settingsPhoneNumberET.getText().toString())) {
            Toast.makeText(getContext(), "please write phone number ", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(settingsAddressET.getText().toString())) {
            Toast.makeText(getContext(), "please write your address", Toast.LENGTH_SHORT).show();
        } else if (checker.equals("clicked")) {
            // if it is clicked it means that image are be selected
            uploadImage();
        }
    }

    private void uploadImage() {
        // ProgressDialog to wait and info
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Update information");
        progressDialog.setMessage("Please wait ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        if (imageUri != null) {
            final StorageReference fileRef = storageProfilePrictureRef
                    .child(uId + ".jpg");

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
                        userMap.put("phone", settingsPhoneNumberET.getText().toString());
                        userMap.put("image", myUrl);
                        ref.child(uId).updateChildren(userMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(getContext(), HomeUserCycleActivity.class));
                        Toast.makeText(getContext(), "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Error.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), "image is not selected.", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay() {

        // retrieve data from database
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);

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

                        if (!image.equals("")) {
                            Picasso.get().load(image).into(settingsProfileImage);
                        }
                        settingsFullNameET.setText(name);
                        settingsPhoneNumberET.setText(phone);
                        settingsAddressET.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_FIRST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData(); // TODO :  this variable contain our uri of picture
            Picasso.get().load(imageUri).into(settingsProfileImage);
        } else {
            Toast.makeText(getContext(), "Error, Try Again.", Toast.LENGTH_SHORT).show();

            HelperMethod.replaceFragment(getFragmentManager(), new PersonalInfoFragment(), R.id.activity_home_user_cycle);

        }
    }
}