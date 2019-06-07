package cl.ubiobio.chillan.ubicateubb;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**Capturing ids of components of a layout**/
        TextView TextViewInformation = findViewById(R.id.textInfo);
        Button ButtonStart = findViewById(R.id.botonComenzar);
        TextView TextViewLoginAdmin = findViewById(R.id.TextViewLoginAdmin);
        TextViewLoginAdmin.setText(Html.fromHtml("<u>Login Administrador</u>"));
        TextViewLoginAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isNetWorkAvailable()){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle("Error de conexión!!");
                    alertDialog.setMessage("Intenta conectarte a internet si quieres entrar como administrador");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alert =alertDialog.create();
                    alert.show();
                }else {
                    Intent intent = new Intent(MainActivity.this,LoginAdmin.class);
                    startActivity(intent);
                }

            }
        });

        /** if  there is no connectivity to red, show a message**/
        if (!isNetWorkAvailable()){
            TextViewInformation.setVisibility(View.VISIBLE);
        }
        /** Button to change of activity**/
        ButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isNetWorkAvailable()){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle("Error de conexión!!");
                    alertDialog.setMessage("Intenta conectarte a internet si quieres acceder al mapa");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alert =alertDialog.create();
                    alert.show();
                }else {
                    Intent intent = new Intent(MainActivity.this,MapaUniversidad.class);
                    startActivity(intent);
                }

            }
        });
    }

    /** Show other activity o interfaces when is pressed the button back from phone**/
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainActivity.this,Splash.class);
        startActivity(intent);

    }

    /** asking about network connectivity**/
    private boolean isNetWorkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo !=null;
    }


}
