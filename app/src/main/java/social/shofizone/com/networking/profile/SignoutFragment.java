package social.shofizone.com.networking.profile;
import android.support.v4.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import social.shofizone.com.networking.R;
import social.shofizone.com.networking.register.ActivityLogin;

import static android.content.ContentValues.TAG;

public class SignoutFragment extends Fragment {

    Button mButtonSignout;
    ProgressBar mProgressBarSignout;
    TextView tvSignOut;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signout,container,false);

        mButtonSignout = (Button) view.findViewById(R.id.buttonSignout);
        tvSignOut = (TextView) view.findViewById(R.id.tv_signout_allert);
        mProgressBarSignout = (ProgressBar) view.findViewById(R.id.progressBarsignout);

        mProgressBarSignout.setVisibility(View.GONE);

        mButtonSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBarSignout.setVisibility(View.VISIBLE);
                mAuth.signOut();
                getActivity().finish();
            }
        });

        setupFirebaseAuth();

        return  view;
    }


    private void setupFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if(firebaseUser != null){
                    Log.d(TAG,"onAuthStateChange: Sing in"+firebaseUser.getUid());
                }else{
                    Log.d(TAG,"onAuthStateChange: Loging out ");
                        Intent intent =  new Intent(getActivity(), ActivityLogin.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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
