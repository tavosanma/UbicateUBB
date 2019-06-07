package cl.ubiobio.chillan.ubicateubb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AulasB extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aula_b);

        String AulasB = getResources().getString(R.string.B1B9);
        String SalasEstudio = getResources().getString(R.string.SalasEstudio);



        TextView Info1=findViewById(R.id.TextViewInfo1);
        TextView Info2=findViewById(R.id.TextViewInfo2);


        Info1.setText(Html.fromHtml("El edificio B ubicado entre los edificios A y C," +
                " cuenta con 6 salas que van desde la "+AulasB+"."));

        Info2.setText(Html.fromHtml("Además, el edificio B, posee "+ SalasEstudio+" para los estudiantes," +
                        " en la que una de ellas es para uso personal y para adquirirla, se debe pedir" +
                        " las llaves en la Biblioteca Principal."));



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