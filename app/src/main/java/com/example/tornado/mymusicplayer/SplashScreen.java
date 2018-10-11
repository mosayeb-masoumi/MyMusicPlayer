package com.example.tornado.mymusicplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SplashScreen extends AppCompatActivity {

    View view;
    Intent intent;

    ImageView imageViewSplash;

    LinearLayout linearLayout;
    Thread SplashThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        imageViewSplash = (ImageView) findViewById(R.id.splash);

        linearLayout = (LinearLayout) findViewById(R.id.lin_lay);

        startAnimations();
    }

    private void startAnimations() {
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.alpha);
        Animation translate = AnimationUtils.loadAnimation(this, R.anim.translate);

        rotate.reset();
        translate.reset();
        linearLayout.clearAnimation();

        imageViewSplash.startAnimation(rotate);

        SplashThread = new Thread(){
            @Override
            public void run() {
                super.run();
                int waited = 0;
                while (waited < 3500) {
                    try {
                        sleep(160);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    waited += 100;
                }

                //intent to next activity
                startActivity(new Intent(SplashScreen.this, MainActivity.class));

                SplashScreen.this.finish();
            }
        };
        SplashThread.start();
    }

}
