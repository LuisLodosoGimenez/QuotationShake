package upv.android.practica2a.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import upv.android.practica2a.QuotationActivity;
import upv.android.practica2a.clases_auxiliares.Quotation;

@Database(entities = {Quotation.class}, version = 1)
public abstract class QuotationDatabase extends RoomDatabase {

    private static QuotationDatabase quotationdatabase;

    public static synchronized QuotationDatabase getInstance(Context context) {
        if(quotationdatabase==null) quotationdatabase = Room.databaseBuilder(context,QuotationDatabase.class,"quotation_database")
                .build();
        return quotationdatabase;
    }

    public abstract QuotationDao quotationDao();
}
