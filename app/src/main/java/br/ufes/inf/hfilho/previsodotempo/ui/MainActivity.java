package br.ufes.inf.hfilho.previsodotempo.ui;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Network;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import br.ufes.inf.hfilho.previsodotempo.R;
import br.ufes.inf.hfilho.previsodotempo.controller.PreferenciasController;
import br.ufes.inf.hfilho.previsodotempo.controller.WeatherController;
import br.ufes.inf.hfilho.previsodotempo.domain.Weather;

public class MainActivity extends AppCompatActivity {
    private String[] dias = { "Domingo", "Segunda-Feira", "Terça-Feira","Quarta-Feira", "Quinta-Feira", "Sexta-Feira", "Sábado"};

    Thread t_update;
    ProgressDialog pd;


    private static final int MY_PERMISSIONS_GPS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateData();
    }


    private void updateData(){
        pd = new ProgressDialog(this);
        pd.setMessage("Por favor, aguarde...");
        pd.setTitle("Atualizando Dados");
        pd.setCancelable(false);
        t_update=new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    final Weather today = WeatherController.getToday(MainActivity.this);
                    final ArrayList<Weather> nextDays = WeatherController.getNextDays(MainActivity.this);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView t_cidade_pais = (TextView) findViewById(R.id.t_cidade_pais);
                            TextView t_clima = (TextView) findViewById(R.id.t_clima);
                            TextView t_temp = (TextView) findViewById(R.id.t_temp);
                            TextView t_last_update = (TextView) findViewById(R.id.t_last_update);

                            LinearLayout l_dias = (LinearLayout) findViewById(R.id.l_dias);
                            int unidade = PreferenciasController.getInteger(MainActivity.this,"unidade",Weather.TEMP_CELSIUS);

                            t_cidade_pais.setText(today.getCidade() + ", " + today.getPais());
                            t_clima.setText(today.getClima());
                            t_temp.setText(today.getTemp(unidade)+ "º" + (unidade==Weather.TEMP_CELSIUS?"C":"F"));

                            l_dias.removeAllViews();

                            Calendar calendar = Calendar.getInstance();
                            int day = calendar.get(Calendar.DAY_OF_WEEK);
                            int startIndex = day-1;
                            String dia = dias[startIndex];
                            addDia(l_dias,dia,today.getMinTemp(unidade),today.getMaxTemp(unidade));

                            startIndex++;
                            int count=0;
                            for(Weather currDay:nextDays){
                                int index = (startIndex+1+count) % 7;
                                dia = dias[index];
                                addDia(l_dias,dia,currDay.getMinTemp(unidade),currDay.getMaxTemp(unidade));
                                count++;
                            }

                            Date lastUpdate = (Date)PreferenciasController.getObject(MainActivity.this,"LastUpdated");
                            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            String strDate = sdfDate.format(lastUpdate);
                            t_last_update.setText("Atualizado em " + strDate);

                        }
                    });
                } catch(IOException e){
                    showError("Rede Offline","Você precisa estar conectado na primeira utilização!");
                } catch(NetworkErrorException e){
                    requestGPSPermission();
                }
                pd.dismiss();
            }
        });
        if(t_update!=null){
            t_update.interrupt();
        }
        t_update.start();
        pd.create();
        pd.show();
    }

    private void addDia(ViewGroup root, String dia, double min, double max){
        View v = LayoutInflater.from(this).inflate(R.layout.include_dia,root,false);
        TextView t_dia = (TextView) v.findViewById(R.id.t_dia);
        TextView t_max = (TextView) v.findViewById(R.id.t_max);
        TextView t_min = (TextView) v.findViewById(R.id.t_min);
        t_dia.setText(dia);
        t_min.setText(String.valueOf((int)min));
        t_max.setText(String.valueOf((int)max));
        root.addView(v);
    }

    private void showError(String title, String msg){
        showError(title,msg,null);
    }

    private void showError(String title, String msg, DialogInterface.OnClickListener listener){
        if(listener==null){
            listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            };
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setPositiveButton("Fechar", listener);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.create();
                builder.show();
            }
        });
    }

    private void requestGPSPermission(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_GPS);

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_GPS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateData();
                } else {
                    showError("Permissão","Você precisa conceder permissão para acessar o GPS!", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface di, int i){
                            //requestGPSPermission();
                        }
                    });
                }
                return;
            }

        }
    }

}
