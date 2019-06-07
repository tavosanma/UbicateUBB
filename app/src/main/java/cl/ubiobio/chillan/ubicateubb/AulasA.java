package cl.ubiobio.chillan.ubicateubb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AulasA extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aulas);
        String Aulas = getResources().getString(R.string.Aulas);
        String Fotocopia = getResources().getString(R.string.Fotocopia);
        String anillado = getResources().getString(R.string.anillado);



        TextView Info1=findViewById(R.id.TextViewInfo1);
        TextView Info2=findViewById(R.id.TextViewInfo2);


        Info1.setText(Html.fromHtml("Las aulas A ubicadas al frente de las aulas B o al frete del Laboratorio" +
                " De Procesos De Alimentos, cuenta con 6 aulas, desde "+Aulas+" y una sala" +
                " para auxuliares los cuales se encargan del funcionamiento de las salas o bien, " +
                " recaudan las cosas perdidas de los estudiantes."));

        Info2.setText(Html.fromHtml("También, el edificio A, cuenta con  servicios de "+Fotocopia+" y "+anillado+
        " la cual posee la siguiente información:"));



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
