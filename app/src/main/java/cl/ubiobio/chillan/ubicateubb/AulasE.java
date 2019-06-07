package cl.ubiobio.chillan.ubicateubb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AulasE extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aulas_e);
        String AulasE = getResources().getString(R.string.E1E4);
        String SalasEstudio = getResources().getString(R.string.TwoSalas);



        TextView Info1=findViewById(R.id.TextViewInfo1);
        TextView Info2=findViewById(R.id.TextViewInfo2);


        Info1.setText(Html.fromHtml("El edificio E ubicado en el sector Marta Colvin o en las cercanías " +
                " de Diseño Gráfico, cuenta con 4 aulas que van desde "+AulasE+
        " y dos "+SalasEstudio+ " en la que una de ellas es de uso personal y se puede" +
                " pedir permiso y las llaves en la Biblioteca Marta Colvin"));

        Info2.setText(Html.fromHtml("Además, el edificio E, posee una sala para auxiliares" +
                " que controlan el funcionamiento de las demás salas o bien recaudan las pertenencias" +
                " perdidas de los estudiantes."));



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