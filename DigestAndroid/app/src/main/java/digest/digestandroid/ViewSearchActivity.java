package digest.digestandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.volley.Response;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import digest.digestandroid.Models.Topic;
import digest.digestandroid.api.APIHandler;

/**
 * Created by sahin on 27.11.2016.
 */

public class ViewSearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SearchView searchView;
    private RecyclerView searchRecyclerView;
    private RecyclerView.Adapter searchAdapter;
    private RecyclerView.LayoutManager searchLayoutManager;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // TODO : Adjust sizes of items in the menu
        inflater.inflate(R.menu.search_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                return true;


            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_search);

        toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchView = (SearchView) findViewById(R.id.search_view_search);
        searchView.setQueryHint("Enter a topic name..");
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {


                        searchView.clearFocus();

                        final Response.Listener<String> tagListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try{
                                    JSONArray obj = (JSONArray) new JSONTokener(response).nextValue();
                                    ArrayList<Topic> arrayList = new ArrayList<Topic>();


                                    int topicNumber = obj.length();
                                    for(int i = 0 ; i < topicNumber ; i++){
                                        JSONObject tempObj = (JSONObject) obj.get(i);
                                        Topic tempTop = new Topic();

                                        Gson gson = new Gson();
                                        tempTop = gson.fromJson(tempObj.toString(),Topic.class);
                                        arrayList.add(tempTop);
                                    }

                                    CacheTopiclist.getInstance().setTagTopics(arrayList);

                                    searchAdapter = new HomeAdapter(CacheTopiclist.getInstance().getTagTopics());
                                    searchRecyclerView.setAdapter(searchAdapter);

                                }catch (JSONException e){}

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

        searchRecyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);
        searchLayoutManager = new LinearLayoutManager(this);
        searchRecyclerView.setLayoutManager(searchLayoutManager);
        searchRecyclerView.setItemAnimator(new DefaultItemAnimator());


        searchAdapter = new HomeAdapter(CacheTopiclist.getInstance().getTagTopics());
        searchRecyclerView.setAdapter(searchAdapter);

    }

}
