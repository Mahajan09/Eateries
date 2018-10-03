package dev.rism.eateries;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by risha on 18-04-2018.
 */

public class HomeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    SearchView searchView;
    ArrayList<RestaurantModel> list;
    ProgressDialog dialog;
    RestaurantAdapter adapter;
    RecyclerView rv;
    int total_count=0;
    String qu;
    GothamTextView tvIndicator;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tvIndicator=(GothamTextView)findViewById(R.id.tv_indicator);
        dialog=new ProgressDialog(HomeActivity.this);
        dialog.setTitle("Loading");
        dialog.setMessage("Fetching data from server");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        list=new ArrayList<>();
        adapter=new RestaurantAdapter(list);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        rv=(RecyclerView) findViewById(R.id.rv_restaurants);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
        rv.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
               if (total_count<=current_page) // If all the restaurants has been added to the list, do nothing
               {

               }
               else
               {
                  new LoadMoreTask().execute(qu,current_page+"");
               }
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        toolbar.setTitleTextColor(Color.parseColor("#dcd9cd"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_home,menu);
        searchView= (SearchView) MenuItemCompat.getActionView(menu.getItem(0));
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_filter:
                FilterDialog dialog=new FilterDialog(HomeActivity.this);
                dialog.setDialogResult(new FilterDialog.OnDialogResult() {
                    @Override
                    public void finish(String city, String cuisine) {
                        JSONObject jsonObject=new JSONObject();
                        try {
                            jsonObject.put("city",city);
                            jsonObject.put("cuisine",cuisine);
                            adapter.getFilter().filter(jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        new FetchTask().execute(query);
        qu=query;
        searchView.setIconified(true);
        invalidateOptionsMenu();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
    public void processResponse(JSONObject obj) throws JSONException {
        JSONArray jsonArray=obj.getJSONArray("restaurants");
        total_count=obj.getInt("results_found");
        Log.d("Total",total_count+"");
        if (jsonArray.length()!=0)
        {
            tvIndicator.setVisibility(View.GONE);
        }
        else
        {
            tvIndicator.setVisibility(View.VISIBLE);
        }
        for (int i=0;i<jsonArray.length();i++)
        {
            JSONObject object=jsonArray.getJSONObject(i).getJSONObject("restaurant");
            RestaurantModel model=new RestaurantModel();
            model.setName(object.getString("name"));
            JSONObject lobj=object.getJSONObject("location");
            model.setAdd(lobj.getString("address"));
            model.setLocation(lobj.getString("city"));
            Log.d("CITY",lobj.getString("city"));
            JSONObject userObj=object.getJSONObject("user_rating");
            model.setRating(userObj.getDouble("aggregate_rating"));
            model.setCuisine(object.getString("cuisines"));
            list.add(model);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    class FetchTask extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String s;
            HttpURLConnection urlConnection= null;
            BufferedReader bufferedReader=null;
            StringBuilder builder=new StringBuilder();

            try {

                Uri uri=Uri.parse(Constants.BASE_URL).buildUpon().appendQueryParameter("q",strings[0]).build();
                URL url=new URL(uri.toString());
                Log.d("site",uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("user-key",Constants.API_KEY);

                InputStream in=urlConnection.getInputStream();
                bufferedReader =new BufferedReader(new InputStreamReader(in));
                    while ((s=bufferedReader.readLine())!=null)
                    {
                        builder.append(s);
                    }}
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return builder.toString();
        }

            @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
                try {
                    list.clear();
                    processResponse(new JSONObject(s));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
    }
    class LoadMoreTask extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String s;
            HttpURLConnection urlConnection= null;
            BufferedReader bufferedReader=null;
            StringBuilder builder=new StringBuilder();

            try {

                Uri uri=Uri.parse(Constants.BASE_URL).buildUpon().appendQueryParameter("q",strings[0]).appendQueryParameter("start",strings[1]).build();
                URL url=new URL(uri.toString());
                Log.d("site",uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("user-key",Constants.API_KEY);

                InputStream in=urlConnection.getInputStream();
                bufferedReader =new BufferedReader(new InputStreamReader(in));
                while ((s=bufferedReader.readLine())!=null)
                {
                    builder.append(s);
                }}
            catch (IOException e)
            {
                e.printStackTrace();
            }

            finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            try {
                processResponse(new JSONObject(s));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
