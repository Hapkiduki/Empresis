package hapkiduki.net.empresis;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
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
    CheckBox chkRemember;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.dialog_vendedor, container);

        ed_vendedor = (TextInputLayout) vista.findViewById(R.id.til_ven);
        btnCancel = (Button) vista.findViewById(R.id.btnCancel);
        btnOk = (Button) vista.findViewById(R.id.btnOk);
        chkRemember = (CheckBox) vista.findViewById(R.id.remember);

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
            if (chkRemember.isChecked())
            Toast.makeText(getActivity(), "Hola", Toast.LENGTH_SHORT).show();
            else Toast.makeText(getActivity(), "Buajajaj", Toast.LENGTH_SHORT).show();
        }else {
            dismiss();
        }
    }
}