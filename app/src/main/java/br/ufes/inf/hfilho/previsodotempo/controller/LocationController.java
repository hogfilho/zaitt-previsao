package br.ufes.inf.hfilho.previsodotempo.controller;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by helder on 17/06/17.
 */

public class LocationController {

    private volatile static boolean locked = true;
    private volatile static Location location;

    public static Location getLocation(Activity c) throws NetworkErrorException {
        locked=true;

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                LocationController.location=location;
                locked=false;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        if (ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Get the location manager
            LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
            // Define the criteria how to select the locatioin provider -> use
            // default
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);

            for(String p:locationManager.getAllProviders()){
                Log.e("Provider",p);
                location = locationManager.getLastKnownLocation(p);
                if(location!=null){
                    locked=false;
                    break;
                }

            }

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener,c.getMainLooper());
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener,c.getMainLooper());
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationListener,c.getMainLooper());

            // Initialize the location fields
            if (location != null) {
                System.out.println("Provider " + provider + " has been selected.");
                LocationController.location = location;
            }

            while(locked){
                try{
                    Thread.sleep(10);
                } catch(InterruptedException e){
                    e.printStackTrace();
                }
            }

        }

        if(location==null){
            throw new NetworkErrorException("Você precisa conceder permissões de GPS");
        }

        return location;
    }


}
