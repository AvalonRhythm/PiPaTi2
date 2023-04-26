package com.example.pipati;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ModoJuego extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    Button btnClassicMode, btnChallengeMode;
    ImageButton btnBack;
    ImageView fotoPerfil;
    SharedPreferences sharedPreferences;
    StringRequest stringRequest;
    RequestQueue request;
    Bitmap imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_juego);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        btnClassicMode = (Button) findViewById(R.id.btnModoClasico);
        btnChallengeMode = (Button) findViewById(R.id.btnModoRetos);

        btnBack = (ImageButton) findViewById(R.id.modoJuegoFlechaAtras);
        fotoPerfil = (ImageView) findViewById(R.id.modoJuegoImagenPerfil);

        String url = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/hrobles002/WEB/descargarImagen.php";
        request = Volley.newRequestQueue(getApplicationContext());
        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("fail")) {
                    Toast.makeText(getApplicationContext(), "Fallo de PHP", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                    Log.d("RESPUESTA_DESCARGAR_IMAGEN", response);

                    try {
                        JSONArray jsona = new JSONArray(response);

                        for (int i = 0; i < jsona.length(); i++) {
                            JSONObject json = jsona.getJSONObject(i);

                            byte[] imagenb = Base64.decode(json.getString("imagen"), Base64.DEFAULT);
                            Bitmap bitmapimagen = BitmapFactory.decodeByteArray(imagenb, 0, imagenb.length);

                            Glide.with(getApplicationContext())
                                    .load(bitmapimagen) // bitmap es el Bitmap que se desea mostrar
                                    .circleCrop() // redondear la imagen
                                    .into(fotoPerfil);
                        }
                    } catch (Exception e) {
                        //
                    }
                    request.cancelAll("descargarImagenPerfil");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //si ha habido algun error con la solicitud
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //se pasan todos los parametros necesarios en la solicitud
                HashMap<String, String> parametros = new HashMap<String, String>();
                parametros.put("username", getIntent().getStringExtra("user"));
                return parametros;
            }
        };

        stringRequest.setTag("descargarImagenPerfil");
        request.add(stringRequest);

        // Se guarda el modo de juego seleccionado, ya que el juego sera diferente en funcion del mismo
        btnClassicMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ModoJuego.this, Partida.class);
                intent.putExtra("ModoJuego", "Clasico");
                intent.putExtra("user", getIntent().getStringExtra("user"));
                startActivity(intent);
                finish();
            }
        });
        btnChallengeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ModoJuego.this, Partida.class);
                intent.putExtra("ModoJuego", "Retos");
                intent.putExtra("user", getIntent().getStringExtra("user"));
                startActivity(intent);
                finish();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ModoJuego.this, MenuPrincipal.class);
                intent.putExtra("user", getIntent().getStringExtra("user"));
                startActivity(intent);
                finish();
            }
        });

        loadPreferences();
    }
    private void loadPreferences(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ModoJuego.this);
        String colorValue = preferences.getString("button_color", "#0000FF"); // El valor por defecto es azul
        btnClassicMode.setBackgroundColor(Color.parseColor(colorValue));
        btnChallengeMode.setBackgroundColor(Color.parseColor(colorValue));

        String language = sharedPreferences.getString("language", "en");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("button_color")) {
            // Obtener el nuevo color de los botones
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ModoJuego.this);
            String colorValue = preferences.getString("button_color", "#0000FF"); // El valor por defecto es azul
            btnClassicMode.setBackgroundColor(Color.parseColor(colorValue));
            btnChallengeMode.setBackgroundColor(Color.parseColor(colorValue));
        } else if (key.equals("language")) {
            String language = sharedPreferences.getString("language", "es");
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.setLocale(locale);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        TextView titulo = (TextView) findViewById(R.id.textoModoJuego);
        outState.putString("Title", titulo.getText().toString());

        outState.putString("Clasico", btnClassicMode.getText().toString());
        outState.putString("Retos", btnChallengeMode.getText().toString());

        if (fotoPerfil.getDrawable() != null) {
            imagen = ((BitmapDrawable)fotoPerfil.getDrawable()).getBitmap();
            outState.putParcelable("imagen", imagen);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        TextView titulo = (TextView) findViewById(R.id.textoModoJuego);
        titulo.setText(outState.getString("Title"));

        btnClassicMode.setText(outState.getString("Clasico"));
        btnChallengeMode.setText(outState.getString("Retos"));

        imagen = outState.getParcelable("imagen");
        fotoPerfil.setImageBitmap(outState.getParcelable("imagen"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desregistrar el listener
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}