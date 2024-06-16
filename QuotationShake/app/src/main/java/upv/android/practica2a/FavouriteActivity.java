package upv.android.practica2a;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import upv.android.practica2a.clases_auxiliares.Adaptador;
import upv.android.practica2a.clases_auxiliares.Quotation;
import upv.android.practica2a.database.QuotationDao;
import upv.android.practica2a.database.QuotationDatabase;
import upv.android.practica2a.database.QuotationSQLiteOpenHelper;
import upv.android.practica2a.tasks.MyAsyncTask;

public class FavouriteActivity extends AppCompatActivity   {

    Adaptador adaptador;
    QuotationSQLiteOpenHelper openHelper;
    QuotationDao quotationDao;
    boolean room;
    Thread hiloBorrarTodos;
    Menu menu;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        handler = new Handler();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String data = prefs.getString("metodo_DATABASES", null);
        if(data.equals("Room")) room = true;
        else room = false;

        adaptador = new Adaptador(new ArrayList<>(),null,null);

        MyAsyncTask hiloObtenerListaQuotations = new MyAsyncTask(this);
        hiloObtenerListaQuotations.execute(room);


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

        hiloBorrarTodos = new Thread(new Runnable() {
            @Override
            public void run() {
                if(room) quotationDao.deleteQuotations();
                else openHelper.deleteAllQuotations();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adaptador.borrarCitas();
                    }
                });
            }
        });



        // ############ CLASE QUE IMPLEMENTA clickListener
        class clickListenerImp implements Adaptador.OnItemClickListener{

            @Override
            public void onItemClickListener(int position) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                String autor = adaptador.citaNumero(position).getQuoteAuthor();
                if(autor==null || autor.equals("")) Toast.makeText(getApplicationContext(), "NO ES POSIBLE OBTENER LA INFORMACIÓN DEL AUTOR", Toast.LENGTH_SHORT).show();

                else {

                    try {
                        String enlace = URLEncoder.encode(autor, "UTF-8");
                        intent.setData(Uri.parse("https://en.wikipedia.org/wiki/Special:Search?search="+enlace));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }


                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            }
        }


        // ############ CLASE QUE IMPLEMENTA longClickListener

        class longClickListenerImp implements Adaptador.OnItemLongClickListener{
            @Override
            public void onItemLongClickListener(int position) {
                eliminarElemento(position);
            }
        }

        Adaptador.OnItemClickListener clickListener = new clickListenerImp();
        Adaptador.OnItemLongClickListener longClickListener = new longClickListenerImp();
        adaptador.setListeners(clickListener,longClickListener);

        RecyclerView rv = findViewById(R.id.recyclerView);
        final RecyclerView.LayoutManager miLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(miLayoutManager);
        rv.setHasFixedSize(true);
        rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(),0));
        rv.setAdapter(adaptador);

    }


    public void rellenarAdapter(List<Quotation> list) {
        adaptador.setList(list);
    }

    public void eliminarElemento(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(FavouriteActivity.this);
        builder.setMessage(R.string.are_you_sure_that_you_want_to_delete_this_quotation);
        builder.setNegativeButton(R.string.no, null);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(room) quotationDao.deleteQuotation(adaptador.citaNumero(position));
                        else openHelper.deleteQuotation(adaptador.citaNumero(position).getQuoteText());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adaptador.borrarCita(position);
                            }
                        });

                    }
                }).start();


            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu_favourite,menu);
        if(adaptador.getItemCount()==0){
            menu.findItem(R.id.menu_favoritos_borrarCitas).setVisible(false);

        }
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_favoritos_borrarCitas){
            AlertDialog.Builder builder = new AlertDialog.Builder(FavouriteActivity.this);
            builder.setMessage("Are you sure that you want to delete all quotations?");
            builder.setNegativeButton(R.string.no, null);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    hiloBorrarTodos.start();
                    item.setVisible(false);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;

        }
        else return super.onOptionsItemSelected(item);

    }
/*
    public ArrayList<Quotation> getMockQuotations(){

        ArrayList<Quotation> res = new ArrayList<>();

        res.add(new Quotation("No hay camino que no se acabe si no se le opone la pereza","Miguel de Cervantes"));
        res.add(new Quotation("Lo malo de los que se creen en posesión de la verdad es que cuando tienen que demostrarlo no aciertan ni una","Camilo José Cela"));
        res.add(new Quotation("No hay camino que no se acabe si no se le opone la pereza","Miguel de Cervantes"));
        res.add(new Quotation("El fumador no solo asume voluntariamente los riesgos que supone fumar, sino que además los impone por su cuenta a las personas que le rodean","Isabel Fernández del Castillo"));
        res.add(new Quotation("La violencia es miedo de las ideas de los demás y poca fe en las propias","Antonio Fraguas Forges"));

        res.add(new Quotation("Todo está conectado","Papa Francisco"));
        res.add(new Quotation("La gravedad de la crisis ecológica nos exige a todos pensar en el bien común","Papa Francisco"));
        res.add(new Quotation("El actual sistema mundial es insostenible","Papa Francisco"));
        res.add(new Quotation("El hábito de gastar y tirar alcanza niveles inauditos","Papa Francisco"));
        res.add(new Quotation("La tierra que recibimos pertenece también a los que vendrán","Papa Francisco"));

        return res;
        }
 */




}