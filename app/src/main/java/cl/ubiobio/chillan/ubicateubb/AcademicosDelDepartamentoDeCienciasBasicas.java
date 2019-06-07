package cl.ubiobio.chillan.ubicateubb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AcademicosDelDepartamentoDeCienciasBasicas extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academicos_del_departamento_de_ciencias_basicas);

        String PrevencionDeRiesgos=getResources().getString(R.string.PrevencionDeRiesgos);
        String PrimerosAuxilios=getResources().getString(R.string.PrimerosAuxilios);
        String AreaDesarrolloPedagogicoTecnologico=getResources().getString(R.string.AreaDesarrolloPedagogicoTecnologico);
        String DepActFisicaDeportesRecreacion=getResources().getString(R.string.DepActFisicaDeportesRecreacion);
        String JefeDepDeportesRecreacion=getResources().getString(R.string.JefeDepDeportesRecreacion);
        TextView info1= findViewById(R.id.TextViewInfo);
        TextView info12= findViewById(R.id.TextViewInfo2);
        info1.setText(Html.fromHtml("El Departamento De Académicos ubicado al costado del Gimnasio Central" +
                " cuenta con una oficina del "+JefeDepDeportesRecreacion+" a cargo del señor Pedro Campo Del pino, una "+AreaDesarrolloPedagogicoTecnologico+
        " y el "+DepActFisicaDeportesRecreacion+" a cargo de la secretaria Karina Vidal Lira."));
        info12.setText(Html.fromHtml("Tambien el departamento posee una "+ PrimerosAuxilios+" por si ocurre un accidente laboral," +
                " y una "+PrevencionDeRiesgos+ " a cargo del señor Cesar Sandoval Gonzáles."));

        Button Return = findViewById(R.id.ButtonReturn);
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
