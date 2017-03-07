//Modif.1 2 de Febrero de 2017
//        Se modifica para mostrar las rutas en un spinner
//http://stackoverflow.com/questions/13377361/how-to-create-a-drop-down-list

//Modif.2 3 de Febrero de 2017
//        Se modifica para mostrar las rutas en un spinner consultando a la base de datos mediante PHP y retornando valores en JSON


//Modif.3 4 de Febrero de 2017
//        Se modifica el desarrollo para pasar el origen y destino de la ruta a MapsActivity
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.StringTokenizer;

//public class SuscriptionActivity extends AppCompatActivity { //Modif.1.old.ln
public class SuscriptionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener { //Modif.1.new.ln
    public String desdehacia="";
    public String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mqttdata";//Modif.2.new.ln
    //Modif.1.new INICIO
    private Spinner spinner;
    private ArrayList<String> rutasAL;
    //Modif.1.new FIN


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suscription);

        //Modif.2.old.ini INICIO
        final Button bSus = (Button) findViewById(R.id.btnSuscribirse);
        rutasAL = new ArrayList<String>();
        spinner = (Spinner) findViewById(R.id.spinner_susr);
        spinner.setOnItemSelectedListener(this);
        //Modif.2.old.end FIN
        bSus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent nextIntent = new Intent(SuscriptionActivity.this, MapsActivity.class);
                                        //nextIntent.putExtra("key","val");//Modif.3.old.ln
                                        nextIntent.putExtra("topico",desdehacia);//Modif.3.new.ln
                                        SuscriptionActivity.this.startActivity(nextIntent);
                                    }
                                });
                if (isOnline() == true) {
                    // Response received from the server
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //StringTokenizer rutas = new StringTokenizer(response, "{\"ruta\":");//Modif.1.old.ln
                                StringTokenizer rutas = new StringTokenizer(response, "{\"");
                                /*List<String>*/ rutasAL = new ArrayList<String>();
                                while (rutas.hasMoreTokens()) {

                                        rutasAL.add(rutas.nextToken());
                                }
                                int imax = rutasAL.size();

                                for (int i = 0; i < imax;i++ )
                                {
                                    if(rutasAL.get(i).contains("ruta")
                                            ||rutasAL.get(i).contains(":")
                                            ||rutasAL.get(i).contains("}"))
                                    {
                                        //rutasAL.remove(i);
                                        rutasAL.set(i,"");
                                    }
                                }
                                rutasAL.removeAll(Arrays.asList(null,""));

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





    }



    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

                Context context = getApplicationContext();
                CharSequence text = parent.getSelectedItem().toString();//"Usted escogió la ruta 0";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                desdehacia=text.toString();//Modif.3.new.ln

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