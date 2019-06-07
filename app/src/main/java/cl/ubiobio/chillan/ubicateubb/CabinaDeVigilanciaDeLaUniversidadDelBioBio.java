package cl.ubiobio.chillan.ubicateubb;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class CabinaDeVigilanciaDeLaUniversidadDelBioBio extends AppCompatActivity {

    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cabina_de_vigilancia_de_la_universidad_del_biobio);


        MobileAds.initialize(this, "ca-app-pub-8377806524744957~3684424569");

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-8377806524744957/2404800783");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



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