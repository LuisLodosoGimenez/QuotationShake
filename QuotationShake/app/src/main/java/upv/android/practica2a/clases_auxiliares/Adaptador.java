package upv.android.practica2a.clases_auxiliares;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import upv.android.practica2a.R;

public class Adaptador extends RecyclerView.Adapter<Adaptador.ViewHolder>{
    private  List<Quotation> listaCitas;
    private  OnItemClickListener clickListener;
    private  OnItemLongClickListener longClickListener;

    public Adaptador(ArrayList<Quotation> lista, OnItemClickListener cl, OnItemLongClickListener lcl){
        listaCitas=lista;
        clickListener = cl;
        longClickListener = lcl;
    }


    public void setList(List<Quotation> l){
        listaCitas = l;
        notifyDataSetChanged();
    }
    public void setListeners(OnItemClickListener cl, OnItemLongClickListener lcl){
        clickListener = cl;
        longClickListener = lcl;

    }

    public interface OnItemClickListener {
        void onItemClickListener(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClickListener(int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quotation_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        holder.tvCita.setText(listaCitas.get(position).getQuoteText());
        holder.tvAutor.setText(listaCitas.get(position).getQuoteAuthor());
    }

    @Override
    public int getItemCount() {
        return listaCitas.size();

    }

    public  Quotation citaNumero(int i){
        return listaCitas.get(i);
    }

    public  void borrarCita(int i){
        listaCitas.remove(i);
        notifyItemRemoved(i);
    }

    public  void borrarCitas(){
        listaCitas.clear();
        notifyDataSetChanged();
    }

    //############  CLASE INTERNA  VIEW HOLDER, (ES LLAMADA EN EL MÉTODO onCreateViewHolder)
    //############  LO ÚNICO QUE HACE ES TENER ASIGNADOS LOS TextView (sus atributos) A LOS CORRESPONDIENTES DEL LAYOUT
    public  class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCita;
        public TextView tvAutor;

        public ViewHolder(View view){
            super(view);
            tvCita = view.findViewById(R.id.etiqueta_cita);
            tvAutor = view.findViewById(R.id.etiqueta_autor);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClickListener(getAdapterPosition());
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    longClickListener.onItemLongClickListener(getAdapterPosition());
                    return true;
                }
            });
        }
    }
    //#########     CLASE INTERNA  VIEW HOLDER, (ES LLAMADA EN EL MÉTODO onCreateViewHolder)
}
