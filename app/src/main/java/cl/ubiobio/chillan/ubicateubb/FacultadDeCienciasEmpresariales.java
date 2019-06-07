package cl.ubiobio.chillan.ubicateubb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FacultadDeCienciasEmpresariales extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facultad_de_ciencias_empresariales);





        TextView Info2=findViewById(R.id.TextViewInfo2);
        TextView Info1=findViewById(R.id.TextViewInfo1);
        TextView Info3=findViewById(R.id.TextViewInfo3);
        TextView Info4=findViewById(R.id.TextViewInfo4);


        Info2.setText(Html.fromHtml("<b>Yaqueline Badillo Castro</b>: Secretaria.<br><b> Horarios Atención</b>: Lunes a Viernes\n" +
                "        desde 08:15 a 12:30 hrs y de 14:10 a 18:15 hrs.</br><br><b> Correo</b>: secretaria-ici-chi@ubiobio.cl.</br><p><b>Marlene Muñoz</b>: Jefa De Carrera.<br><b> Horarios Atención</b>: Las atenciones van desde</br><br>Lunes: 08:30 a 10:30 hrs.</br><br>Martes: 11:00 a 12:00 hrs.</br><br>Jueves: 08:30 a 10:00 hrs.</br><br>Viernes: 08:30" +
                "a 10:30 hrs.</br><br><b>Correo</b>: marlene@ubiobio.cl.</br></p>"));
        Info1.setText(Html.fromHtml("<p>La Facultad De Ciencias Empresariales ubicado en la entrada de la Universidad, cuenta con 2 departamentos en Chillán: <b>Gestión Empresarial</b> y <b>Ciencias De La Computación Y Tecnología De La Información</b> y 3 escuelas: <b>Contador Público Auditor</b>, <b>Ingeniería Comercial</b> e <b>Ingeniería Civil Informática</b>.</p>La Facultad se preocupa también de desarrollar la capacidad emprendedora, de sus estudiantes y de enriquecer su proceso de formación con una visión humanista\n" +
                "        e integradora de su futuro quehacer profesional."));

        Info3.setText(Html.fromHtml("<b>Paola Monroy González</b>: Secretaria.<br><b> Horarios Atención</b>: Lunes a Viernes\n" +
                "        desde 08:15 a 12:30 hrs y de 14:10 a 18:15 hrs.</br><br><b> Correo</b>: secretariacpach@ubiobio.cl.</br><p><b>Cecilia Gallegos Muñoz</b>: Jefa De Carrera.<br><b> Horarios Atención</b>: Las atenciones van desde</br><br>Lunes: 14:10 a 16:20 hrs.</br><br>Martes: 11:50 a 12:30 hrs y de 17:10 a 18:30 hrs.</br><br>Miercoles: 11:10 a 12:30 hrs.</br><br>jueves: 14:10" +
                " a 10:30 hrs.</br><br><b>Correo</b>: cecilia@ubiobio.cl.</br></p>"));
        Info4.setText(Html.fromHtml("<b>Luz Silva Rivera</b>: Secretaria.<br><b> Horarios Atención</b>: Lunes a Viernes\n" +
                "        desde 08:15 a 12:30 hrs y de 14:10 a 18:15 hrs.</br><br><b> Correo</b>: secretariaicoch@ubiobio.cl.</br><p><b>Alvaro Acuña</b>: Jefe De Carrera.<br><b> Horarios Atención</b>: Agendar cita" +
                " por correo o bien consultar a la secretaria de la carrera.</br><br><b>Correo</b>: alacuna@ubiobio.cl</br></p>"));



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