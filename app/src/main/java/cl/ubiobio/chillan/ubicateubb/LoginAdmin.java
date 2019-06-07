package cl.ubiobio.chillan.ubicateubb;


import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

import cl.ubiobio.chillan.ubicateubb.entities.VolleySingleton;

public class LoginAdmin extends AppCompatActivity {

    EditText EditTextUser;
    EditText EditTextPassword;
    Button ButtonEnter;
    Button ButtonReturn;
    public int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);
         EditTextUser = findViewById(R.id.EditTextUser);
         EditTextPassword = findViewById(R.id.EditTextPassword);
         ButtonEnter = findViewById(R.id.ButtonEnter);
         ButtonReturn = findViewById(R.id.ButtonReturn);




         ButtonReturn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(LoginAdmin.this,MainActivity.class);
                 startActivity(intent);
                 finish();
             }
         });

         ButtonEnter.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 if (!EditTextUser.getText().toString().isEmpty()&& !EditTextPassword.getText().toString().isEmpty()){
                     login();
                 }else {
                     if (EditTextPassword.getText().toString().isEmpty()&& EditTextUser.getText().toString().isEmpty()){

                         Toast.makeText(getApplicationContext(),"Contraseña y rut vacíos",Toast.LENGTH_SHORT).show();
                     }else {
                         if (EditTextUser.getText().toString().isEmpty()){
                             Toast.makeText(getApplicationContext(),"rut vacío",Toast.LENGTH_SHORT).show();
                         }else {
                             if (EditTextPassword.getText().toString().isEmpty()){
                                 Toast.makeText(getApplicationContext(),"Contraseña vacía",Toast.LENGTH_SHORT).show();
                             }
                         }
                     }
                 }

             }
         });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LoginAdmin.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void login() {

        String webHost=getString(R.string.webhost);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, webHost+"/login.php"
                    , new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.contains("1") && response.contains("2")){
                        startActivity(new Intent(getApplicationContext(),FunctionsToAdminister.class));
                    }else {
                        if (response.contains("1")){

                            if (i==3){
                                EditTextUser.setEnabled(false);
                                EditTextPassword.setEnabled(false);
                                Handler handlerTimer = new Handler();
                                handlerTimer.postDelayed(new Runnable(){
                                    public void run() {
                                       EditTextUser.setEnabled(true);
                                       EditTextPassword.setEnabled(true);
                                       i=0;
                                    }}, 100000);
                            }
                            Toast.makeText(getApplicationContext(),"Contraseña incorrecta",
                                    Toast.LENGTH_LONG).show();
                            i++;
                        }else {
                            if (response.contains("2")){
                                if (i==3){
                                    EditTextUser.setEnabled(false);
                                    EditTextPassword.setEnabled(false);
                                    Handler handlerTimer = new Handler();
                                    handlerTimer.postDelayed(new Runnable(){
                                        public void run() {
                                            EditTextUser.setEnabled(true);
                                            EditTextPassword.setEnabled(true);
                                            i=0;
                                        }}, 100000);
                                }
                                i++;
                                Toast.makeText(getApplicationContext(),"Usuario incorrecto",
                                        Toast.LENGTH_LONG).show();
                            }else {
                                if (i==3){
                                    EditTextUser.setEnabled(false);
                                    EditTextPassword.setEnabled(false);
                                    Handler handlerTimer = new Handler();
                                    handlerTimer.postDelayed(new Runnable(){
                                        public void run() {
                                            EditTextUser.setEnabled(true);
                                            EditTextPassword.setEnabled(true);
                                            i=0;
                                        }}, 100000);
                                }
                                i++;
                                Toast.makeText(getApplicationContext(),"Usuario y Contraseña incorrectos, intentelo de nuevo",
                                        Toast.LENGTH_LONG).show();
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
                    params.put("username",EditTextUser.getText().toString());
                    params.put("password",EditTextPassword.getText().toString());
                    return params;
                }
            };
        //Volley.newRequestQueue(this).add(stringRequest);
        VolleySingleton.getInstanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);

    }
}
