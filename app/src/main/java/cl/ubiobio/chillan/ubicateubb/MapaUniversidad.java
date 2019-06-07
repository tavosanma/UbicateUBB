package cl.ubiobio.chillan.ubicateubb;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import cl.ubiobio.chillan.ubicateubb.entities.Coordinates;
import cl.ubiobio.chillan.ubicateubb.entities.VolleySingleton;


public class MapaUniversidad extends AppCompatActivity implements LocationSource, OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener, Response.Listener<JSONObject>,Response.ErrorListener {

    private GoogleMap mMap;


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private View popup=null;
    private LocationSource.OnLocationChangedListener listener;
    private Location mCurrentLocation;
    public  LatLng University = new LatLng(-36.603383, -72.079246);
    public  LatLng SecondaryEntry = new LatLng(-36.599704 , -72.076281);
    private LocationManager mLocationManager;
    private static long UPDATE_INTERVAL_IN_MILLISECONDS;
    private AutoCompleteTextView autoCompleteTextView;
    Coordinates coordinates =  new Coordinates();
    public MarkerOptions markerOptions = new MarkerOptions();
    public TextView WalkTextView;
    public TextView DistanceTextView;
    public TextView BikeTextView;
    public TextView WalkTextViewES;
    public TextView DistanceTextViewES;
    public TextView BikeTextViewES;
    public Spinner spinner;
    Button Volver;
    public Polyline line;
    public Polyline line2;
    private Map<String, Marker> HashMapMarkers = new HashMap<>();
    //RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;
    ArrayList<Coordinates> coordinatesArrayListPHP = new ArrayList<>();
    public  String Place;
    Marker marker;

    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_universidad);
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        /** Catching IDs of the components of a layout**/
        //requestQueue = Volley.newRequestQueue(getApplicationContext());




        /** Instance to the class ConexionSQLIteHelper**/



            /** mLocationManager manage position request **/
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        /** Verify if the user have on the GPS, else, we send it the menu to turn it on**/
        boolean enabledGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabledGPS) {
            Toast.makeText(this, "No GPS signal", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        /** we establish the interval of upgrade of the position**/
        UPDATE_INTERVAL_IN_MILLISECONDS = 7000;

        /** The map fragment is searched and we start the map.
         * When the map is ready, we call onMapReady() **/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Volver=findViewById(R.id.Volver);
        Volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapaUniversidad.this,MainActivity.class));

            }
        });
        WalkTextView =  findViewById(R.id.WalkTextView);
        DistanceTextView=  findViewById(R.id.DistanceTextView);
        BikeTextView=  findViewById(R.id.BikeTextView);
        WalkTextViewES =  findViewById(R.id.WalkTextViewES);
        DistanceTextViewES=  findViewById(R.id.DistanceTextViewES);
        BikeTextViewES=  findViewById(R.id.BikeTextViewES);

    }

    /**
     * we manage the answer of the petition of permission**/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            /** if the permission was accepted, we start the process for capture the position **/
            enableMyLocation();
        } else {
            mPermissionDenied = true;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MapaUniversidad.this,MainActivity.class));

    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }


    /**
     * if we perform a click above their position (blue point), we show information about of this point**/

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Mi posición actual:\n" + "Latitud:"+location.getLatitude()+"\n"
                +"Longitud:"+location.getLongitude(), Toast.LENGTH_LONG).show();
    }

    /**
     * dialog of error for when do not accept the permission**/
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }
    private boolean isNetWorkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo !=null;
    }

    /**
     * The map it's found ready, we can modify some configuration**/
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LoadWebService();


        Button buscar = findViewById(R.id.ButtonBuscar);
        autoCompleteTextView = findViewById(R.id.EditTextBuscar);


        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Place = autoCompleteTextView.getText().toString();
                if (Place.equalsIgnoreCase("Académicos Del Departamento De Ciencias Básicas")
                        || Place.equalsIgnoreCase("Karina Vidal")
                        || Place.equalsIgnoreCase("Cesar Sandoval")
                        || Place.equalsIgnoreCase("Jefe Departamento de Deportes y Recreación")|| Place.equalsIgnoreCase("Secretaria Departamento de Actividad Física, Deportes y Recreación") || Place.equalsIgnoreCase("Área de Desarrollo Pedagógico y Tecnológico")|| Place.equalsIgnoreCase("Sala de Primeros Auxilios/Accidentes Laborales")|| Place.equalsIgnoreCase("Unidad de Prevención de Riesgos") || Place.equalsIgnoreCase("Área De Desarrollo Pedagógico Y Tecnológico")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Académicos Del Departamento De Ciencias Básicas");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Facultad De Ciencias Empresariales")
                        || Place.equalsIgnoreCase("Yaqueline Badillo")
                        || Place.equalsIgnoreCase("Marcela Pinto")
                        || Place.equalsIgnoreCase("Claudio Munñoz")
                        || Place.equalsIgnoreCase("Angélica Caro")
                        || Place.equalsIgnoreCase("Luis Gajardo")
                        || Place.equalsIgnoreCase("Gilberto Gutierrez")
                        || Place.equalsIgnoreCase("María Soto")
                        || Place.equalsIgnoreCase("Rodrigo Torres")
                        || Place.equalsIgnoreCase("Luis Ojeda")
                        || Place.equalsIgnoreCase("Luis Meriño")
                        || Place.equalsIgnoreCase("Sala de reuniones tesistas informáticos")
                        || Place.equalsIgnoreCase("Alfonso Rodríguez")
                        || Place.equalsIgnoreCase("Miguel Romero")
                        || Place.equalsIgnoreCase("Marlene muñoz")
                        || Place.equalsIgnoreCase("Paola Monroy")
                        || Place.equalsIgnoreCase("Cecilia Gallegos")
                        || Place.equalsIgnoreCase("Luz Silva")
                        || Place.equalsIgnoreCase("Alvaro Acuña")
                        || Place.equalsIgnoreCase("Paz Arias")
                        || Place.equalsIgnoreCase("Alex Medina")
                        || Place.equalsIgnoreCase("Marianela Moraga")
                        || Place.equalsIgnoreCase("Estela Rodríguez")
                        || Place.equalsIgnoreCase("Benito Umaña")
                        || Place.equalsIgnoreCase("Bernardo Vásquez")
                        || Place.equalsIgnoreCase("Juan Carlos Yévenez")
                        || Place.equalsIgnoreCase("Marcelo Navarrete")
                        || Place.equalsIgnoreCase("Omar Acuña")
                        || Place.equalsIgnoreCase("Luis Améstica")
                        || Place.equalsIgnoreCase("Juan Cabas")
                        || Place.equalsIgnoreCase("Edison Cornejo")
                        || Place.equalsIgnoreCase("Macarena Gallardo")
                        || Place.equalsIgnoreCase("Nataly Guiñez")
                        || Place.equalsIgnoreCase("Carolina Leyton")
                        || Place.equalsIgnoreCase("Virna Ortiz")
                        || Place.equalsIgnoreCase("Froilán Quezada")
                        || Place.equalsIgnoreCase("Rodrigo Romo")
                        || Place.equalsIgnoreCase("Carlos Salazar")
                        || Place.equalsIgnoreCase("César Salazar")
                        || Place.equalsIgnoreCase("FACE")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Facultad De Ciencias Empresariales");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Guardias")
                        ||Place.equalsIgnoreCase("Cabina De Vigilancia De La Universidad Del Bío-Bío")
                        ||Place.equalsIgnoreCase("Gustavo Cordova")
                        ||Place.equalsIgnoreCase("Luis Galdames")
                        ||Place.equalsIgnoreCase("Hector Sepúlveda")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Cabina De Vigilancia De La Universidad Del Bío-Bío");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Laboratorios Centrales De Computación")
                        ||Place.equalsIgnoreCase("Sala de Servidores")
                        ||Place.equalsIgnoreCase("Miguel Pincheira")
                        ||Place.equalsIgnoreCase("Jeanette Landeros")
                        ||Place.equalsIgnoreCase("Joel Acuña")
                        ||Place.equalsIgnoreCase("Fernando Santolaya")
                        ||Place.equalsIgnoreCase("Juan Carlos Figueroa")||Place.equalsIgnoreCase("Sala de VideoConferencia")||Place.equalsIgnoreCase("LC2") ||Place.equalsIgnoreCase("LC1")||Place.equalsIgnoreCase("Impresión de Tesis")||Place.equalsIgnoreCase("Sala de Estudio Laboratorios Centrales")||Place.equalsIgnoreCase("Sala de Laboratorio de Computación 3")||Place.equalsIgnoreCase("Sala de Laboratorio de Computación 2")||Place.equalsIgnoreCase("Sala de Laboratorio de Computación 1")||Place.equalsIgnoreCase("Laboratorio de Especialidad 2")||Place.equalsIgnoreCase("Laboratorio de Especialidad 1")||Place.equalsIgnoreCase("Préstamos Notebooks")||Place.equalsIgnoreCase("Scanner")||Place.equalsIgnoreCase("Lab Centrales")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Laboratorios Centrales De Computación");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Federación Universidad Del Bío-Bío")
                        || Place.equalsIgnoreCase("Sala de Ensayo de Música")|| Place.equalsIgnoreCase("Renovación Pase Escolar")|| Place.equalsIgnoreCase("Federación De Estudiantes")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Federación Universidad Del Bío-Bío");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Departamento De Ciencias Básicas")
                        || Place.equalsIgnoreCase("Laboratorio de Genómica y Biodiversidad")|| Place.equalsIgnoreCase("Laboratorio de Genómica y Biodiversidad")|| Place.equalsIgnoreCase("Laboratorio de Microbiología de Alimentos")|| Place.equalsIgnoreCase("Sala de Reuniones de Postgrado")|| Place.equalsIgnoreCase("Laboratorio de Fisiología")|| Place.equalsIgnoreCase("Laboratorio de Análisis de Alimentos")|| Place.equalsIgnoreCase("Laboratorio de Ingeniería en Recursos Naturales")|| Place.equalsIgnoreCase("Laboratorio de Fisiología Vascular")|| Place.equalsIgnoreCase("Laboratorio de Biología Celular y Molecular")|| Place.equalsIgnoreCase("Laboratorio de Fisiología")|| Place.equalsIgnoreCase("Laboratorio de Productos Naturales")|| Place.equalsIgnoreCase("Laboratorio de Genética Toxicológica")|| Place.equalsIgnoreCase("Laboratorio de Fotoquímica Inorgánica") || Place.equalsIgnoreCase("Laboratorio de Microalgas")|| Place.equalsIgnoreCase("Laboratorio de Microestructura y Modelación de Materiales Morosos")|| Place.equalsIgnoreCase("Laboratorio de Síntesis de Productos Naturales")|| Place.equalsIgnoreCase("Laboratorio de Ecofisiología Vegetal") || Place.equalsIgnoreCase("Laboratorio de Biología") || Place.equalsIgnoreCase("Grupo de Biodiversidad y Cambio Global") || Place.equalsIgnoreCase("Laboratorio de Toxicología de Alimentos")|| Place.equalsIgnoreCase("Laboratorio de Ecológica y Evolución")|| Place.equalsIgnoreCase("Laboratorio de Química 2")|| Place.equalsIgnoreCase("Laboratorio de Química 1")|| Place.equalsIgnoreCase("Laboratorio de Nutrición Aplicada") || Place.equalsIgnoreCase("Laboratorio de Fitoquímica Ecológica") || Place.equalsIgnoreCase("Facultad De Ciencias")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Departamento De Ciencias Básicas");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Snack-Bar")||Place.equalsIgnoreCase("Cafetería Universidad Del Bío-Bío")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Cafetería Universidad Del Bío-Bío");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Administración Central")
                        || Place.equalsIgnoreCase("Cobranzas")|| Place.equalsIgnoreCase("Certificados Alumnos Regular")|| Place.equalsIgnoreCase("Cajero Automático")|| Place.equalsIgnoreCase("Subdirección de Investigación")|| Place.equalsIgnoreCase("Departamento de Servicios Tecnológicos")|| Place.equalsIgnoreCase("Departamento de Personal")|| Place.equalsIgnoreCase("Dirección de Administración y Presupuesto") || Place.equalsIgnoreCase("Admisión y Registro Académico")|| Place.equalsIgnoreCase("Unidad de Formación Integral (Sala UFI)") || Place.equalsIgnoreCase("Unidad de Gestión Curricular y Monitoreo")|| Place.equalsIgnoreCase("Departamento de Pregrado Chillán")|| Place.equalsIgnoreCase("Dirección de Postgrado")|| Place.equalsIgnoreCase("Prorrectoría") || Place.equalsIgnoreCase("Casa Central")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Administración Central");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Laboratorio De Procesos De Alimentos")
                        || Place.equalsIgnoreCase("Sala de Preparación de Muestras")|| Place.equalsIgnoreCase("Sala de Estudio Magister en Ciencias en Alimentos")|| Place.equalsIgnoreCase("Sala de Procesos de Alimentos")|| Place.equalsIgnoreCase("Laboratorio de Evaluación Sensorial")|| Place.equalsIgnoreCase("Lab De Alimentos")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Laboratorio De Procesos De Alimentos");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Laboratorio de Experimentación, Control y Certificación de la Calidad de los Alimentos")
                        || Place.equalsIgnoreCase("LECYCA") || Place.equalsIgnoreCase("Laboratorio de Química(LECYCA)")|| Place.equalsIgnoreCase("Bromatología")|| Place.equalsIgnoreCase("Microbiología")|| Place.equalsIgnoreCase("Análisis de Alimentos y Aguas")|| Place.equalsIgnoreCase("Entrega de Muestras de Agua")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("LECYCA");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Facultad De Ciencias De La Salud Y De Los Alimentos")
                        || Place.equalsIgnoreCase("Pamela Montoya")
                        || Place.equalsIgnoreCase("Soledad Salazar")
                        || Place.equalsIgnoreCase("José Bastias")
                        || Place.equalsIgnoreCase("Romina Venegas")
                        || Place.equalsIgnoreCase("Patricio Oliva")
                        || Place.equalsIgnoreCase("Zusana Gutierrez")
                        || Place.equalsIgnoreCase("Virginia Garcia")
                        || Place.equalsIgnoreCase("Maritza Celis")
                        || Place.equalsIgnoreCase("FCSA")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Facultad De Ciencias De La Salud Y De Los Alimentos");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Centro De Simulación De Enfermería")
                        || Place.equalsIgnoreCase("Laboratorio de Habla y Lenguaje Adulto") || Place.equalsIgnoreCase("Laboratorio de Habla y Lenguaje Infantil") || Place.equalsIgnoreCase("Laboratorio de Voz") || Place.equalsIgnoreCase("Laboratorio de Audiología") || Place.equalsIgnoreCase("Unidad de Metabolismo Energético")|| Place.equalsIgnoreCase("Laboratorio de Experimentación en Técnicas Dietéticas")|| Place.equalsIgnoreCase("Laboratorio de Evaluación del Estado Nutricional")|| Place.equalsIgnoreCase("Centro de Simulación 3") || Place.equalsIgnoreCase("Centro de Simulación 2") || Place.equalsIgnoreCase("Centro de Simulación 1") || Place.equalsIgnoreCase("Laboratorio De Experimentación En Nutrición Y Dietética")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Centro De Simulación De Enfermería");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Aulas A")
                        || Place.equalsIgnoreCase("Anillados")|| Place.equalsIgnoreCase("Fotocopias")|| Place.equalsIgnoreCase("A1FM Hasta A6FM")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Aulas A");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Aulas B")
                        || Place.equalsIgnoreCase("Salas De Estudio B")|| Place.equalsIgnoreCase("B1FM Hasta B9FM")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Aulas B");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Aulas C")
                        || Place.equalsIgnoreCase("Laboratorio de Idiomas")|| Place.equalsIgnoreCase("C1FM Hasta C3FM")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Aulas C");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Aulas D")
                        || Place.equalsIgnoreCase("Sala De Anatomía")|| Place.equalsIgnoreCase("D1FM Hasta D4FM")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Aulas D");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Aulas E")
                        || Place.equalsIgnoreCase("Salas De Estudio E")|| Place.equalsIgnoreCase("E1FM Hasta E4FM")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Aulas E");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Biblioteca Principal")
                        ||Place.equalsIgnoreCase("Sala PIESDI(Programa De Inclusión De Estudiantes Con Discapacidad)")
                        ||Place.equalsIgnoreCase("Mónica Erazo")
                        ||Place.equalsIgnoreCase("Maritza Leiva")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Biblioteca Principal");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Gimnasio Central Fernando May")
                        || Place.equalsIgnoreCase("Cancha para BabyFootball")
                        || Place.equalsIgnoreCase("Pedro Campo")
                        || Place.equalsIgnoreCase("Adolfo Armijo")
                        || Place.equalsIgnoreCase("Luis Riquelme")
                        || Place.equalsIgnoreCase("Cesar Lipan")
                        || Place.equalsIgnoreCase("Manuel Duarte")
                        || Place.equalsIgnoreCase("Andrés Abarca")
                        || Place.equalsIgnoreCase("Cancha para Basquetball")|| Place.equalsIgnoreCase("Cancha para Volleyball")|| Place.equalsIgnoreCase("Cancha de Piso Flotante")|| Place.equalsIgnoreCase("Pedagogía En Eduación Fisica-Salud Estudiantil")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Gimnasio Central Fernando May");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Comedor")||Place.equalsIgnoreCase("Casino UBB")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Casino UBB");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Dirección De Desarrollo Estudiantil")
                        || Place.equalsIgnoreCase("Marcia Inostroza")
                        || Place.equalsIgnoreCase("Carolina Saldías")
                        || Place.equalsIgnoreCase("Karina Aedo")
                        || Place.equalsIgnoreCase("Eduardo Lorenzen")
                        || Place.equalsIgnoreCase("Jacqueline Cuevas")
                        || Place.equalsIgnoreCase("Gabriela Cid")
                        || Place.equalsIgnoreCase("Sonia Contreras")
                        || Place.equalsIgnoreCase("Viviana Riquelme")
                        || Place.equalsIgnoreCase("Sala de Procedimiento y Farmacia")|| Place.equalsIgnoreCase("Médico y Matrona")|| Place.equalsIgnoreCase("Lavado de Materiales y Farmacia")|| Place.equalsIgnoreCase("Jefe de Departamento de Salud Estudiantil")|| Place.equalsIgnoreCase("Esterelización")|| Place.equalsIgnoreCase("Dentista")|| Place.equalsIgnoreCase("Nutricionista y Psicólogo")|| Place.equalsIgnoreCase("DDE")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Dirección De Desarrollo Estudiantil");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Gimnasio Multitaller")
                        || Place.equalsIgnoreCase("Sala de Bicicletas Estáticas")|| Place.equalsIgnoreCase("Sala para Halterofilia")|| Place.equalsIgnoreCase("Sala GT1FM Y De Máquinas")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Gimnasio Multitaller");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Aula Magna Fernando May")
                        ||Place.equalsIgnoreCase("Seminarios")
                        || Place.equalsIgnoreCase("Clauido Rivera")||Place.equalsIgnoreCase("Charlas") ||Place.equalsIgnoreCase("Conferencias")||Place.equalsIgnoreCase("Titulaciones")||Place.equalsIgnoreCase("Sala Multiuso") ||Place.equalsIgnoreCase("Ceremonias")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Aula Magna Fernando May");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Tenis")||Place.equalsIgnoreCase("Canchas De Tenis")
                        ||Place.equalsIgnoreCase("Francisco Álvarez")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Canchas De Tenis");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Estadio UBB") ||Place.equalsIgnoreCase("Pista Atlética")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Estadio UBB");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Duchas Calientes")
                        ||Place.equalsIgnoreCase("Jorge Espinoza")||Place.equalsIgnoreCase("Camarines UBB")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Camarines UBB");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }

                if (Place.equalsIgnoreCase("Diseño Gráfico")
                        ||Place.equalsIgnoreCase("Jacqueline Santos")
                        ||Place.equalsIgnoreCase("Regina Luengo")
                        ||Place.equalsIgnoreCase("Yaricza Osorio")
                        || Place.equalsIgnoreCase("Taller de Gráfica")|| Place.equalsIgnoreCase("Sala de Estudio Personal y Trabajo Colaborativo")|| Place.equalsIgnoreCase("Sala de Presentaciones")|| Place.equalsIgnoreCase("Sala 1 Hasta 7 De Diseño")|| Place.equalsIgnoreCase("Sala Taller de Diseño")|| Place.equalsIgnoreCase("Laboratorio Digital de Diseño 2")|| Place.equalsIgnoreCase("Laboratorio Digital de Diseño 1")|| Place.equalsIgnoreCase("CEDIR")|| Place.equalsIgnoreCase("Sala de Iluminación")|| Place.equalsIgnoreCase("Sala de Fotografía")|| Place.equalsIgnoreCase("Escuela De Diseño Gráfico")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Diseño Gráfico");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Psicología")
                        ||Place.equalsIgnoreCase("Hilda Carriel")
                        ||Place.equalsIgnoreCase("Nelson Zicavo")
                        || Place.equalsIgnoreCase("Sala de Psicoterapia")|| Place.equalsIgnoreCase("Box 2")|| Place.equalsIgnoreCase("Box 1")|| Place.equalsIgnoreCase("Sala Espejo")|| Place.equalsIgnoreCase("Laboratorio de Psicología")|| Place.equalsIgnoreCase("Sala 2(Psicología)")|| Place.equalsIgnoreCase("Sala 1(Psicología)")|| Place.equalsIgnoreCase("Facultad de Educación y Humanidades")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Psicología");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Casa Patronal")
                        ||Place.equalsIgnoreCase("Andrea Zapata")
                        ||Place.equalsIgnoreCase("Emperatriz Nova Bustos")
                        || Place.equalsIgnoreCase("Biblioteca Marta Colvin")  || Place.equalsIgnoreCase("Departamento de Comunicación Visual")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Casa Patronal");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Pastos De Diseño Y Psicología")||Place.equalsIgnoreCase("Cancha De Football Diseño Gráfico")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Cancha De Football Diseño Gráfico");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Casino Marta Colvin") || Place.equalsIgnoreCase("Casino De Diseño Gráfico")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Casino Marta Colvin");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Museo Marta Colvin")||Place.equalsIgnoreCase("Galería Marta Colvin")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Museo Marta Colvin");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Nuevo Departamento De Ciencias Básicas")
                        ||Place.equalsIgnoreCase("Sala de Pre-Prácticos de Biología")||Place.equalsIgnoreCase("Sala de Preparación de Reactivos")||Place.equalsIgnoreCase("Laboratorio de Biología 2(Nuevo Departamento)")||Place.equalsIgnoreCase("Laboratorio de Biología 1(Nuevo Departamento)")||Place.equalsIgnoreCase("Laboratorio de Física(Nuevo Departamento)")||Place.equalsIgnoreCase("Laboratorios De Docencia")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Nuevo Departamento De Ciencias Básicas");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Nuevo Edificio De Investigadores")
                        || Place.equalsIgnoreCase("Gerardo Cabello")
                        || Place.equalsIgnoreCase("Claudio Collado")
                        || Place.equalsIgnoreCase("Anibal Coronel")
                        || Place.equalsIgnoreCase("Patricio Cumsille")
                        || Place.equalsIgnoreCase("Luis Fritz")
                        || Place.equalsIgnoreCase("Elías Irazoqui")
                        || Place.equalsIgnoreCase("Luis Lillo")
                        || Place.equalsIgnoreCase("Claudio Megí")
                        || Place.equalsIgnoreCase("Luis Moreno")
                        || Place.equalsIgnoreCase("Jairo Navarrete")
                        || Place.equalsIgnoreCase("Andres Rodriguez")
                        || Place.equalsIgnoreCase("Fernando Toledo")
                        || Place.equalsIgnoreCase("Carlos Torres")
                        || Place.equalsIgnoreCase("Gijsbertus Van der Veer")
                        || Place.equalsIgnoreCase("Marcela Vidal")
                        || Place.equalsIgnoreCase("Cristian Villavicencio")
                        || Place.equalsIgnoreCase("Enrique Werner")
                        ||Place.equalsIgnoreCase("Secretarias Profesores De Ciencias Básicas")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Nuevo Edificio De Investigadores");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Oficina De Partes")
                        ||Place.equalsIgnoreCase("Mario Sepúlveda")
                        ||Place.equalsIgnoreCase("Miriam oficina de partes")
                        ||Place.equalsIgnoreCase("Sala de Abastecimiento")||Place.equalsIgnoreCase("Sala de Inventario")||Place.equalsIgnoreCase("Mantención Y Movilización")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Oficina De Partes");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Sistema De Lavandería")
                        ||Place.equalsIgnoreCase("Lavadora")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Sistema De Lavandería");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("VRIP")
                        ||Place.equalsIgnoreCase("EG-2")||Place.equalsIgnoreCase("EG-1")||Place.equalsIgnoreCase("Sala de Estudio VRIP") ||Place.equalsIgnoreCase("Vicerrectoría De Investigación Y Postgrado")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("VRIP");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase(Place)){
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get(Place);
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }


            }
        });
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Place = autoCompleteTextView.getText().toString();
                if (Place.equalsIgnoreCase("Académicos Del Departamento De Ciencias Básicas")
                        || Place.equalsIgnoreCase("Karina Vidal")
                        || Place.equalsIgnoreCase("Cesar Sandoval")
                        || Place.equalsIgnoreCase("Jefe Departamento de Deportes y Recreación")|| Place.equalsIgnoreCase("Secretaria Departamento de Actividad Física, Deportes y Recreación") || Place.equalsIgnoreCase("Área de Desarrollo Pedagógico y Tecnológico")|| Place.equalsIgnoreCase("Sala de Primeros Auxilios/Accidentes Laborales")|| Place.equalsIgnoreCase("Unidad de Prevención de Riesgos") || Place.equalsIgnoreCase("Área De Desarrollo Pedagógico Y Tecnológico")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Académicos Del Departamento De Ciencias Básicas");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Facultad De Ciencias Empresariales")
                        || Place.equalsIgnoreCase("Yaqueline Badillo")
                        || Place.equalsIgnoreCase("Marcela Pinto")
                        || Place.equalsIgnoreCase("Claudio Munñoz")
                        || Place.equalsIgnoreCase("Angélica Caro")
                        || Place.equalsIgnoreCase("Luis Gajardo")
                        || Place.equalsIgnoreCase("Gilberto Gutierrez")
                        || Place.equalsIgnoreCase("María Soto")
                        || Place.equalsIgnoreCase("Rodrigo Torres")
                        || Place.equalsIgnoreCase("Luis Ojeda")
                        || Place.equalsIgnoreCase("Luis Meriño")
                        || Place.equalsIgnoreCase("Sala de reuniones tesistas informáticos")
                        || Place.equalsIgnoreCase("Alfonso Rodríguez")
                        || Place.equalsIgnoreCase("Miguel Romero")
                        || Place.equalsIgnoreCase("Marlene muñoz")
                        || Place.equalsIgnoreCase("Paola Monroy")
                        || Place.equalsIgnoreCase("Cecilia Gallegos")
                        || Place.equalsIgnoreCase("Luz Silva")
                        || Place.equalsIgnoreCase("Alvaro Acuña")
                        || Place.equalsIgnoreCase("Paz Arias")
                        || Place.equalsIgnoreCase("Alex Medina")
                        || Place.equalsIgnoreCase("Marianela Moraga")
                        || Place.equalsIgnoreCase("Estela Rodríguez")
                        || Place.equalsIgnoreCase("Benito Umaña")
                        || Place.equalsIgnoreCase("Bernardo Vásquez")
                        || Place.equalsIgnoreCase("Juan Carlos Yévenez")
                        || Place.equalsIgnoreCase("Marcelo Navarrete")
                        || Place.equalsIgnoreCase("Omar Acuña")
                        || Place.equalsIgnoreCase("Luis Améstica")
                        || Place.equalsIgnoreCase("Juan Cabas")
                        || Place.equalsIgnoreCase("Edison Cornejo")
                        || Place.equalsIgnoreCase("Macarena Gallardo")
                        || Place.equalsIgnoreCase("Nataly Guiñez")
                        || Place.equalsIgnoreCase("Carolina Leyton")
                        || Place.equalsIgnoreCase("Virna Ortiz")
                        || Place.equalsIgnoreCase("Froilán Quezada")
                        || Place.equalsIgnoreCase("Rodrigo Romo")
                        || Place.equalsIgnoreCase("Carlos Salazar")
                        || Place.equalsIgnoreCase("César Salazar")
                        || Place.equalsIgnoreCase("")
                        || Place.equalsIgnoreCase("FACE")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Facultad De Ciencias Empresariales");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Guardias")
                        ||Place.equalsIgnoreCase("Cabina De Vigilancia De La Universidad Del Bío-Bío")
                        ||Place.equalsIgnoreCase("Gustavo Cordova")
                        ||Place.equalsIgnoreCase("Luis Galdames")
                        ||Place.equalsIgnoreCase("Hector Sepúlveda")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Cabina De Vigilancia De La Universidad Del Bío-Bío");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Laboratorios Centrales De Computación")
                        ||Place.equalsIgnoreCase("Sala de Servidores")
                        ||Place.equalsIgnoreCase("Miguel Pincheira")
                        ||Place.equalsIgnoreCase("Jeanette Landeros")
                        ||Place.equalsIgnoreCase("Joel Acuña")
                        ||Place.equalsIgnoreCase("Fernando Santolaya")
                        ||Place.equalsIgnoreCase("Juan Carlos Figueroa")||Place.equalsIgnoreCase("Sala de VideoConferencia")||Place.equalsIgnoreCase("LC2") ||Place.equalsIgnoreCase("LC1")||Place.equalsIgnoreCase("Impresión de Tesis")||Place.equalsIgnoreCase("Sala de Estudio Laboratorios Centrales")||Place.equalsIgnoreCase("Sala de Laboratorio de Computación 3")||Place.equalsIgnoreCase("Sala de Laboratorio de Computación 2")||Place.equalsIgnoreCase("Sala de Laboratorio de Computación 1")||Place.equalsIgnoreCase("Laboratorio de Especialidad 2")||Place.equalsIgnoreCase("Laboratorio de Especialidad 1")||Place.equalsIgnoreCase("Préstamos Notebooks")||Place.equalsIgnoreCase("Scanner")||Place.equalsIgnoreCase("Lab Centrales")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Laboratorios Centrales De Computación");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Federación Universidad Del Bío-Bío")
                        || Place.equalsIgnoreCase("Sala de Ensayo de Música")|| Place.equalsIgnoreCase("Renovación Pase Escolar")|| Place.equalsIgnoreCase("Federación De Estudiantes")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Federación Universidad Del Bío-Bío");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Departamento De Ciencias Básicas")
                        || Place.equalsIgnoreCase("Laboratorio de Genómica y Biodiversidad")|| Place.equalsIgnoreCase("Laboratorio de Genómica y Biodiversidad")|| Place.equalsIgnoreCase("Laboratorio de Microbiología de Alimentos")|| Place.equalsIgnoreCase("Sala de Reuniones de Postgrado")|| Place.equalsIgnoreCase("Laboratorio de Fisiología")|| Place.equalsIgnoreCase("Laboratorio de Análisis de Alimentos")|| Place.equalsIgnoreCase("Laboratorio de Ingeniería en Recursos Naturales")|| Place.equalsIgnoreCase("Laboratorio de Fisiología Vascular")|| Place.equalsIgnoreCase("Laboratorio de Biología Celular y Molecular")|| Place.equalsIgnoreCase("Laboratorio de Fisiología")|| Place.equalsIgnoreCase("Laboratorio de Productos Naturales")|| Place.equalsIgnoreCase("Laboratorio de Genética Toxicológica")|| Place.equalsIgnoreCase("Laboratorio de Fotoquímica Inorgánica") || Place.equalsIgnoreCase("Laboratorio de Microalgas")|| Place.equalsIgnoreCase("Laboratorio de Microestructura y Modelación de Materiales Morosos")|| Place.equalsIgnoreCase("Laboratorio de Síntesis de Productos Naturales")|| Place.equalsIgnoreCase("Laboratorio de Ecofisiología Vegetal") || Place.equalsIgnoreCase("Laboratorio de Biología") || Place.equalsIgnoreCase("Grupo de Biodiversidad y Cambio Global") || Place.equalsIgnoreCase("Laboratorio de Toxicología de Alimentos")|| Place.equalsIgnoreCase("Laboratorio de Ecológica y Evolución")|| Place.equalsIgnoreCase("Laboratorio de Química 2")|| Place.equalsIgnoreCase("Laboratorio de Química 1")|| Place.equalsIgnoreCase("Laboratorio de Nutrición Aplicada") || Place.equalsIgnoreCase("Laboratorio de Fitoquímica Ecológica") || Place.equalsIgnoreCase("Facultad De Ciencias")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Departamento De Ciencias Básicas");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Snack-Bar")||Place.equalsIgnoreCase("Cafetería Universidad Del Bío-Bío")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Cafetería Universidad Del Bío-Bío");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Administración Central")
                        || Place.equalsIgnoreCase("Cobranzas")|| Place.equalsIgnoreCase("Certificados Alumnos Regular")|| Place.equalsIgnoreCase("Cajero Automático")|| Place.equalsIgnoreCase("Subdirección de Investigación")|| Place.equalsIgnoreCase("Departamento de Servicios Tecnológicos")|| Place.equalsIgnoreCase("Departamento de Personal")|| Place.equalsIgnoreCase("Dirección de Administración y Presupuesto") || Place.equalsIgnoreCase("Admisión y Registro Académico")|| Place.equalsIgnoreCase("Unidad de Formación Integral (Sala UFI)") || Place.equalsIgnoreCase("Unidad de Gestión Curricular y Monitoreo")|| Place.equalsIgnoreCase("Departamento de Pregrado Chillán")|| Place.equalsIgnoreCase("Dirección de Postgrado")|| Place.equalsIgnoreCase("Prorrectoría") || Place.equalsIgnoreCase("Casa Central")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Administración Central");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Laboratorio De Procesos De Alimentos")
                        || Place.equalsIgnoreCase("Sala de Preparación de Muestras")|| Place.equalsIgnoreCase("Sala de Estudio Magister en Ciencias en Alimentos")|| Place.equalsIgnoreCase("Sala de Procesos de Alimentos")|| Place.equalsIgnoreCase("Laboratorio de Evaluación Sensorial")|| Place.equalsIgnoreCase("Lab De Alimentos")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Laboratorio De Procesos De Alimentos");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Laboratorio de Experimentación, Control y Certificación de la Calidad de los Alimentos")
                        || Place.equalsIgnoreCase("LECYCA") || Place.equalsIgnoreCase("Laboratorio de Química(LECYCA)")|| Place.equalsIgnoreCase("Bromatología")|| Place.equalsIgnoreCase("Microbiología")|| Place.equalsIgnoreCase("Análisis de Alimentos y Aguas")|| Place.equalsIgnoreCase("Entrega de Muestras de Agua")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("LECYCA");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Facultad De Ciencias De La Salud Y De Los Alimentos")
                        || Place.equalsIgnoreCase("Pamela Montoya")
                        || Place.equalsIgnoreCase("Soledad Salazar")
                        || Place.equalsIgnoreCase("José Bastias")
                        || Place.equalsIgnoreCase("Romina Venegas")
                        || Place.equalsIgnoreCase("Patricio Oliva")
                        || Place.equalsIgnoreCase("Zusana Gutierrez")
                        || Place.equalsIgnoreCase("Virginia Garcia")
                        || Place.equalsIgnoreCase("Maritza Celis")
                        || Place.equalsIgnoreCase("FCSA")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Facultad De Ciencias De La Salud Y De Los Alimentos");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Centro De Simulación De Enfermería")
                        || Place.equalsIgnoreCase("Laboratorio de Habla y Lenguaje Adulto") || Place.equalsIgnoreCase("Laboratorio de Habla y Lenguaje Infantil") || Place.equalsIgnoreCase("Laboratorio de Voz") || Place.equalsIgnoreCase("Laboratorio de Audiología") || Place.equalsIgnoreCase("Unidad de Metabolismo Energético")|| Place.equalsIgnoreCase("Laboratorio de Experimentación en Técnicas Dietéticas")|| Place.equalsIgnoreCase("Laboratorio de Evaluación del Estado Nutricional")|| Place.equalsIgnoreCase("Centro de Simulación 3") || Place.equalsIgnoreCase("Centro de Simulación 2") || Place.equalsIgnoreCase("Centro de Simulación 1") || Place.equalsIgnoreCase("Laboratorio De Experimentación En Nutrición Y Dietética")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Centro De Simulación De Enfermería");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Aulas A")
                        || Place.equalsIgnoreCase("Anillados")|| Place.equalsIgnoreCase("Fotocopias")|| Place.equalsIgnoreCase("A1FM Hasta A6FM")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Aulas A");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Aulas B")
                        || Place.equalsIgnoreCase("Salas De Estudio B")|| Place.equalsIgnoreCase("B1FM Hasta B9FM")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Aulas B");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Aulas C")
                        || Place.equalsIgnoreCase("Laboratorio de Idiomas")|| Place.equalsIgnoreCase("C1FM Hasta C3FM")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Aulas C");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Aulas D")
                        || Place.equalsIgnoreCase("Sala De Anatomía")|| Place.equalsIgnoreCase("D1FM Hasta D4FM")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Aulas D");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Aulas E")
                        || Place.equalsIgnoreCase("Salas De Estudio E")|| Place.equalsIgnoreCase("E1FM Hasta E4FM")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Aulas E");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Biblioteca Principal")
                        ||Place.equalsIgnoreCase("Sala PIESDI(Programa De Inclusión De Estudiantes Con Discapacidad)")
                        ||Place.equalsIgnoreCase("Mónica Erazo")
                        ||Place.equalsIgnoreCase("Maritza Leiva")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Biblioteca Principal");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Gimnasio Central Fernando May")
                        || Place.equalsIgnoreCase("Cancha para BabyFootball")
                        || Place.equalsIgnoreCase("Pedro Campo")
                        || Place.equalsIgnoreCase("Adolfo Armijo")
                        || Place.equalsIgnoreCase("Luis Riquelme")
                        || Place.equalsIgnoreCase("Cesar Lipan")
                        || Place.equalsIgnoreCase("Manuel Duarte")
                        || Place.equalsIgnoreCase("Andrés Abarca")
                        || Place.equalsIgnoreCase("Cancha para Basquetball")|| Place.equalsIgnoreCase("Cancha para Volleyball")|| Place.equalsIgnoreCase("Cancha de Piso Flotante")|| Place.equalsIgnoreCase("Pedagogía En Eduación Fisica-Salud Estudiantil")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Gimnasio Central Fernando May");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Comedor")||Place.equalsIgnoreCase("Casino UBB")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Casino UBB");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Dirección De Desarrollo Estudiantil")
                        || Place.equalsIgnoreCase("Marcia Inostroza")
                        || Place.equalsIgnoreCase("Carolina Saldías")
                        || Place.equalsIgnoreCase("Karina Aedo")
                        || Place.equalsIgnoreCase("Eduardo Lorenzen")
                        || Place.equalsIgnoreCase("Jacqueline Cuevas")
                        || Place.equalsIgnoreCase("Gabriela Cid")
                        || Place.equalsIgnoreCase("Sonia Contreras")
                        || Place.equalsIgnoreCase("Viviana Riquelme")
                        || Place.equalsIgnoreCase("Sala de Procedimiento y Farmacia")|| Place.equalsIgnoreCase("Médico y Matrona")|| Place.equalsIgnoreCase("Lavado de Materiales y Farmacia")|| Place.equalsIgnoreCase("Jefe de Departamento de Salud Estudiantil")|| Place.equalsIgnoreCase("Esterelización")|| Place.equalsIgnoreCase("Dentista")|| Place.equalsIgnoreCase("Nutricionista y Psicólogo")|| Place.equalsIgnoreCase("DDE")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Dirección De Desarrollo Estudiantil");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Gimnasio Multitaller")
                        || Place.equalsIgnoreCase("Sala de Bicicletas Estáticas")|| Place.equalsIgnoreCase("Sala para Halterofilia")|| Place.equalsIgnoreCase("Sala GT1FM Y De Máquinas")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Gimnasio Multitaller");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Aula Magna Fernando May")
                        ||Place.equalsIgnoreCase("Seminarios")
                        || Place.equalsIgnoreCase("Clauido Rivera")||Place.equalsIgnoreCase("Charlas") ||Place.equalsIgnoreCase("Conferencias")||Place.equalsIgnoreCase("Titulaciones")||Place.equalsIgnoreCase("Sala Multiuso") ||Place.equalsIgnoreCase("Ceremonias")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Aula Magna Fernando May");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Tenis")||Place.equalsIgnoreCase("Canchas De Tenis")
                        ||Place.equalsIgnoreCase("Francisco Álvarez")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Canchas De Tenis");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Estadio UBB") ||Place.equalsIgnoreCase("Pista Atlética")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Estadio UBB");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Duchas Calientes")
                        ||Place.equalsIgnoreCase("Jorge Espinoza")||Place.equalsIgnoreCase("Camarines UBB")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Camarines UBB");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }

                if (Place.equalsIgnoreCase("Diseño Gráfico")
                        ||Place.equalsIgnoreCase("Jacqueline Santos")
                        ||Place.equalsIgnoreCase("Regina Luengo")
                        ||Place.equalsIgnoreCase("Yaricza Osorio")
                        || Place.equalsIgnoreCase("Taller de Gráfica")|| Place.equalsIgnoreCase("Sala de Estudio Personal y Trabajo Colaborativo")|| Place.equalsIgnoreCase("Sala de Presentaciones")|| Place.equalsIgnoreCase("Sala 1 Hasta 7 De Diseño")|| Place.equalsIgnoreCase("Sala Taller de Diseño")|| Place.equalsIgnoreCase("Laboratorio Digital de Diseño 2")|| Place.equalsIgnoreCase("Laboratorio Digital de Diseño 1")|| Place.equalsIgnoreCase("CEDIR")|| Place.equalsIgnoreCase("Sala de Iluminación")|| Place.equalsIgnoreCase("Sala de Fotografía")|| Place.equalsIgnoreCase("Escuela De Diseño Gráfico")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Diseño Gráfico");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Psicología")
                        ||Place.equalsIgnoreCase("Hilda Carriel")
                        ||Place.equalsIgnoreCase("Nelson Zicavo")
                        || Place.equalsIgnoreCase("Sala de Psicoterapia")|| Place.equalsIgnoreCase("Box 2")|| Place.equalsIgnoreCase("Box 1")|| Place.equalsIgnoreCase("Sala Espejo")|| Place.equalsIgnoreCase("Laboratorio de Psicología")|| Place.equalsIgnoreCase("Sala 2(Psicología)")|| Place.equalsIgnoreCase("Sala 1(Psicología)")|| Place.equalsIgnoreCase("Facultad de Educación y Humanidades")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Psicología");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Casa Patronal")
                        ||Place.equalsIgnoreCase("Andrea Zapata")
                        ||Place.equalsIgnoreCase("Emperatriz Nova Bustos")
                        || Place.equalsIgnoreCase("Biblioteca Marta Colvin")  || Place.equalsIgnoreCase("Departamento de Comunicación Visual")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Casa Patronal");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Pastos De Diseño Y Psicología")||Place.equalsIgnoreCase("Cancha De Football Diseño Gráfico")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Cancha De Football Diseño Gráfico");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Casino Marta Colvin") || Place.equalsIgnoreCase("Casino De Diseño Gráfico")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Casino Marta Colvin");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Museo Marta Colvin")||Place.equalsIgnoreCase("Galería Marta Colvin")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Museo Marta Colvin");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Nuevo Departamento De Ciencias Básicas")
                        ||Place.equalsIgnoreCase("Sala de Pre-Prácticos de Biología")||Place.equalsIgnoreCase("Sala de Preparación de Reactivos")||Place.equalsIgnoreCase("Laboratorio de Biología 2(Nuevo Departamento)")||Place.equalsIgnoreCase("Laboratorio de Biología 1(Nuevo Departamento)")||Place.equalsIgnoreCase("Laboratorio de Física(Nuevo Departamento)")||Place.equalsIgnoreCase("Laboratorios De Docencia")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Nuevo Departamento De Ciencias Básicas");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Nuevo Edificio De Investigadores")
                        || Place.equalsIgnoreCase("Gerardo Cabello")
                        || Place.equalsIgnoreCase("Claudio Collado")
                        || Place.equalsIgnoreCase("Anibal Coronel")
                        || Place.equalsIgnoreCase("Patricio Cumsille")
                        || Place.equalsIgnoreCase("Luis Fritz")
                        || Place.equalsIgnoreCase("Elías Irazoqui")
                        || Place.equalsIgnoreCase("Luis Lillo")
                        || Place.equalsIgnoreCase("Claudio Megí")
                        || Place.equalsIgnoreCase("Luis Moreno")
                        || Place.equalsIgnoreCase("Jairo Navarrete")
                        || Place.equalsIgnoreCase("Andres Rodriguez")
                        || Place.equalsIgnoreCase("Fernando Toledo")
                        || Place.equalsIgnoreCase("Carlos Torres")
                        || Place.equalsIgnoreCase("Gijsbertus Van der Veer")
                        || Place.equalsIgnoreCase("Marcela Vidal")
                        || Place.equalsIgnoreCase("Cristian Villavicencio")
                        || Place.equalsIgnoreCase("Enrique Werner")
                        ||Place.equalsIgnoreCase("Secretarias Profesores De Ciencias Básicas")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Nuevo Edificio De Investigadores");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Oficina De Partes")
                        ||Place.equalsIgnoreCase("Mario Sepúlveda")
                        ||Place.equalsIgnoreCase("Miriam oficina de partes")
                        ||Place.equalsIgnoreCase("Sala de Abastecimiento")||Place.equalsIgnoreCase("Sala de Inventario")||Place.equalsIgnoreCase("Mantención Y Movilización")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Oficina De Partes");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("Sistema De Lavandería")
                        ||Place.equalsIgnoreCase("Lavadora")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("Sistema De Lavandería");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase("VRIP")
                        ||Place.equalsIgnoreCase("EG-2")||Place.equalsIgnoreCase("EG-1")||Place.equalsIgnoreCase("Sala de Estudio VRIP") ||Place.equalsIgnoreCase("Vicerrectoría De Investigación Y Postgrado")) {
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get("VRIP");
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }
                if (Place.equalsIgnoreCase(Place)){
                    MethodInputManagerAndResetSetTextAutoComplete();
                    marker = HashMapMarkers.get(Place);
                    onMarkerClick(marker);
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                    generateToast(Place+ " "+"se encuentra en este lugar");
                    generateToast("Para más información presiona el cuadro de texto");
                    return;
                }


                Toast.makeText(getApplicationContext(),"Este lugar no existe, intente otro",Toast.LENGTH_LONG).show();


            }

        });

        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        /** Activation of controls in the map**/
        mMap.getUiSettings().setZoomControlsEnabled(true); //control of zoom
        mMap.getUiSettings().setMyLocationButtonEnabled(true); //we enable botton to return to their position
        mMap.getUiSettings().setCompassEnabled(true); // the map search the north

        /** Gestión de algunos eventos**/
        mMap.setOnMyLocationClickListener(this); //click above position
       //click large in the map
        mMap.setOnMarkerClickListener(this); //click above mark
        mMap.setOnInfoWindowClickListener(this); //click above the information of a mark

        mMap.addMarker(markerOptions.position(SecondaryEntry)
                .title("Universidad del Bío-Bío").snippet("Entrada secundaria por Diseño gráfico y Psicología")).showInfoWindow();
        mMap.addMarker(markerOptions.position(University)
                .title("Universidad del Bío-Bío").snippet("Entrada principal ")).showInfoWindow();




        enableMyLocation();






        /** iniciamos el proceso de captura de posiciones **/



    }

    private void LoadWebService() {
        String webHost=getString(R.string.webhost);

        String url = webHost+"/WebServiceQueryListCoordinates.php";
        jsonObjectRequest= new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        //requestQueue.add(jsonObjectRequest);
        VolleySingleton.getInstanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
    @Override
    public void onErrorResponse(VolleyError error) {
        generateToast("Error de conexión, vuelve a iniciar la aplicación para ver los marcadores en el mapa");
    }

    @Override
    public void onResponse(JSONObject response) {

        ArrayList<String> ContainPlaces= new ArrayList<>();
           ArrayList<String> TitlePlaces= new ArrayList<>();
           ArrayList<String> SnippetPlaces= new ArrayList<>();
           ArrayList<String> ContainManuallySomePlaces= new ArrayList<>(Arrays.asList("Miriam oficina de partes","Mario Sepulveda","Gerardo Cabello","Claudio Collado","Anibal Coronel","Patricio Cumsille","Luis Fritz","Elías Irazoqui","Luis Lillo","Claudio Megí","Luis Moreno","Jairo Navarrete","Andres Rodriguez","Fernando Toledo","Carlos Torres","Gijsbertus Van der Veer","Marcela Vidal","Cristian Villavicencio","Enrique Werner","Andrea Zapata","Emperatriz Nova Bustos","Jacqueline Santos","Regina Luengo","Yaricza Osorio","Hilda Carriel","Nelson Zicavo","Jorge Espinoza","Francisco Álvarez","Marcia Inostroza","Carolina Saldías","Karina Aedo","Eduardo Lorenzen","Jacqueline Cuevas","Gabriela Cid","Sonia Contreras","Viviana Riquelme","Clauido Rivera","Mónica Erazo","Maritza Leiva","Pedro Campo","Adolfo Armijo","Luis Riquelme","Cesar Lipan","Manuel Duarte","Andrés Abarca","Pamela Montoya","Soledad Salazar","José Bastias","Romina Venegas","Patricio Oliva","Zusana Gutierrez","Virginia Garcia","Maritza Celis","Gustavo Cordova","Luis Galdames","Hector Sepúlveda","Sala de Servidores","Miguel Pincheira","Jeanette Landeros","Joel Acuña","Fernando Santolaya","Juan Carlos Figueroa","Yaqueline Badillo","Marcela Pinto","Claudio Munñoz","Angélica Caro","Luis Gajardo","Gilberto Gutierrez","María Soto","Rodrigo Torres","Luis Ojeda","Luis Meriño","Sala de reuniones tesistas informáticos","Alfonso Rodríguez","Miguel Romero","Marlene muñoz","Paola Monroy","Cecilia Gallegos","Luz Silva","Alvaro Acuña","Paz Arias","Alex Medina","Marianela Moraga","Estela Rodríguez","Benito Umaña","Bernardo Vásquez","Juan Carlos Yévenez","Marcelo Navarrete","Omar Acuña","Luis Améstica","Juan Cabas","Edison Cornejo","Macarena Gallardo","Nataly Guiñez","Carolina Leyton","Virna Ortiz","Froilán Quezada","Rodrigo Romo","Carlos Salazar","César Salazar",
                   "Karina Vidal","Cesar Sandoval","Unidad de Prevención de Riesgos","Sala de Primeros Auxilios/Accidentes Laborales","Área de Desarrollo Pedagógico y Tecnológico"
                ,"Secretaria Departamento de Actividad Física, Deportes y Recreación","Jefe Departamento de Deportes y Recreación","Prorrectoría","Dirección de Postgrado","Departamento de Pregrado Chillán","Unidad de Gestión Curricular y Monitoreo"
                ,"Unidad de Formación Integral (Sala UFI)","Admisión y Registro Académico","Dirección de Administración y Presupuesto","Departamento de Personal","Departamento de Servicios Tecnológicos","Subdirección de Investigación","Cajero Automático","Certificados Alumnos Regular"
                ,"Cobranzas","Sala Multiuso","Titulaciones","Conferencias","Charlas","Seminarios","Ceremonias","Fotocopias","Anillados","Salas De Estudio B","Laboratorio de Idiomas","Sala De Anatomía","Biblioteca Marta Colvin","Centro de Simulación 1","Centro de Simulación 2","Centro de Simulación 3"
                ,"Laboratorio de Evaluación del Estado Nutricional","Laboratorio de Experimentación en Técnicas Dietéticas","Unidad de Metabolismo Energético","Laboratorio de Audiología","Laboratorio de Voz","Laboratorio de Habla y Lenguaje Infantil","Laboratorio de Habla y Lenguaje Adulto"
                ,"Laboratorio de Fitoquímica Ecológica","Laboratorio de Nutrición Aplicada","Laboratorio de Química 1","Laboratorio de Química 2","Laboratorio de Ecológica y Evolución","Laboratorio de Toxicología de Alimentos","Grupo de Biodiversidad y Cambio Global","Laboratorio de Biología"
                ,"Laboratorio de Ecofisiología Vegetal","Laboratorio de Síntesis de Productos Naturales","Laboratorio de Microestructura y Modelación de Materiales Morosos","Laboratorio de Microalgas","Laboratorio de Fotoquímica Inorgánica","Laboratorio de Genética Toxicológica"
                ,"Laboratorio de Productos Naturales","Laboratorio de Fisiología","Laboratorio de Biología Celular y Molecular","Laboratorio de Fisiología Vascular","Laboratorio de Ingeniería en Recursos Naturales","Laboratorio de Análisis de Alimentos","Laboratorio de Fisiología"
                ,"Sala de Reuniones de Postgrado","Laboratorio de Microbiología de Alimentos","Laboratorio de Genómica y Biodiversidad","Nutricionista y Psicólogo","Dentista","Esterelización","Jefe de Departamento de Salud Estudiantil","Lavado de Materiales y Farmacia","Médico y Matrona","Sala de Procedimiento y Farmacia"
                ,"Sala de Fotografía","Sala de Iluminación","CEDIR","Laboratorio Digital de Diseño 1","Laboratorio Digital de Diseño 2","Sala Taller de Diseño","Sala 1 de Diseño","Sala 2 de Diseño","Sala 3 de Diseño","Sala 4 de Diseño","Sala 5 de Diseño","Sala 6 de Diseño","Sala 7 de Diseño"
                ,"Sala de Presentaciones","Sala de Estudio Personal y Trabajo Colaborativo","Taller de Gráfica","ICI","Contador Público Auditor","Ingeniería Comercial","Renovación Pase Escolar","Sala de Ensayo de Música","Cancha de Piso Flotante"
                ,"Cancha para Volleyball","Cancha para Basquetball","Cancha para BabyFootball","Sala para Halterofilia","Sala de Bicicletas Estáticas","Entrega de Muestras de Agua","Análisis de Alimentos y Aguas","Laboratorio Sensorial","Microbiología","Bromatología","Laboratorio de Química(LECYCA)","Laboratorio de Física(Nuevo Departamento)"
                ,"Laboratorio de Biología 1(Nuevo Departamento)","Laboratorio de Biología 2(Nuevo Departamento)","Sala de Preparación de Reactivos","Sala de Pre-Prácticos de Biología","Sala de Inventario","Sala de Abastecimiento","Laboratorio de Evaluación Sensorial","Sala de Procesos de Alimentos","Sala de Estudio Magister en Ciencias en Alimentos"
                ,"Sala de Preparación de Muestras","Scanners","Préstamos Notebooks","Laboratorio de Especialidad 1","Laboratorio de Especialidad 2","Sala de Laboratorio de Computación 1","Sala de Laboratorio de Computación 2","Sala de Laboratorio de Computación 3","Sala de Estudio Laboratorios Centrales","Impresión de Tesis","LC1","LC2","Sala de VideoConferencia"
                ,"Sala de Servidores","Sala 1(Psicología)","Sala 2(Psicología)","Salas De Estudio E","Laboratorio de Psicología","Sala Espejo","Box 1 ","Box 2","Sala de Psicoterapia","Sala de Estudio VRIP","EG-1","EG-2","DST"));
        List<LatLng> latLng = new ArrayList<>();
        coordinates = null;
        coordinatesArrayListPHP=new ArrayList<>();
        JSONArray jsonArray = response.optJSONArray("coordinates");
        try {
            for (int i= 0;i<jsonArray.length();i++){
                coordinates = new Coordinates();
                JSONObject jsonObject = null;
                jsonObject=jsonArray.getJSONObject(i);
                coordinates.setTitle(jsonObject.optString("title"));
                coordinates.setSnippet(jsonObject.optString("snippet"));
                coordinates.setLatitude(jsonObject.optDouble("latitude"));
                coordinates.setLongitude(jsonObject.optDouble("longitude"));
                coordinatesArrayListPHP.add(coordinates);

            }
            for (int i  = 0;i<coordinatesArrayListPHP.size();i++) {

                latLng.add(new LatLng(coordinatesArrayListPHP.get(i).getLatitude(),coordinatesArrayListPHP.get(i).getLongitude()));
                mMap.addMarker( markerOptions.position((latLng.get(i))).title(coordinatesArrayListPHP.get(i).getTitle()).snippet(coordinatesArrayListPHP.get(i).getSnippet())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    /*mMap.addMarker(markerOptions.position(new LatLng(coordinatesArrayListPHP.get(i).getLatitude(), coordinatesArrayListPHP.get(i).getLongitude()
                    )).title(coordinatesArrayListPHP.get(i).getTitle()).snippet(coordinatesArrayListPHP.get(i).getSnippet()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).draggable(true));*/

                    HashMapMarkers.put(coordinatesArrayListPHP.get(i).getTitle(),mMap.addMarker( markerOptions.position((latLng.get(i))).title(coordinatesArrayListPHP.get(i).getTitle()).snippet(coordinatesArrayListPHP.get(i).getSnippet())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))));

                TitlePlaces.add(coordinatesArrayListPHP.get(i).getTitle());
                SnippetPlaces.add(coordinatesArrayListPHP.get(i).getSnippet());

            }
            ContainPlaces.addAll(TitlePlaces);
            ContainPlaces.addAll(SnippetPlaces);
            ContainPlaces.addAll(ContainManuallySomePlaces);

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,ContainPlaces);
            autoCompleteTextView.setAdapter(arrayAdapter);




        }catch (JSONException e ){
            e.printStackTrace();
        }

    }



    private void MethodInputManagerAndResetSetTextAutoComplete() {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
        autoCompleteTextView.setText("");
    }










    /**
     * event that is activated to perform click above the information display for a marker
     **/
    @Override
    public void onInfoWindowClick(Marker marker) {
            for (int i = 0; i < coordinatesArrayListPHP.size(); i++) {
                if (marker.getTitle().equals(coordinatesArrayListPHP.get(i).getTitle())) {
                    String GetTitle = coordinatesArrayListPHP.get(i).getTitle();
                    String GetTitleWithoutSpaceAndAccent = GetTitle.replace(" ", "").replace("-", "").
                            replace("á", "a").replace("é", "e").
                            replace("í", "i").replace("ó", "o").replace("ú", "u").replace("ñ", "ni");
                    String a = "cl.ubiobio.chillan.ubicateubb." + GetTitleWithoutSpaceAndAccent;
                    try {

                        Class<?> c = Class.forName(a);
                        Intent intent = new Intent(this, c);
                        startActivity(intent);
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        } else {
                            Log.d("TAG", "The interstitial wasn't loaded yet.");
                        }

                    } catch (ClassNotFoundException ignored) {
                        if (isNetWorkAvailable()) {
                            String Title = marker.getTitle();
                            Intent intent = new Intent(this, MarcadorAddedForAPP.class);
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            } else {
                                Log.d("TAG", "The interstitial wasn't loaded yet.");
                            }
                            intent.putExtra("title", Title);


                            startActivity(intent);
                        }else {
                            generateToast("No hay conexión a internet, intente conectarse.");
                        }
                    }
                }
            }
                 /*Log.d("click", "click en info");
        LatLng origen = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        String url = obtenerDireccionesURL(origen, marker.getPosition()); //funcion para generar la URL para solicitar la ruta


        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);*/


    }
    public void enableMyLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            /** If the permission was not granted or no has been requested, is requested **/
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            /** When we have the permission, we tell to map that capture the position and we modify of where
             *  is obtain the position, with the goal of control how and when is upgrade**/
            mMap.setMyLocationEnabled(true);
            mMap.setLocationSource(this);


            /** It tells you where the position is captured, in this case the GPS (it can be from the internet), the update
             * interval, the minimum distance that must modify
             * the position to be required an update and the event that captures the position**/
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_INTERVAL_IN_MILLISECONDS, 10, new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    /** When is capture a new position, it is given to event that was "seteado" in the map
                     * to that be aware of their position. In Case of need tracking of position, en this point
                     * it must star el sw of "trackeo"**/

                    MapaUniversidad.this.listener.onLocationChanged(location);

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });

            /** the map is conscious of the position, but we need deliver the first position to map for that change the view
             * delivered , always and when , the phone have registered their position before**/
            mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (mCurrentLocation != null) {
                this.listener.onLocationChanged(mCurrentLocation);
                /** The object Location is not compatible with the map,
                but which we must creates a object compatible with this(LatLng)**/
                LatLng thisLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

                /** we move the map to he obtained position**/
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(thisLocation, 13));
                /** and indicate that establish a zoom 18, more high the number, more near above the ground**/
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

            }else {

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(University, 18));
                generateToast("Intente caminar o verifique la activación de su GPS");
            }

        }

    }

    /**
     * is initialize the event that capture the position for the map**/
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.listener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        this.listener = null;
    }


    /**
     * when a prologizing click is made (a little more than a second), this event will be activated,
     * which will give us the position where the prolonged click was made
     * and will create a marker with the information provided below and erase other existing elements
     **/


    /**
     * Event that is activated to perform click above a marker**/
    @Override
    public boolean onMarkerClick(Marker marker) {
        float resultsMarkerAddForApp[] = new float[10];
        float resultsMarkerAddForAppSecondaryEntry[] = new float[10];
        if (marker.getTitle().equals("Facultad De Ciencias Empresariales")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }

            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(  -36.603494, -72.079027);

            LatLng latLng1 = new LatLng(  -36.599902, -72.075909);
            LatLng latLng2 = new LatLng(  -36.601371, -72.076627);
            LatLng latLng3 = new LatLng(  -36.601384, -72.076882);
            LatLng latLng4 = new LatLng(  -36.602590, -72.077615);
            LatLng latLng5 = new LatLng(  -36.602682, -72.077902);
            LatLng latLng6 = new LatLng(  -36.603662, -72.078626);
            LatLng latLng7 = new LatLng(  -36.603518, -72.078991);
            float results[] = new float[10];

            float results1[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];
            float results4[] = new float[10];
            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng1.latitude, latLng1.longitude, results1);
            Location.distanceBetween(latLng1.latitude, latLng1.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, latLng4.latitude, latLng4.longitude, results4);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, latLng5.latitude, latLng5.longitude, results5);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, latLng6.latitude, latLng6.longitude, results6);
            Location.distanceBetween(latLng6.latitude, latLng6.longitude, latLng7.latitude, latLng7.longitude, results7);

            PolylineOptionsMethodWithSevenPointsSecondaryEntry(latLng1,latLng2,latLng3,latLng4,latLng5,latLng6,latLng7);
            PolylineOptionsMethodWithTwoPoints(latLng);

            CalculateTimePerMetersAndShowDistanceWithTwoPoints((int)results[0]);
            CalculateTimePerMetersAndShowDistanceWithEightPointsES((int)results1[0],(int)results2[0],(int)results3[0],(int)results4[0],(int)results5[0],(int)results6[0],(int)results7[0]);


            return false;

        }

        if (marker.getTitle().equals("Cabina De Vigilancia De La Universidad Del Bío-Bío")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng( -36.603451 , -72.079087);

            LatLng latLng1 = new LatLng(  -36.599902, -72.075909);
            LatLng latLng2 = new LatLng(  -36.601371, -72.076627);
            LatLng latLng3 = new LatLng(  -36.601384, -72.076882);
            LatLng latLng4 = new LatLng(  -36.602590, -72.077615);
            LatLng latLng5 = new LatLng(  -36.602682, -72.077902);
            LatLng latLng6 = new LatLng(  -36.603223, -72.078333);
            LatLng latLng7 = new LatLng(  -36.603412, -72.078993);

            float results[] = new float[10];

            float results1[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];
            float results4[] = new float[10];
            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng1.latitude, latLng1.longitude, results1);
            Location.distanceBetween(latLng1.latitude, latLng1.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, latLng4.latitude, latLng4.longitude, results4);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, latLng5.latitude, latLng5.longitude, results5);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, latLng6.latitude, latLng6.longitude, results6);
            Location.distanceBetween(latLng6.latitude, latLng6.longitude, latLng7.latitude, latLng7.longitude, results7);

            PolylineOptionsMethodWithTwoPoints(latLng);
            PolylineOptionsMethodWithSevenPointsSecondaryEntry(latLng1,latLng2,latLng3,latLng4,latLng5,latLng6,latLng7);

            CalculateTimePerMetersAndShowDistanceWithTwoPoints((int)results[0]);
            CalculateTimePerMetersAndShowDistanceWithEightPointsES((int)results1[0],(int)results2[0],(int)results3[0],(int)results4[0],(int)results5[0],(int)results6[0],(int)results7[0]);
            return false;
        }
        if (marker.getTitle().equals("Laboratorios Centrales De Computación")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603649, -72.078690);

            LatLng latLng1 = new LatLng(  -36.599902, -72.075909);
            LatLng latLng2 = new LatLng(  -36.601371, -72.076627);
            LatLng latLng3 = new LatLng(  -36.601384, -72.076882);
            LatLng latLng4 = new LatLng(  -36.602590, -72.077615);
            LatLng latLng5 = new LatLng(  -36.602682, -72.077902);
            LatLng latLng6 = new LatLng(  -36.603662, -72.078631);

            float results[] = new float[10];

            float results1[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];
            float results4[] = new float[10];
            float results5[] = new float[10];
            float results6[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng1.latitude, latLng1.longitude, results1);
            Location.distanceBetween(latLng1.latitude, latLng1.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, latLng4.latitude, latLng4.longitude, results4);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, latLng5.latitude, latLng5.longitude, results5);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, latLng6.latitude, latLng6.longitude, results6);

            PolylineOptionsMethodWithSixPointsSecondaryEntry(latLng1,latLng2,latLng3,latLng4,latLng5,latLng6);
            PolylineOptionsMethodWithTwoPoints(latLng);

            CalculateTimePerMetersAndShowDistanceWithTwoPoints((int)results[0]);
            CalculateTimePerMetersAndShowDistanceWithSevenPointsES((int)results1[0],(int)results2[0],(int)results3[0],(int)results4[0],(int)results5[0],(int)results6[0]);

            return false;
        }
        if (marker.getTitle().equals("Federación Universidad Del Bío-Bío")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603855, -72.078246);

            LatLng latLng1 = new LatLng(  -36.599902, -72.075909);
            LatLng latLng2 = new LatLng(  -36.601371, -72.076627);
            LatLng latLng3 = new LatLng(  -36.601384, -72.076882);
            LatLng latLng4 = new LatLng(  -36.602590, -72.077615);
            LatLng latLng5 = new LatLng(  -36.602682, -72.077902);
            LatLng latLng6 = new LatLng(  -36.603250, -72.078324);

            float results[] = new float[10];
            float results2[] = new float[10];

            float results3[] = new float[10];
            float results4[] = new float[10];
            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];
            float results8[] = new float[10];
            float results9[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results2);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng1.latitude, latLng1.longitude, results3);
            Location.distanceBetween(latLng1.latitude, latLng1.longitude, latLng2.latitude, latLng2.longitude, results4);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results5);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, latLng4.latitude, latLng4.longitude, results6);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, latLng5.latitude, latLng5.longitude, results7);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, latLng6.latitude, latLng6.longitude, results8);
            Location.distanceBetween(latLng6.latitude, latLng6.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results9);


            PolylineOptionsMethodWithThreePoints(latLng,marker.getPosition());
            PolylineOptionsMethodWithSevenPointsSecondaryEntry(latLng1,latLng2,latLng3,latLng4,latLng5,latLng6,marker.getPosition());

            CalculateTimePerMetersAndShowDistanceWithThreePoints((int)results[0],(int)results2[0]);
            CalculateTimePerMetersAndShowDistanceWithEightPointsES((int)results3[0],(int)results4[0],(int)results5[0],(int)results6[0],(int)results7[0],(int)results8[0],(int)results9[0]);
            return false;

        }
        if (marker.getTitle().equals("Oficina De Partes")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");

            LatLng latLng1 = new LatLng(  -36.599902, -72.075909);
            LatLng latLng2 = new LatLng(  -36.601371, -72.076627);
            LatLng latLng3 = new LatLng(  -36.601384, -72.076882);
            LatLng latLng4 = new LatLng(  -36.602590, -72.077615);
            LatLng latLng5 = new LatLng(  -36.602682, -72.077902);
            LatLng latLng6 = new LatLng(  -36.603250, -72.078324);

            float results[] = new float[10];

            float results1[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];
            float results4[] = new float[10];
            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results);
            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng1.latitude, latLng1.longitude, results1);
            Location.distanceBetween(latLng1.latitude, latLng1.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, latLng4.latitude, latLng4.longitude, results4);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, latLng5.latitude, latLng5.longitude, results5);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, latLng6.latitude, latLng6.longitude, results6);
            Location.distanceBetween(latLng6.latitude, latLng6.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results7);

            PolylineOptionsMethodWithTwoPoints(marker.getPosition());
            PolylineOptionsMethodWithSevenPointsSecondaryEntry(latLng1,latLng2,latLng3,latLng4,latLng5,latLng6,marker.getPosition());

            CalculateTimePerMetersAndShowDistanceWithTwoPoints((int)results[0]);
            CalculateTimePerMetersAndShowDistanceWithEightPointsES((int)results1[0],(int)results2[0],(int)results3[0],(int)results4[0],(int)results5[0],(int)results6[0],(int)results7[0]);
            return false;
        }
        if (marker.getTitle().equals("Departamento De Ciencias Básicas")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng( -36.603207 , -72.078309);
            LatLng latLng3 = new LatLng( -36.603243, -72.078000);

            LatLng latLng4 = new LatLng(  -36.599902, -72.075909);
            LatLng latLng5 = new LatLng(  -36.601371, -72.076627);
            LatLng latLng6 = new LatLng(  -36.601384, -72.076882);
            LatLng latLng7 = new LatLng(  -36.602590, -72.077615);
            LatLng latLng8 = new LatLng(  -36.602682, -72.077902);
            LatLng latLng9 = new LatLng(  -36.603092, -72.078228);
            LatLng latLng10 = new LatLng(-36.603198, -72.078026);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];

            float results4[] = new float[10];
            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];
            float results8[] = new float[10];
            float results9[] = new float[10];
            float results10[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng4.latitude, latLng4.longitude, results4);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, latLng5.latitude, latLng5.longitude, results5);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, latLng6.latitude, latLng6.longitude, results6);
            Location.distanceBetween(latLng6.latitude, latLng6.longitude, latLng7.latitude, latLng7.longitude, results7);
            Location.distanceBetween(latLng7.latitude, latLng7.longitude, latLng8.latitude, latLng8.longitude, results8);
            Location.distanceBetween(latLng8.latitude, latLng8.longitude, latLng9.latitude, latLng9.longitude, results9);
            Location.distanceBetween(latLng9.latitude, latLng9.longitude, latLng10.latitude, latLng10.longitude, results10);

            PolylineOptionsMethodWithFourPoints(latLng,latLng2,latLng3);
            PolylineOptionsMethodWithSevenPointsSecondaryEntry(latLng4,latLng5,latLng6,latLng7,latLng8,latLng9,latLng10);

            CalculateTimePerMetersAndShowDistanceWithFourPoints((int)results[0],(int)results2[0],(int)results3[0]);
            CalculateTimePerMetersAndShowDistanceWithEightPointsES((int)results4[0],(int)results5[0],(int)results6[0],(int)results7[0],(int)results8[0],(int)results9[0],(int)results10[0]);
            return false;


        }
        if (marker.getTitle().equals("Cafetería Universidad Del Bío-Bío")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng( -36.603207 , -72.078309);
            LatLng latLng3 = new LatLng( -36.602698, -72.077776);

            LatLng latLng4 = new LatLng(  -36.599902, -72.075909);
            LatLng latLng5 = new LatLng(  -36.601371, -72.076627);
            LatLng latLng6 = new LatLng(  -36.601384, -72.076882);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];

            float results4[] = new float[10];
            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng4.latitude, latLng4.longitude, results4);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, latLng5.latitude, latLng5.longitude, results5);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, latLng6.latitude, latLng6.longitude, results6);
            Location.distanceBetween(latLng6.latitude, latLng6.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results7);


            PolylineOptionsMethodWithFourPoints(latLng,latLng2,latLng3);
            PolylineOptionsMethodWithFourPointsSecondaryEntry(latLng4,latLng5,latLng6,marker.getPosition());

            CalculateTimePerMetersAndShowDistanceWithFourPoints((int)results[0],(int)results2[0],(int)results3[0]);
            CalculateTimePerMetersAndShowDistanceWithFivePointsES((int)results4[0],(int)results5[0],(int)results6[0],(int)results7[0]);
            return false;
        }
        if (marker.getTitle().equals("Nuevo Edificio De Investigadores")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng( -36.603207 , -72.078309);
            LatLng latLng3 = new LatLng( -36.602725 ,-72.077798);

            LatLng latLng4 = new LatLng(  -36.599902, -72.075909);
            LatLng latLng5 = new LatLng(  -36.601371, -72.076627);
            LatLng latLng6 = new LatLng(  -36.601384, -72.076882);
            LatLng latLng7 = new LatLng(  -36.602353, -72.077461);



            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];
            float results4[] = new float[10];

            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];
            float results8[] = new float[10];
            float results9[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results4);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng4.latitude, latLng4.longitude, results5);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, latLng5.latitude, latLng5.longitude, results6);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, latLng6.latitude, latLng6.longitude, results7);
            Location.distanceBetween(latLng6.latitude, latLng6.longitude,latLng7.latitude, latLng7.longitude,results8);
            Location.distanceBetween(latLng7.latitude,latLng7.longitude,marker.getPosition().latitude, marker.getPosition().longitude, results9);


            PolylineOptionsMethodWithFivePoints(latLng,latLng2,latLng3,marker.getPosition());
            PolylineOptionsMethodWithFivePointsSecondaryEntry(latLng4,latLng5,latLng6,latLng7,marker.getPosition());

            CalculateTimePerMetersAndShowDistanceWithFivePoints((int)results[0],(int)results2[0],(int)results3[0],(int)results4 [0]);
            CalculateTimePerMetersAndShowDistanceWithSixPointsES((int)results5[0],(int)results6[0],(int)results7[0],(int)results8[0],(int)results9[0]);
            return false;
        }
        if (marker.getTitle().equals("Administración Central")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng( -36.602469 ,-72.078418);

            LatLng latLng2 = new LatLng(  -36.599902, -72.075909);
            LatLng latLng3 = new LatLng(  -36.601371, -72.076627);
            LatLng latLng4 = new LatLng(  -36.601384, -72.076882);
            LatLng latLng5 = new LatLng(  -36.602547, -72.077597);
            LatLng latLng6 = new LatLng(  -36.602424, -72.077921);

            float results[] = new float[10];
            float results2[] = new float[10];

            float results3[] = new float[10];
            float results4[] = new float[10];
            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];


            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results2);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng2.latitude, latLng2.longitude, results3);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results4);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, latLng4.latitude, latLng4.longitude, results5);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude,latLng5.latitude, latLng5.longitude,results6);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude,latLng6.latitude, latLng6.longitude,results7);

            PolylineOptionsMethodWithThreePoints(latLng,marker.getPosition());
            PolylineOptionsMethodWithFivePointsSecondaryEntry(latLng2,latLng3,latLng4,latLng5,latLng6);

            CalculateTimePerMetersAndShowDistanceWithThreePoints((int)results[0],(int)results2[0]);
            CalculateTimePerMetersAndShowDistanceWithSixPointsES((int)results3[0],(int)results4[0],(int)results5[0],(int)results6[0],(int)results7[0]);
            return false;
        }
        if (marker.getTitle().equals("LECYCA")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng( -36.603207 , -72.078309);
            LatLng latLng3 = new LatLng( -36.603423, -72.077657);

            LatLng latLng4 = new LatLng(  -36.599902, -72.075909);
            LatLng latLng5 = new LatLng(  -36.601371, -72.076627);
            LatLng latLng6 = new LatLng(  -36.601384, -72.076882);
            LatLng latLng7 = new LatLng(  -36.602590, -72.077615);
            LatLng latLng8 = new LatLng(-36.602713, -72.077280);
            LatLng latLng9 = new LatLng(-36.603395, -72.077631);
            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];

            float results4[] = new float[10];
            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];
            float results8[] = new float[10];
            float results9[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng4.latitude, latLng4.longitude, results4);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, latLng5.latitude, latLng5.longitude, results5);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, latLng6.latitude, latLng6.longitude, results6);
            Location.distanceBetween(latLng6.latitude,latLng6.longitude, latLng7.latitude, latLng7.longitude, results7);
            Location.distanceBetween(latLng7.latitude, latLng7.longitude, latLng8.latitude, latLng8.longitude, results8);
            Location.distanceBetween(latLng8.latitude, latLng8.longitude, latLng9.latitude, latLng9.longitude, results9);

            PolylineOptionsMethodWithFourPoints(latLng,latLng2,latLng3);
            PolylineOptionsMethodWithSixPointsSecondaryEntry(latLng4,latLng5,latLng6,latLng7,latLng8,latLng9);

            CalculateTimePerMetersAndShowDistanceWithFourPoints((int)results[0],(int)results2[0],(int)results3[0]);
            CalculateTimePerMetersAndShowDistanceWithSevenPointsES((int)results4[0],(int)results5[0],(int)results6[0],(int)results7[0],(int)results8[0],(int)results9[0]);
            return false;
        }
        if (marker.getTitle().equals("Laboratorio De Procesos De Alimentos")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng( -36.603207 , -72.078309);
            LatLng latLng3 = new LatLng( -36.603423, -72.077657);

            LatLng latLng4 = new LatLng(  -36.599902, -72.075909);
            LatLng latLng5 = new LatLng(  -36.601371, -72.076627);
            LatLng latLng6 = new LatLng(  -36.601384, -72.076882);
            LatLng latLng7 = new LatLng(  -36.602590, -72.077615);
            LatLng latLng8 = new LatLng(  -36.602713, -72.077280);
            LatLng latLng9 = new LatLng(-36.603395, -72.077631);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];

            float results4[] = new float[10];
            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];
            float results8[] = new float[10];
            float results9[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng4.latitude, latLng4.longitude, results4);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, latLng5.latitude, latLng5.longitude, results5);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, latLng6.latitude, latLng6.longitude, results6);
            Location.distanceBetween(latLng6.latitude,latLng6.longitude, latLng7.latitude, latLng7.longitude, results7);
            Location.distanceBetween(latLng7.latitude, latLng7.longitude, latLng8.latitude, latLng8.longitude, results8);
            Location.distanceBetween(latLng8.latitude, latLng8.longitude, latLng9.latitude, latLng9.longitude, results9);

            PolylineOptionsMethodWithFourPoints(latLng,latLng2,latLng3);
            PolylineOptionsMethodWithSixPointsSecondaryEntry(latLng4,latLng5,latLng6,latLng7,latLng8,latLng9);

            CalculateTimePerMetersAndShowDistanceWithFourPoints((int)results[0],(int)results2[0],(int)results3[0]);
            CalculateTimePerMetersAndShowDistanceWithSevenPointsES((int)results4[0],(int)results5[0],(int)results6[0],(int)results7[0],(int)results8[0],(int)results9[0]);
            return false;
        }
        if (marker.getTitle().equals("Facultad De Ciencias De La Salud Y De Los Alimentos")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng( -36.603207 , -72.078309);

            LatLng latLng3 = new LatLng(  -36.599902, -72.075909);
            LatLng latLng4 = new LatLng(  -36.601371, -72.076627);
            LatLng latLng5 = new LatLng(  -36.601384, -72.076882);
            LatLng latLng6 = new LatLng(  -36.602590, -72.077615);
            LatLng latLng7 = new LatLng( -36.602713, -72.077280);
            LatLng latLng8 = new LatLng(-36.603177, -72.077506);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];

            float results4[] = new float[10];
            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];
            float results8[] = new float[10];
            float results9[] = new float[10];
            float results10[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results3);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng3.latitude, latLng3.longitude, results4);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, latLng4.latitude, latLng4.longitude, results5);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, latLng5.latitude, latLng5.longitude, results6);
            Location.distanceBetween(latLng5.latitude,latLng5.longitude, latLng6.latitude, latLng6.longitude, results7);
            Location.distanceBetween(latLng6.latitude, latLng6.longitude, latLng7.latitude, latLng7.longitude, results8);
            Location.distanceBetween(latLng7.latitude, latLng7.longitude, latLng8.latitude, latLng8.longitude, results9);
            Location.distanceBetween(latLng8.latitude, latLng8.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results10);

            PolylineOptionsMethodWithFourPoints(latLng,latLng2,marker.getPosition());
            PolylineOptionsMethodWithSevenPointsSecondaryEntry(latLng3,latLng4,latLng5,latLng6,latLng7,latLng8,marker.getPosition());

            CalculateTimePerMetersAndShowDistanceWithFourPoints((int)results[0],(int)results2[0],(int)results3[0]);
            CalculateTimePerMetersAndShowDistanceWithEightPointsES((int)results4[0],(int)results5[0],(int)results6[0],(int)results7[0],(int)results8[0],(int)results9[0],(int)results10[0]);
            return false;
        }
        if (marker.getTitle().equals("Centro De Simulación De Enfermería")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng( -36.603207 , -72.078309);
            LatLng latLng3 = new LatLng( -36.603233, -72.077506);
            LatLng latLng4 = new LatLng( -36.603543, -72.077116);

            LatLng latLng5 = new LatLng(  -36.599902, -72.075909);
            LatLng latLng6 = new LatLng(  -36.601371, -72.076627);
            LatLng latLng7 = new LatLng(  -36.601384, -72.076882);
            LatLng latLng8 = new LatLng(  -36.602590, -72.077615);
            LatLng latLng9 = new LatLng( -36.602713, -72.077280);
            LatLng latLng10 = new LatLng(-36.603177, -72.077506);
            LatLng latLng11 = new LatLng(-36.603498, -72.077082);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];
            float results4[] = new float[10];

            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];
            float results8[] = new float[10];
            float results9[] = new float[10];
            float results10[] = new float[10];
            float results11[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, latLng4.latitude, latLng4.longitude, results4);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng5.latitude, latLng5.longitude, results5);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, latLng6.latitude, latLng6.longitude, results6);
            Location.distanceBetween(latLng6.latitude, latLng6.longitude, latLng7.latitude, latLng7.longitude, results7);
            Location.distanceBetween(latLng7.latitude,latLng7.longitude, latLng8.latitude, latLng8.longitude, results8);
            Location.distanceBetween(latLng8.latitude, latLng8.longitude, latLng9.latitude, latLng9.longitude, results9);
            Location.distanceBetween(latLng9.latitude, latLng8.longitude, latLng10.latitude, latLng10.longitude, results10);
            Location.distanceBetween(latLng10.latitude, latLng10.longitude, latLng11.latitude, latLng11.longitude, results11);

            PolylineOptionsMethodWithFivePoints(latLng,latLng2,latLng3,latLng4);
            PolylineOptionsMethodWithSevenPointsSecondaryEntry(latLng5,latLng6,latLng7,latLng8,latLng9,latLng10,latLng11);

            CalculateTimePerMetersAndShowDistanceWithFivePoints((int)results[0],(int)results2[0],(int)results3[0],(int)results4 [0]);
            CalculateTimePerMetersAndShowDistanceWithEightPointsES((int)results5[0],(int)results6[0],(int)results7[0],(int)results8[0],(int)results9[0],(int)results10[0],(int)results11[0]);
            return false;
        }
        if (marker.getTitle().equals("VRIP")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng( -36.603207 , -72.078309);
            LatLng latLng3 = new LatLng( -36.603238, -72.077639);

            LatLng latLng6 = new LatLng(  -36.599902, -72.075909);
            LatLng latLng7 = new LatLng(  -36.601371, -72.076627);
            LatLng latLng8 = new LatLng(  -36.601384, -72.076882);
            LatLng latLng9 = new LatLng(  -36.602303, -72.077416);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];
            float results4[] = new float[10];

            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];
            float results8[] = new float[10];
            float results9[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results4);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng6.latitude, latLng6.longitude, results5);
            Location.distanceBetween(latLng6.latitude, latLng6.longitude, latLng7.latitude, latLng7.longitude, results6);
            Location.distanceBetween(latLng7.latitude, latLng7.longitude, latLng8.latitude, latLng8.longitude, results7);
            Location.distanceBetween(latLng8.latitude,latLng8.longitude, latLng9.latitude, latLng9.longitude, results8);
            Location.distanceBetween(latLng9.latitude, latLng9.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results9);

            PolylineOptionsMethodWithFivePoints(latLng,latLng2,latLng3,marker.getPosition());
            PolylineOptionsMethodWithFivePointsSecondaryEntry(latLng6,latLng7,latLng8,latLng9,marker.getPosition());

            CalculateTimePerMetersAndShowDistanceWithFivePoints((int)results[0],(int)results2[0],(int)results3[0],(int)results4 [0]);
            CalculateTimePerMetersAndShowDistanceWithSixPointsES((int)results5[0],(int)results6[0],(int)results7[0],(int)results8 [0],(int)results9 [0]);
            return false;
        }

        if (marker.getTitle().equals("Aulas A")) {
            if (line != null && line2 != null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429, -72.079031);
            LatLng latLng2 = new LatLng(-36.603245, -72.078398);
            LatLng latLng3 = new LatLng(-36.603253, -72.077223);

            LatLng latLng4 = new LatLng(-36.599902, -72.075909);
            LatLng latLng5 = new LatLng(-36.601371, -72.076627);
            LatLng latLng6 = new LatLng(-36.601384, -72.076882);
            LatLng latLng7 = new LatLng(-36.602593, -72.077565);
            LatLng latLng8 = new LatLng(  -36.602917, -72.076915);
            LatLng latLng9 = new LatLng(  -36.603126, -72.077113);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];

            float results4[] = new float[10];
            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];
            float results8[] = new float[10];
            float results9[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng4.latitude, latLng4.longitude, results4);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, latLng5.latitude, latLng5.longitude, results5);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, latLng6.latitude, latLng6.longitude, results6);
            Location.distanceBetween(latLng6.latitude,latLng6.longitude, latLng7.latitude, latLng7.longitude, results7);
            Location.distanceBetween(latLng7.latitude, latLng7.longitude, latLng8.latitude, latLng8.longitude, results8);
            Location.distanceBetween(latLng8.latitude, latLng8.longitude, latLng9.latitude, latLng9.longitude, results9);

            PolylineOptionsMethodWithFourPoints(latLng,latLng2,latLng3);
            PolylineOptionsMethodWithSixPointsSecondaryEntry(latLng4,latLng5,latLng6,latLng7,latLng8,latLng9);

            CalculateTimePerMetersAndShowDistanceWithFourPoints((int)results[0],(int)results2[0],(int)results3[0]);
            CalculateTimePerMetersAndShowDistanceWithSevenPointsES((int)results4[0],(int)results5[0],(int)results6[0],(int)results7 [0],(int)results8 [0],(int)results9 [0]);
            return false;
        }
        if (marker.getTitle().equals("Aulas B")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng(  -36.603245 , -72.078398);
            LatLng latLng3 = new LatLng( -36.603350, -72.076960);
            LatLng latLng4 = new LatLng( -36.603054, -72.076643);

            LatLng latLng5 = new LatLng(-36.599902, -72.075909);
            LatLng latLng6 = new LatLng(-36.601371, -72.076627);
            LatLng latLng7 = new LatLng(-36.601384, -72.076882);
            LatLng latLng8 = new LatLng(-36.602593, -72.077565);
            LatLng latLng9 = new LatLng(  -36.603034, -72.076685);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];
            float results4[] = new float[10];

            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];
            float results8[] = new float[10];
            float results9[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, latLng4.latitude, latLng4.longitude, results4);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng5.latitude, latLng5.longitude, results5);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, latLng6.latitude, latLng6.longitude, results6);
            Location.distanceBetween(latLng6.latitude, latLng6.longitude, latLng7.latitude, latLng7.longitude, results7);
            Location.distanceBetween(latLng7.latitude,latLng7.longitude, latLng8.latitude, latLng8.longitude, results8);
            Location.distanceBetween(latLng8.latitude, latLng8.longitude, latLng9.latitude, latLng9.longitude, results9);

            PolylineOptionsMethodWithFivePoints(latLng,latLng2,latLng3,latLng4);
            PolylineOptionsMethodWithFivePointsSecondaryEntry(latLng5,latLng6,latLng7,latLng8,latLng9);
            CalculateTimePerMetersAndShowDistanceWithFivePoints((int)results[0],(int)results2[0],(int)results3[0],(int)results4 [0]);
            CalculateTimePerMetersAndShowDistanceWithSixPointsES((int)results5[0],(int)results6[0],(int)results7[0],(int)results8 [0],(int)results9 [0]);
            return false;
        }
        if (marker.getTitle().equals("Aulas C")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng(  -36.603245 , -72.078398);
            LatLng latLng3 = new LatLng( -36.603253, -72.077223);

            LatLng latLng4 = new LatLng(-36.599902, -72.075909);
            LatLng latLng5 = new LatLng(-36.601371, -72.076627);
            LatLng latLng6 = new LatLng(-36.601384, -72.076882);
            LatLng latLng7 = new LatLng(-36.602289, -72.077419);
            LatLng latLng8 = new LatLng(  -36.602627, -72.076713);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];
            float results4[] = new float[10];

            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];
            float results8[] = new float[10];
            float results9[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results4);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng4.latitude, latLng4.longitude, results5);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, latLng5.latitude, latLng5.longitude, results6);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, latLng6.latitude, latLng6.longitude, results7);
            Location.distanceBetween(latLng6.latitude,latLng6.longitude, latLng7.latitude, latLng7.longitude, results8);
            Location.distanceBetween(latLng7.latitude, latLng7.longitude, latLng8.latitude, latLng8.longitude, results9);

            PolylineOptionsMethodWithFivePoints(latLng,latLng2,latLng3,marker.getPosition());
            PolylineOptionsMethodWithFivePointsSecondaryEntry(latLng4,latLng5,latLng6,latLng7,latLng8);

            CalculateTimePerMetersAndShowDistanceWithFivePoints((int)results[0],(int)results2[0],(int)results3[0],(int)results4 [0]);
            CalculateTimePerMetersAndShowDistanceWithSixPointsES((int)results5[0],(int)results6[0],(int)results7[0],(int)results8 [0],(int)results9 [0]);
            return false;
        }
        if (marker.getTitle().equals("Aulas D")) {
            if (line != null && line2 != null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429, -72.079031);
            LatLng latLng2 = new LatLng(-36.603245, -72.078398);
            LatLng latLng3 = new LatLng(-36.603253, -72.077223);

            LatLng latLng4 = new LatLng(-36.599902, -72.075909);
            LatLng latLng5 = new LatLng(-36.601371, -72.076627);
            LatLng latLng6 = new LatLng(-36.601753, -72.076021);
            LatLng latLng7 = new LatLng(  -36.602162, -72.076326);


            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];
            float results4[] = new float[10];

            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];
            float results8[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results4);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng4.latitude, latLng4.longitude, results5);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, latLng5.latitude, latLng5.longitude, results6);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, latLng6.latitude, latLng6.longitude, results7);
            Location.distanceBetween(latLng6.latitude,latLng6.longitude, latLng7.latitude, latLng7.longitude, results8);

            PolylineOptionsMethodWithFivePoints(latLng,latLng2,latLng3,marker.getPosition());
            PolylineOptionsMethodWithFourPointsSecondaryEntry(latLng4,latLng5,latLng6,latLng7);

            CalculateTimePerMetersAndShowDistanceWithFivePoints((int)results[0],(int)results2[0],(int)results3[0],(int)results4 [0]);
            CalculateTimePerMetersAndShowDistanceWithFivePointsES((int)results5[0],(int)results6[0],(int)results7[0],(int)results8 [0]);
            return false;
        }
        if (marker.getTitle().equals("Aulas E")){

            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng(  -36.603245 , -72.078398);
            LatLng latLng3 = new LatLng( -36.603253, -72.077223);

            LatLng latLng4 = new LatLng(-36.600139, -72.075037);
            LatLng latLng5 = new LatLng(-36.599829, -72.074793);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];
            float results4[] = new float[10];

            float results5[] = new float[10];
            float results6[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results4);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng4.latitude, latLng4.longitude, results5);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, latLng5.latitude, latLng5.longitude, results6);

            PolylineOptionsMethodWithFivePoints(latLng,latLng2,latLng3,marker.getPosition());
            PolylineOptionsMethodWithTwoPointsSecondaryEntry(latLng4,latLng5);

            CalculateTimePerMetersAndShowDistanceWithFivePoints((int)results[0],(int)results2[0],(int)results3[0],(int)results4 [0]);
            CalculateTimePerMetersAndShowDistanceWithThreePointsES((int)results5[0],(int)results6[0]);
            return false;
        }
        if (marker.getTitle().equals("Nuevo Departamento De Ciencias Básicas")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng(  -36.603245 , -72.078398);
            LatLng latLng3 = new LatLng( -36.603253, -72.077223);
            LatLng latLng4 = new LatLng( -36.599402, -72.074493);

            LatLng latLng5 = new LatLng(-36.600117, -72.075093);
            LatLng latLng6 = new LatLng(-36.599385, -72.074541);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];
            float results4[] = new float[10];

            float results5[] = new float[10];
            float results6[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, latLng4.latitude, latLng4.longitude, results4);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng5.latitude, latLng5.longitude, results5);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, latLng6.latitude, latLng6.longitude, results6);

            PolylineOptionsMethodWithFivePoints(latLng,latLng2,latLng3,latLng4);
            PolylineOptionsMethodWithTwoPointsSecondaryEntry(latLng5,latLng6);

            CalculateTimePerMetersAndShowDistanceWithFivePoints((int)results[0],(int)results2[0],(int)results3[0],(int)results4 [0]);
            CalculateTimePerMetersAndShowDistanceWithThreePointsES((int)results5[0],(int)results6[0]);
            return false;
        }
        if (marker.getTitle().equals("Biblioteca Principal")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng(  -36.603245 , -72.078398);
            LatLng latLng3 = new LatLng( -36.603350, -72.076960);
            LatLng latLng4 = new LatLng( -36.602663, -72.076400);

            LatLng latLng5 = new LatLng(-36.599902, -72.075909);
            LatLng latLng6 = new LatLng(-36.601371, -72.076627);
            LatLng latLng7 = new LatLng(-36.601753, -72.076021);
            LatLng latLng8 = new LatLng(-36.602278, -72.076151);
            LatLng latLng9 = new LatLng(-36.602630, -72.076372);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];
            float results4[] = new float[10];

            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];
            float results8[] = new float[10];
            float results9[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, latLng4.latitude, latLng4.longitude, results4);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng5.latitude, latLng5.longitude, results5);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, latLng6.latitude, latLng6.longitude, results6);
            Location.distanceBetween(latLng6.latitude, latLng6.longitude, latLng7.latitude, latLng7.longitude, results7);
            Location.distanceBetween(latLng7.latitude,latLng7.longitude, latLng8.latitude, latLng8.longitude, results8);
            Location.distanceBetween(latLng8.latitude,latLng8.longitude, latLng9.latitude, latLng9.longitude, results9);

            PolylineOptionsMethodWithFivePoints(latLng,latLng2,latLng3,latLng4);
            PolylineOptionsMethodWithFivePointsSecondaryEntry(latLng5,latLng6,latLng7,latLng8,latLng9);

            CalculateTimePerMetersAndShowDistanceWithFivePoints((int)results[0],(int)results2[0],(int)results3[0],(int)results4 [0]);
            CalculateTimePerMetersAndShowDistanceWithSixPointsES((int)results5[0],(int)results6[0],(int)results7[0],(int)results8[0],(int)results9[0]);
            return false;
        }
        if (marker.getTitle().equals("Gimnasio Central Fernando May")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng( -36.603207 , -72.078309);
            LatLng latLng3 = new LatLng(  -36.601983, -72.07707);

            LatLng latLng4 = new LatLng(  -36.599902, -72.075909);
            LatLng latLng5 = new LatLng(  -36.601371, -72.076627);
            LatLng latLng6 = new LatLng(  -36.601384, -72.076882);
            LatLng latLng7 = new LatLng(  -36.601945, -72.077043);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];

            float results4[] = new float[10];
            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng4.latitude, latLng4.longitude, results4);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, latLng5.latitude, latLng5.longitude, results5);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, latLng6.latitude, latLng6.longitude, results6);
            Location.distanceBetween(latLng6.latitude,latLng6.longitude, latLng7.latitude, latLng7.longitude, results7);

            PolylineOptionsMethodWithFourPoints(latLng,latLng2,latLng3);
            PolylineOptionsMethodWithFourPointsSecondaryEntry(latLng4,latLng5,latLng6,latLng7);

            CalculateTimePerMetersAndShowDistanceWithFourPoints((int)results[0],(int)results2[0],(int)results3[0]);
            CalculateTimePerMetersAndShowDistanceWithFivePointsES((int)results4[0],(int)results5[0],(int)results6[0],(int)results7[0]);
            return false;

        }
        if (marker.getTitle().equals("Académicos Del Departamento De Ciencias Básicas")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng( -36.603207 , -72.078309);
            LatLng latLng3 = new LatLng(  -36.601893, -72.076962);

            LatLng latLng4 = new LatLng(  -36.599902, -72.075909);


            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];

            float results4[] = new float[10];
            float results5[] = new float[10];


            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng4.latitude, latLng4.longitude, results4);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results5);

            PolylineOptionsMethodWithFourPoints(latLng,latLng2,latLng3);
            PolylineOptionsMethodWithTwoPointsSecondaryEntry(latLng4,marker.getPosition());

            CalculateTimePerMetersAndShowDistanceWithFourPoints((int)results[0],(int)results2[0],(int)results3[0]);
            CalculateTimePerMetersAndShowDistanceWithThreePointsES((int)results4[0],(int)results5[0]);
            return false;

        }
        if (marker.getTitle().equals("Casino UBB")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng(  -36.603245 , -72.078398);
            LatLng latLng3 = new LatLng( -36.603350, -72.076960);
            LatLng latLng4 = new LatLng( -36.602238, -72.076072);

            LatLng latLng5 = new LatLng(-36.599902, -72.075909);
            LatLng latLng6 = new LatLng(-36.601371, -72.076627);
            LatLng latLng7 = new LatLng(-36.601753, -72.076021);
            LatLng latLng8 = new LatLng(-36.602189, -72.076096);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];
            float results4[] = new float[10];

            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];
            float results8[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, latLng4.latitude, latLng4.longitude, results4);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng5.latitude, latLng5.longitude, results5);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, latLng6.latitude, latLng6.longitude, results6);
            Location.distanceBetween(latLng6.latitude, latLng6.longitude, latLng7.latitude, latLng7.longitude, results7);
            Location.distanceBetween(latLng7.latitude,latLng7.longitude, latLng8.latitude, latLng8.longitude, results8);

            PolylineOptionsMethodWithFivePoints(latLng,latLng2,latLng3,latLng4);
            PolylineOptionsMethodWithFourPointsSecondaryEntry(latLng5,latLng6,latLng7,latLng8);

            CalculateTimePerMetersAndShowDistanceWithFivePoints((int)results[0],(int)results2[0],(int)results3[0],(int)results4 [0]);
            CalculateTimePerMetersAndShowDistanceWithFivePointsES((int)results5[0],(int)results6[0],(int)results7[0],(int)results8[0]);
            return false;
        }
        if (marker.getTitle().equals("Dirección De Desarrollo Estudiantil")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng(  -36.603245 , -72.078398);
            LatLng latLng3 = new LatLng( -36.603350, -72.076960);

            LatLng latLng4 = new LatLng(-36.599902, -72.075909);
            LatLng latLng5 = new LatLng(-36.601371, -72.076627);
            LatLng latLng6 = new LatLng(-36.601956, -72.076013);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];
            float results4[] = new float[10];

            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results4);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng4.latitude, latLng4.longitude, results5);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, latLng5.latitude, latLng5.longitude, results6);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, latLng6.latitude, latLng6.longitude, results7);

            PolylineOptionsMethodWithFivePoints(latLng,latLng2,latLng3,marker.getPosition());
            PolylineOptionsMethodWithThreePointsSecondaryEntry(latLng4,latLng5,latLng6);

            CalculateTimePerMetersAndShowDistanceWithFivePoints((int)results[0],(int)results2[0],(int)results3[0],(int)results4 [0]);
            CalculateTimePerMetersAndShowDistanceWithFourPointsES((int)results5[0],(int)results6[0],(int)results7[0]);
            return false;
        }
        if (marker.getTitle().equals("Gimnasio Multitaller")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng(  -36.603245 , -72.078398);
            LatLng latLng3 = new LatLng(  -36.603185 ,-72.077414);

            LatLng latLng4 = new LatLng(  -36.599902, -72.075909);
            LatLng latLng5 = new LatLng(  -36.601371, -72.076627);
            LatLng latLng6 = new LatLng(  -36.601801, -72.076377);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];
            float results4[] = new float[10];

            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results4);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng4.latitude, latLng4.longitude, results5);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, latLng5.latitude, latLng5.longitude, results6);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, latLng6.latitude, latLng6.longitude, results7);


            PolylineOptionsMethodWithFivePoints(latLng,latLng2,latLng3,marker.getPosition());
            PolylineOptionsMethodWithThreePointsSecondaryEntry(latLng4,latLng5,latLng6);

            CalculateTimePerMetersAndShowDistanceWithFivePoints((int)results[0],(int)results2[0],(int)results3[0],(int)results4 [0]);
            CalculateTimePerMetersAndShowDistanceWithFourPointsES((int)results5[0],(int)results6[0],(int)results7[0]);
            return false;
        }
        if (marker.getTitle().equals("Aula Magna Fernando May")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng( -36.603207 , -72.078309);
            LatLng latLng3 = new LatLng(  -36.601909, -72.077190);

            LatLng latLng4 = new LatLng(  -36.599902, -72.075909);
            LatLng latLng5 = new LatLng(  -36.601371, -72.076627);
            LatLng latLng6 = new LatLng(  -36.601384, -72.076882);
            LatLng latLng7 = new LatLng(  -36.601844, -72.077139);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];

            float results4[] = new float[10];
            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng4.latitude, latLng4.longitude, results4);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, latLng5.latitude, latLng5.longitude, results5);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, latLng6.latitude, latLng6.longitude, results6);
            Location.distanceBetween(latLng6.latitude, latLng6.longitude, latLng7.latitude, latLng7.longitude, results7);

            PolylineOptionsMethodWithFourPoints(latLng,latLng2,latLng3);
            PolylineOptionsMethodWithFourPointsSecondaryEntry(latLng4,latLng5,latLng6,latLng7);

            CalculateTimePerMetersAndShowDistanceWithFourPoints((int)results[0],(int)results2[0],(int)results3[0]);
            CalculateTimePerMetersAndShowDistanceWithFivePointsES((int)results4[0],(int)results5[0],(int)results6[0],(int)results7[0]);
            return false;
        }
        if (marker.getTitle().equals("Canchas De Tenis")){

            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng(  -36.603245 , -72.078398);
            LatLng latLng3 = new LatLng( -36.603350, -72.076960);
            LatLng latLng4 = new LatLng(  -36.601837 , -72.075905);

            LatLng latLng5 = new LatLng(  -36.600361, -72.075162);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];
            float results4[] = new float[10];
            float results5[] = new float[10];

            float results6[] = new float[10];
            float results7[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, latLng4.latitude, latLng4.longitude, results4);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results5);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng5.latitude, latLng5.longitude, results6);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results7);

            PolylineOptionsMethodWithSixPoints(latLng,latLng2,latLng3,latLng4,marker.getPosition());
            PolylineOptionsMethodWithTwoPointsSecondaryEntry(latLng5,marker.getPosition());

            CalculateTimePerMetersAndShowDistanceWithSixPoints((int)results[0],(int)results2[0],(int)results3[0],(int)results4 [0],(int)results5 [0]);
            CalculateTimePerMetersAndShowDistanceWithThreePointsES((int)results6[0],(int)results7[0]);
            return false;
        }
        if (marker.getTitle().equals("Estadio UBB")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng(  -36.603181 ,-72.078107);
            LatLng latLng3 = new LatLng(   -36.601369 , -72.076728);

            LatLng latLng4 = new LatLng(  -36.599910, -72.075895);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];
            float results4[] = new float[10];

            float results5[] = new float[10];
            float results6[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results4);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng4.latitude, latLng4.longitude, results5);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results6);

            PolylineOptionsMethodWithFivePoints(latLng,latLng2,latLng3,marker.getPosition());
            PolylineOptionsMethodWithTwoPointsSecondaryEntry(latLng4,marker.getPosition());

            CalculateTimePerMetersAndShowDistanceWithFivePoints((int)results[0],(int)results2[0],(int)results3[0],(int)results4 [0]);
            CalculateTimePerMetersAndShowDistanceWithThreePointsES((int)results5[0],(int)results6[0]);
            return false;
        }
        if (marker.getTitle().equals("Camarines UBB")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng( -36.603181 ,-72.078107);
            LatLng latLng3 = new LatLng( -36.600201, -72.076184);

            LatLng latLng4 = new LatLng(  -36.599902, -72.075937);
            LatLng latLng5 = new LatLng(  -36.600154, -72.076141);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];

            float results4[] = new float[10];
            float results5[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude,latLng3.longitude, results3);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng4.latitude, latLng4.longitude, results4);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, latLng5.latitude, latLng5.longitude, results5);

            PolylineOptionsMethodWithFourPoints(latLng,latLng2,latLng3);
            PolylineOptionsMethodWithTwoPointsSecondaryEntry(latLng4,latLng5);

            CalculateTimePerMetersAndShowDistanceWithFourPoints((int)results[0],(int)results2[0],(int)results3[0]);
            CalculateTimePerMetersAndShowDistanceWithThreePointsES((int)results4[0],(int)results5[0]);
            return false;
        }


        if (marker.getTitle().equals("Museo Marta Colvin")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng( -36.603181 ,-72.078107);

            LatLng latLng3 = new LatLng(  -36.599833, -72.076026);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];

            float results4[] = new float[10];
            float results5[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results3);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng3.latitude, latLng3.longitude, results4);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results5);

            PolylineOptionsMethodWithFourPoints(latLng,latLng2,marker.getPosition());
            PolylineOptionsMethodWithTwoPointsSecondaryEntry(latLng3,marker.getPosition());

            CalculateTimePerMetersAndShowDistanceWithFourPoints((int)results[0],(int)results2[0],(int)results3[0]);
            CalculateTimePerMetersAndShowDistanceWithThreePointsES((int)results4[0],(int)results5[0]);
            return false;
        }
        if (marker.getTitle().equals("Sistema De Lavandería")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng( -36.603181 ,-72.078107);

            LatLng latLng3 = new LatLng(  -36.599833, -72.076026);
            LatLng latLng4 = new LatLng(  -36.598991, -72.075468);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];

            float results4[] = new float[10];
            float results5[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results3);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng3.latitude, latLng3.longitude, results4);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, latLng4.latitude,latLng4.longitude, results5);

            PolylineOptionsMethodWithFourPoints(latLng,latLng2,marker.getPosition());
            PolylineOptionsMethodWithTwoPointsSecondaryEntry(latLng3,latLng4);
            CalculateTimePerMetersAndShowDistanceWithFourPoints((int)results[0],(int)results2[0],(int)results3[0]);
            CalculateTimePerMetersAndShowDistanceWithThreePointsES((int)results4[0],(int)results5[0]);
            return false;
        }
        if (marker.getTitle().equals("Diseño Gráfico")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng( -36.603181 ,-72.078107);

            LatLng latLng3 = new LatLng(  -36.599656, -72.075833);


            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];

            float results4[] = new float[10];
            float results5[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results3);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng3.latitude, latLng3.longitude, results4);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results5);

            PolylineOptionsMethodWithFourPoints(latLng,latLng2,marker.getPosition());
            PolylineOptionsMethodWithTwoPointsSecondaryEntry(latLng3,marker.getPosition());

            CalculateTimePerMetersAndShowDistanceWithFourPoints((int)results[0],(int)results2[0],(int)results3[0]);
            CalculateTimePerMetersAndShowDistanceWithThreePointsES((int)results4[0],(int)results5[0]);
            return false;
        }
        if (marker.getTitle().equals("Psicología")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng( -36.599669, -72.076351);

            LatLng latLng2 = new LatLng(  -36.599572, -72.075884);

            float results[] = new float[10];
            float results2[] = new float[10];

            float results3[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results2);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng2.latitude, latLng2.longitude, results3);

            PolylineOptionsMethodWithThreePoints(latLng,marker.getPosition());
            PolylineOptionsMethodWithOnePointsSecondaryEntry(latLng2);

            CalculateTimePerMetersAndShowDistanceWithThreePoints((int)results[0],(int)results2[0]);
            CalculateTimePerMetersAndShowDistanceWithTwoPointsES((int)results3[0]);
            return false;
        }
        if (marker.getTitle().equals("Casa Patronal")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng( -36.603181 ,-72.078107);
            LatLng latLng3 = new LatLng(  -36.599318, -72.075372);

            LatLng latLng4 = new LatLng(  -36.599650, -72.075857);
            LatLng latLng5 = new LatLng(  -36.599305, -72.075382);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];

            float results4[] = new float[10];
            float results5[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng4.latitude, latLng4.longitude, results4);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, latLng5.latitude,latLng5.longitude, results5);

            PolylineOptionsMethodWithFourPoints(latLng,latLng2,latLng3);
            PolylineOptionsMethodWithTwoPointsSecondaryEntry(latLng4,latLng5);
            CalculateTimePerMetersAndShowDistanceWithFourPoints((int)results[0],(int)results2[0],(int)results3[0]);
            CalculateTimePerMetersAndShowDistanceWithThreePointsES((int)results4[0],(int)results5[0]);
            return false;

        }
        if (marker.getTitle().equals("Casino Marta Colvin")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng( -36.603181 ,-72.078107);
            LatLng latLng3 = new LatLng( -36.599485, -72.075480 );

            LatLng latLng4 = new LatLng(  -36.599478, -72.075514);


            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];

            float results4[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng4.latitude, latLng4.longitude, results4);

            PolylineOptionsMethodWithFourPoints(latLng,latLng2,latLng3);
            PolylineOptionsMethodWithOnePointsSecondaryEntry(latLng4);

            CalculateTimePerMetersAndShowDistanceWithFourPoints((int)results[0],(int)results2[0],(int)results3[0]);
            CalculateTimePerMetersAndShowDistanceWithTwoPointsES((int)results4[0]);
            return false;
        }
        if (marker.getTitle().equals("Cancha De Football Diseño Gráfico")){
            if (line!=null && line2!=null) {

                line.remove();
                line2.remove();
            }
            generateToast("Para más información presiona el cuadro de texto");
            LatLng latLng = new LatLng(-36.603429 ,-72.079031);
            LatLng latLng2 = new LatLng( -36.603181 ,-72.078107);
            LatLng latLng3 = new LatLng( -36.599201, -72.075282 );

            LatLng latLng4 = new LatLng(  -36.599641, -72.075850);
            LatLng latLng5 = new LatLng(  -36.599130, -72.075358);

            float results[] = new float[10];
            float results2[] = new float[10];
            float results3[] = new float[10];
            float results4[] = new float[10];

            float results5[] = new float[10];
            float results6[] = new float[10];
            float results7[] = new float[10];

            Location.distanceBetween(University.latitude, University.longitude, latLng.latitude, latLng.longitude, results);
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng2.latitude, latLng2.longitude, results2);
            Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng3.latitude, latLng3.longitude, results3);
            Location.distanceBetween(latLng3.latitude, latLng3.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results4);

            Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, latLng4.latitude, latLng4.longitude, results5);
            Location.distanceBetween(latLng4.latitude, latLng4.longitude, latLng5.latitude, latLng5.longitude, results6);
            Location.distanceBetween(latLng5.latitude, latLng5.longitude, marker.getPosition().latitude, marker.getPosition().longitude, results7);

            PolylineOptionsMethodWithFivePoints(latLng,latLng2,latLng3,marker.getPosition());
            PolylineOptionsMethodWithThreePointsSecondaryEntry(latLng4,latLng5,marker.getPosition());

            CalculateTimePerMetersAndShowDistanceWithFivePoints((int)results[0],(int)results2[0],(int)results3[0],(int)results4 [0]);
            CalculateTimePerMetersAndShowDistanceWithFourPointsES((int)results5[0],(int)results6[0],(int)results7[0]);
            return false;
        }
        if (marker.getTitle().equalsIgnoreCase("Universidad del Bío-Bío")){
            generateToast("Universidad del Bío-Bío, Andres Bello 720");
            return false;
        }
        if (line!=null && line2!=null) {

            line.remove();
            line2.remove();
        }
        generateToast("Para más información presiona el cuadro de texto");
        Location.distanceBetween(University.latitude, University.longitude, marker.getPosition().latitude, marker.getPosition().longitude, resultsMarkerAddForApp);
        Location.distanceBetween(SecondaryEntry.latitude, SecondaryEntry.longitude, marker.getPosition().latitude, marker.getPosition().longitude, resultsMarkerAddForAppSecondaryEntry);
        PolylineOptionsMethodWithTwoPoints(marker.getPosition());
        PolylineOptionsMethodWithOnePointsSecondaryEntry(marker.getPosition());
        CalculateTimePerMetersAndShowDistanceWithTwoPoints((int)resultsMarkerAddForApp[0]);
        CalculateTimePerMetersAndShowDistanceWithTwoPointsES((int)resultsMarkerAddForAppSecondaryEntry[0]);

        Log.d("click", "click in marker");
        return false;
    }


    private void CalculateTimePerMetersAndShowDistanceWithTwoPoints(int result) {
        int resultWalkMinutes=0;
        int resultBikeMinutes=0;
        int resultWalkSeconds= (int)(result/1.5);
        int resultBikeSeconds= (result/6);
        while (resultWalkSeconds>=60){
            resultWalkMinutes++;
            resultWalkSeconds=resultWalkSeconds-60;
        }
        while (resultBikeSeconds>=60){
            resultBikeMinutes++;
            resultBikeSeconds=resultBikeSeconds-60;
        }
        WalkTextView.setText(resultWalkMinutes+" "+ "Minutos\n"+resultWalkSeconds+ " "+ "Segundos");
        BikeTextView.setText(resultBikeMinutes+" "+ "Minutos\n"+resultBikeSeconds+ " "+ "Segundos");
        DistanceTextView.setText(result+" "+"Metros");
    }
    private void CalculateTimePerMetersAndShowDistanceWithThreePoints(int result1, int result2){
        int resultWalkMinutes=0;
        int resultBikeMinutes=0;
        int resultWalkSeconds= (int)(result1/1.5 + result2/1.5);
        int resultBikeSeconds= (result1/6 + result2/6);
        while (resultWalkSeconds>=60){
            resultWalkMinutes++;
            resultWalkSeconds=resultWalkSeconds-60;
        }
        while (resultBikeSeconds>=60){
            resultBikeMinutes++;
            resultBikeSeconds=resultBikeSeconds-60;
        }
        WalkTextView.setText(resultWalkMinutes+" "+ "Minutos\n"+resultWalkSeconds+ " "+ "Segundos");
        BikeTextView.setText(resultBikeMinutes+" "+ "Minutos\n"+resultBikeSeconds+ " "+ "Segundos");
        DistanceTextView.setText(result1+result2+" "+"Metros");
    }
    private void CalculateTimePerMetersAndShowDistanceWithFourPoints(int result1, int result2,int result3){
        int resultWalkMinutes=0;
        int resultBikeMinutes=0;
        int resultWalkSeconds= (int)(result1/1.5 + result2/1.5 + result3/1.5);
        int resultBikeSeconds= (result1/6 + result2/6 + result3/6);
        while (resultWalkSeconds>=60){
            resultWalkMinutes++;
            resultWalkSeconds=resultWalkSeconds-60;
        }
        while (resultBikeSeconds>=60){
            resultBikeMinutes++;
            resultBikeSeconds=resultBikeSeconds-60;
        }
        WalkTextView.setText(resultWalkMinutes+" "+ "Minutos\n"+resultWalkSeconds+ " "+ "Segundos");
        BikeTextView.setText(resultBikeMinutes+" "+ "Minutos\n"+resultBikeSeconds+ " "+ "Segundos");
        DistanceTextView.setText(result1+result2+result3+" "+"Metros");

    }
    private void CalculateTimePerMetersAndShowDistanceWithFivePoints(int result1, int result2, int result3, int result4) {
        int resultWalkMinutes=0;
        int resultBikeMinutes=0;
        int resultWalkSeconds= (int)(result1/1.5 + result2/1.5 + result3/1.5 + result4/1.5);
        int resultBikeSeconds= (result1/6 + result2/6 + result3/6 + result4/6);
        while (resultWalkSeconds>=60){
            resultWalkMinutes++;
            resultWalkSeconds=resultWalkSeconds-60;
        }
        while (resultBikeSeconds>=60){
            resultBikeMinutes++;
            resultBikeSeconds=resultBikeSeconds-60;
        }
        WalkTextView.setText(resultWalkMinutes+" "+ "Minutos\n"+resultWalkSeconds+ " "+ "Segundos");
        BikeTextView.setText(resultBikeMinutes+" "+ "Minutos\n"+resultBikeSeconds+ " "+ "Segundos");
        DistanceTextView.setText(result1+result2+result3+result4+" "+"Metros");
    }
    private void CalculateTimePerMetersAndShowDistanceWithSixPoints(int result1, int result2, int result3, int result4, int result5) {
        int resultWalkMinutes=0;
        int resultBikeMinutes=0;
        int resultWalkSeconds= (int)(result1/1.5 + result2/1.5 + result3/1.5 + result4/1.5 + result5/1.5);
        int resultBikeSeconds= (result1/6 + result2/6 + result3/6 + result4/6 + result5/6);
        while (resultWalkSeconds>=60){
            resultWalkMinutes++;
            resultWalkSeconds=resultWalkSeconds-60;
        }
        while (resultBikeSeconds>=60){
            resultBikeMinutes++;
            resultBikeSeconds=resultBikeSeconds-60;
        }
        WalkTextView.setText(resultWalkMinutes+" "+ "Minutos\n"+resultWalkSeconds+ " "+ "Segundos");
        BikeTextView.setText(resultBikeMinutes+" "+ "Minutos\n"+resultBikeSeconds+ " "+ "Segundos");
        DistanceTextView.setText(result1+result2+result3+result4+result5+" "+"Metros");
    }

    private void CalculateTimePerMetersAndShowDistanceWithEightPointsES(int result1, int result2, int result3, int result4, int result5,int result6,int result7) {
        int resultWalkMinutes=0;
        int resultBikeMinutes=0;
        int resultWalkSeconds= (int)(result1/1.5 + result2/1.5 + result3/1.5 + result4/1.5 + result5/1.5 +result6/1.5 + result7/1.5);
        int resultBikeSeconds= (result1/6 + result2/6 + result3/6 + result4/6 + result5/6 + result6/6 + result7/6);
        while (resultWalkSeconds>=60){
            resultWalkMinutes++;
            resultWalkSeconds=resultWalkSeconds-60;
        }
        while (resultBikeSeconds>=60){
            resultBikeMinutes++;
            resultBikeSeconds=resultBikeSeconds-60;
        }
        WalkTextViewES.setText(resultWalkMinutes+" "+ "Minutos\n"+resultWalkSeconds+ " "+ "Segundos");
        BikeTextViewES.setText(resultBikeMinutes+" "+ "Minutos\n"+resultBikeSeconds+ " "+ "Segundos");
        DistanceTextViewES.setText(result1+result2+result3+result4+result5+result6+result7+" "+"Metros");
    }
    private void CalculateTimePerMetersAndShowDistanceWithSevenPointsES(int result1, int result2, int result3, int result4, int result5,int result6) {
        int resultWalkMinutes=0;
        int resultBikeMinutes=0;
        int resultWalkSeconds= (int)(result1/1.5 + result2/1.5 + result3/1.5 + result4/1.5 + result5/1.5 +result6/1.5  );
        int resultBikeSeconds= (result1/6 + result2/6 + result3/6 + result4/6 + result5/6 + result6/6 );
        while (resultWalkSeconds>=60){
            resultWalkMinutes++;
            resultWalkSeconds=resultWalkSeconds-60;
        }
        while (resultBikeSeconds>=60){
            resultBikeMinutes++;
            resultBikeSeconds=resultBikeSeconds-60;
        }
        WalkTextViewES.setText(resultWalkMinutes+" "+ "Minutos\n"+resultWalkSeconds+ " "+ "Segundos");
        BikeTextViewES.setText(resultBikeMinutes+" "+ "Minutos\n"+resultBikeSeconds+ " "+ "Segundos");
        DistanceTextViewES.setText(result1+result2+result3+result4+result5+result6+" "+"Metros");
    }
    private void CalculateTimePerMetersAndShowDistanceWithFourPointsES(int result1, int result2, int result3){
        int resultWalkMinutes=0;
        int resultBikeMinutes=0;
        int resultWalkSeconds= (int)(result1/1.5 + result2/1.5 + result3/1.5 );
        int resultBikeSeconds= (result1/6 + result2/6 + result3/6 );
        while (resultWalkSeconds>=60){
            resultWalkMinutes++;
            resultWalkSeconds=resultWalkSeconds-60;
        }
        while (resultBikeSeconds>=60){
            resultBikeMinutes++;
            resultBikeSeconds=resultBikeSeconds-60;
        }
        WalkTextViewES.setText(resultWalkMinutes+" "+ "Minutos\n"+resultWalkSeconds+ " "+ "Segundos");
        BikeTextViewES.setText(resultBikeMinutes+" "+ "Minutos\n"+resultBikeSeconds+ " "+ "Segundos");
        DistanceTextViewES.setText(result1+result2+result3+" "+"Metros");
    }
    private void CalculateTimePerMetersAndShowDistanceWithFivePointsES(int result1, int result2, int result3, int result4){
        int resultWalkMinutes=0;
        int resultBikeMinutes=0;
        int resultWalkSeconds= (int)(result1/1.5 + result2/1.5 + result3/1.5 +result4/1.5 );
        int resultBikeSeconds= (result1/6 + result2/6 + result3/6 + result4/6);
        while (resultWalkSeconds>=60){
            resultWalkMinutes++;
            resultWalkSeconds=resultWalkSeconds-60;
        }
        while (resultBikeSeconds>=60){
            resultBikeMinutes++;
            resultBikeSeconds=resultBikeSeconds-60;
        }
        WalkTextViewES.setText(resultWalkMinutes+" "+ "Minutos\n"+resultWalkSeconds+ " "+ "Segundos");
        BikeTextViewES.setText(resultBikeMinutes+" "+ "Minutos\n"+resultBikeSeconds+ " "+ "Segundos");
        DistanceTextViewES.setText(result1+result2+result3+result4+" "+"Metros");
    }
    private void CalculateTimePerMetersAndShowDistanceWithSixPointsES(int result1, int result2, int result3, int result4, int result5){
        int resultWalkMinutes=0;
        int resultBikeMinutes=0;
        int resultWalkSeconds= (int)(result1/1.5 + result2/1.5 + result3/1.5 +result4/1.5+result5/1.5 );
        int resultBikeSeconds= (result1/6 + result2/6 + result3/6 + result4/6+ result5/6 );
        while (resultWalkSeconds>=60){
            resultWalkMinutes++;
            resultWalkSeconds=resultWalkSeconds-60;
        }
        while (resultBikeSeconds>=60){
            resultBikeMinutes++;
            resultBikeSeconds=resultBikeSeconds-60;
        }
        WalkTextViewES.setText(resultWalkMinutes+" "+ "Minutos\n"+resultWalkSeconds+ " "+ "Segundos");
        BikeTextViewES.setText(resultBikeMinutes+" "+ "Minutos\n"+resultBikeSeconds+ " "+ "Segundos");
        DistanceTextViewES.setText(result1+result2+result3+result4+result5+" "+"Metros");
    }
    private void CalculateTimePerMetersAndShowDistanceWithThreePointsES(int result1, int result2){
        int resultWalkMinutes=0;
        int resultBikeMinutes=0;
        int resultWalkSeconds= (int)(result1/1.5 + result2/1.5 );
        int resultBikeSeconds= (result1/6 + result2/6  );
        while (resultWalkSeconds>=60){
            resultWalkMinutes++;
            resultWalkSeconds=resultWalkSeconds-60;
        }
        while (resultBikeSeconds>=60){
            resultBikeMinutes++;
            resultBikeSeconds=resultBikeSeconds-60;
        }
        WalkTextViewES.setText(resultWalkMinutes+" "+ "Minutos\n"+resultWalkSeconds+ " "+ "Segundos");
        BikeTextViewES.setText(resultBikeMinutes+" "+ "Minutos\n"+resultBikeSeconds+ " "+ "Segundos");
        DistanceTextViewES.setText(result1+result2+" "+"Metros");
    }
    private void CalculateTimePerMetersAndShowDistanceWithTwoPointsES(int result1){
        int resultWalkMinutes=0;
        int resultBikeMinutes=0;
        int resultWalkSeconds= (int)(result1/1.5  );
        int resultBikeSeconds= (result1/6   );
        while (resultWalkSeconds>=60){
            resultWalkMinutes++;
            resultWalkSeconds=resultWalkSeconds-60;
        }
        while (resultBikeSeconds>=60){
            resultBikeMinutes++;
            resultBikeSeconds=resultBikeSeconds-60;
        }
        WalkTextViewES.setText(resultWalkMinutes+" "+ "Minutos\n"+resultWalkSeconds+ " "+ "Segundos");
        BikeTextViewES.setText(resultBikeMinutes+" "+ "Minutos\n"+resultBikeSeconds+ " "+ "Segundos");
        DistanceTextViewES.setText(result1+" "+"Metros");
    }


    private void PolylineOptionsMethodWithTwoPoints(LatLng latLng) {

        line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(University.latitude, University.longitude),
                        new LatLng(latLng.latitude, latLng.longitude))
                .width(10)
                .color(Color.RED));


    }
    private void PolylineOptionsMethodWithThreePoints(LatLng latLng, LatLng latLng2) {
        line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(University.latitude, University.longitude),
                        new LatLng(latLng.latitude, latLng.longitude),new LatLng(latLng2.latitude, latLng2.longitude))
                .width(10)
                .color(Color.RED));
    }
    private void PolylineOptionsMethodWithFourPoints(LatLng latLng, LatLng latLng2, LatLng latLng3) {
        line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(University.latitude, University.longitude),
                        new LatLng(latLng.latitude, latLng.longitude),new LatLng(latLng2.latitude, latLng2.longitude)
                        , new LatLng(latLng3.latitude,latLng3.longitude))
                .width(10)
                .color(Color.RED));
    }
    private void PolylineOptionsMethodWithFivePoints(LatLng latLng, LatLng latLng2, LatLng latLng3, LatLng latLng4) {
        line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(University.latitude, University.longitude),
                        new LatLng(latLng.latitude, latLng.longitude),new LatLng(latLng2.latitude, latLng2.longitude)
                        , new LatLng(latLng3.latitude,latLng3.longitude), new LatLng(latLng4.latitude, latLng4.longitude))
                .width(10)
                .color(Color.RED));
    }
    private void PolylineOptionsMethodWithSixPoints(LatLng latLng, LatLng latLng2, LatLng latLng3, LatLng latLng4, LatLng latLng5) {
        line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(University.latitude, University.longitude),
                        new LatLng(latLng.latitude, latLng.longitude),new LatLng(latLng2.latitude, latLng2.longitude)
                        , new LatLng(latLng3.latitude,latLng3.longitude), new LatLng(latLng4.latitude, latLng4.longitude)
                , new LatLng(latLng5.latitude, latLng5.longitude))
                .width(10)
                .color(Color.RED));
    }

    private void PolylineOptionsMethodWithOnePointsSecondaryEntry(LatLng latLng) {
        line2 = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(SecondaryEntry.latitude, SecondaryEntry.longitude),
                        new LatLng(latLng.latitude, latLng.longitude))
                .width(10)
                .color(Color.RED));
    }
    private void PolylineOptionsMethodWithTwoPointsSecondaryEntry(LatLng latLng, LatLng latLng2) {
        line2 = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(SecondaryEntry.latitude, SecondaryEntry.longitude),
                        new LatLng(latLng.latitude, latLng.longitude),new LatLng(latLng2.latitude, latLng2.longitude))
                .width(10)
                .color(Color.RED));
    }
    private void PolylineOptionsMethodWithThreePointsSecondaryEntry(LatLng latLng, LatLng latLng2,LatLng latLng3) {
        line2 = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(SecondaryEntry.latitude, SecondaryEntry.longitude),
                        new LatLng(latLng.latitude, latLng.longitude),new LatLng(latLng2.latitude, latLng2.longitude)
                ,new LatLng(latLng3.latitude, latLng3.longitude))
                .width(10)
                .color(Color.RED));
    }
    private void PolylineOptionsMethodWithFourPointsSecondaryEntry(LatLng latLng, LatLng latLng2, LatLng latLng3, LatLng latLng4) {
        line2 = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(SecondaryEntry.latitude, SecondaryEntry.longitude),
                        new LatLng(latLng.latitude, latLng.longitude),new LatLng(latLng2.latitude, latLng2.longitude)
                        , new LatLng(latLng3.latitude,latLng3.longitude),new LatLng(latLng4.latitude,latLng4.longitude))
                .width(10)
                .color(Color.RED));
    }
    private void PolylineOptionsMethodWithFivePointsSecondaryEntry(LatLng latLng, LatLng latLng2, LatLng latLng3, LatLng latLng4,LatLng latLng5) {
        line2 = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(SecondaryEntry.latitude, SecondaryEntry.longitude),
                        new LatLng(latLng.latitude, latLng.longitude),new LatLng(latLng2.latitude, latLng2.longitude)
                        , new LatLng(latLng3.latitude,latLng3.longitude),new LatLng(latLng4.latitude,latLng4.longitude)
                        ,new LatLng(latLng5.latitude,latLng5.longitude))
                .width(10)
                .color(Color.RED));
    }
    private void PolylineOptionsMethodWithSixPointsSecondaryEntry(LatLng latLng, LatLng latLng2, LatLng latLng3, LatLng latLng4, LatLng latLng5,LatLng latLng6) {

        line2 = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(SecondaryEntry.latitude, SecondaryEntry.longitude),
                        new LatLng(latLng.latitude, latLng.longitude),new LatLng(latLng2.latitude, latLng2.longitude)
                        , new LatLng(latLng3.latitude,latLng3.longitude), new LatLng(latLng4.latitude, latLng4.longitude)
                        , new LatLng(latLng5.latitude, latLng5.longitude), new LatLng(latLng6.latitude, latLng6.longitude))
                .width(10)
                .color(Color.RED));
    }
    private void PolylineOptionsMethodWithSevenPointsSecondaryEntry(LatLng latLng, LatLng latLng2, LatLng latLng3, LatLng latLng4, LatLng latLng5,LatLng latLng6,LatLng latLng7) {

        line2 = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(SecondaryEntry.latitude, SecondaryEntry.longitude),
                        new LatLng(latLng.latitude, latLng.longitude),new LatLng(latLng2.latitude, latLng2.longitude)
                        , new LatLng(latLng3.latitude,latLng3.longitude), new LatLng(latLng4.latitude, latLng4.longitude)
                        , new LatLng(latLng5.latitude, latLng5.longitude), new LatLng(latLng6.latitude, latLng6.longitude)
                        , new LatLng(latLng7.latitude, latLng7.longitude))
                .width(10)
                .color(Color.RED));
    }













    private void generateToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }



    private double distancia(double lat1, double lng1, double lat2, double lng2){
        double R = 6378.137;
        double dLat  = rad( lat2 - lat1 );
        double dLong = rad( lng2 - lng1 );

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(rad(lat1)) * Math.cos(rad(lat2)) * Math.sin(dLong/2) * Math.sin(dLong/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;

        return d;
    }

    private double rad(double data){
        return data * Math.PI/180;
    }



    /**
     *Generate a String with the urL of request of route**/
    private String obtenerDireccionesURL(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String key = "key=" + getString(R.string.google_maps_key);
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + key;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    /**
     * Obtain a String of datas, obtained from the service web of routes**/
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creamos una conexion http
            urlConnection = (HttpURLConnection) url.openConnection();
            // Conectamos
            urlConnection.connect();
            // Leemos desde URL
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /** clase que crea una tarea async para descargar la ruta en una hilo independiente del procesador **/
    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {

            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("ERROR", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    /** parser para que obtiene los datos necesarios para crear un objeto Polyline para el mapa **/
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(4);
                lineOptions.color(Color.rgb(0, 0, 255));
            }
            if (lineOptions != null) {
                Log.d("ssss", "ruta");
                mMap.addPolyline(lineOptions);
            }
        }
    }

    public class DirectionsJSONParser {

        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

            List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            JSONArray jSteps = null;

            try {

                jRoutes = jObject.getJSONArray("routes");

                for (int i = 0; i < jRoutes.length(); i++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List path = new ArrayList<HashMap<String, String>>();

                    for (int j = 0; j < jLegs.length(); j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                                hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }

            return routes;
        }

        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }

            return poly;
        }
    }


}