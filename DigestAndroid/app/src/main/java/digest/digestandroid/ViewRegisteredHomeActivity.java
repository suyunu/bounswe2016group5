package digest.digestandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Response;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import digest.digestandroid.Models.Channel;
import digest.digestandroid.Models.Topic;
import digest.digestandroid.api.APIHandler;
import digest.digestandroid.fragments.RegisteredHomeFollowedFragment;
import digest.digestandroid.fragments.RegisteredHomeHomeFragment;
import digest.digestandroid.fragments.RegisteredHomeProfileFragment;
import digest.digestandroid.fragments.RegisteredHomeTrendFragment;
/**
 * Created by Sahin on 11/1/2016.
 */

public class ViewRegisteredHomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SearchView searchView;
    private ImageView advancedSearchView;
    private TabLayout tabLayout;
    public static ViewPager viewPager;

    private static RegisteredHomeHomeFragment homeHomeFragment;
    private static RegisteredHomeTrendFragment homeTrendFragment ;
    private static RegisteredHomeFollowedFragment homeFollowedFragment;
    private static RegisteredHomeProfileFragment homeProfileFragment;



    //--------------------------  ABOVE IS FIELD VARIABLES  -------------------------------------------
    //--------------------------  BELOW IS OVERRIDE-CREATE FUNCTIONS  ---------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.registered_home_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setTitle("@"+Cache.getInstance().getUser().getUsername());

        setContentView(R.layout.activity_view_registered_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

        searchView = (SearchView) findViewById(R.id.search_view_home);
        searchView.setQueryHint("Enter a topic name..");

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        searchView.clearFocus();
                        final Response.Listener<String> tagListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                            ArrayList<Topic> arrayList = serializeTopicsFromJson(response);
                            CacheTopiclist.getInstance().setTagTopics(arrayList);

                            Intent intent = new Intent( getApplicationContext(), ViewSearchActivity.class);
                            startActivity(intent);
                            }
                        };

                        APIHandler.getInstance().searchWithTag(query,tagListener);

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                }
        );


        advancedSearchView = (ImageView) findViewById(R.id.advanced_search_button_inhome);
        advancedSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdvancedSearchActivity.class);
                intent.putExtra("previous", "home");
                startActivity(intent);
            }
        });

        //--------------------------  ABOVE IS TOOLBAR  ------------------------------------






        Response.Listener<String> startHomePageResponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                // SET CHANNEL LIST IN THE RESPONSE

                final ArrayList<Channel> channelList = serializeChannelsFromJson(response);
                Cache.getInstance().setUserChannels(channelList);

                //--------------------------  BELOW IS RECYCLERVIEW  -------------------------------------------
                viewPager = (ViewPager) findViewById(R.id.viewpager_home);
                defineViewPager(viewPager);

                tabLayout = (TabLayout) findViewById(R.id.tabs_home);
                tabLayout.setupWithViewPager(viewPager);
                loadViewPager();

                Log.d("Heyyo",""+Cache.getInstance().getUserChannels());


            }
        };

        // GET USER CHANNELS BEFORE LOADING HOME PAGE TABS


        APIHandler.getInstance().getChannelsOfUser(Cache.getInstance().getUser(), startHomePageResponseListener);



    }

    //--------------------------  ABOVE IS OVERRIDE-CREATE FUNCTIONS  ------------------------------------
    //--------------------------  BELOW IS FRAGMENT FUNCTIONS  -------------------------------------------

    private void defineViewPager(ViewPager viewPager) {
        ViewRegisteredHomeActivity.HomePagerAdapter adapter = new ViewRegisteredHomeActivity.HomePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RegisteredHomeHomeFragment(), "HOME");
        adapter.addFragment(new RegisteredHomeTrendFragment(), "TRENDING");
        adapter.addFragment(new RegisteredHomeFollowedFragment(), "FOLLOWED");
        adapter.addFragment(new RegisteredHomeProfileFragment(), "PROFILE");
        viewPager.setAdapter(adapter);
    }

    private void loadViewPager(){
        homeHomeFragment = (RegisteredHomeHomeFragment)((HomePagerAdapter)viewPager.getAdapter()).getItem(0);
        //homeHomeFragment.initializeInfo();
        homeTrendFragment = (RegisteredHomeTrendFragment)((ViewRegisteredHomeActivity.HomePagerAdapter)ViewRegisteredHomeActivity.viewPager.getAdapter()).getItem(1);
        homeFollowedFragment = (RegisteredHomeFollowedFragment)((ViewRegisteredHomeActivity.HomePagerAdapter)ViewRegisteredHomeActivity.viewPager.getAdapter()).getItem(2);
        homeProfileFragment = (RegisteredHomeProfileFragment)((ViewRegisteredHomeActivity.HomePagerAdapter)ViewRegisteredHomeActivity.viewPager.getAdapter()).getItem(3);
    }

    //--------------------------  ABOVE IS FRAGMENT FUNCTIONS  ------------------------------------------
    //--------------------------  BELOW IS OVERRIDE-OPTIONS FUNCTION  -----------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {

            case R.id.action_log_out:

                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();

                return true;
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_create_topic:
                intent = new Intent(getApplicationContext(), CreateTopicFragmentsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_add_channel:
                intent = new Intent(getApplicationContext(), AddChannelActivity.class);
                startActivity(intent);

                return true;

            case R.id.action_refresh:
                String currentFragment = CacheTopiclist.getInstance().getCurrentFragment();
                Log.d("Refresh is pressed","Current fragment is"+ currentFragment);

                if(currentFragment.equals("Home")){
                    APIHandler.getInstance().getRecentTopics(15,topicListQueryListenerAndLoader(currentFragment,homeHomeFragment.homeRecyclerView));
                }else if(currentFragment.equals("Trending")){
                    APIHandler.getInstance().getTrendingTopics(Cache.getInstance().getUser(),topicListQueryListenerAndLoader(currentFragment,homeTrendFragment.trendingRecyclerView));
                }else if(currentFragment.equals("Followed")){
                    APIHandler.getInstance().getFollowedTopics(Cache.getInstance().getUser(),topicListQueryListenerAndLoader(currentFragment,homeFollowedFragment.followedRecyclerView));
                    APIHandler.getInstance().getChannelsOfSubscribedTopics(Cache.getInstance().getUser(),topicListQueryListenerAndLoader(currentFragment+"2",homeFollowedFragment.followedChannelsRecyclerView));
                }else if(currentFragment.equals("Profile")){
                    APIHandler.getInstance().getAllTopicsOfAUser(Cache.getInstance().getUser(),topicListQueryListenerAndLoader(currentFragment,homeProfileFragment.profileRecyclerView));
                    APIHandler.getInstance().getChannelsOfUser(Cache.getInstance().getUser(),topicListQueryListenerAndLoader(currentFragment+"2",homeProfileFragment.profileChannelsRecyclerView));
                }else{
                    Log.d("HEEY","This fragment name is not expected !!! ");
                }
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    //--------------------------  ABOVE IS OVERRIDE-OPTIONS FUNCTION  -----------------------------------
    //--------------------------  BELOW IS LISTENER FUNCTIONS  ------------------------------------------

    public Response.Listener<String> topicListQueryListenerAndLoader(final String currentFragment,final RecyclerView currentRecyclerView){
        return
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("AA",""+response.length());

                if(currentFragment.equals("Home")){
                    final ArrayList<Topic> arrayList = serializeTopicsFromJson(response);
                    CacheTopiclist.getInstance().setRecentTopics(arrayList);
                    loadTopics(currentRecyclerView,arrayList);


                }else if(currentFragment.equals("Trending")){
                    final ArrayList<Topic> arrayList = serializeTopicsFromJson(response);
                    CacheTopiclist.getInstance().setTrendingTopics(arrayList);
                    loadTopics(currentRecyclerView,arrayList);


                }else if(currentFragment.equals("Followed")){

                    final ArrayList<Topic> arrayList = new ArrayList<Topic>();

                    response = response.substring(1,response.length()-1);
                    String[] topicIds = response.split(",");

                    final int numberOfFollowedTopics = topicIds.length;
                    Response.Listener<Topic> getTopicListener = new Response.Listener<Topic>() {
                        @Override
                        public void onResponse(Topic response) {
                            arrayList.add(response);
                            if(arrayList.size() == numberOfFollowedTopics ) {
                                CacheTopiclist.getInstance().setFollowedTopics(arrayList);
                                loadTopics(currentRecyclerView, arrayList);
                            }
                        }
                    };

                    for(int i = 0; i < numberOfFollowedTopics; i++){
                        APIHandler.getInstance().getTopic("", Integer.parseInt(topicIds[i]), getTopicListener);
                    }


                }else if(currentFragment.equals("Profile")) {
                    final ArrayList<Topic> arrayList = serializeTopicsFromJson(response);
                    CacheTopiclist.getInstance().setUserTopics(arrayList);
                    loadTopics(currentRecyclerView, arrayList);

                }else if(currentFragment.equals("Profile2")){

                    Log.d("Hey"," "+Cache.getInstance().getUser().getId()+" Profile2");
                    final ArrayList<Channel> channelList = serializeChannelsFromJson(response);
                    CacheTopiclist.getInstance().setUserChannels(channelList);

                    Log.d("Hey",""+channelList.toString());
                    loadChannels(currentRecyclerView, channelList,false);

                }else if(currentFragment.equals("Followed2")){

                    Log.d("Hey"," What happened, where are channels? Followed2 ");
                    final ArrayList<Channel> channelList = serializeChannelsFromJson(response);

                    CacheTopiclist.getInstance().setFollowedChannels(channelList);

                    Log.d("Hey",""+channelList.toString());
                    loadChannels(currentRecyclerView, channelList,true);


                }else{
                    Log.d("HEEY","This fragment name is not expected !!! ");
                }
            }
        };
    }

    public void loadTopics(RecyclerView currentRecyclerView,final ArrayList<Topic> currentTopicList){
        RecyclerView.Adapter homeAdapter = new HomeAdapter(currentTopicList);
        currentRecyclerView.setAdapter(homeAdapter);

        ((HomeAdapter) homeAdapter).setOnItemClickListener(new HomeAdapter.HomeClickListener() {
            @Override
            public void onItemClick(int pos, View v) {
                Log.d("" + pos, v.toString());

                final int clickedTopicId = currentTopicList.get(pos).getId();




                Response.Listener<Topic> getTopicListener = new Response.Listener<Topic>() {
                    @Override
                    public void onResponse(Topic response) {
                        Log.d("Success", response.toString());
                        Cache.getInstance().setTopic(response);


                        Response.Listener<String> getRelatedTopicsListener = new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {

                                final ArrayList<Topic> arrayList = serializeTopicsFromJson(response);
                                CacheTopiclist.getInstance().setRelatedTopicsOfaTopic(arrayList);

                                Intent intent = new Intent(getApplicationContext(), ViewTopicActivity.class);
                                startActivity(intent);
                            }
                        };

                        APIHandler.getInstance().getRelatedTopicsOfaTopic(clickedTopicId,getRelatedTopicsListener);
                    }
                };
                APIHandler.getInstance().getTopic("", clickedTopicId, getTopicListener);
            }
        });
    }
    public static ArrayList<Topic> serializeTopicsFromJson(String resp){

        final ArrayList<Topic> resultArrayList = new ArrayList<>();

        try {
            JSONArray obj = (JSONArray) new JSONTokener(resp).nextValue();
            int topicNumber = obj.length();
            for (int i = 0; i < topicNumber; i++) {
                JSONObject tempObj = (JSONObject) obj.get(i);
                Topic tempTop = (new Gson()).fromJson(tempObj.toString(), Topic.class);
                resultArrayList.add(tempTop);
            }
        } catch (JSONException e) {}


        return resultArrayList;
    }

    public void loadChannels(RecyclerView currentRecyclerView,final ArrayList<Channel> currentChannelList, boolean tmpProgrsVisiblty){
        RecyclerView.Adapter channelAdapter = new ChannelAdapter(currentChannelList,tmpProgrsVisiblty);
        currentRecyclerView.setAdapter(channelAdapter);



        Log.d("In load channels",""+currentRecyclerView.toString());
        ((ChannelAdapter) channelAdapter).setOnItemClickListener(new ChannelAdapter.ChannelClickListener() {
            @Override
            public void onItemClick(int pos, View v) {
                Log.d("" + pos, v.toString());

                int clickedChannelId = currentChannelList.get(pos).getId();
                final String theChannelName = currentChannelList.get(pos).getName();


                Response.Listener<String> getChannelsListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Success", response.toString());

                        final ArrayList<Topic> arrayList = serializeTopicsFromJson(response);
                        CacheTopiclist.getInstance().setChannelTopics(arrayList);

                        Intent intent = new Intent(getApplicationContext(), ViewChannelTopicsActivity.class);
                        intent.putExtra("cname", theChannelName);
                        startActivity(intent);
                    }
                };
                APIHandler.getInstance().getTopicsFromChannel(clickedChannelId, getChannelsListener);
            }
        });
    }
    public static ArrayList<Channel> serializeChannelsFromJson(String resp){
        final ArrayList<Channel> resultArrayList = new ArrayList<>();

        Log.d("aa",""+resp);

        try {
            JSONArray obj = (JSONArray) new JSONTokener(resp).nextValue();
            int channelNumber = obj.length();
            for (int i = 0; i < channelNumber; i++) {
                if( !( ((obj.get(i)).toString()).equals("null"))) {
                    Log.d("---aa"+channelNumber,"a"+((obj.get(i)).toString())+"a");
                    JSONObject tempObj = (JSONObject) obj.get(i);
                    Channel tempCh = (new Gson()).fromJson(tempObj.toString(), Channel.class);
                    resultArrayList.add(tempCh);
                }
            }
        } catch (JSONException e) {}


        return resultArrayList;
    }

    //--------------------------  ABOVE IS LISTENER FUNCTIONS  ------------------------------------------
    //--------------------------  BELOW IS ADAPTER CLASS  ------------------------------------------


    public class HomePagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> homeFragments = new ArrayList<>();
        private final List<String> homeFragmentTitles = new ArrayList<>();

        public HomePagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return homeFragments.get(position);
        }

        @Override
        public int getCount() {
            return homeFragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            homeFragments.add(fragment);
            homeFragmentTitles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return homeFragmentTitles.get(position);
        }
    }

}
