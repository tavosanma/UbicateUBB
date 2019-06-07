package cl.ubiobio.chillan.ubicateubb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AulasC extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aulas_c);
        String AulasC = getResources().getString(R.string.C1C3);
        String LaboratorioIdiomas = getResources().getString(R.string.LabIdiomas);



        TextView Info1=findViewById(R.id.TextViewInfo1);
        TextView Info2=findViewById(R.id.TextViewInfo2);


        Info1.setText(Html.fromHtml("El edificio C ubicado en frente de las aulas A," +
                " cuenta con 3 salas que van desde la "+AulasC+"."));

        Info2.setText(Html.fromHtml("Además, el edificio C, posee un "+LaboratorioIdiomas
        +" que cuenta con las siguientes funciones:"));



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