package cl.ubiobio.chillan.ubicateubb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LaboratoriosCentralesDeComputacion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laboratorios_centrales_de_computacion);
        Button Return = findViewById(R.id.ButtonReturn);
        TextView info1 = findViewById(R.id.info1);


        info1.setText(Html.fromHtml("* Servicios  de WebHosting y Base de datos local y remota" +
                " proveídos por el Sistema Laboratorio de Especialidad. Para más información visita " + "<a href=\"http://arrau.chillan.ubiobio.cl/laboratorio/\">http://arrau.chillan.ubiobio.cl/laboratorio/</a>" + " o hablar" +
                " con el profesor Juan Carlos Figueroa."));
        info1.setMovementMethod(LinkMovementMethod.getInstance());

        Return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        finish();
    }

}
