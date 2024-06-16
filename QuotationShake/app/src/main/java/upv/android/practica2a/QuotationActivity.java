package upv.android.practica2a;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import upv.android.practica2a.clases_auxiliares.Quotation;
import upv.android.practica2a.database.QuotationDao;
import upv.android.practica2a.database.QuotationDatabase;
import upv.android.practica2a.database.QuotationSQLiteOpenHelper;
import upv.android.practica2a.tasks.HTTPAsyncTask;

public class QuotationActivity extends AppCompatActivity {


    Menu menu;
    String http;

    public TextView tvCita;
    public TextView tvAutor;
    ProgressBar progressBar;

    boolean add_visibilidad;
    boolean room;
    QuotationDao quotationDao;
    QuotationSQLiteOpenHelper openHelper;

    Handler handler;
    HTTPAsyncTask httpAsyncTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation);
        tvCita = findViewById(R.id.texto_scroll_cita);
        tvAutor = findViewById(R.id.autor_nombre_texto);
        progressBar = findViewById(R.id.barra_progreso);

        handler = new Handler();



        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String data = prefs.getString("metodo_DATABASES", null);
        http = prefs.getString("metodo_HTTP",null);

        if(data.equals("Room")){
            room=true;
            quotationDao = QuotationDatabase.getInstance(this).quotationDao();
            Toast.makeText(this, "Using ''Room''", Toast.LENGTH_SHORT).show();

        }
        else{
            room= false;
            openHelper = QuotationSQLiteOpenHelper.getInstance(this);
            Toast.makeText(this, "Using ''SQLiteOpenHelper''", Toast.LENGTH_SHORT).show();
        }



        String nombre = prefs.getString("username", null);
        //Toast.makeText(this,getString(R.string.nameless),Toast.LENGTH_SHORT).show();
        if (savedInstanceState==null) {
            if (nombre.equals("")) nombre = getString(R.string.nameless);
            tvCita.setText(tvCita.getText().toString().replaceAll("%1s!", nombre));
        }
        else{
            tvCita.setText(savedInstanceState.getString("texto_cita"));
            tvAutor.setText(savedInstanceState.getString("texto_autor"));
            add_visibilidad=savedInstanceState.getBoolean("add_visibilidad");
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        if(httpAsyncTask!=null){
            if (httpAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
                httpAsyncTask.cancel(true);
            }
        }
    }

    public boolean isConnected() {
        boolean result = false;
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > 22) {
            final Network activeNetwork = manager.getActiveNetwork();
            if (activeNetwork != null) {
                final NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(activeNetwork);
                result = networkCapabilities != null && (
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
            }
        } else {
            NetworkInfo info = manager.getActiveNetworkInfo();
            result = ((info != null) && (info.isConnected()));
        }
        return result;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu_get,menu);
        if(add_visibilidad) menu.findItem(R.id.menu_añadir_cita_a_favoritos).setVisible(true);
        this.menu = menu;
        return true;
    }

    public void activarBarraProgreso(){
        progressBar.setVisibility(View.VISIBLE);
        menu.findItem(R.id.menu_añadir_cita_a_favoritos).setVisible(false);
        menu.findItem(R.id.menu_nueva_cita).setVisible(false);
    }

    public void mostrarCita(Quotation quotation){
        menu.findItem(R.id.menu_nueva_cita).setVisible(true);
        if(quotation!=null){
            findViewById(R.id.barra_progreso).setVisibility(View.INVISIBLE);
            tvCita.setText(quotation.getQuoteText());
            tvAutor.setText(quotation.getQuoteAuthor());

            new Thread(new Runnable() {
                @Override
                public void run() {

                    if (room) {
                        Quotation q = quotationDao.getQuotation(tvCita.getText().toString());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(q!=null) menu.findItem(R.id.menu_añadir_cita_a_favoritos).setVisible(false);
                                else menu.findItem(R.id.menu_añadir_cita_a_favoritos).setVisible(true);
                            }
                        });
                    }

                    else {
                        boolean existe = openHelper.hasQuotation(quotation);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (existe) {
                                    menu.findItem(R.id.menu_añadir_cita_a_favoritos).setVisible(false);
                                } else
                                    menu.findItem(R.id.menu_añadir_cita_a_favoritos).setVisible(true);
                            }

                        });
                    }
                }
            }).start();

        }else{
            tvCita.setText("NO INTERNET");
            tvAutor.setText("NO INTERNET");
        }


    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.menu_añadir_cita_a_favoritos) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(room) quotationDao.addQuotation(new Quotation(tvCita.getText().toString(),tvAutor.getText().toString()));
                    else openHelper.newQuotation(tvCita.getText().toString(),tvAutor.getText().toString());


                }
            }).start();
            menu.findItem(R.id.menu_añadir_cita_a_favoritos).setVisible(false);
            return true;


        }
        else if (item.getItemId()==R.id.menu_nueva_cita) {
            if(isConnected()){
                if(http.equals("GET")){
                    httpAsyncTask = new HTTPAsyncTask(this);
                    httpAsyncTask.execute(true);
                }

                else{
                    httpAsyncTask = new HTTPAsyncTask(this);
                    httpAsyncTask.execute(false);
                }

                return true;
            }else{
                Toast.makeText(this, "NO HAY CONEXIÓN A INTERNET", Toast.LENGTH_SHORT).show();
                return true;
            }

        }
        else return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("texto_cita",tvCita.getText().toString());
        outState.putString("texto_autor",tvAutor.getText().toString());
        MenuItem add = menu.findItem(R.id.menu_añadir_cita_a_favoritos);
        outState.putBoolean("add_visibilidad",add.isVisible());
        super.onSaveInstanceState(outState);

    }
}