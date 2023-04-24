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
    SharedPreferences sharedPreferences;
    private static final int IMAGE_CODE=112;
    private static final int PHOTO_CODE=111;
    String UPLOAD_URL = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/hrobles002/WEB/subirImagen.php";
    Bitmap bitmap;
    Uri imageUri;
    RequestQueue rq;

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

        btnSave = (Button) findViewById(R.id.btnAtrasAjustes);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();

                GuardarImagen guardarImagen = new GuardarImagen(getApplicationContext(), UPLOAD_URL, getIntent().getStringExtra("user"), imageBytes);
                guardarImagen.execute();
                */
                Toast.makeText(Ajustes.this, getIntent().getStringExtra("user"), Toast.LENGTH_SHORT).show();


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();
                String b64 = Base64.encodeToString(b, Base64.DEFAULT);

                StringRequest sr = new StringRequest(Request.Method.POST, UPLOAD_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String respuesta=response;

                        Toast.makeText(Ajustes.this, respuesta, Toast.LENGTH_SHORT).show();

                        rq.cancelAll("aniadirImagen");
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
                rq = Volley.newRequestQueue(Ajustes.this);
                sr.setTag("aniadirImagen");
                rq.add(sr);


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
                startActivityForResult(intentGallery, IMAGE_CODE);
            }
        });

        //boton sacar foto con la camara
        btnPictureCamera = findViewById(R.id.btnFotoCamara);
        btnPictureCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrimos la cámara para tomar una foto
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intentCamera, PHOTO_CODE);
            }
        });

        loadPreferences();
    }

    public String getStringImagen(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
                if(imageUri==null){
                    bitmap = (Bitmap) data.getExtras().get("data");
                    ImageView fotoPerfil = (ImageView) findViewById(R.id.profilePicture);
                    fotoPerfil.setImageBitmap(bitmap);
                }else {
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    bitmap = BitmapFactory.decodeStream(imageStream);
                    ImageView fotoPerfil = (ImageView) findViewById(R.id.profilePicture);
                    fotoPerfil.setImageBitmap(bitmap);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(Ajustes.this, "1", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(Ajustes.this,  "2",Toast.LENGTH_SHORT).show();
        }
    }

    //COMPACTAR IMAGEN
    protected byte[] tratarImagen(byte[] img){
        /**
         * Basado en el código extraído de Stack Overflow
         * Pregunta: https://stackoverflow.com/questions/57107489/sqliteblobtoobigexception-row-too-big-to-fit-into-cursorwindow-while-writing-to
         * Autor: https://stackoverflow.com/users/3694451/leo-vitor
         */
        while(img.length > 50000){
            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            Bitmap compacto = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth()*0.8), (int)(bitmap.getHeight()*0.8), true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            compacto.compress(Bitmap.CompressFormat.PNG, 100, stream);
            img = stream.toByteArray();
        }
        return img;
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