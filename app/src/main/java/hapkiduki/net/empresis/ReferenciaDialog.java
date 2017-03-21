package hapkiduki.net.empresis;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import java.util.ArrayList;

import hapkiduki.net.empresis.adapters.ReferenciaAdapter;
import hapkiduki.net.empresis.clases.Referencia;

/**
 * Created by Programa-PC on 14/03/2017.
 */

public class ReferenciaDialog extends DialogFragment {

    View vista;

    RecyclerView recyclerReferencias;
    ArrayList<Referencia> listaReferencia;
    ReferenciaAdapter miAdapter;
    Activity actividad;

    ArrayList<Referencia> lista = new ArrayList<>();

    boolean mIsLargeLayout;


    /**
     * @return Creamos la interface que nos permitirá comunicarnos con la activity
     */
    public interface IProductos{
        public void enviaParametros(ArrayList<Referencia> productos);
    }

    IProductos listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_referencia, container);


        listaReferencia=new ArrayList<Referencia>();
        recyclerReferencias = (RecyclerView) vista.findViewById(R.id.recycler_view);
        recyclerReferencias.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerReferencias.setHasFixedSize(true);
        //lista = new ArrayList<Referencia>();

        mIsLargeLayout = true;


        cargarWebService();

        return vista;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    private void cargarWebService() {
        listaReferencia = (ArrayList<Referencia>) Referencia.listAll(Referencia.class);
        miAdapter=new ReferenciaAdapter(getActivity(),listaReferencia);
        //lista = new ArrayList<Referencia>();
        miAdapter.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "Selecciona el elemento: "+
                        listaReferencia.get(recyclerReferencias.getChildPosition(v)).getNomref(), Toast.LENGTH_SHORT).show();
                dismiss();

                int posicion = recyclerReferencias.getChildPosition(v);
                lista.add(listaReferencia.get(posicion));
                listener.enviaParametros(lista);

            }
        });

        recyclerReferencias.setAdapter(miAdapter);


    }

    //Metodo para instanciar nuestro listener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            listener = (ReferenciaDialog.IProductos) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " Debe implementar la interface");
        }
    }






}
