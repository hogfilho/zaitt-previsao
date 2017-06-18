package br.ufes.inf.hfilho.previsodotempo.domain;

import java.io.Serializable;

/**
 * Created by helder on 17/06/17.
 */

public class Weather implements Serializable {
    public static int TEMP_CELSIUS=0;
    public static int TEMP_FAHRENHEIT=1;

    String cidade;
    String pais;
    int condicaoID;
    String icone;
    double mpsVento;
    double kelvinTemp;
    double kelvinTempMin;
    double kelvinTempMax;
    int percentHumid;
    String dateTime;

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public int getCondicaoID() {
        return condicaoID;
    }

    public void setCondicaoID(int condicaoID) {
        this.condicaoID = condicaoID;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public double getMpsVento() {
        return mpsVento;
    }

    public double getKmhVento(){
        return mpsVento*3.6;
    }

    public void setMpsVento(double mpsVento) {
        this.mpsVento = mpsVento;
    }

    public double getKelvinTemp() {
        return kelvinTemp;
    }

    public void setKelvinTemp(double kelvinTemp) {
        this.kelvinTemp = kelvinTemp;
    }

    public double getKelvinTempMin() {
        return kelvinTempMin;
    }

    public void setKelvinTempMin(double kelvinTempMin) {
        this.kelvinTempMin = kelvinTempMin;
    }

    public double getKelvinTempMax() {
        return kelvinTempMax;
    }

    public void setKelvinTempMax(double kelvinTempMax) {
        this.kelvinTempMax = kelvinTempMax;
    }

    public int getPercentHumid() {
        return percentHumid;
    }

    public void setPercentHumid(int percentHumid) {
        this.percentHumid = percentHumid;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public double getTemp(int type){
        return convertTemp(type,kelvinTemp);
    }

    public double getMinTemp(int type){
        return convertTemp(type,kelvinTempMin);
    }

    public double getMaxTemp(int type){
        return convertTemp(type,kelvinTempMax);
    }

    public double convertTemp(int type, double temp){
        if(type==TEMP_CELSIUS){
            return temp - 273.15;
        } else {
            return (temp*9.0/5.0) - 459.67;
        }
    }

    public String getClima(){
        if(condicaoID<300){
            return "Tempestade com trovoadas";
        } else if(condicaoID<500){
            return "Chuviscando";
        } else if(condicaoID<600){
            return "Céu chuvoso";
        } else if(condicaoID<700){
            return "Nevasca";
        } else if(condicaoID<800){
            return "Céu nublado";
        } else if(condicaoID==800){
            return "Céu limpo";
        } else if(condicaoID<900){
            return "Céu limpo com nuvens";
        } else if(condicaoID<950){
            return "Condições extremas";
        } else {
            return "Brisa";
        }
    }


}
