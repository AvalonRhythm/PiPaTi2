package com.example.pipati;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class Ajustes extends AppCompatActivity {

    Button btnSave, btnPictureGallery, btnPictureCamera;
    ImageView fotoPreview;
    SharedPreferences sharedPreferences;
    String UPLOAD_URL = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/hrobles002/WEB/subirImagen.php";
    Bitmap bitmap;
    Uri imageUri;
    String b64;
    RequestQueue request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtenemos las preferencias
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Ajustes.this);
                // Si se ha cambiado el color de los botones se actualiza
                if (key.equals("button_color")) {
                    // Obtener el nuevo color de los botones
                    String colorValue = preferences.getString("button_color", "#F46666"); // El valor por defecto es azul
                    btnSave.setBackgroundColor(Color.parseColor(colorValue));

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
        });

        String language = sharedPreferences.getString("language", "es");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        setContentView(R.layout.activity_ajustes);

        FragmentoAjustes fragmentoPreferencias = new FragmentoAjustes();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerAjustes, fragmentoPreferencias)
                .commit();

        fotoPreview = (ImageView) findViewById(R.id.profilePicture);

        btnSave = (Button) findViewById(R.id.btnAtrasAjustes);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(Ajustes.this, getIntent().getStringExtra("user"), Toast.LENGTH_SHORT).show();

                // Comprobamos que el usuario haya seleccionado una imagen para evitar errores
                if(bitmap!=null) {

                    // Convertimos la imagen en un formato adecuado para guardarla en la base de datos
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] b = baos.toByteArray();
                    b64 = Base64.encodeToString(b, Base64.DEFAULT);

                    // Creamos la petición para guardar la imagen

                    /**
                     * Extraido de: https://stackoverflow.com/questions/17571759/how-do-you-use-the-android-volley-api
                     * Modificado por: Hugo Robles, para cambiar el nomobre de algunos elementos.
                     **/
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String respuesta = response;

                            //Toast.makeText(Ajustes.this, respuesta, Toast.LENGTH_SHORT).show();

                            finish();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //si ha habido algun error con la solicitud
                            Toast.makeText(Ajustes.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            //se pasan todos los parametros necesarios en la solicitud
                            HashMap<String, String> parametros = new HashMap<String, String>();
                            parametros.put("username", getIntent().getStringExtra("user"));
                            parametros.put("image", b64);
                            return parametros;
                        }
                    };

                    //se envia la solicitud con los parametros
                    request = Volley.newRequestQueue(Ajustes.this);
                    request.add(stringRequest);
                }

                Intent intent = new Intent(Ajustes.this, MenuPrincipal.class);
                intent.putExtra("user", getIntent().getStringExtra("user"));
                startActivity(intent);
                finish();
            }
        });

        btnPictureGallery = (Button) findViewById(R.id.btnFotoGaleria);
        btnPictureGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //la galería para seleccionar una imagen
                Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentGallery, 112);
            }
        });

        // Boton para sacar una foto con la camara
        btnPictureCamera = findViewById(R.id.btnFotoCamara);
        btnPictureCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrimos la cámara para tomar una foto
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intentCamera, 111);
            }
        });

        loadPreferences();
    }

    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        // Colocamos la imagen en el ImageView para poder verla antes de guardarla
        if (resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
                if(imageUri==null){
                    bitmap = (Bitmap) data.getExtras().get("data");
                    fotoPreview.setImageBitmap(bitmap);
                }else {
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    bitmap = BitmapFactory.decodeStream(imageStream);
                    fotoPreview.setImageBitmap(bitmap);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        fotoPreview = (ImageView) findViewById(R.id.profilePicture);
        if(fotoPreview.getDrawable()!=null) {
            bitmap = ((BitmapDrawable) fotoPreview.getDrawable()).getBitmap();
            outState.putParcelable("imagen", bitmap);
        }
    }

    // Se recuperan los elementos guardados
    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        bitmap = outState.getParcelable("imagen");
        fotoPreview.setImageBitmap(outState.getParcelable("imagen"));
    }

    private void loadPreferences(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Ajustes.this);
        String colorValue = preferences.getString("button_color", "#F46666"); // El color por defecto es "Rojo"
        btnSave.setBackgroundColor(Color.parseColor(colorValue));
        btnPictureGallery.setBackgroundColor(Color.parseColor(colorValue));
        btnPictureCamera.setBackgroundColor(Color.parseColor(colorValue));

        String language = preferences.getString("language", "es"); // El idioma por defecto es "Español"
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}