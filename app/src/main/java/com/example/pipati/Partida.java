package com.example.pipati;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Random;


public class Partida extends AppCompatActivity {

    SQLiteDatabase db;
    ImageView imgP1, imgP2;
    ImageButton btnPiedra, btnPapel, btnTijeras;
    TextView tvScoreP1, tvScoreP2, nameP1, nameP2;
    SharedPreferences sharedPreferences;
    int player1, player2, turno, p1Score, p2Score, nGames = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtenemos las preferencias
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Partida.this);
                if (key.equals("language")) {
                    String language = preferences.getString("language", "es");
                    Locale locale = new Locale(language);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.setLocale(locale);
                    getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                }
                recreate();
            }
        });

        String language = sharedPreferences.getString("language", "es");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        setContentView(R.layout.activity_partida);

        // Inicializacion de las variables

        DBManager dbManager = new DBManager(this);
        db = dbManager.getWritableDatabase();

        imgP1 = (ImageView) findViewById(R.id.imagenJugador1);
        imgP2 = (ImageView) findViewById(R.id.imagenJugador2);

        btnPiedra = (ImageButton) findViewById(R.id.btnPiedra);
        btnPapel = (ImageButton) findViewById(R.id.btnPapel);
        btnTijeras = (ImageButton) findViewById(R.id.btnTijeras);

        tvScoreP1 = (TextView) findViewById(R.id.scoreJugador1);
        tvScoreP2 = (TextView) findViewById(R.id.scoreJugador2);
        nameP1 = (TextView) findViewById(R.id.nomJugador1);
        nameP2 = (TextView) findViewById(R.id.nomJugador2);

        tvScoreP1.setText(getString(R.string.TextViewPuntuacion) + p1Score);
        tvScoreP2.setText(getString(R.string.TextViewPuntuacion) + p2Score);

        nameP1.setText(getIntent().getStringExtra("user"));

        Random random = new Random();

        // Boton para seleccionar la piedra
        btnPiedra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();

                // Si escogimos el modo clasico la opcion del rival se escogerá aleatoriamente
                if("Clasico".equals(intent.getStringExtra("ModoJuego"))) {
                    imgP1.setImageResource(R.drawable.piedra);
                    player1 = 1;
                    player2 = random.nextInt(2) + 1;
                    int result = checkResults(player1, player2);
                    results(result);

                // Si escogimos el modo retos la opcion del rival la escogera un segundo jugador
                } else {
                    if(turno == 0) {
                        player1 = 1;
                        turno++;
                    } else {
                        player2 = 1;
                        switch (player1){
                            case 1:
                                imgP1.setImageResource(R.drawable.piedra);
                                break;
                            case 2:
                                imgP1.setImageResource(R.drawable.papel);
                                break;
                            case 3:
                                imgP1.setImageResource(R.drawable.tijeras);
                                break;
                        }
                        int result = checkResults(player1, player2);
                        turno = 0;
                        results(result);
                    }
                }
            }
        });

        // Boton para seleccionar papel
        btnPapel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();

                // Si escogimos el modo clasico la opcion del rival se escogerá aleatoriamente
                if("Clasico".equals(intent.getStringExtra("ModoJuego"))) {
                    imgP1.setImageResource(R.drawable.papel);
                    player1 = 2;
                    player2 = random.nextInt(2) + 1;
                    int result = checkResults(player1, player2);
                    results(result);

                // Si escogimos el modo retos la opcion del rival la escogera un segundo jugador
                } else {
                    if(turno == 0) {
                        player1 = 2;
                        turno++;
                    } else {
                        player2 = 2;
                        switch (player1){
                            case 1:
                                imgP1.setImageResource(R.drawable.piedra);
                                break;
                            case 2:
                                imgP1.setImageResource(R.drawable.papel);
                                break;
                            case 3:
                                imgP1.setImageResource(R.drawable.tijeras);
                                break;
                        }
                        int result = checkResults(player1, player2);
                        turno = 0;
                        results(result);
                    }
                }
            }
        });
        btnTijeras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();

                // Si escogimos el modo clasico la opcion del rival se escogerá aleatoriamente
                if("Clasico".equals(intent.getStringExtra("ModoJuego"))) {
                    imgP1.setImageResource(R.drawable.tijeras);
                    player1 = 3;
                    player2 = random.nextInt(2) + 1;
                    int result = checkResults(player1, player2);
                    results(result);

                // Si escogimos el modo retos la opcion del rival la escogera un segundo jugador
                } else {
                    if(turno == 0) {
                        player1 = 3;
                        turno++;
                    } else {
                        player2 = 3;
                        switch (player1){
                            case 1:
                                imgP1.setImageResource(R.drawable.piedra);
                                break;
                            case 2:
                                imgP1.setImageResource(R.drawable.papel);
                                break;
                            case 3:
                                imgP1.setImageResource(R.drawable.tijeras);
                                break;
                        }
                        int result = checkResults(player1, player2);
                        turno = 0;
                        results(result);
                    }
                }
            }
        });
    }

    // Se gestiona el resultado de la ronda comprobando la opcion que ha escogida cada jugador
    // Los resultados posibles son: Perder(0), Ganar(1) y Empate(2)
    public int checkResults(int player1, int player2) {
        int result = 0;
        switch(player1) {
            case 1:
                // El jugador ha elegido piedra
                switch (player2) {
                    case 1:
                        imgP2.setImageResource(R.drawable.piedra);
                        return result = 2;
                    case 2:
                        imgP2.setImageResource(R.drawable.papel);
                        return result = 0;
                    case 3:
                        imgP2.setImageResource(R.drawable.tijeras);
                        return result = 1;

                }
            case 2:
                // El jugador ha elegido papel
                switch (player2) {
                    case 1:
                        imgP2.setImageResource(R.drawable.piedra);
                        return result = 1;

                    case 2:
                        imgP2.setImageResource(R.drawable.papel);
                        return result = 2;

                    case 3:
                        imgP2.setImageResource(R.drawable.tijeras);
                        return result = 0;
                }
            case 3:
                // El jugador ha elegido tijera
                switch (player2) {
                    case 1:
                        imgP2.setImageResource(R.drawable.piedra);
                        return result = 0;
                    case 2:
                        imgP2.setImageResource(R.drawable.papel);
                        return result = 1;
                    case 3:
                        imgP2.setImageResource(R.drawable.tijeras);
                        return result = 2;
                }
        }
        return result;
    }

    // Se gestionan los cambios en la interfaz grafica en funcion del resultado de la ronda
    public void results(int result){
        switch (result){
            case 0:
                nGames++;
                p2Score++;
                tvScoreP2.setText(getString(R.string.TextViewPuntuacion) + p2Score);
                try {
                    endGame();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 1:
                nGames++;
                p1Score++;
                tvScoreP1.setText(getString(R.string.TextViewPuntuacion) + p1Score);
                try {
                    endGame();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }                break;
            case 2:
                Toast.makeText(Partida.this, "Empate", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // Se gestiona lo ocurre al final de la partida
    // Se guarda el resultado de la partida y si se esta en el modo retos se muestra el reto a los jugadores
    public void endGame() throws IOException {
        if (nGames == 3 || p1Score == 2 || p2Score == 2){
            String query = "INSERT INTO games(player1, scoreP1, player2, ScoreP2) VALUES(?, ?, ?, ?)";
            SQLiteStatement statement = db.compileStatement(query);
            statement.bindString(1, nameP1.getText().toString());
            statement.bindString(2, Integer.toString(p1Score));
            statement.bindString(3, nameP2.getText().toString());
            statement.bindString(4, Integer.toString(p2Score));
            long result = statement.executeInsert();
            if (result != -1) {
                Toast.makeText(Partida.this, "Fin de partida", Toast.LENGTH_SHORT).show();
                // Creas el dialogo con un reto aleatorio de retos.txt
                AlertDialog.Builder builder = new AlertDialog.Builder(Partida.this).setCancelable(false);
                TextView textView = new TextView(this);
                String texto = readRandomLine();

                Intent intentExtras = getIntent();
                if (intentExtras.getStringExtra("ModoJuego").equals("Clasico")) {
                    if (p1Score>p2Score){
                        textView.setText("Felicidades " + nameP1.getText().toString() + ", has ganado");
                    } else {
                        textView.setText("Felicidades " + nameP2.getText().toString() + " has ganado");
                    }
                } else {
                    if (p1Score>p2Score){
                        textView.setText("Felicidades " + nameP1.getText().toString() + ", has ganado.\n"
                                + nameP2.getText().toString() + " ahora deberas cumplir el siguiente reto:\n\n"
                                + texto);
                    } else {
                        textView.setText("Felicidades " + nameP2.getText().toString() + ", has ganado.\n"
                                + nameP1.getText().toString() + " ahora deberas cumplir el siguiente reto:\n\n"
                                + texto);
                    }
                }
                builder.setView(textView);
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Partida.this, ModoJuego.class);
                        intent.putExtra("user", getIntent().getStringExtra("user"));
                        startActivity(intent);
                        finish();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(Partida.this, "Error al guardar el resultado", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Partida.this, ModoJuego.class);
                intent.putExtra("user", getIntent().getStringExtra("user"));
                startActivity(intent);
                finish();
            }
        }
    }

    // Funcion que lee una linea aleatoria del fichero de texto retos.txt
    private String readRandomLine(){
        String randomLine = "";
        try {
            InputStream inputStream = getAssets().open("retos.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            int numLines = 0;
            while (reader.readLine() != null) {
                numLines++;
            }

            Random random = new Random();
            int randomLineNum = random.nextInt(numLines);

            inputStream.reset();

            for (int i = 0; i < randomLineNum; i++) {
                randomLine = reader.readLine();
            }
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return randomLine;
    }

    // Se guardan los elementos de la partida para que al rotar la pantalla no se pierdan los datos
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        TextView titulo = (TextView) findViewById(R.id.titulo);

        int imagenP1 = player1;
        int imagenP2 = player2;

        outState.putString("Title", titulo.getText().toString());
        outState.putString("ScoreName", getString(R.string.TextViewPuntuacion));

        outState.putInt("image1", imagenP1);
        outState.putInt("image2", imagenP2);

        outState.putInt("Score1", p1Score);
        outState.putInt("Score2", p2Score);

        outState.putString("Name1", nameP1.getText().toString());
        outState.putString("Name2", nameP2.getText().toString());
    }

    // Se recuperan los elementos guardados
    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        TextView titulo = (TextView) findViewById(R.id.titulo);

        titulo.setText(outState.getString("Title"));

        int imagenP1 = outState.getInt("image1");;
        int imagenP2 = outState.getInt("image2");;

        switch (imagenP1){
            case 1:
                imgP1.setImageResource(R.drawable.piedra);
                break;
            case 2:
                imgP1.setImageResource(R.drawable.papel);
                break;
            case 3:
                imgP1.setImageResource(R.drawable.tijeras);
                break;
        }

        switch (imagenP2){
            case 1:
                imgP2.setImageResource(R.drawable.piedra);
                break;
            case 2:
                imgP2.setImageResource(R.drawable.papel);
                break;
            case 3:
                imgP2.setImageResource(R.drawable.tijeras);
                break;
        }

        p1Score = outState.getInt("Score1");
        p2Score = outState.getInt("Score2");

        tvScoreP1.setText(outState.getString("ScoreName") + Integer.toString(outState.getInt("Score1")));
        tvScoreP2.setText(outState.getString("ScoreName") + Integer.toString(outState.getInt("Score2")));

        nameP1.setText(outState.getString("Name1"));
        nameP2.setText(outState.getString("Name2"));
    }
}