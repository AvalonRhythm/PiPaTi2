package com.example.pipati;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class BuscarUsuario extends AsyncTask<Void, Void, Boolean>  {

    private Context mContext;
    private String username;
    private String pass;

    public BuscarUsuario(Context context, String param1, String param2) {
        mContext = context;
        username = param1;
        pass = param2;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            String urlString = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/hrobles002/WEB/login.php";
            String data = "username=" + URLEncoder.encode(username, "UTF-8") +
                    "&pass=" + URLEncoder.encode(pass, "UTF-8");

            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            String response = bufferedReader.readLine();
            bufferedReader.close();
            inputStream.close();
            urlConnection.disconnect();
            Log.d("mensaje", response);
            return Boolean.parseBoolean(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            // Inicia sesión y muestra la actividad principal
            Intent intent = new Intent(mContext, MenuPrincipal.class);
            intent.putExtra("user", username);
            mContext.startActivity(intent);
            //finish();

            // En caso contrario se muestra un mensaje toast de error
        } else {
            // Muestra un mensaje de error
            Toast.makeText(mContext, "Datos incorrectos, vuelva a intentarlo", Toast.LENGTH_SHORT).show();
        }
    }
}
