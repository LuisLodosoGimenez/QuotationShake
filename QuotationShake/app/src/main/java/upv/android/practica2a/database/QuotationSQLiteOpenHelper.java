package upv.android.practica2a.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import upv.android.practica2a.QuotationActivity;
import upv.android.practica2a.clases_auxiliares.Quotation;


public class QuotationSQLiteOpenHelper extends SQLiteOpenHelper {

    private static QuotationSQLiteOpenHelper mySQL;
    private static final String DATABASE_NAME = "contacts_database";


    public QuotationSQLiteOpenHelper(Context context) {
        super(context,"quotation_database",null,1);
    }

    public synchronized static QuotationSQLiteOpenHelper getInstance(Context context){
        if(mySQL==null) mySQL = new QuotationSQLiteOpenHelper(context);
        return mySQL;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "  +
                QuotationContract.MyBaseColumns.nombre_tabla +  " ( " +
                QuotationContract.MyBaseColumns.nombre_columna + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                QuotationContract.MyBaseColumns.texto_cita + " TEXT NOT NULL, " +
                QuotationContract.MyBaseColumns.nombre_autor + " TEXT); ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    public ArrayList<Quotation> getQuotations(){
        ArrayList<Quotation> res = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Quotation quotation;
        Cursor cursor = db.query(
                QuotationContract.MyBaseColumns.nombre_tabla,
                new String[]{
                        QuotationContract.MyBaseColumns.texto_cita,
                        QuotationContract.MyBaseColumns.nombre_autor
                },
                null,null,null,null,null);
        while (cursor.moveToNext()) {
            quotation = new Quotation(cursor.getString(0),cursor.getString(1));
            res.add(quotation);
        }

        cursor.close();
        db.close();
        return res;

    }

    public void newQuotation(String texto, String autor){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(QuotationContract.MyBaseColumns.texto_cita,texto);
        cv.put(QuotationContract.MyBaseColumns.nombre_autor,autor);
        db.beginTransaction();
        try {
            db.insert(QuotationContract.MyBaseColumns.nombre_tabla,null,cv);
            db.setTransactionSuccessful();
        }
        catch (SQLiteException e) {
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    public void deleteAllQuotations(){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(QuotationContract.MyBaseColumns.nombre_tabla,null,null);
            db.setTransactionSuccessful();
        }
        catch (SQLiteException e) {
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    public void deleteQuotation(String texto){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(QuotationContract.MyBaseColumns.nombre_tabla,QuotationContract.MyBaseColumns.texto_cita+"=?",new String[]{texto});
            db.setTransactionSuccessful();
        }
        catch (SQLiteException e) {
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    public boolean hasQuotation(Quotation quotation){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                QuotationContract.MyBaseColumns.nombre_tabla,
                null,
                QuotationContract.MyBaseColumns.texto_cita + "=?" ,
                new String[]{quotation.getQuoteText()}, null, null, null, null
        );
        if(cursor.getCount()>0) return true;
        return false;
    }
}
