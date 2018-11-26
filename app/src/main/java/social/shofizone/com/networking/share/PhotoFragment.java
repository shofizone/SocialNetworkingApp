package social.shofizone.com.networking.share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import social.shofizone.com.networking.R;
import social.shofizone.com.networking.Utilities.Permissions;
import social.shofizone.com.networking.profile.AccountSettingActivity;

public class PhotoFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "PhotoFragment";

    private static final int  PHOTO_FRAGMENT_NUMBER = 1;
    private static final int  GALLERY_FRAGMENT_NUMBER = 2;
    private static final int  CAMERA_REQUEST_CODE = 5;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        Log.d(TAG, "onCreateView: PhotoFragment Started");


        Button btnLaunchCamera = (Button) view.findViewById(R.id.button_launch_camera);


        btnLaunchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Launching Camera");

                if( ( (ShareActivity)getActivity() ).getCurrentTabNumber() == PHOTO_FRAGMENT_NUMBER ){

                    if(( (ShareActivity)getActivity() ).checkPermission(Permissions.CAMERA_PERMISSION)){
                        Log.d(TAG, "onClick: Startign camera");

                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent,CAMERA_REQUEST_CODE);


                    }else{
                        Intent intent = new Intent(getActivity(),ShareActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                    }

                }
            }
        });


        return view;
    }

    private boolean isRootTask(){
        if(((ShareActivity)getActivity()).getTask()==0){
            return  true;
        }else{
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == CAMERA_REQUEST_CODE){
            Log.d(TAG, "onActivityResult: PhotoStatus hasbeen taken ");
            Log.d(TAG, "onActivityResult: Attempting nevegate to final share screen ");

            //navigate to final share screen

            Log.d(TAG, "onClick: Navigating to final share screen");
            Bitmap bitmap;
            bitmap = (Bitmap) data.getExtras().get("data");
            if(isRootTask()){
                try{
                    Log.d(TAG, "onActivityResult: received new bitmap from camera: " + bitmap);
                    Intent intent = new Intent(getActivity(), NextActivity.class);
                    intent.putExtra(getString(R.string.selected_bitmap), bitmap);
                    startActivity(intent);
                }catch (NullPointerException e){
                    Log.d(TAG, "onActivityResult: NullPointerException: " + e.getMessage());
                }

            }else{

                try{
                    Log.d(TAG, "onActivityResult: received new bitmap from camera: " + bitmap);
                    Intent intent = new Intent(getActivity(), AccountSettingActivity.class);
                    intent.putExtra(getString(R.string.selected_bitmap), bitmap);
                    intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile_fragment));
                    startActivity(intent);
                    getActivity().finish();
                }catch (NullPointerException e){
                    Log.d(TAG, "onActivityResult: NullPointerException: " + e.getMessage());
                }
            }

        }
    }
}
