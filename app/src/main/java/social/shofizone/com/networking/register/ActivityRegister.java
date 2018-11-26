package social.shofizone.com.networking.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import social.shofizone.com.networking.Utilities.FirebaseRules;
import social.shofizone.com.networking.R;
import social.shofizone.com.networking.model.User;

public class ActivityRegister extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private static final String TAG = "ActivityRegister";
    private Context mContext = ActivityRegister.this;

    private EditText mEmail, mUserName, mPassword;
    private TextView mloading;
    private Button mBtSugnUp;
    private ProgressBar mProgressBar;
    private TextView mLogin;
    private String strUserName,strEmail,strPassword;
    private String mAppend = "";

    private FirebaseRules mFirebaseRules;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private String mUserID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mFirebaseRules = new FirebaseRules(ActivityRegister.this);
        fields();
         setupFirebaseAuth();
        init();


        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityRegister.this,ActivityLogin.class));
            }
        });

    }


    private void fields(){
        mEmail = (EditText) findViewById(R.id.registration_email);
        mUserName = (EditText) findViewById(R.id.registration_username);
        mPassword = (EditText) findViewById(R.id.registration_Password);
        mBtSugnUp = (Button)findViewById(R.id.registration_signup);
        mProgressBar = (ProgressBar)findViewById(R.id.registration_progressBar);
        mloading = (TextView)findViewById(R.id.registration_please_wait);
        mLogin = (TextView)findViewById(R.id.already_have_account);

        mProgressBar.setVisibility(View.GONE);
        mloading.setVisibility(View.GONE);


    }

    private void init(){
        mBtSugnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    strEmail = mEmail.getText().toString();
                    strUserName = mUserName.getText().toString();
                    strPassword = mPassword.getText().toString();

                    if(checkInput(strEmail,strUserName,strPassword)){
                            mProgressBar.setVisibility(View.VISIBLE);
                            mloading.setVisibility(View.VISIBLE);
                            mFirebaseRules.registerUserEmail(strEmail,strUserName,strPassword);


                    }
            }
        });
    }

    private boolean checkInput(String strEmail, String strName, String strPassword) {

        if(strEmail.equals("") || strPassword.equals("") || strName.equals("")){
            Toast.makeText(mContext,"Check Fields",Toast.LENGTH_SHORT).show();
            return  false;
        }else{
            return true;
        }
    }



    private void setupFirebaseAuth(){

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();


        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if(user != null){
                    final String userid = user.getUid();
                    mUserID = userid;
                    Log.d(TAG,"onAuthStateChange: Sing_in: "+userid);
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            checkIfUserNameExists(strUserName);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    finish();

                }else{
                    Log.d(TAG,"onAuthStateChange: Sing_out");
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



    // check current user name is exist in database
    private void checkIfUserNameExists(final String username) {
        Log.d(TAG, "checkIfUserNameExists: checking if "+ username+ " Exists");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.user_db))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    if(singleSnapshot.exists()){
                        mAppend = mDatabaseReference.push().getKey().substring(3,10);
                        Log.d(TAG, "onDataChange: We found a match"+ singleSnapshot.getValue(User.class).getUsername());
                        Toast.makeText(mContext,"Username already exits ",Toast.LENGTH_LONG).show();
                    }
                }

                strUserName = strUserName +mAppend;

                mFirebaseRules.addNewUser(strEmail,strUserName,mUserID,"");

                Toast.makeText(mContext, "Sign Up successful ! ", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



}












