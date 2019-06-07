package cl.ubiobio.chillan.ubicateubb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FacultadDeCienciasDeLaSaludYDeLosAlimentos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facultad_de_ciencias_de_la_salud_yde_los_alimentos);
        TextView Info1=findViewById(R.id.TextViewInfo1);
        TextView Info2=findViewById(R.id.TextViewInfo2);
        TextView Info3=findViewById(R.id.TextViewInfo3);
        TextView Info4=findViewById(R.id.TextViewInfo4);
        TextView Info5=findViewById(R.id.TextViewInfo5);



        Info1.setText(Html.fromHtml("Ubicada en la Sede Chillán y estructurada en los Departamentos de <b>Ingeniería En Alimentos</b>;<b>Nutrición Y Salud\n" +
                "    Pública</b>;<b>Enfermería</b> y <b>Ciencias De La Rehabilitación En Salud</b>, y en las escuelas de <b>Ingeniería en Alimentos</b>, <b>Nutrición y Dietética</b>, <b>Enfermería</b> y\n" +
                "    <b>Fonoaudiología</b>, esta Facultad orienta a su desarrollo hacia la ciencia y la tecnología en el ámbito de los alimentos, la nutrición aplicada y\n" +
                "    la salud comunitaria."));


        Info2.setText(Html.fromHtml("<b> Pamela Montoya Cáceres</b>: Jefa de carrera.<br><b> Horarios Atención</b>: Agendar cita por correo o " +
                "hablar con la secretaria de la carrera.</br><br><b> Correo</b>: pmontoya@ubiobio.cl</br><p><b>Soledad Salazar Coñomil</b>: Secretaria<br><b> Horarios Atención</b>: Las atenciones son de lunes a viernes desde 08:15 hasta 12:30 hrs y de 14:10 hasta 18:13 hrs.</br><br><b>Correo</b>: ssalazar@ubiobio.cl</br></p>"));

        Info3.setText(Html.fromHtml("<b> José Miguel Bastias</b>: Jefe de carrera.<br><b> Horarios Atención</b>: Agendar cita por correo o " +
                "hablar con la secretaria de la carrera.</br><br><b> Correo</b>: jobastias@ubiobio.cl</br><p><b>Romina Venegas Plaza</b>: Secretaria<br><b> Horarios Atención</b>: Las atenciones son de lunes a viernes desde 08:15 hasta 12:30 hrs y de 14:10 hasta 18:13 hrs.</br><br><b>Correo</b>: rovenegas@ubiobio.cl</br></p>"));

        Info4.setText(Html.fromHtml("<b> Patricio Oliva Moresco</b>: Jefe de carrera.<br><b> Horarios Atención</b>: Agendar cita por correo o " +
                "hablar con la secretaria de la carrera.</br><br><b> Correo</b>: poliva@ubiobio.cl</br><p><b>Zusana Gutierrez Riquelme</b>: Secretaria<br><b> Horarios Atención</b>: Las atenciones son de lunes a viernes desde 08:15 hasta 12:30 hrs y de 14:10 hasta 18:13 hrs.</br><br><b>Correo</b>: zgutierrez@ubiobio.cl</br></p>"));

        Info5.setText(Html.fromHtml("<b> Virginia Garcia Flores</b>: Jefa de carrera.<br><b> Horarios Atención</b>: Agendar cita por correo o " +
                "hablar con la secretaria de la carrera.</br><br><b> Correo</b>: vgarcia@ubiobio.cl</br><p><b>Maritza Celis Riquelme</b>: Secretaria<br><b> Horarios Atención</b>: Las atenciones son de lunes a viernes desde 08:15 hasta 12:30 hrs y de 14:10 hasta 18:13 hrs.</br><br><b>Correo</b>: mcelis@ubiobio.cl</br></p>"));




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