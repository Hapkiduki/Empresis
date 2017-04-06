package hapkiduki.net.empresis.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hapkiduki.net.empresis.R;
import hapkiduki.net.empresis.clases.Pedido;

/**
 * Created by Programa-PC on 28/03/2017.
 */

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.ViewHolder>{

    private Context context;
    private List<Pedido> pedidos;


    public PedidoAdapter(Context context, List<Pedido> pedidos) {
        this.context = context;
        this.pedidos = pedidos;
    }

    @Override
    public PedidoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_pedidos, parent, false);
        return new PedidoAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PedidoAdapter.ViewHolder holder, final int position) {
        holder.customer.setText(pedidos.get(position).getTercero().getTercero());
        holder.productsQuantity.setText("Productos: 3");
        holder.finalPrice.setText(DecimalFormat.getCurrencyInstance(Locale.US).format(pedidos.get(position).getPrecioTotal()));
        holder.itemView.setTag(pedidos.get(position));

        holder.x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(pedidos, position);
            }
        });

    }

    private void removeItem(List<Pedido> pedidos, int position) {
        pedidos.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView customer;
        TextView productsQuantity, finalPrice;
        Button x;

        public ViewHolder(View itemView) {
            super(itemView);

            customer = (TextView) itemView.findViewById(R.id.tvProduct);
            productsQuantity = (TextView) itemView.findViewById(R.id.tvQuantity);
            finalPrice =  (TextView) itemView.findViewById(R.id.tvPrice);
            x = (Button) itemView.findViewById(R.id.btnDelete);
        }
    }

    //Creamos el filtro o Scope para recorrer nuestro recicler view
    public void filter(ArrayList<Pedido> query){
        pedidos = new ArrayList<>();
        pedidos.addAll(query);
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
