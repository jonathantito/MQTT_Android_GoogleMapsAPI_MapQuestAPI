/*
Modif.1 3 de Febrero de 2017
Se implementa un pantalla de acceso a la aplicación
que se conecta a una BDD local en MySQL bajo XAMPP
* **/

package com.example.dell.testapigoogle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoginActivity extends AppCompatActivity {
    //static int flagRespuesta = 0;
    public String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mqttdata";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final Button bLogin = (Button) findViewById(R.id.bSignIn);
        final Button bConf = (Button) findViewById(R.id.btnConf);

        //Modif.1.old.ini INICIO
        /*
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });
        */
        //Modif.1.old.ini FIN
        bConf.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent confIntent = new Intent(LoginActivity.this, ConfigurationActivity.class);
                LoginActivity.this.startActivity(confIntent);

            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //flagRespuesta=3;
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();

                if (isOnline() == true) {


                    // Response received from the server
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");

                                if (success) {
                                    //Modif.1.old.ini FIN
                                    Intent nextIntent = new Intent(LoginActivity.this, SuscriptionActivity.class);//Modif.1.C.new.ln
                                    LoginActivity.this.startActivity(nextIntent);//Modif.1.C.new.ln

                                } else {
                                    //flagRespuesta=2;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setMessage("Nombre de usuario y/o contraseña errado(s)")
                                            .setNegativeButton("Reintentar", null)
                                            .create()
                                            .show();

                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    };
                    File file = new File (path + "/conf.txt");
                    String [] loadText = Load(file);

                    String finalString = "";

                    for (int i = 0; i < loadText.length; i++)
                    {
                        //finalString += loadText[i] + System.getProperty("line.separator");//Modif.1.old.ln
                        finalString += loadText[i] ;//Modif.1.new.ln
                    }
                    //
                    String LOGIN_REQUEST_URL = "http://" + finalString + "/Login3.php";
                    //////

                    LoginRequest loginRequest = new LoginRequest(username, password, responseListener,LOGIN_REQUEST_URL);
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                    queue.add(loginRequest);



                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Conectese a la red del sistema MQTT por favor")
                            .setNegativeButton("Reintentar", null)
                            .create()
                            .show();
                }


            }

        });


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
    public boolean isOnline() {
        //Modif.1.old.ini INICIO Si funciona pero solo constata cnexión a internet en general

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();

        //Modif.1.old.ini FIN Si funciona pero solo constata cnexión a internet en general
        //Modif.1.old.ini INICIO cuelga la aplicación
        /*
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 192.168.1.7");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
        */
        //Modif.1.old.ini FIN cuelga la aplicación
    }
}
