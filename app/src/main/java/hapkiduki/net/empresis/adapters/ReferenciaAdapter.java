package hapkiduki.net.empresis.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hapkiduki.net.empresis.Activities.ReferenciasActivity;
import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.clases.Referencia;

/**
 * Created by Programa-PC on 01/03/2017.
 */

public class ReferenciaAdapter extends RecyclerView.Adapter<ReferenciaAdapter.ViewHolder> implements View.OnClickListener{

    private Context context;
    private List<Referencia> referencias;
    //para cambios
    private View.OnClickListener listener;

    public ReferenciaAdapter(Context context, List<Referencia> referencias) {
        this.context = context;
        this.referencias = referencias;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list, parent, false);
        itemView.setOnClickListener(this);


        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.nombre.setText(referencias.get(position).getNomref());
        holder.id.setText(referencias.get(position).getCodRef());
        holder.chkProdu.setChecked(referencias.get(position).getState());
        holder.itemView.setTag(referencias.get(position));


        if (context.toString().contains("EmpresisActivity")) {
            holder.chkProdu.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return referencias.size();
    }

    public void setOnClick(View.OnClickListener listener){this.listener = listener;}

    @Override
    public void onClick(View v) {
        if(listener != null)
            listener.onClick(v);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView nombre;
        TextView id;
        CheckBox chkProdu;



        public ViewHolder(View itemView) {
            super(itemView);

            nombre = (TextView) itemView.findViewById(R.id.tvNom);
            id = (TextView) itemView.findViewById(R.id.tvId);
            chkProdu = (CheckBox) itemView.findViewById(R.id.chkProdu);

            chkProdu.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            System.out.println("Posicion: "+getAdapterPosition());
        }
    }

    //Creamos el filtro o Scope para recorrer nuestro recicler view
    public void filter(ArrayList<Referencia> query){
        referencias = new ArrayList<>();
        referencias.addAll(query);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}
