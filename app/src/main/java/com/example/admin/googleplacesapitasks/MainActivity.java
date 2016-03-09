package com.example.admin.googleplacesapitasks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    AutoCompleteTextView mAutoCompleteTextView;
    GooglePlacesAdapter mGooglePlacesAdapter;
    String[] array={"first","second item" ,"third item"};
    String lat="11.9138598",lng="79.8144722",placeDescription="";
    GoogleMap outerMapObj;
    SupportMapFragment supportMapFragment;
    TextView mType;
    public static final int REQUEST_CODE=1;
    ArrayList<String> selectedTypes = new ArrayList<>();
    ArrayList<CheckModel> checkModels=new ArrayList<>();
    boolean alreadySelected=false;
    String pipeLinedString="";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK)
        {
            if (requestCode==REQUEST_CODE){
                alreadySelected=true;
                Log.e("came-->","OnActivityResult");
                selectedTypes.clear();
                checkModels = (ArrayList<CheckModel>)data.getExtras().getSerializable("model");
                for (int i=0;i<checkModels.size();i++)
                {
                    Log.e("modelArrayList->",checkModels.get(i).getTypes()+"-->Selected: "+checkModels.get(i).isSelected);
                    if (checkModels.get(i).isSelected)
                    {
                        selectedTypes.add(checkModels.get(i).getTypes());
                    }
                }
                for (int i = 0; i <selectedTypes.size() ; i++) {
                    pipeLinedString+=selectedTypes.get(i).replace(" ","_")+"|";
                }
                Log.e("selectedTypes:",selectedTypes.toString());
                if (selectedTypes.size()==0) {
                    outerMapObj.clear();
                    supportMapFragment.getMapAsync(this);
                }
                else {
                    new NearByPlacesAsyncTask().execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lng + "&radius=5000&types=" + pipeLinedString + "&key=AIzaSyAQx9-J89loUtGPvuosi-1Wtm-bpMsPkf8");
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setUpDefaults();
        setUpEvents();
    }

    void init()
    {
        mAutoCompleteTextView = (AutoCompleteTextView)findViewById(R.id.auto_complete);
        //**GooglePlacesAdapter is the custom adapter used to set the list content for the autocomplete textview
        mGooglePlacesAdapter = new GooglePlacesAdapter(this,android.R.layout.simple_list_item_1);
        //**all the google map fragment are specified by the support map fragment
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        //**getMapAsync(this) will synchronize the map
        supportMapFragment.getMapAsync(this);
        mType=(TextView)findViewById(R.id.type);
    }

    private void setUpDefaults() {
        mAutoCompleteTextView.setAdapter(mGooglePlacesAdapter);
        //**setThreshold will denote the character sequences for which the list should be sorted
        mAutoCompleteTextView.setThreshold(2);
//        ArrayAdapter<String> adapter;
//        adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, array);
//        mAutoCompleteTextView.setAdapter(adapter);
    }

    void setUpEvents()
    {
        mAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alreadySelected=false;
                Log.e("Main-OnItemClick", "" + position);
                //**below statement is used to access the class inside the adapter to get the item placeId and placeDescription
                GooglePlacesAdapter.GooglePlaceDescription googlePlacesDescription = mGooglePlacesAdapter.getItem(position);
                new GetLatLon().execute(googlePlacesDescription.placeId);
                placeDescription = googlePlacesDescription.placeDescription;
            }
        });
        mType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,GoogleTypeActivity.class);
                if (alreadySelected){
                    intent.putExtra("alreadySelected",alreadySelected);
                    intent.putExtra("model",checkModels);
                }
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        //**Called at first when the map is created
        outerMapObj=googleMap ;
        //**addmarker is used to add the marker to the map obj-outerMapObj
        //**MarkerOptions() is the class the contains all the marker option such as position,title,anchor,alpha,draggable
        outerMapObj.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)))
                .title(placeDescription));
        //**CameraPosition class is used to customize the camera view of the map provides various options such as the zoom,target,build
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)))
                .zoom(8)
                .build();

//        outerMapObj.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))));

        //**animate camera is used to set the camera option to the google map
        outerMapObj.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    class GetLatLon extends AsyncTask<String,String,String>{
        String webData="",data="";
        @Override
        protected String doInBackground(String... params) {
            Log.e("main-Came", "asynTask");
            //**stringBuilder contains the url link for the task
            StringBuilder stringBuilder =new StringBuilder(GooglePlacesAdapter.API+"/details"+GooglePlacesAdapter.JSON);
            stringBuilder.append("?placeid="+params[0]);
            stringBuilder.append("&key=" + GooglePlacesAdapter.BROWSER_KEY);
            Log.e("main-string",stringBuilder.toString());
            try {
                URL url = new URL(stringBuilder.toString());
                HttpURLConnection con=(HttpURLConnection)url.openConnection();
                InputStream io=con.getInputStream();
                BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(io));
                while((data=bufferedReader.readLine())!=null){
                    webData+=data+"\n";
//                    Log.e("main=inside-webdata",webData);
                }
            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
//            Log.e("main-webdata",webData);
            return webData;
        }

        @Override
        protected void onPostExecute(String s){
            LatLonParser latLonParser;
            //gson parser object
            Gson gson = new Gson();
            latLonParser = gson.fromJson(s,LatLonParser.class);
            Log.e("lat->",latLonParser.result.geometry.location.lat);
            Log.e("lng->",latLonParser.result.geometry.location.lng);
            lat = latLonParser.result.geometry.location.lat;
            lng = latLonParser.result.geometry.location.lng;
            outerMapObj.clear();
            supportMapFragment.getMapAsync(MainActivity.this);
            super.onPostExecute(s);
        }
    }

    class NearByPlacesAsyncTask extends AsyncTask<String,String,String>{
        String webData="",data="";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog=new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading..");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                Log.e("places_name_url",params[0]);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                InputStreamReader io = new InputStreamReader(con.getInputStream());
                BufferedReader br = new BufferedReader(io);
                while ((data=br.readLine())!=null){
                    webData+=data+"\n";
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return webData;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Gson gson=new Gson();
            NearByPlacesParser nearByPlaceParser=gson.fromJson(s, NearByPlacesParser.class);
            loadMarker(nearByPlaceParser);
            pipeLinedString="";
            progressDialog.dismiss();
//            for (int i=0;i<nearByPlaceParser.results.size();i++){
//
//            }
        }
    }

    public void loadMarker(NearByPlacesParser nearByPlacesParser)
    {
        outerMapObj.clear();
        outerMapObj.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)))
                .title(placeDescription));
        ArrayList<MarkerOptions> markerlist=new ArrayList<>();
        for (int i=0;i<nearByPlacesParser.results.size();i++) {
//            Log.e("place_names", nearByPlacesParser.results.get(i).name);
            try {
                Bitmap bitmap=new DownloadImage().execute(nearByPlacesParser.results.get(i).icon).get();
                MarkerOptions markerOpt = new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(nearByPlacesParser.results.get(i).geometry.location.lat)
                                , Double.parseDouble(nearByPlacesParser.results.get(i).geometry.location.lng)))
                        .title(nearByPlacesParser.results.get(i).name)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                markerlist.add(markerOpt);
                outerMapObj.addMarker(markerOpt);
//                CameraPosition camPos = CameraPosition.builder().target(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)))
//                        .zoom(15).build();
//                outerMapObj.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
//        for (int i=0;i<markerlist.size();i++){
        LatLngBounds.Builder latlngBuilder=new LatLngBounds.Builder();
        for (MarkerOptions markerOptions:markerlist)
        {
            latlngBuilder.include(markerOptions.getPosition());
        }
        latlngBuilder.include(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
        LatLngBounds latLngBounds=latlngBuilder.build();
        CameraUpdate cameraUpdate=CameraUpdateFactory.newLatLngBounds(latLngBounds,20);
        outerMapObj.animateCamera(cameraUpdate);
//        outerMapObj.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngBounds.getCenter(),12));
//        }

    }
    class DownloadImage extends AsyncTask<String,String,Bitmap>{
        Bitmap bitmap;
        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url=new URL(params[0]);
                HttpURLConnection con= (HttpURLConnection) url.openConnection();
                InputStream is=con.getInputStream();
                bitmap=BitmapFactory.decodeStream(is);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }
    }



}
