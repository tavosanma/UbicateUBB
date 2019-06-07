package cl.ubiobio.chillan.ubicateubb;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cl.ubiobio.chillan.ubicateubb.entities.Coordinates;
import cl.ubiobio.chillan.ubicateubb.entities.VolleySingleton;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CreateMarker extends AppCompatActivity implements LocationSource, OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener,Response.Listener<JSONObject>,Response.ErrorListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener
{

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private LocationSource.OnLocationChangedListener listener;
    private Location mCurrentLocation;
    private LocationManager mLocationManager;
    private static long UPDATE_INTERVAL_IN_MILLISECONDS;
    private EditText EditTextTitle;
    private EditText EditTextSnippet;
    private EditText EditTextInformation;
    private EditText EditTextInformationTwo;
    private EditText EditTextLink;
    private EditText EditTextLatitude;
    private EditText EditTextLongitude;
    private Button MostrarMarcador;
    public LatLng University = new LatLng(-36.603383, -72.079246);
    private Button ButtonCreate;
    private Button ButtonReturn;
    //private RequestQueue requestQueue;
    ArrayList<Coordinates> coordinatesArrayListPHP = new ArrayList<>();
    public MarkerOptions markerOptions = new MarkerOptions();
    JsonObjectRequest jsonObjectRequest;
    Coordinates coordinates =  new Coordinates();
    private StringRequest stringRequest;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_marker);
        //requestQueue = Volley.newRequestQueue(getApplicationContext());



        EditTextTitle = findViewById(R.id.EditTextTitle);
        EditTextSnippet = findViewById(R.id.EditTextSnippet);
        EditTextInformation = findViewById(R.id.EditTextInformation);
        EditTextLatitude = findViewById(R.id.EditTextLatitude);
        EditTextLongitude = findViewById(R.id.EditTextLongitude);
        EditTextInformationTwo = findViewById(R.id.EditTextInformationTwo);
        EditTextLink = findViewById(R.id.EditTextLink);
        ButtonCreate = findViewById(R.id.ButtonCreate);
        ButtonReturn = findViewById(R.id.ButtonReturn);
        MostrarMarcador=findViewById(R.id.MostrarMarcadores);
        MostrarMarcador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                MostrarMarcador.setEnabled(false);
                LoadWebService();
            }
        });




        ButtonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateMarker.this, FunctionsToAdminister.class));

            }
        });
        ButtonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (EditTextLatitude.getText().toString().isEmpty()||EditTextLongitude.getText().toString().isEmpty()||EditTextInformation.getText().toString().isEmpty()||EditTextTitle.getText().toString().isEmpty() || EditTextSnippet.getText().toString().isEmpty()) {
                    generateToast("Campos vacíos o no se ha puesto un marcador en el mapa, verifique.");
                } else {
                        String webHost=getString(R.string.webhost);
                        stringRequest = new StringRequest(Request.Method.POST, webHost+"/QueryIfExistTitle.php"
                                , new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.contains("1")) {
                                    generateToast("El título ingresado ya existe, intente otro");

                                } else {
                                    CreateWebService();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("title", EditTextTitle.getText().toString());

                                return params;
                            }
                        };
                        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);

                }
            }
        });


        /** mLocationManager gestiona las peticiones de posicion **/
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        /** Verificamos si el usuario tiene encendido el GPS, si no,
         * lo enviamos al menú para que lo encienda **/
        boolean enabledGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabledGPS) {
            Toast.makeText(this, "No hay señal de GPS", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        /** Establecemos el intervalo de actualización de la posicion **/
        UPDATE_INTERVAL_IN_MILLISECONDS = 7000;

        /** Se busca el fragmento del mapa e iniciamos el mapa.
         * cuando el mapa se encuentre listo, se llamará a onMapReady() **/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }





    /**
     * gestionamos la respuesta de la petición de permisos
     **/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            /** si el permiso fue aceptado, iniciamos el proceso de captura de posiciones **/
            enableMyLocation();
        } else {
            mPermissionDenied = true;
        }

    }



    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CreateMarker.this, FunctionsToAdminister.class));

    }
    private void CreateWebService() {
        String webHost=getString(R.string.webhost);

        String url =webHost+"/ServiceWebJsonRegisterCoordinates.php?title="+EditTextTitle.getText().toString()
                +"&snippet="+EditTextSnippet.getText().toString()
                +"&latitude="+EditTextLatitude.getText().toString()
                +"&longitude="+EditTextLongitude.getText().toString()
                +"&information="+EditTextInformation.getText().toString()
                +"&informationtwo="+EditTextInformationTwo.getText().toString()
                +"&link="+EditTextLink.getText().toString();
        url=url.replace(" ","%20");
        url=url.replace("\n","%0A");
        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                EditTextTitle.setText("");
                EditTextLatitude.setText("");
                EditTextSnippet.setText("");
                EditTextLongitude.setText("");
                EditTextInformation.setText("");
                EditTextInformationTwo.setText("");
                EditTextLink.setText("");
                MostrarMarcador.setEnabled(false);
                mMap.clear();
                LoadWebService();

            generateToast("Marcador Registrado exitosamente");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                generateToast("Marcador no registrado, intentelo de nuevo");

            }
        });
        //requestQueue.add(stringRequest);
        VolleySingleton.getInstanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);

    }










    /**
     * Si realizamos un click sobre su posicion (punto azul), mostraremos información acerca de ese punto
     **/
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Mi posición actual:\n" + "Latitud:"+location.getLatitude()+"\n"
                +"Longitud:"+location.getLongitude(), Toast.LENGTH_LONG).show();
    }

    /**
     * Dialogo de error para cuando no se acepte el permiso
     **/
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    /**
     * el mapa se encuentra listo, podemos modificar algunas configuraciones
     **/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LoadWebService();
        /** Activacion de controles en el mapa **/
        mMap.getUiSettings().setZoomControlsEnabled(true); //control de zoom
        mMap.getUiSettings().setMyLocationButtonEnabled(true); //habilitamos boton para regresar a su posicion
        mMap.getUiSettings().setCompassEnabled(true); //el mapa busca el norte

        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        /** Gestión de algunos eventos**/
        mMap.setOnMyLocationClickListener(this); //click sobre posicion
        mMap.setOnMapLongClickListener(this);//click largo en el mapa
        mMap.setOnMarkerClickListener(this); //click sobre marcador
        //click sobre la informacion de un marcador

        /** iniciamos el proceso de captura de posiciones **/
        enableMyLocation();
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {
                EditTextLatitude.setText(String.valueOf(marker.getPosition().latitude));
                EditTextLongitude.setText(String.valueOf(marker.getPosition().longitude));

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                EditTextLatitude.setText(String.valueOf(marker.getPosition().latitude));
                EditTextLongitude.setText(String.valueOf(marker.getPosition().longitude));
            }
        });


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
        generateToast("error" + error.toString());
        Log.d("error", error.toString());
    }
    @Override
    public void onResponse(JSONObject response) {
        coordinatesArrayListPHP=new ArrayList<>();
        coordinates = null;
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
                mMap.addMarker(markerOptions.position(new LatLng(coordinatesArrayListPHP.get(i).getLatitude(), coordinatesArrayListPHP.get(i).getLongitude()
                )).title(coordinatesArrayListPHP.get(i).getTitle()).snippet(coordinatesArrayListPHP.get(i).getSnippet()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            }

        }catch (JSONException e ){
            e.printStackTrace();
        }
    }

    private void enableMyLocation() {
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


                        CreateMarker.this.listener.onLocationChanged(location);

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
                LatLng thisLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()); //Zoom en el gps

                /** we move the map to he obtained position**/
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(thisLocation, 13));
                /** and indicate that establish a zoom 18, more high the number, more near above the ground**/
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

            }else {

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(University, 17));
                generateToast("Intente caminar o verifique la activación de su GPS");
            }

        }

    }

    /**
     * se inicializa el evento que captura las posiciones para el mapa
     **/


    /**
     * cuando se realiza un click prologando (poco más de un segundo),
     * se activará este evento, el cual nos entregará la posición donde se realizo el click prolongado
     * y creará un margador con la información dispuesta a continuación y borrará otros elementos
     * existentes
     **/
    @Override
    public void onMapLongClick(LatLng latLng) {
        MostrarMarcador.setEnabled(true);
        markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        mMap.clear();
        markerOptions.title("Presiona un momento y arrastra el marcador.").draggable(true);
        EditTextLatitude.setText(String.valueOf(markerOptions.getPosition().latitude));
        EditTextLongitude.setText(String.valueOf(markerOptions.getPosition().longitude));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.addMarker(markerOptions);


    }

    /**
     * Evento que se activa al realizar click sobre un marcador
     **/
    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("click", "click en marker");
        return false;
    }

    private void generateToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.listener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        this.listener = null;
    }


    private double distancia(double lat1, double lng1, double lat2, double lng2) {
        double R = 6378.137;
        double dLat = rad(lat2 - lat1);
        double dLong = rad(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(rad(lat1)) * Math.cos(rad(lat2)) * Math.sin(dLong / 2) * Math.sin(dLong / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;

        return d;
    }

    private double rad(double data) {
        return data * Math.PI / 180;
    }

    /**
     * evento que se activa al realizar click sobre la información desplegada por un marcador
     **/



    /**
     * Genera un string con la URL de solicitud de ruta
     **/
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
     * Obtiene string de datos obtenidos desde el servicio web de rutas
     **/
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



    /*** clase que crea una tarea async para descargar la ruta en una hilo independiente del procesador
     **/
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

    /**
     * parser para que obtiene los datos necesarios para crear un objeto Polyline para el mapa
     **/
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
