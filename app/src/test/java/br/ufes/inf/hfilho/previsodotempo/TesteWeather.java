package br.ufes.inf.hfilho.previsodotempo;

import org.junit.Test;

import br.ufes.inf.hfilho.previsodotempo.domain.Weather;

import static org.junit.Assert.*;

public class TesteWeather {
    @Test
    public void mpsToKMH() throws Exception {
        Weather w = new Weather();
        w.setMpsVento(7.6);
        assertEquals(w.getMpsVento()*3.6, w.getKmhVento(),0);
    }
}