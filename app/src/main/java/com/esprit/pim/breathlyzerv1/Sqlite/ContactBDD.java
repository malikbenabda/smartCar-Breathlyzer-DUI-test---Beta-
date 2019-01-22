package com.esprit.pim.breathlyzerv1.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.esprit.pim.breathlyzerv1.Entity.Contact;


import java.util.ArrayList;
import java.util.List;

public class ContactBDD {

    private static final int VERSION_BDD = 1;
    private static final String NAME_BDD = "contacts.db";

    private SQLiteDatabase bdd;

    private DBHelper dbHelper;

    public ContactBDD(Context context) {
        super();
        dbHelper = new DBHelper(context, NAME_BDD, null, VERSION_BDD);
    }

    public void open() {
        //on ouvre la BDD en �criture
        bdd = dbHelper.getWritableDatabase();
    }

    public void close() {
        //on ferme l'acc�s � la BDD
        bdd.close();
    }

    public SQLiteDatabase getBDD() {
        return bdd;
    }

    public long insertTop(Contact partis) {
        Cursor cursor = bdd.rawQuery("select max(" + DBHelper.ID_CONTACT + ") from " + DBHelper.TABLE_CONTACT, null);
        if (cursor.moveToFirst())
            partis.setId(cursor.getInt(0) + 1);
        else partis.setId(1);

        //TODO Add Article to data base
        ContentValues values = new ContentValues();
        values.put(DBHelper.ID_CONTACT, partis.getId());
        values.put(DBHelper.NAME_CONTACT, partis.getName());
        values.put(DBHelper.NUMBER_CONTACT, partis.getNumber());
        //values.put(DBHelper.LOGO_ARTICLE, partis.getLogo());
        return bdd.insert(DBHelper.TABLE_CONTACT, null, values);
    }

    public int removeAllArticles() {
        //TODO Remove all Table
        return bdd.delete(DBHelper.TABLE_CONTACT, null, null);
    }

    public int removeArticle(int index) {
        //TODO Remove one Article
        return bdd.delete(DBHelper.TABLE_CONTACT, "`" + DBHelper.ID_CONTACT+ "`=? and " + DBHelper.NAME_CONTACT, new String[]{String.valueOf(index)});
    }

    public List<Contact> selectAll() {
        List<Contact> list = new ArrayList<Contact>();

        //TODO Get list of Article
        Cursor cursor = this.bdd.query(DBHelper.TABLE_CONTACT, new String[]{"*"},
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Contact article = new Contact();
                article.setId(cursor.getInt(0));
                article.setName(cursor.getString(1));
                article.setNumber(cursor.getString(2));
               // article.setLogo(cursor.getBlob(3));
                list.add(article);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }
}
