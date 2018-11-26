package social.shofizone.com.networking.profile;

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

import social.shofizone.com.networking.Utilities.BottomNavHelper;
import social.shofizone.com.networking.Utilities.FirebaseRules;
import social.shofizone.com.networking.Utilities.GridImageAdapter;
import social.shofizone.com.networking.Utilities.UniversalImageHelper;
import social.shofizone.com.networking.R;
import social.shofizone.com.networking.model.PhotoStatus;
import social.shofizone.com.networking.model.User;
import social.shofizone.com.networking.model.UserAccountSettings;
import social.shofizone.com.networking.model.UserSettings;

public class ProfileFragment extends Fragment {

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
    private Button mEditProfileButton;
    ProgressBar mProgressBar;




    //vars
    private int mFollowersCount = 0;
    private int mFollowingCount = 0;
    private int mPostsCount = 0;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
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



        mBottomNavigationViewEx = (BottomNavigationViewEx) view.findViewById(R.id.bottom_navigation_item);
        mContext = getActivity();
        mProfileMenu = (ImageView) view.findViewById(R.id.profile_setting);
        mToolbar = (Toolbar) view.findViewById(R.id.profile_toolbar);
        mFirebaseRules = new FirebaseRules(getActivity());

        mProgressBar = (ProgressBar)view.findViewById(R.id.spin_kit);
        FadingCircle fadingCircle = new FadingCircle();
        mProgressBar.setIndeterminateDrawable(fadingCircle);

        mToolbar.setTitle("");
        mToolbar.setSubtitle("");

        mEditProfileButton =  (Button)view.findViewById(R.id.button_edit_profile_f) ;

        mEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: navigating to "+ mContext.getString(R.string.edit_profile_fragment));

                Intent intent = new Intent(getActivity(),AccountSettingActivity.class);
                intent.putExtra(getString(R.string.calling_activity),getString(R.string.profile_activity));
                startActivity(intent);

            }

        });


        setupToolBar();
        setupNavIcon();
        setupFirebaseAuth();
        setupGridView();

        getFollowersCount();
        getFollowingCount();
        getPostsCount();

        return view;

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

    private void setupGridView(){
        Log.d(TAG, "setupGridView: setting up grid view");

        final ArrayList<PhotoStatus> statuses = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbms_user_status))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                    statuses.add(singleSnapshot.getValue(PhotoStatus.class));


                }


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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
        
    }

    private void getFollowersCount(){
        mFollowersCount = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_followers))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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

    private void getFollowingCount(){
        mFollowingCount = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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

    private void getPostsCount(){
        mPostsCount = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbms_user_status))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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

    private void setProfileWidgets(UserSettings userSettings){
        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

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
