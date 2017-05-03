package hapkiduki.net.empresis.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hapkiduki.net.empresis.Activities.ProductosActivity;
import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.clases.Referencia;

/**
 * Created by Programa-PC on 01/03/2017.
 */

public class ReferenciaAdapter extends RecyclerView.Adapter<ReferenciaAdapter.ViewHolder> {

    private Context context;
    private List<Referencia> referencias;
    private SparseBooleanArray mSelectedItemsIds;
    List<Referencia> oldReferences;

    public ReferenciaAdapter(Context context, List<Referencia> referencias) {
        this.context = context;
        this.referencias = referencias;
        mSelectedItemsIds = new SparseBooleanArray();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.nombre.setText(referencias.get(position).getNomref());
        holder.id.setText(referencias.get(position).getCodRef());
        holder.itemView.setTag(referencias.get(position));
        holder.itemView.setBackgroundColor(mSelectedItemsIds.get(position) ? 0x9934B5E4 : Color.TRANSPARENT);
    }


    public boolean toggleSelection(int position) {
        return selectView(position, !mSelectedItemsIds.get(position));
    }

    //Remove selected selections
    public void removeSelection() {

        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    private boolean selectView(int position, boolean value) {

        boolean valor = false;
        if (value) {
            mSelectedItemsIds.put(position, value);
            valor =  true;
        }else{
            mSelectedItemsIds.delete(position);
            valor = false;
        }
        notifyDataSetChanged();
        return valor;
    }

    //Get total selected count
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    //Return all selected ids
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }


    @Override
    public int getItemCount() {
        if (referencias != null)
            return referencias.size();
        return 0;
        /*return referencias.size();*/
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

    //Metodo de prueba
    private static class FooterViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar getProgressBar() {
            return progressBar;
        }

        private ProgressBar progressBar;

        public FooterViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.footer);
        }
    }

    /** Creamos el filtro o Scope para recorrer nuestro recicler view */
    public void filter(ArrayList<Referencia> query, ArrayList<Referencia> referencias){
        this.referencias = new ArrayList<>();
        this.referencias.addAll(query);
        notifyDataSetChanged();
        oldReferences = referencias;

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
