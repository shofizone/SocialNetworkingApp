package social.shofizone.com.networking.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import social.shofizone.com.networking.R;
import social.shofizone.com.networking.Utilities.BottomNavHelper;
import social.shofizone.com.networking.Utilities.ViewPostFragment;
import social.shofizone.com.networking.Utilities.ViewProfileFragment;
import social.shofizone.com.networking.model.PhotoStatus;
import social.shofizone.com.networking.model.User;


public class ProfileActivity extends AppCompatActivity implements
        ProfileFragment.OnGridImageSelectedListener ,

        ViewProfileFragment.OnGridImageSelectedListener{
    @Override
    public void onGridImageSelected(PhotoStatus photo, int activityNumber) {
        Log.d(TAG, "onGridImageSelected: selected an image gridview: " + photo.toString());

        ViewPostFragment fragment = new ViewPostFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putInt(getString(R.string.activity_number), activityNumber);

        fragment.setArguments(args);

        FragmentTransaction transaction  = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_profile, fragment);
        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();
    }


    private static final String TAG = "ProfileActivity";
    Context mContext = ProfileActivity.this;
    ImageView mProfileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

         mProfileImage = (ImageView)findViewById(R.id.profileImage);
         init();
        //setupNavIcon();




    }




    private void init(){
        Log.d(TAG, "init: inflating "+ "Profile fragment");

        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.calling_activity))){
            Log.d(TAG, "init: searching for user object attached as intent extra");
            if(intent.hasExtra(getString(R.string.intent_user))){
                User user = intent.getParcelableExtra(getString(R.string.intent_user));
                if(!user.getUserid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    Log.d(TAG, "init: inflating view profile");
                    ViewProfileFragment fragment = new ViewProfileFragment();
                    Bundle args = new Bundle();
                    args.putParcelable(getString(R.string.intent_user),
                            intent.getParcelableExtra(getString(R.string.intent_user)));
                    fragment.setArguments(args);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container_profile, fragment);
                    transaction.addToBackStack(getString(R.string.view_profile_fragment));
                    transaction.commit();
                }else{
                    Log.d(TAG, "init: inflating Profile");
//                    ProfileFragment fragment = new ProfileFragment();
//                    FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
//                    transaction.replace(R.id.container, fragment);
//                    transaction.addToBackStack(getString(R.string.profile_fragment));
//                    transaction.commit();
                }
            }else{
                Toast.makeText(mContext, "something went wrong", Toast.LENGTH_SHORT).show();
            }

        }else{
            ProfileFragment profileFragment = new ProfileFragment();

            android.support.v4.app.FragmentTransaction transaction =  ProfileActivity.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container_profile,profileFragment);
            transaction.addToBackStack(getString(R.string.profile_fragment));
            transaction.commit();
        }



    }



    private  void setupNavIcon(){

        BottomNavigationViewEx bottomNavigationViewEx = findViewById (R.id.bottom_navigation_item);

        BottomNavHelper.SetupBottomNav(bottomNavigationViewEx);
        BottomNavHelper.enableNavClick(mContext,this,bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();

        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);


    }




}
