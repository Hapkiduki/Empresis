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

import hapkiduki.net.empresis.adapters.TerceroAdapter;
import hapkiduki.net.empresis.clases.Tercero;

/**
 * Created by Programa-PC on 07/03/2017.
 */

public class TerceroDialog extends DialogFragment{


    View vista;
    RecyclerView recyclerTerceros;
    ArrayList<Tercero> listaTerce;
    TerceroAdapter miAdapter;
    Activity actividad;

    //Creamos la interface para comunicarnos con la activity
    public interface TerceroDialogListner{

        public void onDialogPositiveClick(ArrayList<Tercero> listaTerce, int posicion);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    TerceroDialogListner listener;
    ///

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.activity_tercero, container);

        listaTerce=new ArrayList<Tercero>();
        recyclerTerceros = (RecyclerView) vista.findViewById(R.id.recycler_view);
        recyclerTerceros.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerTerceros.setHasFixedSize(true);


        cargarWebServiceImagenes();

        return vista;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    private void cargarWebServiceImagenes() {
        listaTerce = (ArrayList<Tercero>) Tercero.listAll(Tercero.class);
        miAdapter=new TerceroAdapter(getActivity(),listaTerce);
        miAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ArrayList lista = new ArrayList();
                Toast.makeText(getActivity(), "Cliente: "+
                        listaTerce.get(recyclerTerceros.getChildPosition(v)).getTercero(), Toast.LENGTH_SHORT).show();
                dismiss();
                listener.onDialogPositiveClick(listaTerce, recyclerTerceros.getChildPosition(v));


            }
        });
        recyclerTerceros.setAdapter(miAdapter);
    }

    //Metodo para instanciar nuestro listener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            listener = (TerceroDialogListner) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " Debe implementar la interface");
        }
    }





}
