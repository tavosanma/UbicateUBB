package cl.ubiobio.chillan.ubicateubb;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cl.ubiobio.chillan.ubicateubb.entities.Administrator;
import cl.ubiobio.chillan.ubicateubb.entities.VolleySingleton;

public class AdminConfiguration extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {


    Button create , volver,ButtonCambiarPassword,ButtonCambiarUser;
    EditText EditTextUsuario, EditTextPassword;
    JsonObjectRequest jsonObjectRequest ;
    //RequestQueue requestQueue;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_configuration);

       // requestQueue= Volley.newRequestQueue(getApplicationContext());

        cargarWebService();
        create=findViewById(R.id.ButtonCreate);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!EditTextUsuario.getText().toString().isEmpty()&& !EditTextPassword.getText().toString().isEmpty()){
                    login();
                }else {
                    if (EditTextUsuario.getText().toString().isEmpty()&& EditTextPassword.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(),"Contraseña y Usuario vacíos",Toast.LENGTH_SHORT).show();
                    }else {
                        if (EditTextUsuario.getText().toString().isEmpty()){
                            Toast.makeText(getApplicationContext(),"Usuario vacío",Toast.LENGTH_SHORT).show();
                        }else {
                            if (EditTextPassword.getText().toString().isEmpty()){
                                Toast.makeText(getApplicationContext(),"Contraseña vacía",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }


            }
        });


        EditTextUsuario=findViewById(R.id.EditTextUsuario);
        EditTextPassword=findViewById(R.id.EditTextPassword);
        ButtonCambiarUser=findViewById(R.id.ButtonCambiarUser);
        ButtonCambiarPassword=findViewById(R.id.ButtonCambiarPassword);
        ButtonCambiarPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialog= new AlertDialog.Builder(AdminConfiguration.this);
                View mView= getLayoutInflater().inflate(R.layout.cambiar_password,null);
                final EditText EditTextCambiarPassword=mView.findViewById(R.id.EditTextCambiarPassword);
                final Button ButtonAceptarCambiarPassword=mView.findViewById(R.id.ButtonAceptarCambiarPassword);
                EditTextCambiarPassword.setText(EditTextPassword.getText().toString());
                alertDialog.setView(mView);
                final AlertDialog dialog = alertDialog.create();
                dialog.show();
                ButtonAceptarCambiarPassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditTextPassword.setText(EditTextCambiarPassword.getText().toString());
                        dialog.dismiss();
                    }
                });
            }

        });
        ButtonCambiarUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialog= new AlertDialog.Builder(AdminConfiguration.this);
                View mView= getLayoutInflater().inflate(R.layout.cambiar_user,null);
                final EditText EditTextCambiarUsuario=mView.findViewById(R.id.EditTextCambiarUsuario);
                final Button ButtonAceptarCambiarUsuario=mView.findViewById(R.id.ButtonAceptarCambiarUsuario);
                EditTextCambiarUsuario.setText(EditTextUsuario.getText().toString());
                alertDialog.setView(mView);
                final AlertDialog dialog = alertDialog.create();
                dialog.show();
                ButtonAceptarCambiarUsuario.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditTextUsuario.setText(EditTextCambiarUsuario.getText().toString());
                        dialog.dismiss();
                    }
                });


            }

        });

        volver=findViewById(R.id.Volver);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminConfiguration.this,FunctionsToAdminister.class));
                finish();
            }
        });




    }

    private void cargarWebService() {

        String webHost=getString(R.string.webhost);
        String url = webHost+"/QueryAdministrator.php";
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        //requestQueue.add(jsonObjectRequest);
        VolleySingleton.getInstanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
    public void login() {
        String webHost=getString(R.string.webhost);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webHost+"/login.php"
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("1") && response.contains("2")) {
                    Toast.makeText(getApplicationContext(), "Usuario y Contraseña ya registrados.", Toast.LENGTH_LONG).show();
                } else {
                    if (response.contains("1")) {
                        insertar();
                    } else {
                        if (response.contains("2")) {
                            insertar();
                        } else {
                            insertar();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("username",EditTextUsuario.getText().toString());
                params.put("password",EditTextPassword.getText().toString());
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);

    }

    private void insertar() {
        final AlertDialog.Builder alertDialog= new AlertDialog.Builder(AdminConfiguration.this);
        alertDialog.setTitle("Dialogo de confirmación");
        alertDialog.setMessage("¿Está seguro que desea actualizar los datos?");
        alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String webHost=getString(R.string.webhost);
                String url = webHost+"/ServiceWebJsonUpdateAdministrator.php?";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equalsIgnoreCase("actualiza")) {
                           Toast.makeText(getApplicationContext(),"Se actualizo correctamente",Toast.LENGTH_LONG).show();
                           startActivity(new Intent(AdminConfiguration.this,FunctionsToAdminister.class));


                        } else {

                            Toast.makeText(getApplicationContext(),"No Se actualizo correctamente, intentelo de nuevo",Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"No Se ha podido conectar",Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        String id = "0";
                        String username = EditTextUsuario.getText().toString();
                        String password = EditTextPassword.getText().toString();

                        Map<String, String> parametros = new HashMap<>();
                        parametros.put("id", id);
                        parametros.put("username", username);
                        parametros.put("password", password);

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


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(),"No se puede conectar "+error.toString(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject response) {

        Administrator administrator = null;
        JSONArray jsonArray = response.optJSONArray("administrator");
        try {
            administrator=new Administrator();
            JSONObject jsonObject=null;
            jsonObject=jsonArray.getJSONObject(0);

            administrator.setUsername(jsonObject.optString("username"));
            administrator.setPassword(jsonObject.optString("password"));
            EditTextUsuario.setText(administrator.getUsername());
            EditTextPassword.setText(administrator.getPassword());


        }catch (JSONException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"No se ha podido establecer conexión con el servidor",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AdminConfiguration.this,FunctionsToAdminister.class));
        finish();

    }


}
