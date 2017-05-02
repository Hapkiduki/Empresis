package hapkiduki.net.empresis.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.clases.DeleteListener;
import hapkiduki.net.empresis.clases.Referencia;

/**
 * Created by Programa-PC on 08/03/2017.
 */

public class GestionPedidoAdapter extends RecyclerView.Adapter<GestionPedidoAdapter.ViewHolder>{

    private Context context;
    private List<Referencia> referencias;


    DeleteListener listener;
    boolean filtroEstado;


    public GestionPedidoAdapter(Context context, List<Referencia> referencias) {
        this.context = context;
        this.referencias = referencias;

    }

    @Override
    public GestionPedidoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_pedidos, parent, false);
        return new GestionPedidoAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final GestionPedidoAdapter.ViewHolder holder, final int position) {
        holder.product.setText(referencias.get(position).getNomref());
        holder.quantity.setText(referencias.get(position).getCantPed());
        holder.price.setText(""+DecimalFormat.getCurrencyInstance(Locale.US).format(Double.parseDouble(referencias.get(position).getPrice())));
        holder.itemView.setTag(referencias.get(position));

        holder.x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener = (DeleteListener) context;
                listener.pinchado(referencias.get(holder.getAdapterPosition()).getCodRef());
                if (filtroEstado)
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

    public void setFilter(ArrayList<Referencia> query) {
        filtroEstado = true;
        referencias = new ArrayList<>();
        referencias.addAll(query);
        notifyDataSetChanged();
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


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
