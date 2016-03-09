package com.example.admin.googleplacesapitasks;

import java.io.Serializable;


/**
 * Created by Admin on 2/2/2016.
 */
public class LatLonParser implements Serializable {
    public Result result;
    public class Result implements Serializable{
        public Geometry geometry;
        public class Geometry implements Serializable{
            public Location location;
            public class Location implements Serializable{
                String lat,lng;
            }
        }
    }
}
