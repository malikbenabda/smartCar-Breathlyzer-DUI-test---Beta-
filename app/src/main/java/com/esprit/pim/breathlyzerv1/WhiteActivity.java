package com.esprit.pim.breathlyzerv1;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


import com.esprit.pim.breathlyzerv1.Adapters.WhitelistAdapter;
import com.esprit.pim.breathlyzerv1.Entity.Contact;
import com.esprit.pim.breathlyzerv1.Sqlite.ContactBDD;


import java.util.List;

import co.dift.ui.SwipeToAction;


public class WhiteActivity extends AppCompatActivity {

    RecyclerView list;
    ContactBDD contBDD;
    WhitelistAdapter wladapter;
    public  List<Contact> listItem;
    SwipeToAction swipeToAction;
    private static final int PICK_CONTACT = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white);
        list = (RecyclerView) findViewById(R.id.listview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(true);
        contBDD = new ContactBDD(getApplicationContext());
        contBDD.open();
        ((FloatingActionButton)findViewById(R.id.add_contact)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // user BoD suggests using Intent.ACTION_PICK instead of .ACTION_GET_CONTENT to avoid the chooser
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);

                startActivityForResult(intent, PICK_CONTACT);
            }
        });
        listItem = contBDD.selectAll() ;

        list.setAdapter(new WhitelistAdapter(this, listItem));
       // list.setLayoutManager(new LinearLayoutManager(this));
        swipeToAction = new SwipeToAction(list, new SwipeToAction.SwipeListener<Contact>() {
            @Override
            public boolean swipeLeft( Contact itemData) {
                return true;
            }

            @Override
            public boolean swipeRight(Contact itemData) {
                return true;
            }

            @Override
            public void onClick(Contact itemData) {
                displaySnackbar(itemData.getName() + " clicked", null, null);

            }

            @Override
            public void onLongClick(final Contact itemData) {
                displaySnackbar(itemData.getName() + " clicked", null, null);
                final int pos = removeContact(itemData);
                displaySnackbar(itemData.getName() + "is removed", "Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addContact(pos, itemData);
                    }
                });

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
    private int removeContact(Contact ct) {
        int pos = listItem.indexOf(ct);
        listItem.remove(ct);
        wladapter.notifyItemRemoved(pos);
        return pos;
    }

    private void addContact(int pos,Contact ct) {
        listItem.add(pos, ct);
        wladapter.notifyItemInserted(pos);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Contact ct = new Contact();
        contBDD.open();
        if (data != null) {
            Uri uri = data.getData();

            if (uri != null) {
                Cursor c = null;
                try {
                    c = getContentResolver().query(uri, new String[]{
                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.Contacts.DISPLAY_NAME},
                            null, null, null);

                    if (c != null && c.moveToFirst()) {
                       String number = c.getString(0);
                        String name = c.getString(1);


                        ct.setName(name);
                        ct.setNumber(number);

                        contBDD.insertTop(ct);
                        listItem = contBDD.selectAll() ;



                    }
                } finally {
                    if (c != null) {
                        c.close();

                    }
                }

        list.setAdapter(new WhitelistAdapter(this, listItem));
                contBDD.close();
//                Set<Contact> tasksSet = new HashSet<Contact>(listItem);
//                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
//                        .edit()
//                        .putStringSet("contacts_list",tasksSet )
//                        .commit();
            }
        }

    }



}
