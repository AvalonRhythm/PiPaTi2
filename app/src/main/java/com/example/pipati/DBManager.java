package com.example.pipati;

/**
 * Extraido de: https://developer.android.com/training/data-storage/sqlite?hl=es-419
 * Modificado por: Hugo Robles, para cambiar el nomobre de algunos elementos.
 **/

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public class DBManager extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "PiPaTiDB.db";
    private static int DATABASE_VERSION = 1;

    public DBManager(Context context) {super(context, DATABASE_NAME, null, DATABASE_VERSION);}


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crea la tabla "users" y "games"
        db.execSQL("CREATE TABLE users (_id INTEGER PRIMARY KEY, nomUser text, pass text)");
        db.execSQL("CREATE TABLE games (_id INTEGER PRIMARY KEY, player1 text, scoreP1 int, player2 text, scoreP2 int)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Actualiza la version de la base de datos
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS games");
        onCreate(db);
    }
}
