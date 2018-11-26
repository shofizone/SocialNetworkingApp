package social.shofizone.com.networking.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import social.shofizone.com.networking.Utilities.BottomNavHelper;
import social.shofizone.com.networking.R;
import social.shofizone.com.networking.SectionPagerAdapter;
import social.shofizone.com.networking.Utilities.UniversalImageLoader;
import social.shofizone.com.networking.register.ActivityLogin;

public class HomeActivity extends AppCompatActivity {

        private FirebaseAuth mAuth;
        private FirebaseAuth.AuthStateListener mAuthStateListener;

    private static final String TAG = "HomeActivity";
    Context mContext = HomeActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupNavIcon();
        setupViewPager();
        setupFirebaseAuth();
        initImageLoader();

        //mAuth.signOut();
    }




    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }


    private void checkUser(FirebaseUser firebaseUser){
        if(firebaseUser == null){
            startActivity(new Intent(mContext, ActivityLogin.class));
        }
    }

    private void setupFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
               checkUser(firebaseUser);
                if(firebaseUser != null){
                    Log.d(TAG,"onAuthStateChange: Sing in"+firebaseUser.getUid());
                }else{
                    Log.d(TAG,"onAuthStateChange: Sing in");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private  void setupNavIcon(){

        BottomNavigationViewEx bottomNavigationViewEx =  (BottomNavigationViewEx) findViewById (R.id.bottom_navigation_item);

        BottomNavHelper.SetupBottomNav(bottomNavigationViewEx);
        BottomNavHelper.enableNavClick(HomeActivity.this,this,bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();

        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

    }


    public void setupViewPager(){

        SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        sectionPagerAdapter.addFragment(new CameraFragment());
        sectionPagerAdapter.addFragment(new HomeFragment());
        sectionPagerAdapter.addFragment(new MessageFragment());
        ViewPager viewPager = (ViewPager)findViewById(R.id.container_center);
        viewPager.setAdapter(sectionPagerAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs_home);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera2);

        View view1 = getLayoutInflater().inflate(R.layout.home_logo_view, null);
        view1.findViewById(R.id.logo_icon);
        tabLayout.getTabAt(1).setCustomView(view1);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_notification2);
        tabLayout.setMinimumWidth(200);
        tabLayout.setSelectedTabIndicatorHeight(2);


    }
}
