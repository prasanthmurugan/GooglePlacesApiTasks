package com.example.admin.googleplacesapitasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.android.gms.maps.GoogleMap;

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

/**
 * Created by Admin on 2/2/2016.
 */
public class GooglePlacesAdapter extends ArrayAdapter<GooglePlacesAdapter.GooglePlaceDescription> implements Filterable {
    public  static final String API = "https://maps.googleapis.com/maps/api/place";
    public static final  String TYPE = "/autocomplete";
    public static final String JSON = "/json";
    public static final String BROWSER_KEY = "AIzaSyCzRtbkkFUhmHDWq4DhGtXlTwziBZxuMKg";
    ArrayList<GooglePlaceDescription> mArraylist=new ArrayList<>();


    public GooglePlacesAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public int getCount() {
        if(mArraylist==null){
            Log.v("LOG","Warn, null filteredData");
            return 0;
        }else{
            return mArraylist.size();
        }
    }

    @Override
    public GooglePlaceDescription getItem(int position) {
        return mArraylist.get(position);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.e("place-constraint",constraint.toString());
                FilterResults filterResults =new FilterResults();
                mArraylist=PlaceListAsyncTask(constraint.toString());
                filterResults.values=mArraylist;
                Log.e("place-mArraylist",mArraylist.toString());
                filterResults.count=mArraylist.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    ArrayList<GooglePlaceDescription> PlaceListAsyncTask(String params){
            ArrayList<GooglePlaceDescription> placesList=new ArrayList<>();
            String webData="",data="";
            StringBuilder stringBuilder=new StringBuilder(API+TYPE+JSON);
            stringBuilder.append("?input="+params);
            stringBuilder.append("&types=geocode");
            stringBuilder.append("&language=en");
            stringBuilder.append("&key=" + BROWSER_KEY);
            Log.e("place-StringBuilder",stringBuilder.toString());
            try {
                URL url = new URL(stringBuilder.toString());
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                InputStream io = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(io));
                while ((data=bufferedReader.readLine())!=null){
                    webData+=data+"\n";
                }
                JSONObject jsonObject = new JSONObject(webData);
                JSONArray jsonArray = jsonObject.getJSONArray("predictions");
                Log.e("place-jsonArray",""+jsonArray);
                for(int i=0;i<jsonArray.length();i++)
                {
                    placesList.add(new GooglePlaceDescription(jsonArray.getJSONObject(i).getString("place_id"), jsonArray.getJSONObject(i).getString("description")));
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return placesList;
    }

    class GooglePlaceDescription{
        String placeId,placeDescription;
        public GooglePlaceDescription(String placeId,String placeDescription)
        {
            this.placeId = placeId;
            this.placeDescription = placeDescription;
        }

        @Override
        public String toString() {
            return placeDescription;
        }
    }
}
