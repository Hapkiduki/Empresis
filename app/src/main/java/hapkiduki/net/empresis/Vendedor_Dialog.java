package hapkiduki.net.empresis;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class Vendedor_Dialog extends DialogFragment implements View.OnClickListener {

    View vista;
    TextInputLayout ed_vendedor;
    Button btnCancel, btnOk;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.dialog_vendedor, container);

        ed_vendedor = (TextInputLayout) vista.findViewById(R.id.til_ven);
        btnCancel = (Button) vista.findViewById(R.id.btnCancel);
        btnOk = (Button) vista.findViewById(R.id.btnOk);

        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        return vista;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }


    //Metodo para instanciar nuestro listener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    /*    try{
            listener = (TerceroDialogListner) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " Debe implementar la interface");
        }*/
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == btnOk.getId()){
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("vendedor_text", ed_vendedor.getEditText().getText().toString());
                editor.commit();
            Toast.makeText(getActivity(), "Vendedor asignado: "+ed_vendedor.getEditText().getText(), Toast.LENGTH_SHORT).show();
            dismiss();
        }else {
            dismiss();
        }
    }
}