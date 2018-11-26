package social.shofizone.com.networking.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import social.shofizone.com.networking.Utilities.FirebaseRules;
import social.shofizone.com.networking.Utilities.UniversalImageHelper;
import social.shofizone.com.networking.R;
import social.shofizone.com.networking.model.User;
import social.shofizone.com.networking.model.UserAccountSettings;
import social.shofizone.com.networking.model.UserSettings;
import social.shofizone.com.networking.share.ShareActivity;

public class EditProfileFragment extends Fragment{
    private FirebaseRules mFirebaseRules;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final String TAG = "EditProfileFragment";
    Context mContext = getActivity();
    private String mUserID;
    private UserSettings mUserSettings;

    ProgressBar mProgressBar;
    public static ProgressBar mProgressBarProfilePhoto;

//Edit profile widgets
    ImageView mImageBack,mProfileImage;
    TextView mChangeName,mChangeUsername,mChangeDescription,mChangeBiodata,mChangeEmail,mChangePhone,mTvChangePhoto;
    ImageView mEditProfileCheckMark;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_edit_profile,container,false);

        mImageBack = (ImageView) view.findViewById(R.id.edit_profile_close_image) ;
        mProfileImage = (ImageView) view.findViewById(R.id.edit_profile_photo) ;
        mChangeName = (TextView) view.findViewById(R.id.edti_profile_name);
        mChangeUsername = (TextView) view.findViewById(R.id.edit_profile_username);
        mChangeDescription = (TextView) view.findViewById(R.id.edit_profile_description_edit_profile);
        mChangeBiodata = (TextView) view.findViewById(R.id.edit_profile_biodata);
        mChangeEmail = (TextView) view.findViewById(R.id.edit_profile_profile_email);
        mChangePhone = (TextView) view.findViewById(R.id.edit_profile_phone);
        mFirebaseRules = new FirebaseRules(getActivity());
        mEditProfileCheckMark = (ImageView) view.findViewById(R.id.edit_profile_done_image);
        mTvChangePhoto = (TextView) view.findViewById(R.id.tv_edit_profile_chae_photo);

        mProgressBar = (ProgressBar)view.findViewById(R.id.spin_kit);
        mProgressBarProfilePhoto = (ProgressBar)view.findViewById(R.id.spin_kit2);
        FadingCircle fadingCircle = new FadingCircle();
        mProgressBar.setIndeterminateDrawable(fadingCircle);

        setupFirebaseAuth();
        mProgressBarProfilePhoto.setVisibility(View.GONE);



        mImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        mEditProfileCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Done button Clicked");
                saveProfileSettings();
            }
        });

   return  view;
    }




    private void setProfileWidgets(UserSettings userSettings){
        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        mUserSettings = userSettings;

        UniversalImageHelper.setImage(settings.getProfilephoto(),mProfileImage,null,"");

        mChangeName.setText(settings.getDisplayname());
        mChangeBiodata.setText(settings.getBiodata());
        mChangeDescription.setText(settings.getDescription());

        mChangeUsername.setText(user.getUsername());
        mChangeEmail.setText(user.getEmail());
        mChangePhone.setText(String.valueOf(user.getPhonenumber()));
        setProfileImage(settings.getProfilephoto());
        mProgressBar.setVisibility(View.GONE);

        mTvChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShareActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//268435456
                getActivity().startActivity(intent);

                getActivity().finish();
            }
        });

    }


    public void removeProfilePhotoProgressBar(){
        mProgressBarProfilePhoto.setVisibility(View.GONE);
    }
    public void addingProfilePhotoProgressBar(){
        mProgressBarProfilePhoto.setVisibility(View.VISIBLE);
    }




    private void saveProfileSettings(){
        final String displayName = mChangeName.getText().toString();
        final String username = mChangeUsername.getText().toString();
        final String description = mChangeDescription.getText().toString();
        final String biodata = mChangeBiodata.getText().toString();
        final String email = mChangeEmail.getText().toString();
        final long phone = Long.parseLong(mChangePhone.getText().toString());



        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //case1 : did not change user name or email

                if(!mUserSettings.getUser().getUsername().equals(username)){
                        checkIfUserNameExists(username);
                }



                // case: they change their user name or email



                if(!mUserSettings.getSettings().getDisplayname().equals(displayName)){
                    //update displya name

                    mFirebaseRules.updateUserAccountSetting(displayName,null,null,0);
                }

                if(!mUserSettings.getSettings().getDescription().equals(description)){
                    mFirebaseRules.updateUserAccountSetting(null,null,description,0);

                }

                if(!mUserSettings.getSettings().getBiodata().equals(biodata)){
                    mFirebaseRules.updateUserAccountSetting(null,biodata,null,0);
                }

                if(mUserSettings.getUser().getPhonenumber() != phone){
                    mFirebaseRules.updateUserAccountSetting(null,null,null,phone);
                }

                getActivity().finish();

            }


            //updating other information



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
// check current user name is exist in database
    private void checkIfUserNameExists(final String username) {
        Log.d(TAG, "checkIfUserNameExists: checking if "+ username+ " Exists");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.user_db))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    // add the user
                    mFirebaseRules.updateUsername(username);

                    Toast.makeText(getActivity(),"Saved Username  ",Toast.LENGTH_LONG).show();
                }
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    if(singleSnapshot.exists()){
                        Log.d(TAG, "onDataChange: We found a match"+ singleSnapshot.getValue(User.class).getUsername());
                        Toast.makeText(getActivity(),"Username already exits ",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }





    public void setProfileImage(String imageUrl){
        //String imageUrl = "demarillac.org/wp-content/uploads/2016/08/male.jpg";
        UniversalImageHelper.setImage(imageUrl,mProfileImage,null,"");
    }







    /*######################Firebase############### */

    private void setupFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mUserID = mAuth.getCurrentUser().getUid();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();



                if(firebaseUser != null){
                    Log.d(TAG,"onAuthStateChange: Sing in"+firebaseUser.getUid());
                }else{
                    Log.d(TAG,"onAuthStateChange: Sing in");
                }
            }
        };

        // Read from the database
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // retrieve information form firebase


                setProfileWidgets(mFirebaseRules.getUserSettings(dataSnapshot));

                Log.d(TAG, "onDataChange: Getting user data"+ dataSnapshot);
                // retrieve images form firebase



            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);

        }
    }





}
