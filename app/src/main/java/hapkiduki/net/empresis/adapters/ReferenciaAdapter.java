package hapkiduki.net.empresis.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
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

public class ReferenciaAdapter extends RecyclerView.Adapter<ReferenciaAdapter.ViewHolder> /*implements View.OnClickListener*/{


    private Context context;
    private List<Referencia> referencias;
    //para cambios
    //private View.OnClickListener listener;
    private SparseBooleanArray mSelectedItemsIds;


    public ReferenciaAdapter(Context context, List<Referencia> referencias) {
        this.context = context;
        this.referencias = referencias;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list, parent, false);
        //itemView.setOnClickListener(this);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        //final Referencia referencia = referencias.get(position);

        holder.nombre.setText(referencias.get(position).getNomref());
        holder.id.setText(referencias.get(position).getCodRef());

        holder.itemView.setTag(referencias.get(position));

        holder.itemView.setBackgroundColor(mSelectedItemsIds.get(position) ? 0x9934B5E4 : Color.TRANSPARENT);

         //lista = new ArrayList<>();

        //holder.itemView.setBackgroundColor(referencia.getState() ? Color.CYAN : Color.DKGRAY);
       /* holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                referencia.setState(!referencia.getState());
                holder.itemView.setBackgroundColor(referencia.getState() ? Color.CYAN : Color.TRANSPARENT);

                if(referencia.getState()){
                    lista.add(position);
                }

                for(int x : lista){
                    System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                            "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxSelecciona: "+x);
                }
            }
        });*/



    }

//sujeto a cambios
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    //Remove selected selections
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    private void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();

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
        return referencias.size();
    }

    /*public void setOnClick(View.OnClickListener listener){this.listener = listener;}

    @Override
    public void onClick(View v) {
        if(listener != null)
            listener.onClick(v);

    }*/


    public static class ViewHolder extends RecyclerView.ViewHolder{


        TextView nombre;
        TextView id;


        public ViewHolder(View itemView) {
            super(itemView);

            nombre = (TextView) itemView.findViewById(R.id.tvNom);
            id = (TextView) itemView.findViewById(R.id.tvId);


        }


    }

    /** Creamos el filtro o Scope para recorrer nuestro recicler view */
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
