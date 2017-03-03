//Modif.1 2 de Febrero de 2017
//        Se modifica para mostrar las rutas en un spinner
//http://stackoverflow.com/questions/13377361/how-to-create-a-drop-down-list
package com.example.dell.testapigoogle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
//import android.widget.AdapterView;

//public class SuscriptionActivity extends AppCompatActivity { //Modif.1.old.ln
public class SuscriptionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener { //Modif.1.new.ln
//Modif.2.new INICIO
    public String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mqttdata";
    //private Button bSus;

    //Modif.2.new FIN
    //Modif.1.new INICIO
    //private Spinner spinner;
    private Spinner spinner;
    private ArrayList<String> rutasAL;
    //private static final String []rutas = {"UIO-AMB","UIO-GYE", "UIO-ESM"};
    //Modif.1.new FIN


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suscription);

        //Modif.2.old.ini INICIO
        final Button bSus = (Button) findViewById(R.id.btnSuscribirse);
        rutasAL=new ArrayList<String>();
        spinner = (Spinner) findViewById(R.id.spinner_susr);
        spinner.setOnItemSelectedListener(this);

        //Modif.2.old.end FIN
        //bSus.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View v) {
                //final String ruta="UIO-AMB";
                if (isOnline() == true) {
                    // Response received from the server
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //JSONObject jsonResponse = new JSONObject(response);
                                //boolean success = jsonResponse.getBoolean("success");
                                //JSONArray JSONrutas = new JSONArray(response);
                                //String[] rutas = response.split("{\"ruta\":");
                                //String responsex =response;
                                StringTokenizer rutas = new StringTokenizer(response, "{\"ruta\":");
                                /*List<String>*/ rutasAL = new ArrayList<String>();
                                while (rutas.hasMoreTokens()) {
                                    rutasAL.add(rutas.nextToken());
                                }
/*
                                spinner = (Spinner) findViewById(R.id.spinner_susr);

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(SuscriptionActivity.this, android.R.layout.simple_spinner_item, rutasAL);

                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.setAdapter(adapter);
                                spinner.setOnItemSelectedListener(this);
                                */

                                //String[] rutasAR = new String[rutasAL.size()];
                                //rutasAR = rutasAL.toArray(rutasAR);
/*
                                Spinner spinner = (Spinner) findViewById(R.id.spinner_susr);
// Create an ArrayAdapter using the string array and a default spinner layout
                                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, rutasAR, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                                spinner.setAdapter(adapter);
*//*
                                Spinner sp = (Spinner) findViewById(R.id.spinner_susr);
                                ArrayAdapter<String> adapter =
                                        new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, rutasAR);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp.setAdapter(adapter);
                                AdapterView.OnItemSelectedListener spinnerListener = new myOnItemSelectedListener(this);
                                sp.setOnItemSelectedListener(spinnerListener);*/
                                //sp.setSelection(dftIndex);
                                spinner.setAdapter(new ArrayAdapter<String>(SuscriptionActivity.this, android.R.layout.simple_spinner_dropdown_item, rutasAL));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    };

                    File file = new File(path + "/conf.txt");
                    String[] loadText = Load(file);

                    String finalString = "";

                    for (int i = 0; i < loadText.length; i++) {
                        //finalString += loadText[i] + System.getProperty("line.separator");//Modif.1.old.ln
                        finalString += loadText[i];//Modif.1.new.ln
                    }
                    //
                    String LOGIN_REQUEST_URL = "http://" + finalString + "/Topico4.php";
                    //////

                    SuscriptionRequest suscriptionRequest = new SuscriptionRequest(responseListener, LOGIN_REQUEST_URL);
                    RequestQueue queue = Volley.newRequestQueue(SuscriptionActivity.this);
                    queue.add(suscriptionRequest);


                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SuscriptionActivity.this);
                    builder.setMessage("Conectese a la red del sistema MQTT por favor")
                            .setNegativeButton("Reintentar", null)
                            .create()
                            .show();
                }

                //Modif.1.new INICIO
/*
try{
    if (!rutasAL.isEmpty())
    {
                spinner = (Spinner) findViewById(R.id.spinner_susr);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(SuscriptionActivity.this, android.R.layout.simple_spinner_item, rutasAL);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(this);
    }
}
catch(Exception e)
{
    e.printStackTrace();
}
*/
                //Modif.1.new FIN
            //}
        //});
        //spinner.setOnItemSelectedListener(this);




    }


/*
    public class myOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        Context mContext;

        public myOnItemSelectedListener(Context context){
            this.mContext = context;
        }

        public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {

            Country country = countryList.get(pos);
            displayCountryInformation(country);

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            //nothing here
        }
    }
    */

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

    public boolean isOnline() {
        //Modif.1.old.ini INICIO Si funciona pero solo constata cnexión a internet en general

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    public static String[] Load(File file)
    {
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(file);
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);

        String test;
        int anzahl=0;
        try
        {
            while ((test=br.readLine()) != null)
            {
                anzahl++;
            }
        }
        catch (IOException e) {e.printStackTrace();}

        try
        {
            fis.getChannel().position(0);
        }
        catch (IOException e) {e.printStackTrace();}

        String[] array = new String[anzahl];

        String line;
        int i = 0;
        try
        {
            while((line=br.readLine())!=null)
            {
                array[i] = line;
                i++;
            }
        }
        catch (IOException e) {e.printStackTrace();}
        return array;
    }
}