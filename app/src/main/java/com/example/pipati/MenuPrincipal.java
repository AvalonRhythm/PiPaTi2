package com.example.pipati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.ContentValues;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MenuPrincipal extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    Button btnJugar, btnHistorial, btnAjustes;
    ImageButton btnBack;
    ImageView fotoPerfil;
    StringRequest stringRequest;
    RequestQueue request;
    SharedPreferences sharedPreferences;
    Bitmap imagen;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        // Obtenemos las preferencias
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        // Inicializamos las variables
        btnJugar = (Button) findViewById(R.id.botonJugar);
        btnHistorial = (Button) findViewById(R.id.botonHistorial);
        btnAjustes = (Button) findViewById(R.id.botonAjustes);
        btnBack = (ImageButton) findViewById(R.id.menuPrincipalFlechaAtras);
        fotoPerfil = (ImageView) findViewById(R.id.menuPrincipalImagenPerfil);

        String url = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/hrobles002/WEB/descargarImagen.php";
        request = Volley.newRequestQueue(getApplicationContext());
        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("fail")){
                    Toast.makeText(getApplicationContext(), "Fallo de PHP", Toast.LENGTH_SHORT).show();
                }
                else{
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
                    }catch (Exception e){
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

        //se envia la solicitud con los parametros
        stringRequest.setTag("descargarImagenPerfil");
        request.add(stringRequest);

        // Boton que abre la actividad de "Modo de Juego"
        btnJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuPrincipal.this, ModoJuego.class);
                intent.putExtra("user", getIntent().getStringExtra("user"));
                startActivity(intent);
                finish();
            }
        });

        // Boton que carga la actividad Historico
        btnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuPrincipal.this, Historico.class);
                intent.putExtra("user", getIntent().getStringExtra("user"));
                startActivity(intent);
                finish();
            }
        });

        // Boton carga la actividad Ajustes
        btnAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuPrincipal.this, Ajustes.class);
                intent.putExtra("user", getIntent().getStringExtra("user"));
                startActivity(intent);
                finish();
            }
        });

        // Boton que carga la actividad anterior
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuPrincipal.this, Login.class);
                intent.putExtra("user", getIntent().getStringExtra("user"));
                startActivity(intent);
                finish();
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("users").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("SUSCRIPCION_EXITOSA", "Suscripcion exitosa");
                } else {
                    Log.d("SUSCRIPCION_FALLIDA", "Suscripcion fallida");

                }
            }
        });

        registrarToken();
        loadPreferences();
    }

    private void registrarToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("ERROR_GET_TOKEN", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        token = task.getResult();

                        Log.d("TOKEN_DISPOSITIVO", token);
                        //Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();

                        String url = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/hrobles002/WEB/registrarDispositivo.php";
                        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("REGISTRO_TOKEN", response);
                                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                                request.cancelAll("registrarToken");
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //si ha habido algun error con la solicitud
                                Log.d("ERROR_REGISTRO_TOKEN", error.toString());
                                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                //se pasan todos los parametros necesarios en la solicitud
                                HashMap<String, String> parametros = new HashMap<String, String>();
                                parametros.put("username", getIntent().getStringExtra("user"));
                                parametros.put("token", token);
                                return parametros;
                            }
                        };

                        //se envia la solicitud con los parametros
                        request = Volley.newRequestQueue(getApplicationContext());
                        stringRequest.setTag("subirToken");
                        request.add(stringRequest);
                        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
                    }
                });
    }


    // Funcion que carga las preferencias del usuario
    private void loadPreferences(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MenuPrincipal.this);
        String colorValue = preferences.getString("button_color", "#F46666"); // El color por defecto es "Rojo"
        btnJugar.setBackgroundColor(Color.parseColor(colorValue));
        btnHistorial.setBackgroundColor(Color.parseColor(colorValue));
        btnAjustes.setBackgroundColor(Color.parseColor(colorValue));

        String language = preferences.getString("language", "es"); // El idioma por defecto es "Español"
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MenuPrincipal.this);
        // Si se ha cambiado el color de los botones se actualiza
        if (key.equals("button_color")) {
            // Obtener el nuevo color de los botones
            String colorValue = preferences.getString("button_color", "#F46666"); // El valor por defecto es azul
            btnJugar.setBackgroundColor(Color.parseColor(colorValue));
            btnHistorial.setBackgroundColor(Color.parseColor(colorValue));
            btnAjustes.setBackgroundColor(Color.parseColor(colorValue));

        // Si se ha cambiado el idioma se actualiza
        } else if (key.equals("language")) {
            String language = preferences.getString("language", "es");
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.setLocale(locale);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
        recreate();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("Jugar", btnJugar.getText().toString());
        outState.putString("Historial", btnHistorial.getText().toString());
        outState.putString("Ajustes", btnAjustes.getText().toString());

        if (fotoPerfil.getDrawable() != null) {
            imagen = ((BitmapDrawable)fotoPerfil.getDrawable()).getBitmap();
            outState.putParcelable("imagen", imagen);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        btnJugar.setText(outState.getString("Jugar"));
        btnHistorial.setText(outState.getString("Historial"));
        btnAjustes.setText(outState.getString("Ajustes"));

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