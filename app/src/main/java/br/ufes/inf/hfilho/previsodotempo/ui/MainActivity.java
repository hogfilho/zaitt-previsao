package br.ufes.inf.hfilho.previsodotempo.ui;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
    private static final int REQUEST_CODE_CONFIG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateData();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                            Date lastUpdate = (Date)PreferenciasController.getObject(MainActivity.this,"LastUpdated");
                            TextView t_cidade_pais = (TextView) findViewById(R.id.t_cidade_pais);
                            TextView t_clima = (TextView) findViewById(R.id.t_clima);
                            TextView t_temp = (TextView) findViewById(R.id.t_temp);
                            TextView t_last_update = (TextView) findViewById(R.id.t_last_update);

                            LinearLayout l_dias = (LinearLayout) findViewById(R.id.l_dias);
                            int unidade = PreferenciasController.getInteger(MainActivity.this,"unidade",Weather.TEMP_CELSIUS);

                            t_cidade_pais.setText(today.getCidade() + ", " + today.getPais());
                            t_clima.setText(today.getClima());
                            t_temp.setText(String.format("%.1f",today.getTemp(unidade))+ "º" + (unidade==Weather.TEMP_CELSIUS?"C":"F"));

                            l_dias.removeAllViews();

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(lastUpdate);
                            int day = calendar.get(Calendar.DAY_OF_WEEK);
                            int startIndex = day-1;
                            String dia = dias[startIndex];
                            addDia(l_dias,dia,today.getIcone(),today.getMinTemp(unidade),today.getMaxTemp(unidade),0);

                            int count=0;
                            for(Weather currDay:nextDays){
                                int index = (startIndex+1+count) % 7;
                                dia = dias[index];
                                addDia(l_dias,dia,currDay.getIcone(),currDay.getMinTemp(unidade),currDay.getMaxTemp(unidade),1+count);
                                count++;
                            }


                            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            String strDate = sdfDate.format(lastUpdate);
                            t_last_update.setText("Atualizado em " + strDate);

                        }
                    });
                } catch(IOException e){
                    showError("Rede Offline","Você precisa estar conectado na primeira utilização!");
                } catch(NetworkErrorException e){
                    e.printStackTrace();
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

    private void addDia(ViewGroup root, final String dia, String icone, double min, double max, int id){
        View v = LayoutInflater.from(this).inflate(R.layout.include_dia,root,false);
        v.setId(id);
        TextView t_dia = (TextView) v.findViewById(R.id.t_dia);
        TextView t_max = (TextView) v.findViewById(R.id.t_max);
        TextView t_min = (TextView) v.findViewById(R.id.t_min);
        ImageView i_icone = (ImageView) v.findViewById(R.id.i_icone);

        t_dia.setText(dia);
        t_min.setText(String.valueOf((int)min));
        t_max.setText(String.valueOf((int)max));
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,TempDetailsActivity.class);
                i.putExtra("id",view.getId());
                i.putExtra("dia",dia);
                startActivity(i);
            }
        });

        Glide.with(MainActivity.this)
                .load("http://openweathermap.org/img/w/"+icone+".png")
                .into(i_icone);

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_GPS);

                }
            });
        }
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
                    //caso o usuario tenha negado, vai automaticamente pedir de novo no onResume
                    showError("Permissão", "Você precisa conceder permissão para acessar o GPS!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestGPSPermission();
                        }
                    });
                }
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case REQUEST_CODE_CONFIG:
                updateData();
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(MainActivity.this,ConfigActivity.class);
                startActivityForResult(i,REQUEST_CODE_CONFIG);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
