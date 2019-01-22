package com.esprit.pim.breathlyzerv1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Cyber info on 12/4/2015.
 */
public class ActivateScreen extends AppCompatActivity {

    private Button callTaxi;
    private Button callFriend;
    private Button takePic;
    private Button tired;
    public String taxinumb;
    public TextView locks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activateactivity);

        Intent intent = getIntent();
        String  lock = intent.getStringExtra(MainActivity.txtkey);

        locks =(TextView) findViewById(R.id.lockstate);
        locks.setText(lock);


        takePic = (Button) findViewById(R.id.camShot);
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, 0);

            }
        });
        tired = (Button) findViewById(R.id.tired);
        tired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ActivateScreen.this, WebviewActivity.class));

            }
        });
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        taxinumb = preferences.getString("taxin", null);

        Log.i("tnum", "tellll*SSSSS ****///***//::::" + taxinumb);
        callTaxi = (Button) findViewById(R.id.calltaxi);
        callTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent callTaxintent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+taxinumb));
                try{
                    startActivity(callTaxintent);
                }

                catch (android.content.ActivityNotFoundException ex){
                    Toast.makeText(getApplicationContext(), "can't make the CALL", Toast.LENGTH_SHORT).show();
                }

            }
        });

        callFriend = (Button) findViewById(R.id.callfriend);
        callFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivateScreen.this, FriendslistActivity.class));
            }

        });
    }
}