package social.shofizone.com.networking.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import social.shofizone.com.networking.share.ShareActivity;
import social.shofizone.com.networking.home.HomeActivity;
import social.shofizone.com.networking.message.MessageActivity;
import social.shofizone.com.networking.profile.ProfileActivity;
import social.shofizone.com.networking.R;
import social.shofizone.com.networking.search.SearchActivity;

public class BottomNavHelper {

    public static void SetupBottomNav(BottomNavigationViewEx bottomNavigationViewEx){

        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
        bottomNavigationViewEx.setIconSize(30, 30);

    }


    public static void enableNavClick(final Context context, final Activity callingActivity, BottomNavigationViewEx bottomNavigationViewEx){

        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               switch (item.getItemId()){
                   case R.id.home:
                       Intent intent = new Intent(context, HomeActivity.class);
                       context.startActivity(intent);
                       callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                       break;
                   case R.id.search:
                       context.startActivity(new Intent(context, SearchActivity.class));
                       callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                       break;
                   case R.id.add:
                       context.startActivity(new Intent(context, ShareActivity.class));
                       callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                       break;
                   case R.id.notification:
                       context.startActivity(new Intent(context, MessageActivity.class));
                       callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                       break;

                   case R.id.profile:
                       context.startActivity(new Intent(context, ProfileActivity.class));
                       callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                       break;





               }


                return false;
            }
        });
    }


}
