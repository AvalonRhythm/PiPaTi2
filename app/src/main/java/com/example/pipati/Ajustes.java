package com.example.pipati;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Ajustes extends AppCompatActivity {

    Button btnSave, btnPictureGallery, btnPictureCamera;
    SharedPreferences sharedPreferences;
    private Uri imagen;
    private Bitmap bimagen;
    private String b64;
    private String nombreimagen;
    private static final int IMAGE_CODE=112;
    private static final int PHOTO_CODE=111;
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
                //mirar si la version es mayor o igual a marshmallow
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //si lo es mirar si se han dado los permisos de lectura
                    if (ActivityCompat.checkSelfPermission(Ajustes.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        //si no hay permisos de lectura darselos
                        ActivityCompat.requestPermissions(Ajustes.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_CODE);
                    } else {
                        //si ya hay permisos
                        elegirfoto();
                    }
                } else {
                    //si la version es menor a marshmallow
                    elegirfoto();
                }
            }
        });

        //boton sacar foto con la camara
        btnPictureCamera = findViewById(R.id.btnFotoCamara);
        btnPictureCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //se ha de mirar primero si la version es igual o superior a la marshmallow
                //si es igual o superior
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //si no hay permisos para acceder a la cámara, o escribir
                    if (ActivityCompat.checkSelfPermission(Ajustes.this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(Ajustes.this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        //dar permisos de camara y escritura
                        ActivityCompat.requestPermissions(Ajustes.this,new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PHOTO_CODE);
                    } else {
                        //si ya hay permisos se abre la camara
                        abrirCamara();
                    }
                }
                //si la version es menor a la marshmallow
                else {
                    abrirCamara();
                }
            }
        });

        loadPreferences();
    }
    //metodo para cuando se usa la camara
    private void abrirCamara() {
        ContentValues cv = new ContentValues();
        //informacion de la imagen
        cv.put(MediaStore.Images.Media.TITLE, "Nueva Imagen");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Nueva Imagen sacada con la cámara");
        //uri de la imagen
        imagen = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        //crear intent para la camara
        Intent camarai = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camarai.putExtra(MediaStore.EXTRA_OUTPUT, imagen);
        startActivityForResult(camarai, 1111);
    }

    //metodo para cuando se elige foto de la galeria
    private void elegirfoto() {
        //crear intent para la galeria
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1112);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //si se esta sacando una foto
        if(requestCode==PHOTO_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //se han dado los permisos necesarios
                abrirCamara();
            } else {
                //no se han dado los permisos necesarios
                Toast.makeText(this, "No se han aceptado los permisos", Toast.LENGTH_SHORT).show();
            }
        }
        //si se esta eligiendo una foto de la galeria
        if(requestCode==IMAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //se han dado los permisos necesarios
                elegirfoto();
            } else {
                //no se han dado los permisos necesarios
                Toast.makeText(this, "No se han aceptado los permisos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //cuando ya se ha sacado la foto
        if (resultCode == RESULT_OK && requestCode == 1111) {
            //guardar la imagen en bitmap para luego subirla a la bd

            //encontrar directorio de la galeria
            File directorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            //crear nombre de la foto sacada
            String tiempo = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            nombreimagen = "IMG_" + tiempo + ".png";
            File imagenfinal = new File(directorio, nombreimagen);

            try {
                //guardar la foto en un file y enviarla a la galeria
                FileOutputStream fos = new FileOutputStream(imagenfinal);
                bimagen.compress(Bitmap.CompressFormat.PNG, 100, fos);
                //ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //byte[] b = baos.toByteArray();
                //b64 = Base64.encodeToString(b, Base64.DEFAULT);
                //fos.flush();
                fos.close();

                //crear intent para que guarde la informacion de la imagen
                //Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                //intent.setData(Uri.fromFile(imagenfinal));
                //sendBroadcast(intent);

                Toast.makeText(this, "Se ha guardado la imagen en la galería", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                //no hace nada
            }

        }
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