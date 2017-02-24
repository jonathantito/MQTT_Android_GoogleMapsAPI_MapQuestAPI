//Modif.1 2 de Febrero de 2017
//        Se modifica para mostrar las rutas en un spinner
//http://stackoverflow.com/questions/13377361/how-to-create-a-drop-down-list
package com.example.dell.testapigoogle;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
//import android.widget.AdapterView;

//public class SuscriptionActivity extends AppCompatActivity { //Modif.1.old.ln
public class SuscriptionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener { //Modif.1.new.ln

    //Modif.1.new INICIO
    private Spinner spinner;
    private static final String []rutas = {"UIO-AMB","UIO-GYE", "UIO-ESM"};
    //Modif.1.new FIN

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suscription);

        //Modif.1.new INICIO
        spinner = (Spinner) findViewById(R.id.spinner_susr);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SuscriptionActivity.this, android.R.layout.simple_spinner_item,rutas);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //Modif.1.new FIN
    }
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                Context context = getApplicationContext();
                CharSequence text = "Usted escogió la ruta 0";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                break;
            case 1:
                // Whatever you want to happen when the second item gets selected
                break;
            case 2:
                // Whatever you want to happen when the thrid item gets selected
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
