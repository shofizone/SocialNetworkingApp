package social.shofizone.com.networking.Utilities;

        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v4.app.Fragment;
        import android.util.Log;
        import android.view.GestureDetector;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.Query;
        import com.google.firebase.database.ValueEventListener;
        import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.Locale;
        import java.util.TimeZone;


        import social.shofizone.com.networking.R;
        import social.shofizone.com.networking.model.PhotoStatus;
        import social.shofizone.com.networking.model.User;
        import social.shofizone.com.networking.model.UserAccountSettings;


public class ViewPostFragment extends Fragment {

        private static final String TAG = "ViewPostFragment";

//    public interface OnCommentThreadSelectedListener{
//        void onCommentThreadSelectedListener(PhotoStatus photo);
//    }
//    OnCommentThreadSelectedListener mOnCommentThreadSelectedListener;


        public ViewPostFragment(){
                super();
                setArguments(new Bundle());
        }

        //firebase
        private FirebaseAuth mAuth;
        private FirebaseAuth.AuthStateListener mAuthStateListener;
        private FirebaseDatabase mFirebaseDatabase;
        private DatabaseReference mDatabaseReference;
        private FirebaseRules mFirebaseRules;


        //widgets
        private SquareImageView mPostImage;
        private BottomNavigationViewEx mBottomNavigationViewEx;
        private TextView mBackLabel, mCaption, mUsername, mTimestamp, mLikes, mComments;
        private ImageView mBackArrow, mEllipses, mHeartRed, mHeartWhite, mProfileImage, mComment;


        //vars
        PhotoStatus mPhotoStatus;
        private int mActivityNumber = 0;
        private String photoUsername = "";
        private String profilePhotoUrl = "";
        private UserAccountSettings mUserAccountSettings;
        private GestureDetector mGestureDetector;

        // private Heart mHeart;
        private Boolean mLikedByCurrentUser;
        private StringBuilder mUsers;
        private String mLikesString = "";
        private User mCurrentUser;
        Heart mHeart;

        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.fragment_view_post, container, false);

                mPostImage = (SquareImageView) view.findViewById(R.id.post_image);
                mBottomNavigationViewEx = (BottomNavigationViewEx) view.findViewById(R.id.bottom_navigation_item);
                mBackArrow = (ImageView) view.findViewById(R.id.backArrow);
                mBackLabel = (TextView) view.findViewById(R.id.tvBackLabel);
                mCaption = (TextView) view.findViewById(R.id.image_caption);
                mUsername = (TextView) view.findViewById(R.id.username);
                mTimestamp = (TextView) view.findViewById(R.id.image_time_posted);
                mEllipses = (ImageView) view.findViewById(R.id.ivEllipses);
                mHeartRed = (ImageView) view.findViewById(R.id.image_like_blue);
                mHeartWhite = (ImageView) view.findViewById(R.id.image_heart);
                mProfileImage = (ImageView) view.findViewById(R.id.profile_photo);
                mLikes = (TextView) view.findViewById(R.id.image_likes);
                mComment = (ImageView) view.findViewById(R.id.speech_bubble);
                mComments = (TextView) view.findViewById(R.id.image_comments_link);

                mHeartRed.setVisibility(View.VISIBLE);
            mHeartWhite.setVisibility(View.GONE);


                    mHeart = new Heart(mHeartWhite, mHeartRed);
                    mGestureDetector = new GestureDetector(getActivity(),new GestureListener());

            mBackArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: navigating back");
                    getActivity().getSupportFragmentManager().popBackStack();

                }
            });



                try{
                        mPhotoStatus = getPhotoFromBundle();
                        UniversalImageLoader.setImage(mPhotoStatus.getImage_path(), mPostImage, null, "");
                        mActivityNumber = getActivityNumFromBundle();
                        String photo_id = getPhotoFromBundle().getPhoto_id();



                }catch (NullPointerException e){
                        Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage() );
                }

            setupNavIcon();


            setupFirebaseAuth();
            photoDetails();
            testToggle();


            return view;
        }



        private void testToggle(){

            mHeartRed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mHeart.toggleLike();
                }
            });

            mHeartWhite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mHeart.toggleLike();
                }
            });






            mHeartRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return mGestureDetector.onTouchEvent(motionEvent);
                }
            });

            mHeartWhite.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return mGestureDetector.onTouchEvent(motionEvent);
                }
            });
        }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.d(TAG, "onDoubleTapEvent: Doble tab event");
            mHeart.toggleLike();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }
    }


    private void setupWidgets() {
                String timestampDiff = getTimestampDifference();
                if (!timestampDiff.equals("0")) {
                        mTimestamp.setText(timestampDiff + " DAYS AGO");
                } else {
                        mTimestamp.setText("TODAY");
                }

                mCaption.setText(getPhotoFromBundle().getCaption());


                UniversalImageLoader.setImage(mUserAccountSettings.getProfilephoto(),mProfileImage,null,"");
                mUsername.setText(mUserAccountSettings.getDisplayname());


        }



        public void photoDetails(){
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                Query query = reference
                        .child(getString(R.string.user_account_settings))
                        .orderByChild(getString(R.string.field_user_id))
                        .equalTo(mPhotoStatus.getUser_id());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                            mUserAccountSettings = singleSnapshot.getValue(UserAccountSettings.class);
                        }

                        setupWidgets();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: query cancled");
                    }

                });


        }

        /**
         * Returns a string representing the number of days ago the post was made
         * @return
         */
        private String getTimestampDifference(){
                Log.d(TAG, "getTimestampDifference: getting timestamp difference." );

                String difference = "";
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Dhaka"));//google 'android list of timezones'
                Date today = c.getTime();
                sdf.format(today);
                Date timestamp;
                final String photoTimestamp = getPhotoFromBundle().getDate_created();
                try{
                        timestamp = sdf.parse(photoTimestamp);
                        difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 )));
                }catch (ParseException e){
                        Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage() );
                        difference = "0";
                }
                Log.d(TAG, "getTimestampDifference: "+ difference);
                return difference;

        }



        /**
         * retrieve the activity number from the incoming bundle from profileActivity interface
         * @return
         */
        private int getActivityNumFromBundle(){
                Log.d(TAG, "getActivityNumFromBundle: arguments: " + getArguments());

                Bundle bundle = this.getArguments();
                if(bundle != null) {
                        return bundle.getInt(getString(R.string.activity_number));
                }else{
                        return 0;
                }
        }
        /**
         *  retrieve the photo from the incoming bundle from profileActivity interface
         * @return
         */

        private PhotoStatus getPhotoFromBundle(){
                Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());

                Bundle bundle = this.getArguments();
                if(bundle != null) {
                        return bundle.getParcelable(getString(R.string.photo));
                }else{
                        return null;
                }
        }

        private  void setupNavIcon(){
                BottomNavHelper.SetupBottomNav(mBottomNavigationViewEx);
                BottomNavHelper.enableNavClick(getActivity(),getActivity(),mBottomNavigationViewEx);
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









