package com.example.pipati;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Base64;
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

public class GuardarImagen extends AsyncTask<Void, Void, Boolean>  {

    private Context mContext;
    private String username;
    private byte[] image;

    public GuardarImagen(String param1, byte[] param2) {
        username = param1;
        image = param2;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            String urlString = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/hrobles002/WEB/guardarImagen.php";
            String data = "username=" + URLEncoder.encode(username, "UTF-8") +
                    "&image=" + Base64.encodeToString(image, Base64.DEFAULT);

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
            Log.d("Mensaje", response);
            return Boolean.parseBoolean(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Toast.makeText(mContext, "Imagen guardada", Toast.LENGTH_SHORT).show();
        } else {
            // Muestra un mensaje de error
            Toast.makeText(mContext, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
        }
    }
}

