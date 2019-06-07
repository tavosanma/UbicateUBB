package cl.ubiobio.chillan.ubicateubb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CasinoUBB extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_casino_ubb);
        TextView Info2= findViewById(R.id.TextViewInfo2);



        Info2.setText(Html.fromHtml("Para más información sobre" +
                " ésta y otras becas internas, acercarse a Dirección De Desarrollo estudiantil" +
                " o bien visita " + "<a href=\"http://ubiobio.cl/desarrolloestudiantil/\">http://ubiobio.cl/desarrolloestudiantil/"));
        Info2.setMovementMethod(LinkMovementMethod.getInstance());

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
