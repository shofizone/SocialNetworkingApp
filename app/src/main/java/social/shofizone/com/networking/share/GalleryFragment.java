package social.shofizone.com.networking.share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;

import social.shofizone.com.networking.R;
import social.shofizone.com.networking.Utilities.FIleSearch;
import social.shofizone.com.networking.Utilities.FilesPaths;
import social.shofizone.com.networking.Utilities.GridImageAdapter;
import social.shofizone.com.networking.Utilities.UniversalImageLoader;
import social.shofizone.com.networking.profile.AccountSettingActivity;

public class GalleryFragment extends android.support.v4.app.Fragment{

    private static final String TAG = "GalleryFragment";



    //widgets

    private GridView mGridView;
    private ProgressBar mProgressBar;
    private ImageView mGalleryImage;
    private Spinner directorySpinner;
    private ArrayList<String> directories;
    private static final int NUMBER_GRID_COL = 3;
    private String mAppend = "file:/";
    private String mSelectedImage;

    //variables

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        Log.d(TAG, "onCreateView: GalleryFragment Started");

        mGalleryImage = (ImageView) view.findViewById(R.id.gallery_image_big);
        mGridView = (GridView) view.findViewById(R.id.gird_view_gallery);
        directorySpinner = (Spinner) view.findViewById(R.id.spinner_directory);


        mProgressBar = (ProgressBar) view.findViewById(R.id.spin_kit);
        //setup spin_kit
//        DoubleBounce doubleBounce = new DoubleBounce();
//        mProgressBar.setIndeterminateDrawable(doubleBounce);


        directories = new ArrayList<>();

        mProgressBar.setVisibility(View.GONE);

        ImageView close = (ImageView) view.findViewById(R.id.image_view_close_share_gallery);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Closing the gallery");

                getActivity().finish();
            }
        });


        TextView nextScreen = (TextView) view.findViewById(R.id.tv_next_gallery);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick: Navigating to final share screen");

                if(isRootTask()){
                    Intent intent = new Intent(getActivity(),NextActivity.class);
                    intent.putExtra(getString(R.string.selected_image),mSelectedImage);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getActivity(),AccountSettingActivity.class);
                    intent.putExtra(getString(R.string.selected_image),mSelectedImage);
                    intent.putExtra(getString(R.string.return_to_fragment),getString(R.string.edit_profile_fragment));
                    startActivity(intent);
                    getActivity().finish();
                }



            }
        });

        init();

        initImageLoader();




        return view;
    }

    private boolean isRootTask(){
        if(((ShareActivity)getActivity()).getTask()==0){
            return  true;
        }else{
            return false;
        }
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }



    private void init(){

        FilesPaths filesPaths = new FilesPaths();
//        check for other folder "storage/emulated/0/pictures"

        if(FIleSearch.getDirectoryPath(filesPaths.PICTURES) != null){
            directories = FIleSearch.getDirectoryPath(filesPaths.PICTURES);
        }
        directories.add(filesPaths.CAMERA);

        ArrayList<String> directoryNames= new ArrayList<>();
        for(int i=0;i<directories.size();i++){
            int index = directories.get(i).lastIndexOf("/");

            String string = directories.get(i).substring(index+1);
            string = Character.toUpperCase(string.charAt(0)) + string.substring(1);
            directoryNames.add(string);
        }




        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,directoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);

        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d(TAG, "onItemClick: selected: "+ directories.get(position));

                //setup image grid for selected directory chosen

                setupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }




    private void setupGridView(String selectedDir){
        Log.d(TAG, "setupGridView: Directory chosen"+ selectedDir);
        final ArrayList<String> imgUrls = FIleSearch.getFilePaths(selectedDir);

        // revers the ally list
        Collections.reverse(imgUrls);

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUMBER_GRID_COL;

        mGridView.setColumnWidth(imageWidth);

        //user grid adapter to adapt images

        GridImageAdapter adapter = new GridImageAdapter(getActivity(),R.layout.profile_grid_images_view,mAppend, imgUrls);
        mGridView.setAdapter(adapter);


        //set the first image to display , when the activity fragment  view inflated


        try{
            setImage(imgUrls.get(0),mGalleryImage,mAppend);
            mSelectedImage = imgUrls.get(0);
        }catch (ArrayIndexOutOfBoundsException e){

            Log.d(TAG, "setupGridView: ArrayIndexOutOfBoundsException "+ e.getMessage());

        }


        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: Selected image" + imgUrls.get(i));

                setImage(imgUrls.get(i),mGalleryImage,mAppend);
                mSelectedImage = imgUrls.get(i);
            }
        });
    }






    private void setImage(String imgUrl,ImageView image,String append){

        Log.d(TAG, "setImage: Setting image");

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(append + imgUrl, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

}
