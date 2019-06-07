package cl.ubiobio.chillan.ubicateubb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CasaPatronal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_casa_patronal);
        TextView info3= findViewById(R.id.TextViewInfo3);



        info3.setText(Html.fromHtml("Para más información sobre" +
                "  " + "<a href=\"http://werken.ubiobio.cl/\">http://werken.ubiobio.cl/</a> "));
        info3.setMovementMethod(LinkMovementMethod.getInstance());

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
