package cl.ubiobio.chillan.ubicateubb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CanchasDeTenis extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canchas_de_tenis);

        TextView info2= findViewById(R.id.TextViewInfo2);



        info2.setText(Html.fromHtml("Para más información sobre" +
                " los horarios y otras ramas deportivas, visita " + "<a href=\"http://destudiantil.ubiobio.cl/dde_chillan/?page_id=1796\">http://destudiantil.ubiobio.cl/dde_chillan/?page_id=1796/</a> "));
        info2.setMovementMethod(LinkMovementMethod.getInstance());

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
