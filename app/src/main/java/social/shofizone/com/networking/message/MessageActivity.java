package social.shofizone.com.networking.message;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import social.shofizone.com.networking.Utilities.BottomNavHelper;
import social.shofizone.com.networking.R;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupNavIcon();
    }


    private  void setupNavIcon(){

        BottomNavigationViewEx bottomNavigationViewEx =  (BottomNavigationViewEx) findViewById (R.id.bottom_navigation_item);

        BottomNavHelper.SetupBottomNav(bottomNavigationViewEx);
        BottomNavHelper.enableNavClick(MessageActivity.this,this,bottomNavigationViewEx);


        Menu menu = bottomNavigationViewEx.getMenu();

        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);






    }
}
