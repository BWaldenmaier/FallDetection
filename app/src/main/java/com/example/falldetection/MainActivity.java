package com.example.falldetection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView itemListView;
    private ArrayList<String> mWhoFell = new ArrayList<>();
    private ArrayList<String> mDate = new ArrayList<>();
    private ArrayList<String> mTime = new ArrayList<>();
    private ArrayList<Integer> images = new ArrayList<>();
    private MyAdapter adapter;
    private String loggedInUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            loadFalls();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void makeAdapter(){
        itemListView = findViewById(R.id.itemListView);
        adapter = new MyAdapter(this, mWhoFell, mDate, mTime, images);
        itemListView.setAdapter(adapter);
        itemListView.invalidateViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            loadFalls();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadFalls() throws JSONException {
        String url ="http://lxvongobsthndl.ddns.net:3000/updateFalls";

        loggedInUser = getIntent().getStringExtra("username");

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(loggedInUser);

        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.POST, url, jsonArray, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (mWhoFell != null){
                            mWhoFell.clear();
                        }
                        if (mDate != null){
                            mDate.clear();
                        }
                        if (mTime != null){
                            mTime.clear();
                        }
                        if (images != null){
                            images.clear();
                        }

                        for (int i = 0; i < response.length(); i++){

                            try {
                                JSONObject entry = response.getJSONObject(i);
                                Object id = entry.get("id");
                                String dateAndTime = (String) entry.get("date");
                                String date = dateAndTime.substring(0,10);
                                String yy = date.substring(0,4);
                                String mm = date.substring(5,7);
                                String dd = date.substring(8,10);
                                String germanDate = dd +"."+ mm +"."+yy;
                                String time = dateAndTime.substring(11,19);

                                Object userid = entry.get("userid");

                                mWhoFell.add("Grandma");
                                mDate.add(germanDate);
                                mTime.add(time);
                                images.add(R.mipmap.granny);

                                makeAdapter();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        MySingleton.getInstance(MainActivity.this).addToRequestque(jsonArrayRequest);
    }

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        ArrayList<String> rWhoFell;
        ArrayList<String> rDate;
        ArrayList<String> rTime;
        ArrayList<Integer> rImages;

        MyAdapter(Context c, ArrayList<String> whoFell, ArrayList<String> date, ArrayList<String> time, ArrayList<Integer> images) {
            super(c, R.layout.item_row, R.id.itemwhoFell, whoFell);
            this.context = c;
            this.rWhoFell = whoFell;
            this.rDate = date;
            this.rTime = time;
            this.rImages = images;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.item_row, parent, false);
            ImageView images = row.findViewById(R.id.itemIcon);
            TextView myWhoFell = row.findViewById(R.id.itemwhoFell);
            TextView myDate = row.findViewById(R.id.itemDate);
            TextView myTime = row.findViewById(R.id.itemTime);

            //set our resources on views
            images.setImageResource(rImages.get(position));
            myWhoFell.setText(rWhoFell.get(position));
            myDate.setText(rDate.get(position));
            myTime.setText(rTime.get(position));
            //myDeleteButtons.setImageResource(rdeleteButtons.get(position));
            return row;
        }
    }
}