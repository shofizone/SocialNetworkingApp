package social.shofizone.com.networking.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import social.shofizone.com.networking.R;
import social.shofizone.com.networking.Utilities.MainFeedListAdapter;
import social.shofizone.com.networking.model.PhotoStatus;
import social.shofizone.com.networking.model.UserAccountSettings;

public class HomeFragment extends android.support.v4.app.Fragment{
    private static final String TAG = "HomeFragment";

    //vars
    private ArrayList<PhotoStatus> mPhotos;
    private ArrayList<PhotoStatus> mPaginatedPhotos;
    private ArrayList<String> mFollowing;
    private int recursionIterator = 0;
       private ListView mListView;
   // private ElasticListView mListView;
    private MainFeedListAdapter adapter;
    private int resultsCount = 0;
    private ArrayList<UserAccountSettings> mUserAccountSettings;
    //    private ArrayList<UserStories> mAllUserStories = new ArrayList<>();
    private JSONArray mMasterStoriesArray;

    private RecyclerView mRecyclerView;
   // public StoriesRecyclerViewAdapter mStoriesAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
      mListView = (ListView) view.findViewById(R.id.listView);
       // mListView = (ElasticListView) view.findViewById(R.id.listView);

      //  initListViewRefresh();
     // getFollowing();

        return view;

    }




    private void getFollowing() {
        Log.d(TAG, "getFollowing: searching for following");

     //   clearAll();
        //also add your own id to the list
        mFollowing.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Query query = FirebaseDatabase.getInstance().getReference()
                .child(getActivity().getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                ;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "getFollowing: found user: " + singleSnapshot
                            .child(getString(R.string.field_user_id)).getValue());

                    mFollowing.add(singleSnapshot
                            .child(getString(R.string.field_user_id)).getValue().toString());
                }

               // getPhotos();
//                getMyUserAccountSettings();
               // getFriendsAccountSettings();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }


    private void getPhotos(){
        Log.d(TAG, "getPhotos: getting list of photos");

        for(int i = 0; i < mFollowing.size(); i++){
            final int count = i;
            Query query = FirebaseDatabase.getInstance().getReference()
                    .child(getActivity().getString(R.string.dbms_user_status))
                    .child(mFollowing.get(i))
                    .orderByChild(getString(R.string.field_user_id))
                    .equalTo(mFollowing.get(i))
                    ;
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                        mPhotos.add(singleSnapshot.getValue(PhotoStatus.class));
                    }
                    if(count== mFollowing.size() - 1) {
                        displayPhotos();
                    }
                }



                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: query cancelled.");
                }
            });

        }
    }


    private void displayPhotos(){
//        mPaginatedPhotos = new ArrayList<>();
        if(mPhotos != null){

            try{

                //sort for newest to oldest
                Collections.sort(mPhotos, new Comparator<PhotoStatus>() {
                    public int compare(PhotoStatus o1, PhotoStatus o2) {
                        return o2.getDate_created().compareTo(o1.getDate_created());
                    }
                });

                //we want to load 10 at a time. So if there is more than 10, just load 10 to start
                int iterations = mPhotos.size();
                if(iterations > 10){
                    iterations = 10;
                }
//
                resultsCount = 0;
                for(int i = 0; i < iterations; i++){
                    mPaginatedPhotos.add(mPhotos.get(i));
                    resultsCount++;
                    Log.d(TAG, "displayPhotos: adding a photo to paginated list: " + mPhotos.get(i).getPhoto_id());
                }

                adapter = new MainFeedListAdapter(getActivity(), R.layout.layout_mainfeed_listitem, mPaginatedPhotos);
                mListView.setAdapter(adapter);

                // Notify update is done
            //    mListView.notifyUpdated();

            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage() );
            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage() );
            }
        }
    }
}
