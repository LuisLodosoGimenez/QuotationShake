package upv.android.practica2a.clases_auxiliares;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import upv.android.practica2a.database.QuotationContract;


@Entity (tableName = "tabla_citas")
public class Quotation {

    @PrimaryKey(autoGenerate = true)
    private int ID;
    @ColumnInfo(name= "cita_texto")
    @NonNull private String quoteText;
    @ColumnInfo(name = "cita_autor")
    private String quoteAuthor;

    public Quotation(){}
    public Quotation(String text, String Author){
        quoteText = text;
        quoteAuthor = Author;
    }

    public int getID() {
        return ID;
    }
    public void setID(int id) {
        this.ID = id;
    }
    public String getQuoteText() {
        return quoteText;
    }
    public void setQuoteText(String quoteText) {
        this.quoteText = quoteText;
    }
    public String getQuoteAuthor() {
        return quoteAuthor;
    }
    public void setQuoteAuthor(String quoteAuthor) {
        this.quoteAuthor = quoteAuthor;
    }
}
