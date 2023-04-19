package com.example.pipati;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.PreparedStatement;
import java.sql.SQLData;
import java.util.Locale;

public class Login extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    SQLiteDatabase db;
    AlertDialog dialogRegistro;
    SharedPreferences sharedPreferences;
    Button btnLogin, btnRegistro;
    EditText editTextUser, editTextPass;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);

        // Conseguir las preferencias antes de establecer el layout
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        // Se establece el idioma antes del layout para que aparezca el definido por el usuario
        // en vez del que viene en el dispositivo por defecto
        String language = sharedPreferences.getString("language", "es");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        setContentView(R.layout.activity_login);

        // Inicializacion de variables
        DBManager dbManager = new DBManager(this);
        db = dbManager.getWritableDatabase();

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegistro = (Button) findViewById(R.id.btnRegistro);
        editTextUser = (EditText) findViewById(R.id.textInputUsuario);
        editTextPass = (EditText) findViewById(R.id.textInputContrasenia);

        // Se construye el dialogo de la pantalla de registro
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setView(R.layout.dialog_registro);

        builder.setPositiveButton("Registrarse", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editUsername = dialogRegistro.findViewById(R.id.edit_username);
                EditText editPassword = dialogRegistro.findViewById(R.id.edit_password);
                EditText editPassword2 = dialogRegistro.findViewById(R.id.edit_password2);

                String username = editUsername.getText().toString();
                String password = editPassword.getText().toString();
                String password2 = editPassword2.getText().toString();

                // Comprobamos si las dos contraseñas coinciden para poder registrar el usuario
                if(checkSignIn(password, password2)) {
                    RegistroUsuario registroUsuario = new RegistroUsuario();
                    registroUsuario.execute(username, password, "2023-04-23");

                }
            }
        });
        builder.setNegativeButton("Cancelar", null);
        dialogRegistro = builder.create();

        // Boton que muestra el dialog de registro
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogRegistro.show();
            }
        });

        // Boton que gestiona el inicio de sesion
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editTextUser.getText().toString();
                String password = editTextPass.getText().toString();
                // Si el usuario existe en la base de datos se inicia sesion con ese usuario
                checkLogin(username, password);
            }
        });

        // Se cargan las preferencias del usuario
        loadPreferences();
    }

    // Función que comprueba si el usuario existe en la BD
    public void checkLogin(String username, String password) {
        BuscarUsuario buscarUsuario = new BuscarUsuario(Login.this, username, password);
        buscarUsuario.execute();
    }

    // Función que comprueba que dos contraseñas coinciden
    public boolean checkSignIn(String pass, String pass2){
        boolean result = false;
        if (pass.equals(pass2)){
            result = true;
        }
        return result;
    }

    // Funcion que carga las preferencias del usuario
    private void loadPreferences(){
        // Cargamos el color de los botones
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Login.this);
        String colorValue = preferences.getString("button_color", "#F46666"); // Color por defecto "Rojo"
        btnLogin.setBackgroundColor(Color.parseColor(colorValue));
        btnRegistro.setBackgroundColor(Color.parseColor(colorValue));

        // Cargamos el idioma
        String language = preferences.getString("language", "es"); // Idioma por defecto "Español"
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    // Función que actualiza las preferencias si estas son cambiadas por el usuario
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Login.this);
        // Si se ha cambiado el color de los botones se actualiza
        if (key.equals("button_color")) {
            // Obtener el nuevo color de los botones
            String colorValue = preferences.getString("button_color", "#F46666"); // El valor por defecto es azul
            btnLogin.setBackgroundColor(Color.parseColor(colorValue));
            btnRegistro.setBackgroundColor(Color.parseColor(colorValue));

        // Si se ha cambiado el idioma se actualiza
        }  else if (key.equals("language")) {
            String language = preferences.getString("language", "es");
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.setLocale(locale);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
        recreate();
    }

    // Guardar elementos de la actividad
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("User", editTextUser.getText().toString());
        outState.putString("Password", editTextPass.getText().toString());
    }

    // Restaurar elementos de la actividad
    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        editTextUser.setText(outState.getString("User"));
        editTextPass.setText(outState.getString("Password"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}