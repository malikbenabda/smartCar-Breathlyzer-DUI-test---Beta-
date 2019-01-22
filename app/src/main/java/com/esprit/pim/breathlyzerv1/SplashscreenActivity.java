package com.esprit.pim.breathlyzerv1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.ProgressBar;


public class SplashscreenActivity extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT =3000 ;
    private int i = 0;
    ProgressBar progressBar=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen_activity);
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over

                // Start your app main activity
                progressBar = (ProgressBar) findViewById(R.id.activity_splash_progress_bar);
                // Start your loading

                if (i == 0 || i == 10) {
                    //make the progress bar visible
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setMax(150);
                    Intent intent = new Intent(SplashscreenActivity.this, MainActivity.class);

                    startActivity(intent);
                }else if ( i< progressBar.getMax() ) {
                    //Set first progress bar value
                    progressBar.setProgress(i);
                    //Set the second progress bar value
                    progressBar.setSecondaryProgress(i + 10);
                }else {
                    progressBar.setProgress(0);
                    progressBar.setSecondaryProgress(0);
                    i = 0;
                    progressBar.setVisibility(View.GONE);

                }
                i = i + 10;
                Intent i = new Intent(SplashscreenActivity.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);



    }


}
