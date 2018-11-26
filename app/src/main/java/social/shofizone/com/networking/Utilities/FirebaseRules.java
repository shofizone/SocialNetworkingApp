package social.shofizone.com.networking.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import social.shofizone.com.networking.R;
import social.shofizone.com.networking.model.PhotoStatus;
import social.shofizone.com.networking.model.User;
import social.shofizone.com.networking.model.UserAccountSettings;
import social.shofizone.com.networking.model.UserSettings;
import social.shofizone.com.networking.profile.AccountSettingActivity;

public class FirebaseRules {
    private static final String TAG = "FirebaseRules";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    StorageReference mStorageReference;

    Context mContext;
    String mUserId;

    private double mPhotoUploadProgress = 0;


    public FirebaseRules(Context context) {
        mAuth= FirebaseAuth.getInstance();
        mContext = context;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

       if(mAuth.getCurrentUser() != null){
           mUserId = mAuth.getCurrentUser().getUid();

       }
    }


    public boolean  registerUserEmail(String email,String userName,String password){

        final boolean[] status = {false};
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    status[0] = !task.isSuccessful();
                    Toast.makeText(mContext,"Registration is Unsuccessful",Toast.LENGTH_LONG).show();
                }else {
                    mUserId = mAuth.getCurrentUser().getUid();
                    status[0] = task.isSuccessful();
                    Toast.makeText(mContext,"Registration is Successful",Toast.LENGTH_LONG).show();
                }

            }


        });

        return status[0];

    }




//    public boolean checkExistUser(String username, DataSnapshot datasnapshot){
//        Log.d(TAG, "checkIfUsernameExists: checking if " + username + " already exists.");
//
//        User user = new User();
//
//        for (DataSnapshot ds: datasnapshot.child(mUserId).getChildren()){
//            Log.d(TAG, "checkIfUsernameExists: datasnapshot: " + ds);
//
//            user.setUsername(ds.getValue(User.class).getUsername());
//            Log.d(TAG, "checkIfUsernameExists: username: " + user.getUsername());
//
//            if(StringManipulation.expandUsername(user.getUsername()).equals(username)){
//                Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + user.getUsername());
//                return true;
//            }
//        }
//        return false;
//    }




public void addNewUser(String email, String username,String userId,String profilePhoto){

        User getInfo = new User(username,email,mUserId,123);
        mDatabaseReference.child(mContext.getString(R.string.user_db))
                .child(mUserId)
                .setValue(getInfo);

    UserAccountSettings userAccountSettings = new UserAccountSettings(
            "",
            "",
            "",
            username,
            "",
            mUserId,
            0,
            0,
            0);

    mDatabaseReference.child(mContext.getString(R.string.user_account_settings))
            .child(mUserId)
            .setValue(userAccountSettings);
}






public  UserSettings getUserSettings(DataSnapshot dataSnapshot){
    Log.d(TAG, "getUserAccountSettings: Retrieving data form firebse");

    UserAccountSettings settings =  new UserAccountSettings();
    User user = new User();

    for(DataSnapshot ds: dataSnapshot.getChildren()){


            //user Accountsetting node

            if(ds.getKey().equals(mContext.getString(R.string.user_account_settings))){
                Log.d(TAG, "getUserAccountSettings: dataSnapshot"+ds);


                try{


                            settings.setDisplayname(
                                    ds.child(mUserId)
                                    .getValue(UserAccountSettings.class)
                                    .getDisplayname()
                            );


                            settings.setUsername(
                                    ds.child(mUserId)
                                            .getValue(UserAccountSettings.class)
                                            .getUsername()
                            );

                            settings.setBiodata(
                                    ds.child(mUserId)
                                            .getValue(UserAccountSettings.class)
                                            .getBiodata()
                            );

                            settings.setDescription(
                                    ds.child(mUserId)
                                            .getValue(UserAccountSettings.class)
                                            .getDescription()
                            );

                            settings.setFollowers(
                                    ds.child(mUserId)
                                            .getValue(UserAccountSettings.class)
                                            .getFollowers()
                            );

                            settings.setFollowing(
                                    ds.child(mUserId)
                                            .getValue(UserAccountSettings.class)
                                            .getFollowing()
                            );

                            settings.setPosts(
                                    ds.child(mUserId)
                                            .getValue(UserAccountSettings.class)
                                            .getPosts()
                            );


                            settings.setProfilephoto(
                                    ds.child(mUserId)
                                            .getValue(UserAccountSettings.class)
                                            .getProfilephoto()
                            );

                }catch (NullPointerException e){
                    Log.e(TAG, "getUserAccountSettings: NullPointerException: "+ e.getMessage() );
                }

            }



        //user  node

        if(ds.getKey().equals(mContext.getString(R.string.user_db))){
            Log.d(TAG, "getUserAccountSettings: dataSnapshot"+ds);


            try{


                user.setUsername(
                        ds.child(mUserId)
                                .getValue(User.class)
                                .getUsername()
                );

                user.setEmail(
                        ds.child(mUserId)
                                .getValue(User.class)
                                .getEmail()
                );

                user.setPhonenumber(
                        ds.child(mUserId)
                                .getValue(User.class)
                                .getPhonenumber()
                );





            }catch (NullPointerException e){
                Log.e(TAG, "getUser: NullPointerException: "+ e.getMessage() );
            }

        }





    }


    return new UserSettings(user,settings);

}

    public void updateUsername(String username) {

        Log.d(TAG, "updateUsername: Updating user name to "+ username);
        mDatabaseReference.child(mContext.getString(R.string.user_db))
                .child(mUserId)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
        mDatabaseReference.child(mContext.getString(R.string.user_account_settings))
                .child(mUserId)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);

    }


    // Updating user other info

    public void updateUserAccountSetting(String displaName, String bioData,String description, long phoneNumber){

        Log.d(TAG, "updateUserAccountSetting: Updating user settings");


            if(displaName != null){
                mDatabaseReference.child(mContext.getString(R.string.user_account_settings))
                        .child(mUserId)
                        .child(mContext.getString(R.string.field_display_name))
                        .setValue(displaName);


            }
            if(phoneNumber != 0){
            mDatabaseReference.child(mContext.getString(R.string.user_db))
                    .child(mUserId)
                    .child(mContext.getString(R.string.field_phone_number))
                    .setValue(phoneNumber);

            }
            if(description != null){
            mDatabaseReference.child(mContext.getString(R.string.user_account_settings))
                    .child(mUserId)
                    .child(mContext.getString(R.string.field_description))
                    .setValue(description);

            }
            if(bioData != null) {

                mDatabaseReference.child(mContext.getString(R.string.user_account_settings))
                        .child(mUserId)
                        .child(mContext.getString(R.string.field_bio_data))
                        .setValue(bioData);

            }

        Toast.makeText(mContext,"Information Updated",Toast.LENGTH_LONG).show();










    }

    public int getImageCount(DataSnapshot dataSnapshot) {

        int count = 0;
        for(DataSnapshot ds: dataSnapshot.child(mContext.getString(R.string.dbms_user_status))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getChildren()){
            count++;
        }

        return  count;
    }


    private String randomeNumberForImage(){
         final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
         SecureRandom rnd = new SecureRandom();
        int len = 10;

            StringBuilder sb = new StringBuilder( len );
            for( int i = 0; i < len; i++ )
                sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
            return sb.toString();

    }
    public void upLoadPhoto(String photoType, final String caption, int image_count, String imgurl,Bitmap bitmap) {

        Log.d(TAG, "upLoadPhoto: Attempting to upload photo");

        final FilesPaths filesPaths = new FilesPaths();
        //case 1 new photo

        if(photoType.equals(mContext.getString(R.string.new_photo))){
            Log.d(TAG, "upLoadPhoto: Uploading new  photo");
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filesPaths.FIREBASE_IMAGE_STORAGE + "/"+ user_id +"/"+ "image_" +(image_count + 1)+randomeNumberForImage());

            if(bitmap == null){
                bitmap = ImageManager.getBitmap(imgurl);
            }
            //convert image url to bitmap

            byte[] bytes = ImageManager.getByteFromBitmap(bitmap,100);
            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   // Uri firebaseUrl = taskSnapshot.getUploadSessionUri();

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri firebaseUrl = urlTask.getResult();

                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                    //add the new photo to 'photos' node and 'user_photos' node

                    addPhotoToDatabase(caption,firebaseUrl.toString());

                    //navigate to the main feed so the user can see their photo


//                    Intent intent = new Intent (mContext, HomeActivity.class);
//                    mContext.startActivity(intent);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: PhotoStatus upload failed.");
                    Toast.makeText(mContext, "PhotoStatus upload failed ", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if(progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });





        }else if(photoType.equals(mContext.getString(R.string.profile_photo))){
            ((AccountSettingActivity)mContext).setViewPager(
                    ((AccountSettingActivity)mContext).mSectionStatePagerAdepter
                            .getFragmentNumber(mContext.getString(R.string.edit_profile_fragment))
            );
            Log.d(TAG, "upLoadPhoto: Uploading new profile photo");




            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final StorageReference storageReference = mStorageReference
                    .child(filesPaths.FIREBASE_IMAGE_STORAGE + "/"+ user_id +"/"+ "profile_photo");

            //convert image url to bitmap

            if(bitmap == null){
                Log.d(TAG, "upLoadPhoto: Get the bitmap from camera");
                bitmap = ImageManager.getBitmap(imgurl);
            }

            byte[] bytes = ImageManager.getByteFromBitmap(bitmap,100);
            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri firebaseUri = urlTask.getResult();

                   // Uri firebaseUri = taskSnapshot.getUploadSessionUri();
                   // Uri  firebaseUri = taskSnapshot.getDownloadUrl();
                    //Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();


                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                 //insert into the account setting node

                   setProfiePhoto(firebaseUri.toString());




                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: PhotoStatus upload failed.");
                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if(progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "Photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });


        }

        // case 2 new profile photo


    }

    public void setProfiePhoto(String url) {
        Log.d(TAG, "setProfiePhoto: Setting porfile photo");

        mDatabaseReference.child(mContext.getString(R.string.user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo))
                .setValue(url);



    }
    private String  getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Dhaka"));
        return  sdf.format(new Date());
    }

    private void addPhotoToDatabase(String caption, String firebaseUrl) {

        Log.d(TAG, "addPhotoToDatabase: Adding photo to databse");
        String tags = StringManipulation.getTags(caption);
        String newPhotoKye = mDatabaseReference.child(mContext.getString(R.string.dbms_status)).push().getKey();
        PhotoStatus status = new PhotoStatus();
        status.setCaption(caption);
        status.setDate_created(getTimestamp()) ;
        status.setImage_path(firebaseUrl);
        status.setTags(tags);
        status.setPhoto_id(newPhotoKye);
        status.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());


        //insert into database
        mDatabaseReference.child(mContext.getString(R.string.dbms_user_status))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(newPhotoKye).setValue(status);

        mDatabaseReference.child(mContext.getString(R.string.dbms_status)).child(newPhotoKye).setValue(status);

    }


}
