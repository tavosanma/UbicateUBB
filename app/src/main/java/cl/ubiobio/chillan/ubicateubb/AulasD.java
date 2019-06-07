package cl.ubiobio.chillan.ubicateubb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AulasD extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aulas_d);
        String AulasD = getResources().getString(R.string.D1D4);
        String SalaAnatomia = getResources().getString(R.string.SalaAnatomia);



        TextView Info1=findViewById(R.id.TextViewInfo1);
        TextView Info2=findViewById(R.id.TextViewInfo2);


        Info1.setText(Html.fromHtml("El edificio D ubicado en frente de las aulas C o del Gimnasio Multitaller," +
                " cuenta con 4 salas que van desde la "+AulasD+". Tambien, cuenta" +
                " con una sala para auxiliares que son los encargados del funcionamiento de las salas" +
                " y de recaudar las pertenencias perdidas de los alumnos."));

        Info2.setText(Html.fromHtml("Además, el edificio D, posee una "+SalaAnatomia
                +" para el estudio de algunas de las esctructuras del cuerpo humano."));



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