package br.ufes.inf.hfilho.previsodotempo.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by helder on 17/06/17.
 */

public class PreferenciasController {
    public static String getString(Context c, String key,String defaultValue){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        return sharedPref.getString(key, defaultValue);
    }

    public static int getInteger(Context c,String key, int defaultValue){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        return sharedPref.getInt(key, defaultValue);
    }

    public static void setString(Context c, String key, String value){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor e = sharedPref.edit();
        e.putString(key,value);
        e.commit();
    }

    public static void setInteger(Context c, String key, int value){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor e = sharedPref.edit();
        e.putInt(key,value);
        e.commit();
    }

    public static void setObject(Context c, String key, Serializable obj){
        setString(c,key,objectToString(obj));
    }

    public static Object getObject(Context c, String key){
        return stringToObject(getString(c,key,null));
    }


    private static String objectToString(Serializable object) {
        String encoded = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            encoded = Base64.encodeToString(byteArrayOutputStream.toByteArray(),0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encoded;
    }


    private static Serializable stringToObject(String string){
        if(string==null)return null;
        byte[] bytes = Base64.decode(string,0);
        Serializable object = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream( new ByteArrayInputStream(bytes) );
            object = (Serializable)objectInputStream.readObject();
        } catch (IOException|ClassNotFoundException|ClassCastException e) {
            e.printStackTrace();
        }
        return object;
    }
}
