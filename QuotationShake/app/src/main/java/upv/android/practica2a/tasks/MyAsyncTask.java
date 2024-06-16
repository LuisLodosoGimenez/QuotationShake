package upv.android.practica2a.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.List;

import upv.android.practica2a.FavouriteActivity;
import upv.android.practica2a.clases_auxiliares.Quotation;
import upv.android.practica2a.database.QuotationDatabase;
import upv.android.practica2a.database.QuotationSQLiteOpenHelper;

public class MyAsyncTask extends AsyncTask<Boolean,Void, List<Quotation>> {

    private final WeakReference<FavouriteActivity> weakReference;
    FavouriteActivity activityReference;

    public MyAsyncTask(FavouriteActivity favouriteActivity){
        weakReference = new WeakReference<>(favouriteActivity);
    }

    @Override
    protected List<Quotation> doInBackground(Boolean... booleans) {
        Context context = weakReference.get().getApplicationContext();
        if(context!=null) {
            List<Quotation> res;
            if (booleans[0])
                res = QuotationDatabase.getInstance(context).quotationDao().getQuotations();
            else res = QuotationSQLiteOpenHelper.getInstance(context).getQuotations();
            return res;
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Quotation> quotations) {
        activityReference= weakReference.get();
        if (activityReference!=null)activityReference.rellenarAdapter(quotations);
        super.onPostExecute(quotations);
    }
}
