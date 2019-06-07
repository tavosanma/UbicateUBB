package cl.ubiobio.chillan.ubicateubb;

import android.content.DialogInterface;
import android.os.Bundle;

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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.Marker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import cl.ubiobio.chillan.ubicateubb.entities.Coordinates;
import cl.ubiobio.chillan.ubicateubb.entities.VolleySingleton;


public class UpdateAndDeleteMarker extends AppCompatActivity implements LocationSource, OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnMarkerClickListener, Response.Listener<JSONObject>,Response.ErrorListener {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private LocationSource.OnLocationChangedListener listener;
    private Location mCurrentLocation;
    public  LatLng University = new LatLng(-36.603383, -72.079246);
    private LocationManager mLocationManager;
    private static long UPDATE_INTERVAL_IN_MILLISECONDS;
    public EditText EditTextTitle,EditTextSnippet,EditTextLongitude,EditTextLatitude,EditTextInformation,EditTextInformationtwo,EditTextLink;
    public Button ButtonDelete, ButtonReturn,ButtonUpdate;
    ArrayList<Coordinates> coordinatesArrayListPHP = new ArrayList<>();
    public MarkerOptions markerOptions = new MarkerOptions();
    Coordinates coordinates =  new Coordinates();
    private StringRequest stringRequest;
    JsonObjectRequest jsonObjectRequest;
    //private RequestQueue requestQueue;










    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_and_delete_marker);

        //requestQueue = Volley.newRequestQueue(getApplicationContext());

        /** Catching IDs of the components of a layout**/

        EditTextLatitude=findViewById(R.id.EditTextLatitude);
        EditTextLongitude=findViewById(R.id.EditTextLongitude);
        EditTextSnippet=findViewById(R.id.EditTextSnippet);
        EditTextTitle=findViewById(R.id.EditTextTitle);
        EditTextInformation=findViewById(R.id.EditTextInformation);
        EditTextInformationtwo=findViewById(R.id.EditTextInformationtwo);
        EditTextLink=findViewById(R.id.EditTextLink);

        ButtonReturn=findViewById(R.id.ButtonReturn);

        ButtonDelete=findViewById(R.id.ButtonDelete);
        ButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EditTextTitle.getText().toString().isEmpty()){
                    generateToast("Título vacío, Presione un marcador.");
                }else {
                    final AlertDialog.Builder alertDialog= new AlertDialog.Builder(UpdateAndDeleteMarker.this);
                    alertDialog.setTitle("Dialogo de confirmación");
                    alertDialog.setMessage("¿Está seguro que desea borrar "+EditTextTitle.getText().toString());

                    alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String webHost=getString(R.string.webhost);
                            String url = webHost+"/delete.php?title="+EditTextTitle.getText().toString();
                            stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                        if (response.trim().equalsIgnoreCase("elimina")){
                                            EditTextLatitude.setText("");
                                            EditTextTitle.setText("");
                                            EditTextSnippet.setText("");
                                            EditTextLongitude.setText("");
                                            EditTextInformation.setText("");
                                            EditTextInformationtwo.setText("");
                                            EditTextLink.setText("");
                                            generateToast("Marcador se eliminó correctamente");
                                            mMap.clear();
                                            LoadWebService();
                                        }else {
                                            if (response.trim().equalsIgnoreCase("noExiste")){
                                                generateToast("No se encuentra el marcador");
                                            }else {
                                                generateToast("No se ha eliminado el marcador");
                                            }
                                        }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                        generateToast("no se ha podido conectar, vuelva a iniciar la aplicación");
                                }
                            });
                           // requestQueue.add(stringRequest);
                            VolleySingleton.getInstanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);
                        }
                    });
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    alertDialog.create().show();

                }
            }
        });




        ButtonUpdate=findViewById(R.id.ButtonUpdate);
        ButtonUpdate.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (EditTextSnippet.getText().toString().isEmpty()||EditTextLatitude.getText().toString().isEmpty()||EditTextLongitude.getText().toString().isEmpty()){
                   generateToast("Campos vacíos, completelos");
               }else {
                    final AlertDialog.Builder alertDialog= new AlertDialog.Builder(UpdateAndDeleteMarker.this);
                    alertDialog.setTitle("Dialogo de confirmación");
                    alertDialog.setMessage("¿Está seguro que desea actualizar éste marcador?");
                    alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String webHost=getString(R.string.webhost);
                            String url = webHost+"/ServiceWebJsonUpdateCoordinates.php?";
                            stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if (response.trim().equalsIgnoreCase("actualiza")){
                                        EditTextLatitude.setText("");
                                        EditTextTitle.setText("");
                                        EditTextSnippet.setText("");
                                        EditTextLongitude.setText("");
                                        EditTextInformation.setText("");
                                        EditTextInformationtwo.setText("");
                                        EditTextLink.setText("");
                                        generateToast("Marcador se actualizó correctamente");
                                        mMap.clear();
                                        LoadWebService();
                                    }else {

                                        generateToast("No se ha actualizado correctamente");
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    generateToast("no se ha podido conectar, vuelva a iniciar la aplicación");
                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    String title=EditTextTitle.getText().toString();
                                    String snippet=EditTextSnippet.getText().toString();
                                    String latitude=EditTextLatitude.getText().toString();
                                    String longitude=EditTextLongitude.getText().toString();
                                    String information=EditTextInformation.getText().toString();
                                    String informationtwo=EditTextInformationtwo.getText().toString();
                                    String link=EditTextLink.getText().toString();
                                    //String information=EditTextTitle.getText().toString();
                                    Map<String,String> parametros = new HashMap<>();
                                    parametros.put("title",title);
                                    parametros.put("snippet",snippet);
                                    parametros.put("latitude",latitude);
                                    parametros.put("longitude",longitude);
                                    parametros.put("information",information);
                                    parametros.put("informationtwo",informationtwo);
                                    parametros.put("link",link);

                                    return parametros;
                                }
                            };
                            //requestQueue.add(stringRequest);
                            VolleySingleton.getInstanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);
                        }
                    });
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    alertDialog.create().show();



               }
           }
       });

        ButtonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateAndDeleteMarker.this,FunctionsToAdminister.class));

            }
        });




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
        startActivity(new Intent(UpdateAndDeleteMarker.this,FunctionsToAdminister.class));


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


        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        /** Activation of controls in the map**/
        mMap.getUiSettings().setZoomControlsEnabled(true); //control of zoom
        mMap.getUiSettings().setMyLocationButtonEnabled(true); //we enable botton to return to their position
        mMap.getUiSettings().setCompassEnabled(true); // the map search the north

        /** Gestión de algunos eventos**/
        mMap.setOnMyLocationClickListener(this); //click above position
        //click large in the map
        mMap.setOnMarkerClickListener(this);

        enableMyLocation();

        /** iniciamos el proceso de captura de posiciones **/
        LoadWebService();
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                marker.showInfoWindow();

            }

            @Override
            public void onMarkerDrag(Marker marker) {
                EditTextLatitude.setText(String.valueOf(marker.getPosition().latitude));
                EditTextLongitude.setText(String.valueOf(marker.getPosition().longitude));
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                EditTextTitle.setText(marker.getTitle());
                EditTextSnippet.setText(marker.getSnippet());
                EditTextLatitude.setText(String.valueOf(marker.getPosition().latitude));
                EditTextLongitude.setText(String.valueOf(marker.getPosition().longitude));
                marker.showInfoWindow();

            }
        });



    }

    private void LoadWebService() {
        String webHost=getString(R.string.webhost);
        String url = webHost+"/WebServiceQueryListCoordinates.php";
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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
                        mMap.addMarker(markerOptions.position(new LatLng(coordinatesArrayListPHP.get(i).getLatitude(), coordinatesArrayListPHP.get(i).getLongitude()
                        )).title(coordinatesArrayListPHP.get(i).getTitle()).snippet(coordinatesArrayListPHP.get(i).getSnippet()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).draggable(true));




                    }

                }catch (JSONException e ){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        //requestQueue.add(jsonObjectRequest);
        VolleySingleton.getInstanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }


    /**
     * event that is activated to perform click above the information display for a marker
     **/

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


                        UpdateAndDeleteMarker.this.listener.onLocationChanged(location);



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
        EditTextTitle.setText(marker.getTitle());
        EditTextSnippet.setText(marker.getSnippet());
        EditTextLatitude.setText(String.valueOf(marker.getPosition().latitude));
        EditTextLongitude.setText(String.valueOf(marker.getPosition().longitude));
        String webHost=getString(R.string.webhost);
        String url =webHost+"/WebServiceQueryTitleByTitle.php?title="+EditTextTitle.getText().toString();

        jsonObjectRequest= new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        //requestQueue.add(jsonObjectRequest);
        VolleySingleton.getInstanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
        Log.d("click", "click in marker");
        return false;
    }




    private void generateToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(),"No se pudo conectar "+error.toString(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        Coordinates coordinates = new Coordinates();
        JSONArray jsonArray = response.optJSONArray("coordinates");
        JSONObject jsonObject=null;
        try {
            jsonObject=jsonArray.getJSONObject(0);

           if (jsonObject.getString("information").equalsIgnoreCase("")||
                   jsonObject.getString("informationtwo").equalsIgnoreCase("")||
                   jsonObject.getString("link").equalsIgnoreCase("")) {
               coordinates.setInformation(jsonObject.optString("information"));
               EditTextInformation.setText(coordinates.getInformation());
               coordinates.setInformationtwo(jsonObject.optString("informationtwo"));
               EditTextInformationtwo.setText(coordinates.getInformationtwo());
               coordinates.setLink(jsonObject.optString("link"));
               EditTextLink.setText(coordinates.getLink());
           }else {
               coordinates.setInformation(jsonObject.optString("information"));
               EditTextInformation.setText(coordinates.getInformation());
               coordinates.setInformationtwo(jsonObject.optString("informationtwo"));
               EditTextInformationtwo.setText(coordinates.getInformationtwo());
               coordinates.setLink(jsonObject.optString("link"));
               EditTextLink.setText(coordinates.getLink());
           }

        }catch (JSONException e){
            e.printStackTrace();
        }

    }
}
