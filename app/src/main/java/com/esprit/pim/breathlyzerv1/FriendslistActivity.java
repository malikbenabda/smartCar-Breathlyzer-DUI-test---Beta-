package com.esprit.pim.breathlyzerv1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.esprit.pim.breathlyzerv1.Adapters.WhitelistAdapter;
import com.esprit.pim.breathlyzerv1.Entity.Contact;
import com.esprit.pim.breathlyzerv1.R;
import com.esprit.pim.breathlyzerv1.Sqlite.ContactBDD;

import java.util.List;

import co.dift.ui.SwipeToAction;

/**
 * Created by zaineb on 17/01/16.
 */
public class FriendslistActivity extends AppCompatActivity {
    RecyclerView list;
    ContactBDD contBDD;
    WhitelistAdapter wladapter;
    public List<Contact> listItem;
    SwipeToAction swipeToAction;
String myCurrentLocation;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendslist_activity);
        list = (RecyclerView) findViewById(R.id.listvieww);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(true);
        contBDD = new ContactBDD(getApplicationContext());
        contBDD.open();
        listItem = contBDD.selectAll() ;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String latText = prefs.getString("latitude", null);
        String lonText = prefs.getString("longitude", null);
        myCurrentLocation = "https://www.google.co.id/maps/@"+latText+","+lonText;
        list.setAdapter(new WhitelistAdapter(this, listItem));
        displaySnackbar("Long click to make a call ", null, null);
        displaySnackbar( "Click to send a msg ", null, null);
        swipeToAction = new SwipeToAction(list, new SwipeToAction.SwipeListener<Contact>() {
            @Override
            public boolean swipeLeft(final Contact itemData) {

                return true;
            }

            @Override
            public boolean swipeRight(Contact itemData) {
                return true;
            }

            @Override
            public void onClick(Contact itemData) {
                displaySnackbar(myCurrentLocation, null, null);
                Log.i("list", "msg sent");
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(itemData.getNumber(), null, "Hello My Friend, could you come and collect me? i'm drunk and i can't drive. here is my location  "+myCurrentLocation + "    Thank you",null,null);
                Log.i("list", "msg sent"+myCurrentLocation);
                displaySnackbar("Your msg is sent successfully  ", null, null);

            }

            @Override
            public void onLongClick(Contact itemData) {
                Log.i("list","about to call");
             String phoneNo =  itemData.getNumber();
                        String phoneNumber = "tel:" + phoneNo;
                 Intent act = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
                try{
                    startActivity(act);
                }

                catch (android.content.ActivityNotFoundException ex){
                    Toast.makeText(getApplicationContext(), "can't make the CALL", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }


    private void displaySnackbar(String text, String actionName, View.OnClickListener action) {
        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG)
                .setAction(actionName, action);

        View v = snack.getView();
        v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.BLACK);

        snack.show();
    }
}

