package social.shofizone.com.networking.Utilities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
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
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import social.shofizone.com.networking.R;
import social.shofizone.com.networking.model.PhotoStatus;
import social.shofizone.com.networking.model.User;
import social.shofizone.com.networking.model.UserAccountSettings;
import social.shofizone.com.networking.model.UserSettings;
import social.shofizone.com.networking.profile.AccountSettingActivity;
import social.shofizone.com.networking.profile.ProfileActivity;

public class ViewProfileFragment extends Fragment {

    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_GRID_COLUMNS = 3;



    private FirebaseRules mFirebaseRules;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    public interface OnGridImageSelectedListener{
        void onGridImageSelected(PhotoStatus photo, int activityNumber);
    }
    OnGridImageSelectedListener mOnGridImageSelectedListener;


    private static final String TAG = "ProfileFragment";
    Context mContext;

    private TextView mDisplayName, mFollowing, mFollowers, mUserName, mBiodata, mPosts,mDescription;
    private GridView mGridView;
    private ImageView mProfilePhoto;
    private ImageView mProfileMenu, mBackArrowImage;
    private BottomNavigationViewEx mBottomNavigationViewEx;
    private Toolbar mToolbar;
    private Button mEditProfileButton,mAddFriend,mUnFriend;
    ProgressBar mProgressBar;




    //vars
    private User mUser;
    private int mFollowersCount = 0;
    private int mFollowingCount = 0;
    private int mPostsCount = 0;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);
        Log.d(TAG, "onCreateView: Started");


        mDisplayName = (TextView) view.findViewById(R.id.tvDisplayName);
        mUserName = (TextView) view.findViewById(R.id.profile_name_top_bar);
        mBiodata = (TextView) view.findViewById(R.id.tvBiodata);
        mProfilePhoto = (ImageView) view.findViewById(R.id.profileImage);
        mFollowing = (TextView) view.findViewById(R.id.tvFollowing);
        mFollowers = (TextView) view.findViewById(R.id.tvFollowers);
        mPosts = (TextView) view.findViewById(R.id.tvpost_profile);
        mDescription = (TextView) view.findViewById(R.id.tvDescription);
        mGridView = (GridView) view.findViewById(R.id.gridViewProfile);
        mUnFriend = (Button) view.findViewById(R.id.button_un_friend);
        mAddFriend = (Button) view.findViewById(R.id.button_add_Friend);



        mBottomNavigationViewEx = (BottomNavigationViewEx) view.findViewById(R.id.bottom_navigation_item);
        mContext = getActivity();
        mProfileMenu = (ImageView) view.findViewById(R.id.profile_setting);
        mBackArrowImage = (ImageView) view.findViewById(R.id.backArrow_view_profile);
        mToolbar = (Toolbar) view.findViewById(R.id.profile_toolbar);
        mFirebaseRules = new FirebaseRules(getActivity());

        mProgressBar = (ProgressBar)view.findViewById(R.id.spin_kit);
        FadingCircle fadingCircle = new FadingCircle();
        mProgressBar.setIndeterminateDrawable(fadingCircle);

        mEditProfileButton =  (Button)view.findViewById(R.id.button_edit_profile_f) ;

        mToolbar.setTitle("");
        mToolbar.setSubtitle("");

//        mEditProfileButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick: navigating to "+ mContext.getString(R.string.edit_profile_fragment));
//
//                Intent intent = new Intent(getActivity(),AccountSettingActivity.class);
//                intent.putExtra(getString(R.string.calling_activity),getString(R.string.profile_activity));
//                startActivity(intent);
//            }
//        });


        try{
                mUser =getUserObjFormBundle();
            init();
        }catch (NullPointerException e){
            Log.d(TAG, "onCreateView: NullPointerException:  " +e.getMessage());
            Toast.makeText(mContext,"Something went Wrong !",Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack();
        }

        setupToolBar();
        setupNavIcon();
        setupFirebaseAuth();
        isFollowing();

        getFollowingCount();
        getFollowersCount();
        getPostsCount();



        mBackArrowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back");
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().finish();
            }
        });
        mAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: now following: " + mUser.getUsername());

                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_following))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(mUser.getUserid())
                        .child(getString(R.string.field_user_id))
                        .setValue(mUser.getUserid());

                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_followers))
                        .child(mUser.getUserid())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.field_user_id))
                        .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                setFollowing();

            }
        });

        mUnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: now un Friending"+mUser.getUsername());

                Log.d(TAG, "onClick: now unfollowing: " + mUser.getUsername());

                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_following))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(mUser.getUserid())
                        .removeValue();

                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_followers))
                        .child(mUser.getUserid())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .removeValue();
                setUnfollowing();
            }
        });


        return view;

    }

    private void getFollowersCount(){
        mFollowersCount = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_followers))
                .child(mUser.getUserid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found follower:" + singleSnapshot.getValue());
                    mFollowersCount++;
                }
                mFollowers.setText(String.valueOf(mFollowersCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getPostsCount(){
        mPostsCount = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbms_user_status))
                .child(mUser.getUserid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found post:" + singleSnapshot.getValue());
                    mPostsCount++;
                }
                mPosts.setText(String.valueOf(mPostsCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFollowingCount(){
        mFollowingCount = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_following))
                .child(mUser.getUserid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found following user:" + singleSnapshot.getValue());
                    mFollowingCount++;
                }
                mFollowing.setText(String.valueOf(mFollowingCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setFollowing(){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        mAddFriend.setVisibility(View.GONE);
        mUnFriend.setVisibility(View.VISIBLE);
        mEditProfileButton.setVisibility(View.GONE);
    }



    private void setUnfollowing(){
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        mAddFriend.setVisibility(View.VISIBLE);
        mUnFriend.setVisibility(View.GONE);
        mEditProfileButton.setVisibility(View.GONE);
    }

    private void setCurrentUsersProfile(){
        Log.d(TAG, "setFollowing: updating UI for showing this user their own profile");
        mAddFriend.setVisibility(View.GONE);
        mUnFriend.setVisibility(View.GONE);
        mEditProfileButton.setVisibility(View.VISIBLE);
    }

    private void isFollowing(){
        Log.d(TAG, "isFollowing: checking if following this users.");
        setUnfollowing();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild(getString(R.string.field_user_id)).equalTo(mUser.getUserid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue());

                    setFollowing();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void init(){

        //set profile widget
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        Query query1 = reference1.child(getString(R.string.user_account_settings))
                .orderByChild(getString(R.string.field_user_id)).equalTo(mUser.getUserid());
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found UserAccountSettings:" + singleSnapshot.getValue(UserAccountSettings.class).toString());

                    UserSettings settings = new UserSettings();
                    settings.setUser(mUser);
                    settings.setSettings(singleSnapshot.getValue(UserAccountSettings.class));
                    setProfileWidgets(settings);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //get user photos

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
        Query query2 = reference2
                .child(getString(R.string.dbms_user_status))
                .child(mUser.getUserid());

        query2.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<PhotoStatus>  statuses = new ArrayList <PhotoStatus>();
                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    statuses.add(singleSnapshot.getValue(PhotoStatus.class));
                }

                setupImageGrid(statuses);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });

    }

    private void setupImageGrid(final ArrayList<PhotoStatus> statuses){
        //setup our image grid
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        mGridView.setColumnWidth(imageWidth);

        ArrayList<String> imgUrls = new ArrayList<String>();
        for(int i = 0; i < statuses.size(); i++){
            imgUrls.add(statuses.get(i).getImage_path());
        }

        // revers the ally list
        //  Collections.reverse(imgUrls);

        GridImageAdapter adapter = new GridImageAdapter(getActivity(),R.layout.profile_grid_images_view,
                "", imgUrls);
        mGridView.setAdapter(adapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOnGridImageSelectedListener.onGridImageSelected(statuses.get(position), ACTIVITY_NUM);
            }
        });


    }


    private User getUserObjFormBundle(){

        Log.d(TAG, "getUserObjFormBundle: "+ getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null){
            return bundle.getParcelable(getString(R.string.intent_user));
        }else{
            return null;
        }

    }

    @Override
    public void onAttach(Context context) {
        try{
            mOnGridImageSelectedListener = (OnGridImageSelectedListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
        super.onAttach(context);
    }

//    private void setupGridView(){
//        Log.d(TAG, "setupGridView: setting up grid view");
//
//        final ArrayList<PhotoStatus> statuses = new ArrayList<>();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//        Query query = reference
//                .child(getString(R.string.dbms_user_status))
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
//
//                    statuses.add(singleSnapshot.getValue(PhotoStatus.class));
//                }
//                //setup our image grid
//                int gridWidth = getResources().getDisplayMetrics().widthPixels;
//                int imageWidth = gridWidth/NUM_GRID_COLUMNS;
//                mGridView.setColumnWidth(imageWidth);
//
//                ArrayList<String> imgUrls = new ArrayList<String>();
//                for(int i = 0; i < statuses.size(); i++){
//                    imgUrls.add(statuses.get(i).getImage_path());
//                }
//                // revers the ally list
//              //  Collections.reverse(imgUrls);
//
//                GridImageAdapter adapter = new GridImageAdapter(getActivity(),R.layout.profile_grid_images_view,
//                        "", imgUrls);
//                mGridView.setAdapter(adapter);
//
//                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        mOnGridImageSelectedListener.onGridImageSelected(statuses.get(position), ACTIVITY_NUM);
//                    }
//                });
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d(TAG, "onCancelled: query cancelled.");
//            }
//        });
//
//    }

    private void setProfileWidgets(UserSettings userSettings){
        Log.d(TAG, "setProfileWidgets: Attempting to initing profile widget");
        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        Log.d(TAG, "setProfileWidgets: Settign Up" + settings.toString());

        UniversalImageHelper.setImage(settings.getProfilephoto(),mProfilePhoto,null,"");

        mDisplayName.setText(settings.getDisplayname());
        mUserName.setText(settings.getUsername());
        mBiodata.setText(settings.getBiodata());
        mPosts.setText(String.valueOf(settings.getPosts()));
        mFollowers.setText(String.valueOf(settings.getFollowers()));
        mFollowing.setText(String.valueOf(settings.getFollowing()));
        mDescription.setText(settings.getDescription());
        mProgressBar.setVisibility(View.GONE);
    }



    private void setupToolBar() {
        ((ProfileActivity) getActivity()).setSupportActionBar(mToolbar);

        mProfileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, AccountSettingActivity.class));

            }
        });
    }


    private  void setupNavIcon(){
        BottomNavHelper.SetupBottomNav(mBottomNavigationViewEx);
        BottomNavHelper.enableNavClick(mContext,getActivity(),mBottomNavigationViewEx);
        Menu menu = mBottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);
    }




    /*######################Firebase############### */

    private void setupFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
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


             //  setProfileWidgets(mFirebaseRules.getUserSettings(dataSnapshot));

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
