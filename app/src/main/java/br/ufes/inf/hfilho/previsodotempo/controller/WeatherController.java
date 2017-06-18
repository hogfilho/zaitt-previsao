package br.ufes.inf.hfilho.previsodotempo.controller;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.location.Location;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.ufes.inf.hfilho.previsodotempo.domain.Weather;

/**
 * Created by helder on 17/06/17.
 */

public class WeatherController {
    private static String appID = "e7c8fc5e73f563a33fc3d3bcf59a6a8b";
    public static final int TYPE_CITY = 0;
    public static final int TYPE_LATLON = 1;

    @SuppressWarnings("unchecked")
    public static Weather getToday(Activity c) throws IOException, NetworkErrorException{
        int tipoQuery = PreferenciasController.getInteger(c,"tipo",TYPE_LATLON);
        Weather ret=null;
        try{
            if(tipoQuery==TYPE_LATLON){
                Location l = LocationController.getLocation(c);
                ret=getTodayByLatLong(c,String.valueOf(l.getLatitude()),String.valueOf(l.getLongitude()));
                PreferenciasController.setObject(c,"Today",ret);
            } else {
                String cidade = PreferenciasController.getString(c,"cidade","Vitoria");
                String pais = PreferenciasController.getString(c,"pais","BR");
                ret=getTodayByCity(c,cidade,pais);
                PreferenciasController.setObject(c,"Today",ret);
            }
            PreferenciasController.setObject(c,"LastUpdated",new Date());
        } catch(SocketException e){//se estiver offline, pega do cache
            e.printStackTrace();
            ret=(Weather)PreferenciasController.getObject(c,"Today");
        }
        if(ret==null){
            throw new IOException("Você precisa estar conectado na primeira utilização.");
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Weather> getNextDays(Activity c) throws IOException, NetworkErrorException {
        int tipoQuery = PreferenciasController.getInteger(c,"tipo",TYPE_LATLON);
        ArrayList<Weather> ret=null;
        try{
            if(tipoQuery==TYPE_LATLON){
                Location l = LocationController.getLocation(c);
                ret=getNextDaysByLatLong(c,String.valueOf(l.getLatitude()),String.valueOf(l.getLongitude()));
                PreferenciasController.setObject(c,"NextDays",ret);
            } else {
                String cidade = PreferenciasController.getString(c,"cidade","Vitoria");
                String pais = PreferenciasController.getString(c,"pais","BR");
                ret=getNextDaysByCity(c,cidade,pais);
                PreferenciasController.setObject(c,"NextDays",ret);
            }
            PreferenciasController.setObject(c,"LastUpdated",new Date());
        } catch(SocketException e){ //se estiver offline, pega do cache
            e.printStackTrace();
            ret=(ArrayList<Weather>)PreferenciasController.getObject(c,"NextDays");
        }
        if(ret==null){
            throw new IOException("Você precisa estar conectado na primeira utilização.");
        }
        return ret;
    }

    private static Weather getTodayByCity(Context c, String qCidade, String qPais) throws SocketException {
        Weather w = null;
        try {
            String html = HTMLController.getString("http://api.openweathermap.org/data/2.5/weather?q=" + qCidade + "," + qPais + "&mode=xml&appid=" + appID);
            w = extractWeatherFromTodayData(html);
        } catch (MalformedURLException e) { //URL da api está malformada, nunca vai acontecer
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            throw new SocketException("Você está offline!");
        }
        return w;
    }

    private static ArrayList<Weather> getNextDaysByCity(Context c, String qCidade, String qPais) throws SocketException {
        ArrayList<Weather> arr = null;
        try {
            String html = HTMLController.getString("http://api.openweathermap.org/data/2.5/forecast?q=" + qCidade + "," + qPais + "&mode=xml&appid=" + appID);
            arr = extractWeatherFromNextDaysData(html);
        } catch (MalformedURLException e) { //URL da api está malformada, nunca vai acontecer
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            throw new SocketException("Você está offline!");
        }
        return arr;
    }

    private static Weather getTodayByLatLong(Context c, String latitude, String longitude) throws SocketException {
        Weather w = null;
        try {
            String html = HTMLController.getString("http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&mode=xml&appid=" + appID);
            w = extractWeatherFromTodayData(html);
        } catch (MalformedURLException e) { //URL da api está malformada, nunca vai acontecer
            e.printStackTrace();
        } catch (IOException e) {
            throw new SocketException("Você está offline!");
        }
        return w;
    }

    private static ArrayList<Weather> getNextDaysByLatLong(Context c, String latitude, String longitude) throws SocketException {
        ArrayList<Weather> arr = null;
        try {
            String html = HTMLController.getString("http://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude + "&mode=xml&appid=" + appID);
            arr = extractWeatherFromNextDaysData(html);
        } catch (MalformedURLException e) { //URL da api está malformada, nunca vai acontecer
            e.printStackTrace();
        } catch (IOException e) {
            throw new SocketException("Você está offline!");
        }
        return arr;
    }


    private static Weather extractWeatherFromTodayData(String html){
        String tmp;
        String cidade=null;
        String pais=null;
        String condicaoID=null;
        String icone = null;
        String kelvinTemp=null;
        String kelvinTempMin=null;
        String kelvinTempMax=null;
        String mpsVento=null;
        String percentHumid=null;
        String dateTime=null;

        //Extrair cidade
        tmp = RegexController.extractGroup(html,"<city([^>]+)>",0);
        if(tmp!=null){
            cidade = RegexController.extractGroup(tmp,"name=\"([^\"]+)\"",1);
        }
        //Extrair País
        pais = RegexController.extractGroup(html,"<country>(.+?)</country>",1);
        //Extrair Condicao Climatica e Icone
        tmp = RegexController.extractGroup(html,"<weather([^>]+)>",0);
        if(tmp!=null){
            condicaoID = RegexController.extractGroup(tmp,"number=\"([^\"]+)\"",1);
            icone = RegexController.extractGroup(tmp,"icon=\"([^\"]+)\"",1);
        }
        //Extrair temperatura atual, minima e máxima
        tmp = RegexController.extractGroup(html,"<temperature[^>]+>",0);
        if(tmp!=null){
            kelvinTemp = RegexController.extractGroup(tmp,"value=\"([^\"]+)\"",1);
            kelvinTempMin = RegexController.extractGroup(tmp,"min=\"([^\"]+)\"",1);
            kelvinTempMax = RegexController.extractGroup(tmp,"max=\"([^\"]+)\"",1);
        }
        //Extrair velocidade do vento
        tmp = RegexController.extractGroup(html,"<speed[^>]+>",0);
        if(tmp!=null){
            mpsVento = RegexController.extractGroup(tmp,"value=\"([^\"]+)\"",1);
        }
        //Extrair Humidade
        tmp = RegexController.extractGroup(html,"<humidity[^>]+>",0);
        if(tmp!=null){
            percentHumid = RegexController.extractGroup(tmp,"value=\"([^\"]+)\"",1);
        }
        //Extrair Datetime
        tmp = RegexController.extractGroup(html,"<lastupdate[^>]+>",0);
        if(tmp!=null){
            dateTime= RegexController.extractGroup(tmp,"value=\"([^\"]+)\"",1);
        }

        //cria o objeto Weather
        Weather w = new Weather();
        w.setCidade(cidade);
        w.setPais(pais);
        if(condicaoID!=null){
            w.setCondicaoID(Integer.parseInt(condicaoID));
        }
        w.setIcone(icone);
        if(kelvinTemp!=null){
            w.setKelvinTemp(Double.parseDouble(kelvinTemp));
        }
        if(kelvinTempMin!=null){
            w.setKelvinTempMin(Double.parseDouble(kelvinTempMin));
        }
        if(kelvinTempMax!=null){
            w.setKelvinTempMax(Double.parseDouble(kelvinTempMax));
        }
        if(mpsVento!=null){
            w.setMpsVento(Double.parseDouble(mpsVento));
        }
        if(percentHumid!=null){
            w.setPercentHumid(Integer.parseInt(percentHumid));
        }
        w.setDateTime(dateTime);
        return w;
    }

    private static ArrayList<Weather> extractWeatherFromNextDaysData(String html){
        String tmp;
        String cidade;
        String pais;
        String lastDay="";

        ArrayList<ArrayList<Weather>> arrDays = new ArrayList<>();

        for(int i=0;i<6;i++){
            arrDays.add(new ArrayList<Weather>());
        }


        //Extrair cidade
        cidade = RegexController.extractGroup(html,"<name>(.+?)</name>",1);
        //Extrair País
        pais = RegexController.extractGroup(html,"<country>(.+?)</country>",1);

        Pattern p = Pattern.compile("<time [\\w\\W]+?</time>");
        Matcher m = p.matcher(html);

        int currentDayID=0;
        while(m.find()){ //para cada dia, cria um Weather e adiciona na lista correta para então fazer a media de 3 em 3 horas
            String day = m.group(0);
            String dateTime = RegexController.extractGroup(day,"from=\"([^\"]+)\"",1);
            String date = dateTime.split("T")[0];
            if(lastDay.isEmpty()){
                lastDay = date;
            }

            if(!lastDay.equals(date)){
                lastDay=date;
                currentDayID++;
            }

            String condicaoID=null;
            String icone = null;
            String kelvinTemp=null;
            String kelvinTempMin=null;
            String kelvinTempMax=null;
            String mpsVento=null;
            String percentHumid=null;

            //Extrair Condicao Climatica e Icone
            tmp = RegexController.extractGroup(day,"<symbol([^>]+)>",0);
            if(tmp!=null){
                condicaoID = RegexController.extractGroup(tmp,"number=\"([^\"]+)\"",1);
                icone = RegexController.extractGroup(tmp,"var=\"([^\"]+)\"",1);
            }
            //Extrair temperatura atual, minima e máxima
            tmp = RegexController.extractGroup(day,"<temperature[^>]+>",0);
            if(tmp!=null){
                kelvinTemp = RegexController.extractGroup(tmp,"value=\"([^\"]+)\"",1);
                kelvinTempMin = RegexController.extractGroup(tmp,"min=\"([^\"]+)\"",1);
                kelvinTempMax = RegexController.extractGroup(tmp,"max=\"([^\"]+)\"",1);
            }
            //Extrair velocidade do vento
            tmp = RegexController.extractGroup(day,"<windSpeed[^>]+>",0);
            if(tmp!=null){
                mpsVento = RegexController.extractGroup(tmp,"value=\"([^\"]+)\"",1);
            }
            //Extrair Humidade
            tmp = RegexController.extractGroup(day,"<humidity[^>]+>",0);
            if(tmp!=null){
                percentHumid = RegexController.extractGroup(tmp,"value=\"([^\"]+)\"",1);
            }

            //cria o objeto Weather
            Weather w = new Weather();
            w.setCidade(cidade);
            w.setPais(pais);
            if(condicaoID!=null){
                w.setCondicaoID(Integer.parseInt(condicaoID));
            }
            w.setIcone(icone);
            if(kelvinTemp!=null){
                w.setKelvinTemp(Double.parseDouble(kelvinTemp));
            }
            if(kelvinTempMin!=null){
                w.setKelvinTempMin(Double.parseDouble(kelvinTempMin));
            }
            if(kelvinTempMax!=null){
                w.setKelvinTempMax(Double.parseDouble(kelvinTempMax));
            }
            if(mpsVento!=null){
                w.setMpsVento(Double.parseDouble(mpsVento));
            }
            if(percentHumid!=null){
                w.setPercentHumid(Integer.parseInt(percentHumid));
            }
            w.setDateTime(dateTime);

            arrDays.get(currentDayID).add(w);
        }

        arrDays.remove(0); // o primeiro dia não nos interessa, só os seguintes
        //para cada dia, faz uma media dos dados;
        ArrayList<Weather> ret = new ArrayList<>();
        for(ArrayList<Weather> day: arrDays){
            int total = day.size();
            int percentHumidSum=0;
            double mpsVentoSum=0;
            double kelvinTempSum=0;
            double kelvinTempMin=Double.MAX_VALUE;
            double kelvinTempMax= Double.MIN_VALUE;
            for(Weather d : day){
                percentHumidSum+=d.getPercentHumid();
                mpsVentoSum+=d.getMpsVento();
                kelvinTempSum+=d.getKelvinTemp();
                if(d.getKelvinTempMin()<kelvinTempMin){
                    kelvinTempMin=d.getKelvinTempMin();
                }
                if(d.getKelvinTempMax()>kelvinTempMax){
                    kelvinTempMax=d.getKelvinTempMax();
                }
            }
            if(day.size()>0) {
                int i = (day.size() / 2);
                if(i>=day.size()){
                    i--;
                }
                Weather media = day.get(i);
                media.setPercentHumid(percentHumidSum / day.size());
                media.setMpsVento(mpsVentoSum / day.size());
                media.setKelvinTempMax(kelvinTempMax);
                media.setKelvinTemp(kelvinTempSum / day.size());
                media.setKelvinTempMin(kelvinTempMin);
                ret.add(media);
            }
        }

        return ret;
    }
}
