package cl.ubiobio.chillan.ubicateubb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Psicologia extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psicologia);




        TextView Info2=findViewById(R.id.infoPsicologia2);


        Info2.setText(Html.fromHtml("<b>Hilda Carriel Villalobos </b>: Secretaria.<br><b> Horarios Atención</b>: Lunes a Viernes\n" +
                "        desde 08:15 a 12:30 hrs y de 14:10 a 18:15 hrs.</br><br><b> Correo</b>: hcarriel@ubiobio.cl.</br><p><b>Nelson Zicavo Martinez</b>:Jefe De Carrera.<br><b> Horarios Atención</b>: Agendar cita por correo o hablar con la secretaria" +
                " de la carrera.</br><br><b>Correo</b>: nzicavo@ubiobio.cl.</br></p>"+ "<p>* Cuenta con centro psicosocial gratuito.</p><p>* Posee Salva Escala para estudiantes en silla de ruedas.</p><p>* Servicio de lavandería.</p><p>* Cuenta" +
                " con sala espejo.</p><p>* Cuenta con sala de psicoterapia para niños.</p><p>* Posee 4 box para atención al paciente.</p><p>* Cuenta con sala de estar para el estudio y descanso de sus estudiantes.</p>"));




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
