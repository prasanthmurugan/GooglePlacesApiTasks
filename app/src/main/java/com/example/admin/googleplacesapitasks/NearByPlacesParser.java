package com.example.admin.googleplacesapitasks;

import android.util.Log;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Admin on 2/5/2016.
 */
public class NearByPlacesParser implements Serializable {
    List<Results> results;
    public class Results implements Serializable{
        String icon;
        String name;
        Geometry geometry;
        public class Geometry implements Serializable{
            Location location;
            public class Location implements Serializable{
                String lat,lng;
            }
        }
    }
}
