package cl.ubiobio.chillan.ubicateubb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LaboratorioDeProcesosDeAlimentos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laboratorio_de_procesos_de_alimentos);
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
