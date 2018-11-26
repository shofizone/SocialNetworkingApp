package social.shofizone.com.networking.share;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import social.shofizone.com.networking.SectionPagerAdapter;
import social.shofizone.com.networking.Utilities.BottomNavHelper;
import social.shofizone.com.networking.R;
import social.shofizone.com.networking.Utilities.Permissions;

public class ShareActivity extends AppCompatActivity {
    private static final String TAG = "ShareActivity";
    Context mContext = ShareActivity.this;

    private ViewPager mViewPager;





public int getTask(){
    Log.d(TAG, "getTask: Task" + getIntent().getFlags());
    return getIntent().getFlags();
    
}




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);






        //check for permissions
        if(checkPermissionsArray(Permissions.PERMISSIONS)){

            setupViewpager();

        }else{
            verifyPermissions(Permissions.PERMISSIONS);
        }
       //setupNavIcon();
    }
//return tab number
    // Gallery = 0
    //photo = 1


    public int getCurrentTabNumber(){

        return mViewPager.getCurrentItem();

    }


private void setupViewpager(){
    SectionPagerAdapter adapter =new SectionPagerAdapter(getSupportFragmentManager());

    adapter.addFragment(new GalleryFragment());
    adapter.addFragment(new PhotoFragment());

    mViewPager = (ViewPager) findViewById(R.id.container_center);
    mViewPager.setAdapter(adapter);

    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_bottom_share_activity);
    tabLayout.setupWithViewPager(mViewPager);

    tabLayout.getTabAt(0).setText(getString(R.string.gallery));
    tabLayout.getTabAt(1).setText(getString(R.string.photo));
}











    //Verify all the permissions methods

    private void verifyPermissions(String[] permissions) {

        Log.d(TAG, "verifyPermissions: Verifying permissions! ");

        ActivityCompat.requestPermissions(ShareActivity.this,permissions,1);
    }


    //checking  permission of array
    public boolean checkPermissionsArray(String[] permissions){
        Log.d(TAG, "checkPermissionsArray: checking permisssions");

        for(int i=0;i<permissions.length;i++){

            String check = permissions[i];
            if (!checkPermission(check)){
                return false;
            }

        }
        return  true;

    }


    //checking single permission
    public boolean checkPermission(String permission) {
        Log.d(TAG, "checkPermission: Checking permission "+ permission);
        int permissionRequest = ActivityCompat.checkSelfPermission(mContext,permission);

        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermission: \n Permission was not granted for "+ permission);

            return  false;

        }else{
            Log.d(TAG, "checkPermission: \n Permission was granted for "+ permission);
            return  true;
        }
    }


    private  void setupNavIcon(){

        BottomNavigationViewEx bottomNavigationViewEx =  (BottomNavigationViewEx) findViewById (R.id.bottom_navigation_item);

        BottomNavHelper.SetupBottomNav(bottomNavigationViewEx);
        BottomNavHelper.enableNavClick(ShareActivity.this,this,bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();

        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);






    }
}
