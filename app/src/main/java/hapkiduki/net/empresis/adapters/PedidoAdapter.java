package hapkiduki.net.empresis.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.clases.Referencia;
import hapkiduki.net.empresis.clases.Tercero;

/**
 * Created by Programa-PC on 08/03/2017.
 */

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.ViewHolder>{

    private Context context;
    private List<Referencia> referencias;


    public PedidoAdapter(Context context, List<Referencia> referencias) {
        this.context = context;
        this.referencias = referencias;
    }

    @Override
    public PedidoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_pedidos, parent, false);
        return new PedidoAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PedidoAdapter.ViewHolder holder, final int position) {
        holder.product.setText(referencias.get(position).getNomref());
        holder.quantity.setText(referencias.get(position).getQuantity());
        holder.price.setText(referencias.get(position).getPrice());
        holder.itemView.setTag(referencias.get(position));

        holder.x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(referencias, position);
            }
        });

    }

    private void removeItem(List<Referencia> referencias, int position) {
        referencias.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return referencias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView product;
        TextView quantity, price;
        Button x;

        public ViewHolder(View itemView) {
            super(itemView);

            product = (TextView) itemView.findViewById(R.id.tvProduct);
            quantity = (TextView) itemView.findViewById(R.id.tvQuantity);
            price =  (TextView) itemView.findViewById(R.id.tvPrice);
            x = (Button) itemView.findViewById(R.id.btnDelete);
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
