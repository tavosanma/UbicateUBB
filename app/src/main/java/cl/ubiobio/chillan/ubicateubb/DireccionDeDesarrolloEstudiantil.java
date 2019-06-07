package cl.ubiobio.chillan.ubicateubb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DireccionDeDesarrolloEstudiantil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direccion_de_desarrollo_estudiantil);
        TextView link=findViewById(R.id.link);
        TextView link2=findViewById(R.id.link2);



        link.setText(Html.fromHtml("Para más información visita " + "<a href=\"http://www.ubiobio.cl/desarrolloestudiantil/\">http://www.ubiobio.cl/desarrolloestudiantil/</a> "));
        link.setMovementMethod(LinkMovementMethod.getInstance());
        link2.setText(Html.fromHtml("Para reserva de horas con trabajadores sociales, visita " + "<a href=\"https://intranet.ubiobio.cl/a1d67401f450d9b860b1a68d9934f367/intranet/?\">https://intranet.ubiobio.cl/a1d67401f450d9b860b1a68d9934f367/intranet/?</a> "));
        link2.setMovementMethod(LinkMovementMethod.getInstance());



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
