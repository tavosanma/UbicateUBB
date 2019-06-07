package cl.ubiobio.chillan.ubicateubb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AulaMagnaFernandoMay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aula_magna_fernando_may);
        String SalaMultiUso=getResources().getString(R.string.SalaMultiUso);
        String Titulaciones=getResources().getString(R.string.Titulaciones);
        String Seminarios=getResources().getString(R.string.Seminarios);
        String Ceremonias=getResources().getString(R.string.Ceremonias);
        String Conferencias=getResources().getString(R.string.Conferencias);

        TextView info1= findViewById(R.id.TextViewInfo);
        TextView info2= findViewById(R.id.TextViewInfo2);
        TextView info3= findViewById(R.id.TextViewInfo3);
        TextView info4= findViewById(R.id.TextViewInfo4);


        info1.setText(Html.fromHtml("El Aula Magna ubicado al frente del Gimnasio Central, " +
                "es la encargada de realizar actividades como, "+ Conferencias+", "+Seminarios+" y "
        +Ceremonias+". Además, aquí se efectúa el proceso de "+Titulaciones+" para los estudiantes quienes hayan egresado."));
        info2.setText(Html.fromHtml("Tambíen en el Aula Magna existe una "+SalaMultiUso+" para los estudiantes" +
                " que la quieran usar, siempre y cuando sea solicitada por el encargado, con " +
                "un horario de 08:30 a 16:30 hrs."));
        info3.setText(Html.fromHtml("Por último, puedes hacer la solicitud de la sala y de otros equipamientos en "
        + "<a href=\"http://ubiobio.cl/aulamagna/\">http://ubiobio.cl/aulamagna/</a> "));
        info3.setMovementMethod(LinkMovementMethod.getInstance());
        info4.setText(Html.fromHtml("Para más inforción visita " + "<a href=\"http://noticias.ubiobio.cl/\">http://noticias.ubiobio.cl/</a> "));
        info4.setMovementMethod(LinkMovementMethod.getInstance());

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