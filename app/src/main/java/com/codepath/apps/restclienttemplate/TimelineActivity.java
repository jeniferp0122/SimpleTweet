package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;


public class TimelineActivity extends AppCompatActivity {
     public static final String TAG  = "";
    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    SwipeRefreshLayout swipeContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline2);
         client = TwitterApp.getRestClient(this);
        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light));
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG,"fetching new data!");
                populatedHomeTimeline();
            }
        });
         //Find the recycler view
        rvTweets = findViewById(R.id.rvTweets);
        //initialize list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this,tweets);
        // recycler view setup : layout manager and the adapter
       rvTweets.setLayoutManager(new LinearLayoutManager(this));
       rvTweets.setAdapter(adapter);
         populatedHomeTimeline();

    }

    private void populatedHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG,"onSuccess!" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                   adapter.clear();
                    adapter.addAll(Tweet.fromJsonArray(jsonArray));
                    // Stop animation (This will be after 3 seconds)
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                   Log.e(TAG,"Json exception",e);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG,"onFailure" + response ,throwable);

            }
        });
    }
}