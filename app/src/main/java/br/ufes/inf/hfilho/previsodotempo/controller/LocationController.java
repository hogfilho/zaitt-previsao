package br.ufes.inf.hfilho.previsodotempo.controller;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Created by helder on 17/06/17.
 */

public class LocationController {

    private volatile static boolean locked = true;
    private volatile static Location location;

    public static Location getLocation(Activity c) throws NetworkErrorException {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(c);
        if (ContextCompat.checkSelfPermission(c, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(c, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if (location != null) {
                                LocationController.location=location;
                            }
                            locked = false;
                        }
                    });
            while (locked) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
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
