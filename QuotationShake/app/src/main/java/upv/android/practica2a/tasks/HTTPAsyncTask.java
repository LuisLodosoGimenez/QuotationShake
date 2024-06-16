package upv.android.practica2a.tasks;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import upv.android.practica2a.FavouriteActivity;
import upv.android.practica2a.QuotationActivity;
import upv.android.practica2a.clases_auxiliares.Quotation;

public class HTTPAsyncTask extends AsyncTask<Boolean,Void, Quotation> {

    private final WeakReference<QuotationActivity> weakReference;
    int nrespuesta;
    String respuesta;

    public HTTPAsyncTask(QuotationActivity quotationActivity){
        weakReference = new WeakReference<>(quotationActivity);
    }


    @Override
    protected Quotation doInBackground(Boolean... booleans) {

        Quotation quotation = null;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(weakReference.get().getApplicationContext());
        String idioma = prefs.getString("idioma","English");
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("api.forismatic.com");
        builder.appendPath("api");
        builder.appendPath("1.0");
        builder.appendPath("");

        if(booleans[0]){
            builder.appendQueryParameter("method", "getQuote");
            builder.appendQueryParameter("format", "json");
            if(idioma.equals("English")||idioma.equals("Inglés")) builder.appendQueryParameter("lang", "en");
            else builder.appendQueryParameter("lang", "ru");


            try {
                URL url = new URL(builder.build().toString());
                respuesta = builder.toString();
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader  reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    Gson gson = new Gson();
                    quotation = gson.fromJson(reader,Quotation.class);
                    reader.close();

// Retrieve and process the response
                }

                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }else{
            String body;
            if(idioma.equals("English")||idioma.equals("Inglés")) body = "method=getQuote&format=json&lang=en";
            else body = "method=getQuote&format=json&lang=ru";
            try {
                URL url = new URL(builder.build().toString());
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(body);
                writer.flush();
                writer.close();
                nrespuesta = connection.getResponseCode();
                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader  reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    Gson gson = new Gson();
                    quotation = gson.fromJson(reader,Quotation.class);
                    reader.close();

// Retrieve and process the response
                }

                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return quotation;


    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        QuotationActivity quotationActivity = weakReference.get();
        if(quotationActivity!=null){
            quotationActivity.activarBarraProgreso();
        }
    }

    @Override
    protected void onPostExecute(Quotation quotation) {
        super.onPostExecute(quotation);

        QuotationActivity quotationActivity = weakReference.get();

        if(quotation!=null) Toast.makeText(quotationActivity.getApplicationContext(), "NUEVA CITA",Toast.LENGTH_SHORT).show();
        else Toast.makeText(quotationActivity.getApplicationContext(), "CODE: "+nrespuesta+" ---- " + respuesta,Toast.LENGTH_SHORT).show();

        if(quotationActivity!=null){
            quotationActivity.mostrarCita(quotation);
        }
    }
}
