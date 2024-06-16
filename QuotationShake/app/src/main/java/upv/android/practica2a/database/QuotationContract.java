package upv.android.practica2a.database;

import android.provider.BaseColumns;

public class QuotationContract {
    private QuotationContract(){}

    static class MyBaseColumns implements BaseColumns {
        static final String nombre_tabla = "tabla_citas";
        static final String nombre_columna = "ID";
        static final String nombre_autor = "cita_autor";
        static final String texto_cita = "cita_texto";

    }
}
