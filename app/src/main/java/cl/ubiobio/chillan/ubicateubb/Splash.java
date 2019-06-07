package cl.ubiobio.chillan.ubicateubb;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/** Class for show a screen during a few seconds**/
public class Splash extends AppCompatActivity {
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mediaPlayer=MediaPlayer.create(this,R.raw.grabacion);
        mediaPlayer.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                Intent intent = new Intent(Splash.this,MainActivity.class);
                startActivity(intent);
            }
        },3500);


    }


}





