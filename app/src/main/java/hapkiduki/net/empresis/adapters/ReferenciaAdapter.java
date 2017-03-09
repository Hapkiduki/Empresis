package hapkiduki.net.empresis.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.clases.Referencia;

/**
 * Created by Programa-PC on 01/03/2017.
 */

public class ReferenciaAdapter extends RecyclerView.Adapter<ReferenciaAdapter.ViewHolder>{

    private Context context;
    private List<Referencia> referencias;

    public ReferenciaAdapter(Context context, List<Referencia> referencias) {
        this.context = context;
        this.referencias = referencias;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.nombre.setText(referencias.get(position).getNomref());
        holder.id.setText(referencias.get(position).getCodRef());
        holder.itemView.setTag(referencias.get(position));
    }

    @Override
    public int getItemCount() {
        return referencias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView nombre;
        TextView id;


        public ViewHolder(View itemView) {
            super(itemView);

            nombre = (TextView) itemView.findViewById(R.id.tvNom);
            id = (TextView) itemView.findViewById(R.id.tvId);
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
