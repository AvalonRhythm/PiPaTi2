package com.example.pipati;

/**
 * Extraido de: https://stackoverflow.com/questions/25647881/android-asynctask-example-and-explanation
 * Modificado por: Hugo Robles, para cambiar el nomobre de algunos elementos.
 **/

import android.os.AsyncTask;
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

public class RegistroUsuario extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        try {
            String username = params[0];
            String pass = params[1];

            // Url del php que se encargara de gestionar la peticion en el servidor
            String urlString = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/hrobles002/WEB/registro.php";
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
            String response = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                response += line;
            }
            bufferedReader.close();
            inputStream.close();
            urlConnection.disconnect();
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}

