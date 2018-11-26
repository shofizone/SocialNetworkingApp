package social.shofizone.com.networking.share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import social.shofizone.com.networking.R;
import social.shofizone.com.networking.Utilities.FirebaseRules;
import social.shofizone.com.networking.Utilities.UniversalImageLoader;
import social.shofizone.com.networking.home.HomeActivity;

public class NextActivity extends AppCompatActivity {

    private static final String TAG = "NextActivity";
    Context mContext = NextActivity.this;

    private FirebaseRules mFirebaseRules;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    private String mAppend = "file:/";
    private int mImageCount;
    private String mImgUrl;
    Intent mIntent;
    TextView mTvShare;
    ImageView mIvShareImage;
    Button mBtShare;
    private EditText mCaption;
    private Bitmap mBitmap;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        mFirebaseRules = new FirebaseRules(mContext);
        mCaption = (EditText) findViewById(R.id.caption_next_activity) ;
        setupFirebaseAuth(); // setup fireBase
        init(); // init click listener
        setImage(); // get the image form incoming intent



    }

    public void init(){
        ImageView back = (ImageView) findViewById(R.id.image_view_back_next_activity);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Closing the next activity");

                finish();
            }
        });

        mTvShare = (TextView)findViewById(R.id.tv_share_next);

        mTvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //up load the image to fire base
                Toast.makeText(mContext,"Uploading PhotoStatus",Toast.LENGTH_LONG).show();
                String caption = mCaption.getText().toString();
                if(mIntent.hasExtra(getString(R.string.selected_image))){
                    mImgUrl = mIntent.getStringExtra(getString(R.string.selected_image));
                    mFirebaseRules.upLoadPhoto(getString(R.string.new_photo),caption,mImageCount,mImgUrl,null);
                }else if(mIntent.hasExtra(getString(R.string.selected_bitmap))){
                    mBitmap =(Bitmap) mIntent.getParcelableExtra(getString(R.string.selected_bitmap));
                    mFirebaseRules.upLoadPhoto(getString(R.string.new_photo),caption,mImageCount,null,mBitmap);
                }
                Intent intent = new Intent (mContext, HomeActivity.class);
                mContext.startActivity(intent);
            }
        });

        mBtShare = (Button)findViewById(R.id.bt_next_share);
        mBtShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //up load the image to fire base
                Toast.makeText(mContext,"Uploading PhotoStatus",Toast.LENGTH_LONG).show();
                String caption = mCaption.getText().toString();
                if(mIntent.hasExtra(getString(R.string.selected_image))){
                    mImgUrl = mIntent.getStringExtra(getString(R.string.selected_image));
                    mFirebaseRules.upLoadPhoto(getString(R.string.new_photo),caption,mImageCount,mImgUrl,null);
                }else if(mIntent.hasExtra(getString(R.string.selected_bitmap))){
                    mBitmap =(Bitmap) mIntent.getParcelableExtra(getString(R.string.selected_bitmap));
                    mFirebaseRules.upLoadPhoto(getString(R.string.new_photo),caption,mImageCount,null,mBitmap);
                }
                    Intent intent = new Intent (mContext, HomeActivity.class);
                    mContext.startActivity(intent);
            }
        });

    }


    private void someMethod(){

        /*
                step 1 : create a data model for photos
                step 2 add parties for photos (caption,date,image_url,photo_id,tags,user_id)
                step 3 count the number of photo user already have
                step 4
                        a)upload the photo to firebase stograge
                        b)insert the photo to photo node
                        c) insert the photo to user_photo node
         */

    }


    private void setImage(){
         mIntent = getIntent();

        mIvShareImage = (ImageView)findViewById(R.id.imageshare_next_activity);

        if(mIntent.hasExtra(getString(R.string.selected_image))){
            mImgUrl = mIntent.getStringExtra(getString(R.string.selected_image));
            UniversalImageLoader.setImage(mImgUrl,
                    mIvShareImage,null,mAppend);
            Log.d(TAG, "setImage: We gont new image");

        }else if(mIntent.hasExtra(getString(R.string.selected_bitmap))){
            mBitmap =(Bitmap) mIntent.getParcelableExtra(getString(R.string.selected_bitmap));
            Log.d(TAG, "setImage: We got new bitmap");
            mIvShareImage.setImageBitmap(mBitmap);
        }

    }



    /*######################Firebase############### */

    private void setupFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        Log.d(TAG, "onDataChange: Our Image count" + mImageCount);

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

                mImageCount =mFirebaseRules.getImageCount(dataSnapshot);

                Log.d(TAG, "onDataChange: Our Image count" + mImageCount);

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
