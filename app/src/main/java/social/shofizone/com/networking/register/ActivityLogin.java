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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import social.shofizone.com.networking.Utilities.FirebaseRules;
import social.shofizone.com.networking.R;
import social.shofizone.com.networking.home.HomeActivity;

public class ActivityLogin extends AppCompatActivity {

    private Button mLoginButton;
    private TextView mSignUp;
    private ProgressBar mProgressBar;
    private TextView mloading;
    private  EditText mEmail, mPassword;

    private String mStrEmail,nStrPassword;
    private String mAppend;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseRules mFirebaseRules;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    private static final String TAG = "ActivityLogin";
    Context mContext = ActivityLogin.this;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        mLoginButton = (Button) findViewById(R.id.login_button_login);
        mSignUp = (TextView) findViewById(R.id.dont_have_account);
        mloading = (TextView) findViewById(R.id.login_please_wait);
        mProgressBar = (ProgressBar) findViewById(R.id.login_progressBar);
        mEmail =(EditText) findViewById(R.id.login_email);
        mPassword =(EditText) findViewById(R.id.login_password);
        mProgressBar.setVisibility(View.GONE);
        mloading.setVisibility(View.GONE);



        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityLogin.this,ActivityRegister.class));

            }
        });

        loginMethod();
        setupFirebaseAuth();
    }

    private boolean isStringNull(String string){
        if(string.equals("")){
            return true;
        }else{
            return false;
        }
    }

    public void loginMethod(){
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "Attempting to login.... ");

                mStrEmail = mEmail.getText().toString();
                nStrPassword = mPassword.getText().toString();

                if(mStrEmail.equals("") || nStrPassword.equals("")){
                    Toast.makeText(mContext,"You must provide email and password",Toast.LENGTH_SHORT).show();
                }else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    mloading.setVisibility(View.VISIBLE);


                    Log.d(TAG, "This is Email: "+ mStrEmail + " And started to authenticate");

                    mAuth.signInWithEmailAndPassword(mStrEmail, nStrPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        Log.d(TAG, "signInWithEmail: Sign in Successful");
                                        Toast.makeText(mContext,"Sign in Successful",Toast.LENGTH_SHORT).show();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        startActivity(new Intent(mContext, HomeActivity.class));
                                        finish();


                                        mProgressBar.setVisibility(View.GONE);
                                        mloading.setVisibility(View.GONE);
                                    } else {

                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(mContext, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();


                                        mProgressBar.setVisibility(View.GONE);
                                        mloading.setVisibility(View.GONE);
                                    }


                                }
                            });


                }

                if(mAuth.getCurrentUser() != null){
                    startActivity(new Intent(mContext, HomeActivity.class));
                    finish();
                }

            }
        });
    }





    /*//  ########################################Firebase ################################ */


    private void setupFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
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
}
