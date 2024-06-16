package upv.android.practica2a;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    public void boton_pulsado(View view){

        if(view.getId()==R.id.button){
            Intent intent = new Intent(this, QuotationActivity.class);
            startActivity(intent);
        }

        if(view.getId()==R.id.button2){
            Intent intent = new Intent(this, FavouriteActivity.class);
            startActivity(intent);
        }
        if(view.getId()==R.id.button3){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        if(view.getId()==R.id.button4){
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);


        }
    }
}