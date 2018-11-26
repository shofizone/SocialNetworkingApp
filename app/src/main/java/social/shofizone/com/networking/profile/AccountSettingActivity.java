package social.shofizone.com.networking.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import social.shofizone.com.networking.Utilities.BottomNavHelper;
import social.shofizone.com.networking.R;
import social.shofizone.com.networking.SectionStatePagerAdepter;
import social.shofizone.com.networking.Utilities.FirebaseRules;

public class AccountSettingActivity extends AppCompatActivity{
    private static final String TAG = "AccountSettingActivity";
    Context mContext = AccountSettingActivity.this;
    ViewPager mViewPager;
    RelativeLayout mRelativeLayout;
    public SectionStatePagerAdepter mSectionStatePagerAdepter;





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_settings);
        mViewPager = (ViewPager)findViewById(R.id.container_center);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.rele1_activity_account_setting);



        setupNavIcon();
        setupSetupSettingList();
        setupFragment();
        getIncomingIntent();

       // setupToolBar();
        ImageView backArrowImage = (ImageView) findViewById(R.id.imageView_back_arrow);
        backArrowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }



    private void getIncomingIntent(){
        Intent intent = getIntent();
        //if there is  an imageURL attached in extra ,then it chosen form gallery/photofragmen
        if(intent.hasExtra(getString(R.string.selected_image))|| intent.hasExtra(getString(R.string.selected_bitmap))){
            Log.d(TAG, "getIncomingIntent: New incoming image url");

            if(intent.getStringExtra(getString(R.string.return_to_fragment)).equals(getString(R.string.edit_profile_fragment))){
                        //set the new profile image

                if(intent.hasExtra(getString(R.string.selected_image))){

                    FirebaseRules firebaseRules = new FirebaseRules(AccountSettingActivity.this);
                    firebaseRules.upLoadPhoto(getString(R.string.profile_photo),null,0,
                            intent.getStringExtra(getString(R.string.selected_image)),null);

                }else if(intent.hasExtra(getString(R.string.selected_bitmap))){
                    FirebaseRules firebaseRules = new FirebaseRules(AccountSettingActivity.this);
                    firebaseRules.upLoadPhoto(getString(R.string.profile_photo),null,0,
                            null,(Bitmap)intent.getParcelableExtra(getString(R.string.selected_bitmap)));
                }


            }
       }


        if(intent.hasExtra(getString(R.string.calling_activity))){
            Log.d(TAG, "getIncomingIntent: Receve from profile activity and starting fragment " + mSectionStatePagerAdepter.getFragmentNumber(getString(R.string.edit_profile_fragment)));
            setViewPager(mSectionStatePagerAdepter.getFragmentNumber("Edit Profile"));


        }
    }



    private  void setupFragment(){
        mSectionStatePagerAdepter = new SectionStatePagerAdepter(getSupportFragmentManager());
        mSectionStatePagerAdepter.addFragment(new EditProfileFragment(), getString(R.string.edit_profile_fragment));
        mSectionStatePagerAdepter.addFragment(new SignoutFragment(),getString(R.string.sign_out));
    }
    private void  setupSetupSettingList(){
        ListView profileSettingListView = (ListView)findViewById(R.id.account_setting_list);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(getString(R.string.edit_profile_fragment));
        arrayList.add(getString(R.string.sign_out));

        ArrayAdapter arrayAdapter = new ArrayAdapter(mContext,android.R.layout.simple_expandable_list_item_1,arrayList);

        profileSettingListView.setAdapter(arrayAdapter);
        profileSettingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setViewPager(i);
            }
        });

    }

    private  void setupNavIcon(){

        BottomNavigationViewEx bottomNavigationViewEx = findViewById (R.id.bottom_navigation_item);

        BottomNavHelper.SetupBottomNav(bottomNavigationViewEx);
        BottomNavHelper.enableNavClick(mContext,this,bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();

        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);


    }
    public void setViewPager(int fragmentNumber){
        mRelativeLayout.setVisibility(View.GONE);
        mViewPager.setAdapter(mSectionStatePagerAdepter);
        mViewPager.setCurrentItem(fragmentNumber);

    }





}
