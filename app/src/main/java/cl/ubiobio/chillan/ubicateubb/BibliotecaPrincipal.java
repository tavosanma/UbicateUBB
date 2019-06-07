package cl.ubiobio.chillan.ubicateubb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BibliotecaPrincipal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biblioteca_principal);
        String BiblioInfo1 = getResources().getString(R.string.BibliotecaInfo1);
        String Piesdi = getResources().getString(R.string.Piesdi);




        TextView Info1=findViewById(R.id.TextViewInfo1);
        TextView Info2=findViewById(R.id.TextViewInfo2);
        TextView Info5=findViewById(R.id.TextViewInfo5);
        TextView Info7=findViewById(R.id.TextViewInfo7);



        Info1.setText(Html.fromHtml(BiblioInfo1));
        Info5.setText(Html.fromHtml("* La Biblioteca pone a disposición una sala para estudiantes en situación de discapacidad ("+Piesdi+")."));
        Info7.setText(Html.fromHtml("Para más información visita " + "<a href=\"http://werken.ubiobio.cl/\">http://werken.ubiobio.cl/</a> "));
        Info7.setMovementMethod(LinkMovementMethod.getInstance());
        Info2.setText(Html.fromHtml("A continuación se presenta a los encargados y los horarios de atención de la Biblioteca Principal:<br>" +
                "                <p><b>Mónica Isabel Erazo Alcayala:</b> Jefa Biblioteca.</br><br><b>Correo:</b> merazo@ubiobio.cl.</br></p><br><b>Maritza Alejandra Leiva San Martín:</b> Secretaria.</br><br><b>Correo:</b> mleiva@ubiobio.cl.</br>"));





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