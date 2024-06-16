package upv.android.practica2a.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

import upv.android.practica2a.clases_auxiliares.Quotation;

@Dao
public interface QuotationDao {

    @Insert
    void addQuotation(Quotation quotation);
    @Delete
    void deleteQuotation(Quotation quotation);
    @Query("SELECT * FROM tabla_citas")
    List<Quotation> getQuotations();
    @Query("SELECT * FROM tabla_citas WHERE cita_texto = :texto")
    Quotation getQuotation(String texto);
    @Query("DELETE FROM tabla_citas")
    void deleteQuotations();

}
