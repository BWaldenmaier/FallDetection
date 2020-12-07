package com.example.falldetection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView itemListView;
    private String[] whoFell = {"Grandma"};
    private String[] date = {"06.12.2020"};
    private String[] time = {"16:12:40"};
    private int[] images = {R.drawable.alert};
    private String loggedInUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemListView = findViewById(R.id.itemListView);
        MyAdapter adapter = new MyAdapter(this, whoFell, date, time, images);
        itemListView.setAdapter(adapter);

        loadFalls();
    }

    private void loadFalls(){
        String url ="http://lxvongobsthndl.ddns.net:3000/update";

        String loggedInUser = getIntent().getStringExtra("username");

        // POST parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", loggedInUser);

        JSONObject jsonObj = new JSONObject(params);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObj, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        MySingleton.getInstance(MainActivity.this).addToRequestque(jsonObjectRequest);

    }

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        String[] rWhoFell;
        String[] rDate;
        String[] rTime;
        int[] rImgs;

        MyAdapter(Context c, String[] whoFell, String[] date, String []time, int[] imgs) {
            super(c, R.layout.item_row, R.id.itemwhoFell, whoFell);
            this.context = c;
            this.rWhoFell = whoFell;
            this.rTime = time;
            this.rDate = date;
            this.rImgs = imgs;
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.item_row, parent, false);
            ImageView images = row.findViewById(R.id.itemIcon);
            TextView mywhoFell = row.findViewById(R.id.itemwhoFell);
            TextView myDate = row.findViewById(R.id.itemDate);
            TextView myTime = row.findViewById(R.id.itemTime);


            // now set our resources on views
            images.setImageResource(rImgs[position]);
            mywhoFell.setText(rWhoFell[position]);
            myDate.setText(rDate[position]);
            myTime.setText(rTime[position]);

            return row;
        }
    }
}