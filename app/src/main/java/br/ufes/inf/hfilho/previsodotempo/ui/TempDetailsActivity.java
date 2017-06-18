package br.ufes.inf.hfilho.previsodotempo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import br.ufes.inf.hfilho.previsodotempo.R;
import br.ufes.inf.hfilho.previsodotempo.controller.PreferenciasController;
import br.ufes.inf.hfilho.previsodotempo.domain.Weather;

public class TempDetailsActivity extends AppCompatActivity {

    Thread t_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detalhes");

        updateDetails();
    }

    private void updateDetails() {
        if (t_details != null) {
            t_details.interrupt();
        }
        t_details = new Thread(new Runnable() {
            @Override
            public void run() {
                final TextView t_dia = (TextView) findViewById(R.id.t_dia);
                final TextView t_clima = (TextView) findViewById(R.id.t_clima);
                final TextView t_details = (TextView) findViewById(R.id.t_details);
                final ImageView i_icone = (ImageView) findViewById(R.id.i_icone);
                int id = getIntent().getIntExtra("id", 0);

                Weather w;
                if (id == 0) {
                    w = (Weather) PreferenciasController.getObject(TempDetailsActivity.this, "Today");
                } else {
                    w = ((ArrayList<Weather>) PreferenciasController.getObject(TempDetailsActivity.this, "NextDays")).get(id - 1);
                }
                if (w != null) {
                    final String clima = w.getClima();
                    final String dia = getIntent().getStringExtra("dia");
                    int iUnidade = PreferenciasController.getInteger(TempDetailsActivity.this, "unidade", Weather.TEMP_CELSIUS);
                    String unidade = iUnidade == Weather.TEMP_CELSIUS ? "C" : "F";
                    double minTemp = w.getMinTemp(iUnidade);
                    double maxTemp = w.getMaxTemp(iUnidade);
                    double vento = w.getKmhVento();
                    int humidade = w.getPercentHumid();
                    String cidade = w.getCidade();
                    String pais = w.getPais();
                    final String icone = w.getIcone();
                    final String details = "A temperatura em " + cidade + ", " + pais + " estará entre " + String.format("%.1f",minTemp) + "º" + unidade + " e " + String.format("%.1f",maxTemp) + "º" + unidade + " e a velocidade do vento estará em torno de " + String.format("%.1f",vento) + " Km/h.\nA previsão é que a humidade relativa do ar seja " + humidade + "%.\nO clima estará mais para " + clima.toLowerCase() + ".";

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            t_dia.setText(dia);
                            t_clima.setText(clima);
                            t_details.setText(details);
                            Glide.with(TempDetailsActivity.this)
                                    .load("http://openweathermap.org/img/w/"+icone+".png")
                                    .into(i_icone);

                        }
                    });
                }


            }
        });
        t_details.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
