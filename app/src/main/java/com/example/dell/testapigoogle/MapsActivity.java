//Modif.1 6 de Febrero de 2017
// Se programa un mapa en el que se indique la ruta a seguir por un automovil entre dos gestos "tap"
//Modif.2 7 de Febrero de 2017
// Se modifica para trabajar con la ruta a la que se suscribio el usuario
//Modif.3 8 de Febrero de 2017
// Se modifica para publicar mensaje mqtt

package com.example.dell.testapigoogle;

import android.content.Context;
import android.media.Ringtone;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
//Modif.1 importaciones añadidas INICIO
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.PolylineOptions;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//Modif.1 importaciones añadidas FIN
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback { //Modif.1.old
//public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {//Modif.1.new
    private GoogleMap mMap;
    ArrayList<LatLng> MarkerPoints; //Modif.1.new.ln
    String valOriDes="";//Modif.2.new.ln
    String topicoCompleto="";//Modif.3.new.ln
    //Modif.1.new VARIABLES AÑADIDAS fin
    //Modif.4.new.ini Configuración del Broker MQTT HiveMQ
    static String MQTTHOST; //= "tcp://192.168.1.7:1883";
    static String USERNAME = "ruta";
    static String PASSWORD ="123abc456xyz";
    String topicStr = "ruta/";
    MqttAndroidClient client;
    MqttConnectOptions options;
    Vibrator vibrator;
    Ringtone myRingtone;
    String topicom;
    public String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mqttdata";//Modif.2.new.ln
    //Modif.4.new.end del Broker MQTT HiveMQ MQTT HiveMQ


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//Modif.1 1
        setContentView(R.layout.activity_maps);

        File file = new File(path + "/conf.txt");
        String[] loadText = Load(file);

        String finalString = "";

        for (int i = 0; i < loadText.length; i++) {
            finalString += loadText[i];
        }
        MQTTHOST="tcp://"+finalString+":1883";
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST,clientId);

        options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        Bundle extras = getIntent().getExtras();
        ////client =  (MqttAndroidClient) extras.getSerializable("clienteMQTTobj");
        if(extras != null)
        {
            //String value = extras.getString("key");//Modif.2.old.ln
            valOriDes = extras.getString("topico");//Modif.2.new.ln
            topicoCompleto = extras.getString("topicompleto");//Modif.3.new.ln


            Context context = getApplicationContext();
            CharSequence text = valOriDes;//"Usted escogió la ruta 0";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

        //Modif.1.new INICIO
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //checkLocationPermission();//Modif.1.error
        }
        // Initializing
        MarkerPoints = new ArrayList<>();
        //Modif.1.new FIN

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap; //Modif.1 2

        //Modif.1.new INICIO
        //Se inicializa la localización, no se tiene éxito al tratar de inicializar los servicios de Google Play
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //buildGoogleApiClient();//Modif.1.error
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            //buildGoogleApiClient();//Modif.1.error
            mMap.setMyLocationEnabled(true);
        }

        //Modif.1.old FIN

        //Modif.1 Evento Onclick INICIO
        // Evento de clic sobre el mapa
        //mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {//Modif.2.old.ln

            //@Override//Modif.2.old.ln
            //public void onMapClick(LatLng point) {//Modif.2.old.ln

                // Verifica si se marco los 2 lugares, no solo 1
                //Modif.2.old.ln INICIO
                /*
                if (MarkerPoints.size() > 1) {
                    MarkerPoints.clear();
                    mMap.clear();
                }*/
                //Modif.2.old.ln FIN

                // Cada tap son puntos en el mapa a almacenarse
                //MarkerPoints.add(point);//Modif.2.old.ln************
                //MarkerPoints.add()//Modif.2.new.ln
        /*
                GeoApiContext context = new GeoApiContext().setApiKey("YOUR_API_KEY");
                GeocodingResult[] results =  GeocodingApi.geocode(context,
                "1600 Amphitheatre Parkway Mountain View, CA 94043").await();
                System.out.println(results[0].formattedAddress);
                */

                // Se configura los marcadores
                //MarkerOptions options = new MarkerOptions();//Modif.2.old.ln************

                // Su posición es en el punto en el que se dio el tap
                //options.position(point);//Modif.2.old.ln************

                /**
                 * El marcador de origen es de color verde
                 * l marcador del destino es de color rojo
                 */
                if (MarkerPoints.size() == 1) {
                    //options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));//Modif.2.old.ln************
                } else if (MarkerPoints.size() == 2) {
                    //options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));//Modif.2.old.ln************
                }

                //mMap.addMarker(options);//Modif.2.old.ln************

                // Verifica si el origen y el destino fueron capturados
                ////Modif.2.old.ini************ INICIO
                //if (MarkerPoints.size() >= 2) {
                //    LatLng origin = MarkerPoints.get(0);
                //    LatLng dest = MarkerPoints.get(1);
                ////Modif.2.old.ini************ FIN
                    // Se arma la url que consultara al Google Directions API
                    //String url = getUrl(origin, dest);//Modif.2.old.ln************

                    String[] separated = valOriDes.split(",");
                    String ori = separated[0];
                    String des = separated[1];
                    String url = getUrl(ori, des);
                    Log.d("onMapClick", url.toString());
                    FetchUrl FetchUrl = new FetchUrl();

                    // Comienza la descarga en formato JSON de la ruta calculada por el Google Directions API
                    FetchUrl.execute(url);
                    //Se mueve el enfoque del mapa
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));//Modif.2.old.ln************
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng());//Modif.2.new.ln************
                    //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));//Modif.2.old.ln************
                //}//Modif.2.old.ln************

            //}//Modif.2.old.ln
        //});//Modif.2.old.ln
        //Modif.1 Evento Onclick FIN


    }

    //Modif.1 CÓDIGO AÑADIDO inicio
    public void pub(View v)
    {
        //String topic = "foo/bar";//Modif.1.D.old

        //String topic = topicStr;//Modif.1.D.new
        //String payload = "the payload";//Modif.1.D.old
        String message = "evento reportado!!";//Modif.1.D.old
        byte[] encodedPayload = new byte[0];
        try {
            //encodedPayload = payload.getBytes("UTF-8");//Modif.1.D.old
            //MqttMessage message = new MqttMessage(encodedPayload);//Modif.1.D.old
            //client.publish(topic, message);//Modif.1.D.old
            client.publish(topicoCompleto, message.getBytes(),0,false);//Modif.1.D.new
            //} catch (UnsupportedEncodingException | MqttException e) {//Modif.1.D.old
        } catch (MqttException e) {//Modif.1.D.new
            e.printStackTrace();
        }
    }
    //Modif.2 función para suscribirse por MQTT INICIO

    //Modif.1 método getUrl INICIO
    //private String getUrl(LatLng origin, LatLng dest) {//Modif.1.new.ln
    private String getUrl(String origin, String dest) {//Modif.2.new.ln
        // origen de la ruta
        String str_origin = "origin=" + origin;//origin.latitude + "," + origin.longitude;

        // Ddestino de la ruta
        String str_dest = "destination=" + dest;//dest.latitude + "," + dest.longitude;


        // Sensor del GPS
        //String sensor = "sensor=false";//Modif.1.old.ln
        String sensor = "sensor=false";//Modif.1.new.ln

        // Formato del servicio Web
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Formato en el que entrega el resultado el servicio web
        String output = "json";

        // URL armada para enviarse al servicio web
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }
    //Modif.1 método getUrl FIN

    //Modif.1 Subclase FetchUrl INICIO
    // Extrae la data del servicio web
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // Para guardar la data que devuelva el Web Service
            String data = "";

            try {
                // Extrayendo los datos
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // IInvoca al hilo para pasarle los datos en JSON
            parserTask.execute(result);

        }
    }
    //Modif.1 Subclase FetchUrl FIN

    //Modif.1 método downloadUrl INICIO
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Se crea una conexión http para enviar la URL
            urlConnection = (HttpURLConnection) url.openConnection();

            // Conectandose a la URL
            urlConnection.connect();

            // RLeyendo datos de la URL
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    //Modif.1 método downloadUrl FIN

    //Modif.1 subclase ParserTask INICIO
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Manejando la data en un hilo no visible para el usuario
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Comienza el analisis de la data obtenida
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }
@Override
protected void onPostExecute(List<List<HashMap<String, String>>> result) {
    ArrayList<LatLng> points;
    PolylineOptions lineOptions = null;

    // Atravesando todas las rutas
    for (int i = 0; i < result.size(); i++) {
        points = new ArrayList<>();
        lineOptions = new PolylineOptions();

        // Desde la i-ésima ruta
        List<HashMap<String, String>> path = result.get(i);

        // Analizando todos los puntos de la i-ésima ruta
        for (int j = 0; j < path.size(); j++) {
            HashMap<String, String> point = path.get(j);

            double lat = Double.parseDouble(point.get("lat"));
            double lng = Double.parseDouble(point.get("lng"));
            LatLng position = new LatLng(lat, lng);
            if (j==path.size()/2 || j==(path.size()+1)/2) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(position));//Modif.2.new.ln
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));//Modif.2.old.ln
                mMap.animateCamera(CameraUpdateFactory.zoomTo(7));//Modif.2.new.ln
            }
            points.add(position);
        }

        // Se añade todos los puntos de la ruta
        lineOptions.addAll(points);
        //lineOptions.width(10);//Modif.2.old.ln
        lineOptions.width(7);//Modif.2.new.ln
        lineOptions.color(Color.RED);

        Log.d("onPostExecute","onPostExecute se decodifico las lineas");

    }

    // Se dibuja la poli-linea en google maps
    if(lineOptions != null) {
        mMap.addPolyline(lineOptions);
    }
    else {
        Log.d("onPostExecute","no se dibujo las polilineas");
    }

}
    }
    //Modif.1 subclase ParserTask FIN

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
