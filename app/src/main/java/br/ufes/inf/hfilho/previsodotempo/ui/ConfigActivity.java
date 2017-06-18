package br.ufes.inf.hfilho.previsodotempo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import br.ufes.inf.hfilho.previsodotempo.R;
import br.ufes.inf.hfilho.previsodotempo.controller.PreferenciasController;
import br.ufes.inf.hfilho.previsodotempo.controller.WeatherController;
import br.ufes.inf.hfilho.previsodotempo.domain.Weather;

public class ConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Configurações");
        RadioGroup vg_unidade = (RadioGroup) findViewById(R.id.vg_unidade);
        EditText et_cidade = (EditText) findViewById(R.id.et_cidade);
        EditText et_pais = (EditText) findViewById(R.id.et_pais);

        final RadioGroup vg_loc = (RadioGroup) findViewById(R.id.vg_localizacao);
        int unidade = PreferenciasController.getInteger(this,"unidade", Weather.TEMP_CELSIUS);
        if(unidade==Weather.TEMP_CELSIUS){
            ((RadioButton)vg_unidade.getChildAt(0)).setChecked(true);
        } else {
            ((RadioButton)vg_unidade.getChildAt(1)).setChecked(true);
        }

        int tipo = PreferenciasController.getInteger(this,"tipo", WeatherController.TYPE_LATLON);
        if(tipo==WeatherController.TYPE_LATLON){
            ((RadioButton)vg_loc.getChildAt(0)).setChecked(true);
            setEditTextState(false);
        } else {
            ((RadioButton)vg_loc.getChildAt(1)).setChecked(true);
            setEditTextState(true);
        }
        vg_loc.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(((RadioButton)vg_loc.getChildAt(0)).isChecked()){
                    setEditTextState(false);
                } else {
                    setEditTextState(true);
                }
            }
        });

        et_cidade.setText(PreferenciasController.getString(this,"cidade","Vitória"));
        et_pais.setText(PreferenciasController.getString(this,"pais","BR"));
    }

    private void setEditTextState(boolean state){
        EditText et_cidade = (EditText) findViewById(R.id.et_cidade);
        EditText et_pais = (EditText) findViewById(R.id.et_pais);
        et_cidade.setEnabled(state);
        et_pais.setEnabled(state);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setResult(RESULT_OK);
        save();
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        save();
        super.onBackPressed();
    }

    private void save(){
        RadioGroup vg_unidade = (RadioGroup) findViewById(R.id.vg_unidade);
        RadioGroup vg_loc = (RadioGroup) findViewById(R.id.vg_localizacao);
        EditText et_cidade = (EditText) findViewById(R.id.et_cidade);
        EditText et_pais = (EditText) findViewById(R.id.et_pais);
        int unidade = ((RadioButton)vg_unidade.getChildAt(0)).isChecked()?Weather.TEMP_CELSIUS:Weather.TEMP_FAHRENHEIT;
        int localizacao = ((RadioButton)vg_loc.getChildAt(0)).isChecked()?WeatherController.TYPE_LATLON:WeatherController.TYPE_CITY;
        PreferenciasController.setInteger(this,"unidade",unidade);
        PreferenciasController.setInteger(this,"tipo",localizacao);
        if(localizacao==WeatherController.TYPE_CITY){
            PreferenciasController.setString(this,"cidade",et_cidade.getText().toString().trim());
            PreferenciasController.setString(this,"pais",et_pais.getText().toString().trim());
        }
    }
}
